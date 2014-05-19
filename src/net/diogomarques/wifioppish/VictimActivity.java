package net.diogomarques.wifioppish;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.diogomarques.wifioppish.logging.TextLog;
import net.diogomarques.wifioppish.sensors.BatterySensor;
import net.diogomarques.wifioppish.sensors.PedometerSensor;
import net.diogomarques.wifioppish.sensors.ScreenOnSensor;
import net.diogomarques.wifioppish.sensors.SensorGroup.GroupKey;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Activity to promote interaction with the victims. It allows a 
 * simplified visualization of the system status and the sending of
 * textual messages to the Internet (best effort).
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class VictimActivity extends Activity {
	
	/* controls */
	private Button mBtnSendMessage;
	private EditText mEditTextMessage;
	private TextView mTextViewStatus;
	private ImageView mImageStatus;
	private ListView mListMessagesSent;
	
	/* state machine */
	private IEnvironment environment;
	private Handler updatesHandler;
	private static TextLog log;
	private LocationProvider location;
	private ScreenOnSensor screenOn;
	private PedometerSensor steps;
	private BatterySensor battery;
	
	/* app data */
	private ArrayList<TextMessageListItem> data;
	private ArrayAdapter<TextMessageListItem> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* user interface setup */
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		setContentView(R.layout.activity_victim);
		
		mBtnSendMessage = (Button) findViewById(R.id.btnSendTextMessage);
		mEditTextMessage = (EditText) findViewById(R.id.txtTextMessage);
		mTextViewStatus = (TextView) findViewById(R.id.lblSystemStatus);
		mImageStatus = (ImageView) findViewById(R.id.imgSystemStatus);
		mListMessagesSent = (ListView) findViewById(R.id.lstSentMessages);
		
		// only enable send button if text message contains at least 4 characters
		mEditTextMessage.addTextChangedListener(new TextWatcher(){
	        public void afterTextChanged(Editable s) {
	            mBtnSendMessage.setEnabled( (s.length() >= 4) );
	        }
	        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
	        public void onTextChanged(CharSequence s, int start, int before, int count){}
	    });
		mBtnSendMessage.setEnabled(false);
		
		// prepare list to manage text messages in queue
		data = new ArrayList<VictimActivity.TextMessageListItem>();
		adapter = new TextMessageArrayAdapter(getApplicationContext(),
		        android.R.layout.simple_list_item_1, data);
		mListMessagesSent.setAdapter(adapter);
		
		// button sends message to queue and clears the input
		mBtnSendMessage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String contents = mEditTextMessage.getText().toString();
				
				// send message
				net.diogomarques.wifioppish.networking.Message newMessage = environment.createTextMessage(contents);
				environment.pushMessageToQueue(newMessage);
				
				// put on Message log
				data.add(new TextMessageListItem(newMessage));
				Collections.sort(data);
				adapter.notifyDataSetChanged();
				mEditTextMessage.setText("");
			}
		});
		
		/* state machine */
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor prefEditor = sp.edit();
		String id = NodeIdentification.getMyNodeId(this);
		prefEditor.putString("nodeID", id);
		prefEditor.commit();
		
		try {
			log = new TextLog();
		} catch (IOException e) {
			Log.e("TextLog", "External Storage not available");
		}
		
		location = new LocationProvider(this);
		location.startLocationDiscovery();
				
		// setup state machine
		updatesHandler = new StateChangeHandler(this);
		environment = AndroidEnvironment.createInstance(this, updatesHandler);
		// stop wifi AP that might be left open on abnormal app exit
		environment.getNetworkingFacade().stopAccessPoint();
		
		// add sensors to environment
		screenOn = new ScreenOnSensor();
		screenOn.startSensor(this);
		environment.getSensorGroup().addSensor(GroupKey.ScreenOn, screenOn);
		battery = new BatterySensor();
		battery.startSensor(this);
		environment.getSensorGroup().addSensor(GroupKey.Battery, battery);
		steps = new PedometerSensor();
		steps.startSensor(this);
		environment.getSensorGroup().addSensor(GroupKey.Steps, steps);
		
		environment.deliverMessage("my node ID is " + id);
		
		// start State Machine and populate system status fields
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPostExecute(Void result) {}

			@Override
			protected void onPreExecute() {}

			@Override
			protected Void doInBackground(Void... params) {
				environment.startStateLoop(environment.getPreferences()
						.getStartState());
				return null;
			}
		}.execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_victim, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
		
			// show wifioppish time parameters
			case R.id.menu_victim_settings:
			Intent i = new Intent(this, MyPreferenceActivity.class);
			startActivity(i);
			break;
			
			// show confirmation popup to make victim safe
			case R.id.menu_victim_marksafe:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIcon(android.R.drawable.stat_sys_warning);
			builder.setMessage(R.string.victim_marksafe_contents)
			       .setTitle(R.string.victim_marksafe_title);
			builder.setPositiveButton(
					getResources().getString(android.R.string.yes),
					new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							environment.markVictimAsSafe(true);
							item.setTitle(R.string.menu_victim_safe_off);
							item.setEnabled(false);
						}
					}
			);
			builder.setNegativeButton(
					getResources().getString(android.R.string.no),
					null
			);
			AlertDialog dialog = builder.create();
			dialog.show();

			default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Changes interface to show the current application status
	 * @param curState Current state of State Machine
	 */
	private void changeSystemStatus(IEnvironment.State curState) {
		String[] descriptions = getResources().getStringArray(R.array.victim_lblSystemStatus);
		String text = "";
		int imgResource = -1;
		
		switch(curState) {
			case Beaconing:
			text = descriptions[2];
			imgResource = R.drawable.beaconing;
			break;
			
			case InternetConn:
			text = descriptions[3];
			imgResource = R.drawable.internet_available;
			break;
			
			case Providing:
			text = descriptions[4];
			imgResource = R.drawable.providing;
			break;
			
			case Scanning:
			text = descriptions[0];
			imgResource = R.drawable.scanning;
			break;
				
			case Station:
			text = descriptions[1];
			imgResource = R.drawable.station;
			break;
			
			case InternetCheck:
			text = descriptions[5];
			imgResource = R.drawable.check_internet;
			break;
		}
		
		mTextViewStatus.setText(text);
		mImageStatus.setImageResource(imgResource);
	}
	
	/**
	 * List item container for a text message with sending status and the 
	 * message envelope
	 * @author André Silva <asilva@lasige.di.fc.ul.pt>
	 */
	private class TextMessageListItem implements Comparable<TextMessageListItem> {
		private net.diogomarques.wifioppish.networking.Message msgObject;
		private boolean sentNetwork;
		private boolean sentWebservice;
		
		/**
		 * Creates a new instance
		 * @param envelope Message envelope to be sent over network
		 */
		public TextMessageListItem(net.diogomarques.wifioppish.networking.Message envelope) {
			super();
			this.msgObject = envelope;
			this.sentNetwork = false;
		}
		
		/**
		 * Gets the text message contained in the envelope
		 * @return Text message
		 */
		public String getMessage() {
			return msgObject.getMessage();
		}
		
		/**
		 * Gets the timestamp of the envelope
		 * @return timestamp of message creation
		 */
		public long getTimestamp() {
			return msgObject.getTimestamp();
		}

		/**
		 * Return whenever the message is sent or not
		 * @return True if already sent; False otherwise
		 */
		public boolean isSentNetwork() {
			return sentNetwork;
		}

		/**
		 * Sets the sending status of message
		 * @param sent True if already sent; False otherwise
		 */
		public void setSentNetwork(boolean sent) {
			this.sentNetwork = sent;
		}
		
		/**
		 * Checks if the message on the envelope is equal to the message to compare
		 * @param other Message to compare to the one stored in the envelope
		 * @return True if the message is equal (same attributes); false otherwise
		 */
		public boolean messageEquals(net.diogomarques.wifioppish.networking.Message other) {
			return msgObject.equals(other);
		}

		@Override
		public String toString() {
			return getMessage();
		}

		@Override
		public int compareTo(TextMessageListItem another) {
			// descending order
			long result = another.getTimestamp() - this.getTimestamp();
			
			if(result == 0)
				return 0;
			
			return result < 0 ? -1 : 1;
		}

		public boolean isSentWebservice() {
			return sentWebservice;
		}

		public void setSentWebservice(boolean sentWebservice) {
			this.sentWebservice = sentWebservice;
		}
	}
	
	/**
	 * ArrayList Adapater for message list
	 * @author André Silva <asilva@lasige.di.fc.ul.pt>
	 */
	private class TextMessageArrayAdapter extends ArrayAdapter<TextMessageListItem> {
		final private ArrayList<TextMessageListItem> data;
		private Context context;
		
	    public TextMessageArrayAdapter(Context context, int resource,
				List<TextMessageListItem> objects) {
			super(context, resource, objects);
			this.context = context;
			this.data = (ArrayList<VictimActivity.TextMessageListItem>) objects;
		}
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	      LayoutInflater inflater = (LayoutInflater) context
	          .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	      View rowView = inflater.inflate(R.layout.listview_victim_messages, parent, false);
	      TextView textView = (TextView) rowView.findViewById(R.id.lblMessageContents);
	      TextMessageListItem curItem = data.get(position);
	      textView.setText(curItem.getMessage());
	      
	      if(curItem.isSentWebservice())
	    	  textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sent_cloud, 0, 0, 0);
	      else if(curItem.isSentNetwork())
	    	  textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.green_check, 0, 0, 0);
	      else
	    	  textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.waiting, 0, 0, 0);
	    	  
	      return rowView;
	    }
	}
	
	/**
	 * Handles state/role changes to update the user interface
	 * 
	 * @author André Silva <asilva@lasige.di.fc.ul.pt>
	 */
	static class StateChangeHandler extends Handler {
		
		/**
		 * New log line to show in the log window
		 */
		public static final int LOG_MSG = 800;
		
		/**
		 * The node role (Beaconing, Providing, Scanning, Station) was changed
		 */
		public static final int ROLE = 801;
		
		/**
		 * A new message was sent to the network
		 */
		public static final int MSG_SENT = 804;
		
		/**
		 * A Message was sent directly to the webservice
		 */
		public static final int MSG_SENT_WS = 805;
		
		final WeakReference<VictimActivity> mActivity;

		StateChangeHandler(VictimActivity act) {
			mActivity = new WeakReference<VictimActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			
			final VictimActivity activity = mActivity.get();
			
			if(activity == null)
				return;
			
			// state changed
			if (msg.what == ROLE) {
				final String role = (String) msg.obj;
				
				// detect role
				for (IEnvironment.State typе : IEnvironment.State.values()) {
			        if (typе.toString().equals(role)) {
			        	activity.changeSystemStatus(typе);
			        	break;
			        }
			    }
				
			// message successfully sent to network, mark as sent
			} else if(msg.what == MSG_SENT) {
				final net.diogomarques.wifioppish.networking.Message sent = 
						(net.diogomarques.wifioppish.networking.Message) msg.obj;
				
				for (int i = 0; i < activity.data.size(); i++) {
					TextMessageListItem tmli = activity.data.get(i);
					if(tmli.messageEquals(sent)) {
						tmli.setSentNetwork(true);
						activity.adapter.notifyDataSetChanged();
						break;
					}
				}
			
			// message successfully sent to webservice
			} else if(msg.what == MSG_SENT_WS) {
				final net.diogomarques.wifioppish.networking.Message sent = 
						(net.diogomarques.wifioppish.networking.Message) msg.obj;
				
				for (int i = 0; i < activity.data.size(); i++) {
					TextMessageListItem tmli = activity.data.get(i);
					if(tmli.messageEquals(sent)) {
						tmli.setSentWebservice(true);
						activity.adapter.notifyDataSetChanged();
						break;
					}
				}
				
			// text log message	
			} else if(msg.what == LOG_MSG) {
				try {
					if(log != null)
						log.storeLine(msg.obj.toString());
				} catch (IOException e) {
					Log.e("TextLog", "Cannot write to log file: " + e.getMessage());
				}
			}
			
		}
	}
	
}
