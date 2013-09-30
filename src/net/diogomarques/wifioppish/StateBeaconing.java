package net.diogomarques.wifioppish;

import android.content.Context;
import net.diogomarques.wifioppish.IEnvironment.State;

/**
 * Android implementation of state {@link IEnvironment.State#Beaconing}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateBeaconing extends AState {
	
	private Context context;

	public StateBeaconing(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		final INetworkingFacade networking = environment.getNetworkingFacade();
		environment.deliverMessage("entered beaconing state");
		environment.deliverMessage("(re) starting AP");
		networking.startAcessPoint();
		networking.receiveFirst(timeout,
				new INetworkingFacade.OnReceiveListener() {
					@Override
					public void onReceiveTimeout(boolean forced) {
						environment
								.deliverMessage("t_beac timeout, stopping AP");
						// stop ap and go to scanning
						networking.stopAccessPoint();
						environment.gotoState(State.Scanning);
					}

					@Override
					public void onMessageReceived(String msg) {
						environment.deliverMessage("message received: " + msg);
						environment.gotoState(State.Providing);
					}
				});
	}
}
