package net.diogomarques.wifioppish;

import java.util.ArrayList;
import java.util.List;

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
		
		// prepare messages to be sent to network and add auto-message
		final List<Message> toSend = environment.fetchMessagesFromQueue();
		Message autoMessage = environment.createTextMessage("");
		toSend.add(autoMessage);
		
		// send messages for the network
		// TODO: adjust period
		int period = 2000; 
		new CountDownTimer(environment.getPreferences().getTCon(), period) {
			
			@Override
			public void onTick(long arg0) {
				
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

					@Override
					public void onMessageSent(Message msg) {
						environment.deliverCustomMessage(
								msg, VictimActivity.StateChangeHandler.MSG_SENT);
						environment.deliverMessage("message successfully sent");
						environment.removeFromQueue(msg);
					}
				};
				
				for(Message msg : toSend) {
					Log.w("Station", "About to send message: " + msg.toString());
					networking.send(msg, listener);
				}
			}

			@Override
			public void onFinish() {
				environment
						.deliverMessage("t_con finished, exiting station mode");
				environment.gotoState(State.Scanning);
				environment.clearQueue();
				toSend.clear();
			}
		}.start();
	}
}
