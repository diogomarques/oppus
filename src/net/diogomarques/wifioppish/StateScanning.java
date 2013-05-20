package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;

public class StateScanning extends AState {

	public StateScanning(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout) {
		final INetworkingFacade networking = environment.getNetworkingFacade();
		environment.sendMessage("entered scanning state");		
		networking.scanForAP(timeout, new INetworkingFacade.OnAccessPointScanListener() {

			@Override
			public void onScanTimeout() {
				environment.sendMessage("t_scan timeout");
				environment.gotoState(State.Beaconing);
				;
			}

			@Override
			public void onEmergencyAPConnected() {
				environment.sendMessage("connected to AP!");
				environment.gotoState(State.Station);
			}
		});
	}
}
