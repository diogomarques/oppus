package net.diogomarques.wifioppish;

import android.content.Context;
import android.util.Log;
import net.diogomarques.wifioppish.IEnvironment.State;

/**
 * Android implementation of state {@link IEnvironment.State#Internet}
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class StateInternet extends AState {

	public StateInternet(IEnvironment env) {
		super(env);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start(int timeout, Context context) {
		Log.w("Machine State", "Internet");

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

						environment.gotoState(State.Connected);
					}
				});

	}


}
