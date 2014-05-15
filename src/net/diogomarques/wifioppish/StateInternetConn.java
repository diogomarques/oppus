package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;
import android.content.Context;
import android.util.Log;


/**
 * Android implementation of state {@link IEnvironment.State#In}
 * 
 * @author André Rodrigues
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class StateInternetConn extends AState {

	public StateInternetConn(IEnvironment env) {
		super(env);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "Internet Connected");
		
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
