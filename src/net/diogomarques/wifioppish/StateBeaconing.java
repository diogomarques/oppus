package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;

/* NOTE: in this implementation, beaconing lasts t_beac when it first starts and also when StateProviding finishes due to disconnection.
 * 
 */
public class StateBeaconing extends AState {

	public StateBeaconing(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout) {
		environment.sendMessage("entered beaconing state");
		environment.sendMessage("(re) starting AP");
		networking.startWifiAP();
		networking
				.setOnReceiveListener(new INetworkingFacade.OnReceiveListener() {
					@Override
					public void onReceiveTimeout(boolean forced) {
						environment.sendMessage("t_beac timeout, stopping AP");
						// stop ap and go to scanning
						networking.stopWifiAP();
						environment.gotoState(State.Scanning);
					}

					@Override
					public void onMessageReceived(String msg) {
						environment.sendMessage("message received: " + msg);
						environment.gotoState(State.Providing);
					}
				});
		networking.receiveFirst(timeout);
	}
}
