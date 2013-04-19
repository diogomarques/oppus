package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;

public class StateScanning extends AState {

	public StateScanning(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start() {
		environment.sendMessage("entered scanning state");
		networking
				.setOnAccessPointScanListener(new INetworkingFacade.OnAccessPointScanListener() {

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
		networking.scanForAP(preferences.getTScan(),
				preferences.getScanPeriod());
	}
}
