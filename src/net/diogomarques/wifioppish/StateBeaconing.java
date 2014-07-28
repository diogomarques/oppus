package net.diogomarques.wifioppish;

import android.content.Context;
import android.util.Log;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.networking.Message;
import net.diogomarques.wifioppish.networking.MessageGroup;

/**
 * Android implementation of state {@link IEnvironment.State#Beaconing}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateBeaconing extends AState {

	/**
	 * Creates a new Beaconing state
	 * @param environment Environment running the state machine
	 */
	public StateBeaconing(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "Beaconing");
		
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
						
						// goes to internet state if enabled
						if(environment.internetState())
							environment.gotoState(State.InternetCheck);
						else
							environment.gotoState(State.Scanning);
					}

					@Override
					public void onMessageReceived(Message m) {
						environment.deliverMessage("message received: " + m.toString());
						environment.pushMessageToQueue(m);
						environment.gotoState(State.Providing);
					}

					@Override
					public void onMessageReceived(MessageGroup msgs) {
						for(Message m : msgs) {
							environment.deliverMessage("message received: " + m.toString());
							environment.pushMessageToQueue(m);
						}
						
						environment.gotoState(State.Providing);
					}
				});
	}
}
