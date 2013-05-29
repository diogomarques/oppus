package net.diogomarques.wifioppish;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

	// TODO choose console lines according to screen size
	private static final int DEFAULT_CONSOLE_LINES = 15;

	TextView console;
	Button btStart;
	IEnvironment mEnvironment;
	ConsoleHandler mHandler;
	LinkedBlockingQueue<String> mConsoleBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		console = (TextView) findViewById(R.id.console);
		btStart = (Button) findViewById(R.id.buttonStart);
		btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				processStart();
			}
		});
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
	}

	protected void processStart() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPostExecute(Void result) {
			}

			@Override
			protected void onPreExecute() {
				btStart.setEnabled(false);
			}

			@Override
			protected Void doInBackground(Void... params) {
				mEnvironment.gotoState(mEnvironment.getPreferences()
						.getStartState());
				return null;
			}
		}.execute();
	}

	private void addTextToConsole(final String txt) {
		console.post(new Runnable() {
			@Override
			public void run() {
				addToBufferWithTimestamp(txt);
				console.setText(getCurrentBuffer());
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

}
