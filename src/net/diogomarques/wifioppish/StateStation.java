package net.diogomarques.wifioppish;

import net.diogomarques.utils.CountDownTimer;
import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.INetworkingFacade.OnSendListener;

public class StateStation extends AState {

	public StateStation(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout) {
		final INetworkingFacade networking = environment.getNetworkingFacade();
		
		// TODO: adjust period
		int period = 1000;
		new CountDownTimer(environment.getPreferences().getTCon(), period) {
			
			@Override
			public void onTick(long arg0) {
				OnSendListener listener = new OnSendListener() {
					
					@Override
					public void onSendError(String errorMsg) {
						environment.sendMessage("send error: " + errorMsg);	
						cancel();
						environment.gotoState(State.Scanning);
					}
					
					@Override
					public void onMessageSent(String msg) {
						environment.sendMessage("message successfully sent");	
					}
				};
				
				networking.send("alive", listener);				
			}
			
			@Override
			public void onFinish() {
				environment.sendMessage("t_con finished, exiting station mode");	
				environment.gotoState(State.Scanning);
				
			}
		}.start();
	}

}
