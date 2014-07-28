package net.diogomarques.wifioppish;

import android.content.Context;
import android.util.Log;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.networking.Message;
import net.diogomarques.wifioppish.networking.MessageGroup;

/**
 * Android implementation of state {@link IEnvironment.State#Providing}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateProviding extends AState {

	/**
	 * Creates a new Providing state
	 * @param environment Environment running the state machine
	 */
	public StateProviding(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "Providing");
		
		final INetworkingFacade networking = environment.getNetworkingFacade();
		environment.deliverMessage("entered providing state");

		networking.receive(timeout, new INetworkingFacade.OnReceiveListener() {
			@Override
			public void onReceiveTimeout(boolean forced) {
				// t_pro reached
				if (forced) {
					environment.deliverMessage("t_pro timeout, stopping AP");
					networking.stopAccessPoint();
					
					//goes to internet state if enabled
					if(environment.internetState())
						environment.gotoState(State.InternetCheck);
					else
						environment.gotoState(State.Scanning);
					}
				// no connections
				else {
					environment.deliverMessage("no connections to provide for");
					environment.gotoState(State.Beaconing);
				}
			}

			@Override
			public void onMessageReceived(Message m) {
				environment.deliverMessage("message received: " + m.toString());
				
				// avoid duplicates
				if( !m.getNodeId().equals(environment.getMyNodeId())) {
					environment.pushMessageToQueue(m);
					environment.storeReceivedMessage(m);
				}
			}
			
			@Override
			public void onMessageReceived(MessageGroup msgs) {
				for(Message m : msgs)
					onMessageReceived(m);
			}
		});

	}

}
