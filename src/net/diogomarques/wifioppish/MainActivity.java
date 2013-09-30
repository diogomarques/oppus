package net.diogomarques.wifioppish;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Main UI activity, in which statuses from operations occurring in the state
 * machine are printed out to a buffer.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public class MainActivity extends Activity {

	/**
	 * An handler that prints received messages to the console.
	 */
	static class ConsoleHandler extends Handler {
		final WeakReference<MainActivity> mActivity;

		ConsoleHandler(MainActivity act) {
			mActivity = new WeakReference<MainActivity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			String txt = (String) msg.obj;
			if (mActivity.get() != null) // lifecycle not ended
				mActivity.get().addTextToConsole(txt);
		}
	}

	private static final int DEFAULT_CONSOLE_LINES = 120;

	TextView mConsoleTextView;
	Button mStartButton;
	ScrollView mScrollView;
	IEnvironment mEnvironment;
	ConsoleHandler mHandler;
	LinkedBlockingQueue<String> mConsoleBuffer;
	TextLog log;
	LocationProvider location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Force portrait view
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); 
		setContentView(R.layout.activity_main);
		// reset prefs to default
		if (AndroidPreferences.DEBUG)
			PreferenceManager.getDefaultSharedPreferences(this).edit().clear()
					.commit();
		// load default preferences
		PreferenceManager.setDefaultValues(this, R.xml.preferences, true);
		// build state
		mConsoleBuffer = new LinkedBlockingQueue<String>(DEFAULT_CONSOLE_LINES);
		mHandler = new ConsoleHandler(this);
		mEnvironment = AndroidEnvironment.createInstance(this, mHandler);
		// stop wifi AP that might be left open on abnormal app exit
		mEnvironment.getNetworkingFacade().stopAccessPoint();
		// setup views
		mScrollView = (ScrollView) findViewById(R.id.scroll);
		mConsoleTextView = (TextView) findViewById(R.id.console);
		mStartButton = (Button) findViewById(R.id.buttonStart);
		mStartButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				processStart();
			}
		});	
		
		try {
			log = new TextLog();
		} catch (IOException e) {
			Log.e("TextLog", "External Storage not available");
		}
		
		location = new LocationProvider(getApplicationContext());
		
		// generate unique ID for this node
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Editor prefEditor = sp.edit();
		String id = generateNodeId();
		prefEditor.putString("nodeID", id);
		prefEditor.commit();
		mEnvironment.deliverMessage("my node ID is " + id);
	}

	String getCurrentBuffer() {
		StringBuilder builder = new StringBuilder();
		for (String line : mConsoleBuffer) {
			builder.append(line + "\n");
		}
		return builder.toString();
	}

	void addToBufferWithTimestamp(String line) {
		if (mConsoleBuffer.remainingCapacity() < 1)
			mConsoleBuffer.poll();
		String now = SimpleDateFormat.getTimeInstance().format(new Date());
		mConsoleBuffer.offer(now + " " + line);
		mScrollView.smoothScrollBy(0, mConsoleTextView.getBottom());
	}

	protected void processStart() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPostExecute(Void result) {
			}

			@Override
			protected void onPreExecute() {
				mStartButton.setEnabled(false);
			}

			@Override
			protected Void doInBackground(Void... params) {
				mEnvironment.startStateLoop(mEnvironment.getPreferences()
						.getStartState());
				return null;
			}
		}.execute();
	}

	private void addTextToConsole(final String txt) {
		mConsoleTextView.post(new Runnable() {
			@Override
			public void run() {
				addToBufferWithTimestamp(txt);
				mConsoleTextView.setText(getCurrentBuffer());
				try {
					if(log != null)
						log.storeLine(txt);
				} catch (IOException e) {
					Log.e("TextLog", "Cannot write to log file: " + e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, MyPreferenceActivity.class);
			startActivity(i);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * (Hppefully) Generates an unique ID in the network
	 * @return 7 digit alphanumeric ID
	 */
	private String generateNodeId() {
		char[] set = new char[36];
		Random r = new Random();
		int pos = 0;
		
		// numbers
		for(int i = 48; i <= 57; i++)
			set[pos++] = (char) i;
		
		// letters
		for(int i = 65; i <= 90; i++)
			set[pos++] = (char) i;
		
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < 7; i++)
			sb.append(set[r.nextInt(36)]);
		
		return sb.toString();
	}

}
