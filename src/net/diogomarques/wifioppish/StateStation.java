package net.diogomarques.wifioppish;

import net.diogomarques.utils.CountDownTimer;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.INetworkingFacade.OnSendListener;
import net.diogomarques.wifioppish.networking.Message;
import android.content.Context;
import android.util.Log;


/**
 * Android implementation of state {@link IEnvironment.State#Station}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateStation extends AState {

	public StateStation(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "Station");
		
		context = c;
		environment.deliverMessage("entered station state");
		final INetworkingFacade networking = environment.getNetworkingFacade();
		
		// send messages for the network
		// TODO: adjust period
		int period = 1000; 
		new CountDownTimer(environment.getPreferences().getTCon(), period) {

			private boolean sentOnce = false;
			
			@Override
			public void onTick(long arg0) {
				
				if( !sentOnce ) {
				
					OnSendListener listener = new OnSendListener() {
	
						@Override
						public void onSendError(String errorMsg) {
							environment.deliverMessage("send error: " + errorMsg
									+ "[" + environment.getCurrentState().name()
									+ "]");
							cancel();
							environment.gotoState(State.Scanning);
						}
	
						@Override
						public void onMessageSent(String msg) {
							environment.deliverMessage("message successfully sent");
						}
					};
	
					// Prepare message to be sent
					double[] location = environment.getMyLocation();
					String nodeID = environment.getMyNodeId();
									
					Message msg = new Message(
							"I'm alive!",
							System.currentTimeMillis(),
							location,
							nodeID
					);
					
					networking.send(msg, listener);
					sentOnce = true;
				}
			}

			@Override
			public void onFinish() {
				environment
						.deliverMessage("t_con finished, exiting station mode");
				environment.gotoState(State.Scanning);

			}
		}.start();
	}
}
