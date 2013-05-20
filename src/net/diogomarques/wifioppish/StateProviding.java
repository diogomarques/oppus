package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;

public class StateProviding extends AState {

	public StateProviding(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout) {
		environment.sendMessage("entered providing state");

		networking.receive(timeout, new INetworkingFacade.OnReceiveListener() {
			@Override
			public void onReceiveTimeout(boolean forced) {
				// t_pro reached
				if (forced) {
					environment.sendMessage("t_pro timeout, stopping AP");
					networking.stopWifiAP();
					environment.gotoState(State.Scanning);
				}
				// no connections
				else {
					environment.sendMessage("no connections to provide for");
					environment.gotoState(State.Beaconing);
				}
			}

			@Override
			public void onMessageReceived(String msg) {
				environment.sendMessage("message received: " + msg);
			}
		});

	}

}
