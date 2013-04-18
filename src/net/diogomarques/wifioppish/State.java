package net.diogomarques.wifioppish;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public abstract class State {

	protected Context mContext;

	protected Preferences mPreferences;

	protected INetworkingFacade mNetworking;
	
	protected Handler mConsoleHandler;

	public State(Context context, Handler handler, Preferences preferences,
			INetworkingFacade networking) {
		mContext = context;
		mConsoleHandler = handler;
		mPreferences = preferences;
		mNetworking = networking;
	}
	
	public void writeToConsole(String txt) {
		mConsoleHandler.sendMessage(Message.obtain(mConsoleHandler, 0, txt));		
	}
	
	public abstract void start();

}
