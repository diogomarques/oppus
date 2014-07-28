package net.diogomarques.wifioppish;

import android.content.Context;
import android.util.Log;
import net.diogomarques.wifioppish.IEnvironment.State;

/**
 * Android implementation of state {@link IEnvironment.State#InternetCheck}
 * 
 * @author André Rodrigues
 */
public class StateInternetCheck extends AState {

	/**
	 * Creates a new InternetCheck state
	 * @param environment Environment running the state machine
	 */
	public StateInternetCheck(IEnvironment env) {
		super(env);
	}

	@Override
	public void start(int timeout, Context context) {
		Log.w("Machine State", "Internet Checking");

		final INetworkingFacade networking = environment.getNetworkingFacade();
		environment.deliverMessage("entered Internet state");

		networking.scanForInternet(timeout,
				new INetworkingFacade.OnInternetConnection() {

					@Override
					public void onScanTimeout() {
						environment.deliverMessage("t_int timeout");

						if (environment.getLastState() == State.Scanning) {
							environment.deliverMessage("t_internet timeout");
							environment.gotoState(State.Beaconing);
						} else {
							environment.deliverMessage("t_internet timeout");
							environment.gotoState(State.Scanning);
						}

					}

					@Override
					public void onInternetConnection() {

						environment
								.deliverMessage("connected to the internet!");

						environment.gotoState(State.InternetConn);
					}
				});

	}


}
