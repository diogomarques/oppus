package net.diogomarques.wifioppish;

import net.diogomarques.utils.CountDownTimer;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.INetworkingFacade.OnSendListener;
import net.diogomarques.wifioppish.networking.Message;
import android.content.Context;
import android.util.Log;


/**
 * Android implementation of state 
 */
public class StateConnected extends AState {

	public StateConnected(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "State Connected");
		
		context = c;
		environment.deliverMessage("entered connected state");
		final INetworkingFacade networking = environment.getNetworkingFacade();
		if (environment.getLastState() == State.Scanning) {
			environment.deliverMessage("t_i_con timeout");
			environment.gotoState(State.Beaconing);
		} else {
			environment.deliverMessage("t_i_con timeout");
			environment.gotoState(State.Scanning);
		}
	}
}
