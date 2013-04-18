package net.diogomarques.wifioppish;

import android.content.Context;
import android.os.Handler;

public class StateProviding extends State {

	public StateProviding(Context context, Handler handler,
			Preferences preferences, INetworkingFacade networking) {
		super(context, handler, preferences, networking);
	}

	@Override
	public void start() {
		writeToConsole("entered providing state");
		mNetworking.setOnReceiveListener(new INetworkingFacade.OnReceiveListener() {
			@Override
			public void onReceiveTimeout(boolean forced) {
				// t_pro reached
				if (forced) {
					writeToConsole("t_pro timeout, stopping AP");
					mNetworking.stopWifiAP();
					gotoScanning();
				}
				// no connections
				else {
					writeToConsole("no connections to provide for");
					gotoBeaconing();
				}
			}
			@Override
			public void onMessageReceived(String msg) {
				writeToConsole("message received: " + msg);			
			}			
		});
		mNetworking.receive(mPreferences.getTBeac());
		
	}
	
	private void gotoBeaconing() {
		new StateBeaconing(mContext, mConsoleHandler, mPreferences, mNetworking).start();
	}

	private void gotoScanning() {
		new StateScanning(mContext, mConsoleHandler, mPreferences, mNetworking).start();
	}

}
