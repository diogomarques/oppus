package net.diogomarques.wifioppish;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.diogomarques.wifioppish.logging.TextLog;

import android.app.Activity;
import android.content.Context;
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
				data.add(new TextMessageListItem(contents, newMessage.getTimestamp()));
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
			
			case Internet:
			text = descriptions[3];
			imgResource = R.drawable.internet;
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
		}
		
		mTextViewStatus.setText(text);
		mImageStatus.setImageResource(imgResource);
}
	
	/**
	 * List item container for a text message with sending status
	 * @author André Silva <asilva@lasige.di.fc.ul.pt>
	 */
	private class TextMessageListItem {
		private String message;
		private boolean sent;
		private long timestamp;
		
		public TextMessageListItem(String message, long timestamp) {
			super();
			this.message = message;
			this.timestamp = timestamp;
			this.sent = false;
		}
		
		public String getMessage() {
			return message;
		}
		
		public long getTimestamp() {
			return timestamp;
		}

		public boolean isSent() {
			return sent;
		}

		public void setSent(boolean sent) {
			this.sent = sent;
		}

		@Override
		public String toString() {
			return message;
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
	      
	      if(curItem.isSent())
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
		 * Log line to show in the log window
		 */
		public static final int LOG_MSG = 800;
		
		/**
		 * The node role (Beaconing, Providing, Scanning, Station) was changed
		 */
		public static final int ROLE = 801;
		
		
		public static final int MSG_SENT = 804;
		
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
				
			// message successfully sent, mark as sent
			} else if(msg.what == MSG_SENT) {
				final net.diogomarques.wifioppish.networking.Message sent = 
						(net.diogomarques.wifioppish.networking.Message) msg.obj;
				
				for(TextMessageListItem m : activity.data) {
					if(m.getTimestamp() == sent.getTimestamp() && m.getMessage().equals(sent.getMessage())) {
						m.setSent(true);
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
