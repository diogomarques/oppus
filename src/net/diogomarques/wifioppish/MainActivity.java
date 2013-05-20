package net.diogomarques.wifioppish;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	protected static final String MSG_CONSOLE = "msgConsole";
	// TODO get console line from ui prefs
	private static final int DEFAULT_CONSOLE_LINES = 20;
	TextView console;
	Button btSend, btStart;
	Context mContext;
	AndroidPreferences mPreferences;
	INetworkingFacade mNetworking;
	Handler mHandler;
	LinkedBlockingQueue<String> mConsoleBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mConsoleBuffer = new LinkedBlockingQueue<String>(DEFAULT_CONSOLE_LINES);
		mContext = this;
		mPreferences = new AndroidPreferences(mContext);
		mNetworking = new AndroidNetworkingFacade(mContext, mPreferences);
		// stop wifi AP that might be left open on abnormal app exit
		mNetworking.stopWifiAP();
		console = (TextView) findViewById(R.id.console);
		btSend = (Button) findViewById(R.id.buttonSend);
		btStart = (Button) findViewById(R.id.buttonStart);

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String txt = (String) msg.obj;
				addTextToConsole(txt);
			}
		};

		btStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				processStart();
			}
		});

		btSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				processSend();
			}
		});

	}

	protected String getCurrentBuffer() {
		StringBuilder builder = new StringBuilder();
		for (String line : mConsoleBuffer) {
			builder.append(line + "\n");
		}
		return builder.toString();
	}

	protected void addToBuffer(String line) {
		if (mConsoleBuffer.remainingCapacity() < 1)
			mConsoleBuffer.poll();
		String now = SimpleDateFormat.getTimeInstance().format(new Date());
		mConsoleBuffer.offer(now + " " + line);
	}

	protected void processSend() {

		final INetworkingFacade networking = mNetworking;
		networking.setOnSendListener(new INetworkingFacade.OnSendListener() {

			@Override
			public void onSendError(String errorMsg) {

				addTextToConsole("send error: " + errorMsg);
			}

			@Override
			public void onMessageSent(String msg) {
				addTextToConsole("message sent: " + msg);

			}
		});
		new Thread() {
			@Override
			public void run() {
				networking.send("hello");
			};
		}.start();
	}

	protected void processStart() {

		

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPostExecute(Void result) {
				btStart.setEnabled(true);
			}

			@Override
			protected void onPreExecute() {
				btStart.setEnabled(false);
			}

			@Override
			protected Void doInBackground(Void... params) {

				IEnvironment environment = new AndroidEnvironment(mHandler,
						mNetworking, mPreferences);
				environment.prepare();
				environment.gotoState(IEnvironment.State.Scanning);
				return null;

			}
		}.execute();

	}

	private void addTextToConsole(final String txt) {

		console.post(new Runnable() {

			@Override
			public void run() {
				addToBuffer(txt);
				console.setText(getCurrentBuffer());

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
