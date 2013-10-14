package net.diogomarques.wifioppish;

import android.content.Context;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.networking.Message;

/**
 * Android implementation of state {@link IEnvironment.State#Providing}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateProviding extends AState {

	public StateProviding(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		final INetworkingFacade networking = environment.getNetworkingFacade();
		environment.deliverMessage("entered providing state");

		networking.receive(timeout, new INetworkingFacade.OnReceiveListener() {
			@Override
			public void onReceiveTimeout(boolean forced) {
				// t_pro reached
				if (forced) {
					environment.deliverMessage("t_pro timeout, stopping AP");
					networking.stopAccessPoint();
					environment.gotoState(State.Scanning);
				}
				// no connections
				else {
					environment.deliverMessage("no connections to provide for");
					environment.gotoState(State.Beaconing);
				}
			}

			@Override
			public void onMessageReceived(String msg) {
				environment.deliverMessage("message received: " + msg);
			}

			@Override
			public void onMessageReceived(Message m) {
				// discard self messages from appearing in log
				if(!environment.getMyNodeId().equals(m.getAuthor()))
					environment.deliverMessage("message received: " + m.toString());
			}
		});

	}

}
