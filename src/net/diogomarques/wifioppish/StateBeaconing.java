package net.diogomarques.wifioppish;

import android.content.Context;
import android.os.Handler;

/* NOTE: in this implementation, beaconing lasts t_beac when it first starts and also when StateProviding finishes due to disconnection.
 * 
 */
public class StateBeaconing extends State {

	public StateBeaconing(Context context, Handler handler,
			Preferences preferences, INetworkingFacade networking) {
		super(context, handler, preferences, networking);
	}

	@Override
	public void start() {
		writeToConsole("entered beaconing state");
		writeToConsole("(re) starting AP");
		mNetworking.startWifiAP();
		mNetworking.setOnReceiveListener(new INetworkingFacade.OnReceiveListener() {
			@Override
			public void onReceiveTimeout(boolean forced) {
				writeToConsole("t_beac timeout, stopping AP");
				// stop ap and go to scanning
				mNetworking.stopWifiAP();
				gotoScanning();
			}

			@Override
			public void onMessageReceived(String msg) {
				writeToConsole("message received: " + msg);
				gotoProviding();
			}
		});
		mNetworking.receiveFirst(mPreferences.getTBeac());
	}

	private void gotoProviding() {
		new StateProviding(mContext, mConsoleHandler, mPreferences, mNetworking)
				.start();
	}

	private void gotoScanning() {
		new StateScanning(mContext, mConsoleHandler, mPreferences, mNetworking)
				.start();
	}
}
