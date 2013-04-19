package net.diogomarques.wifioppish;

public class StateScanning extends State {

	public StateScanning(IEnvironment environment, IPreferences preferences,
			INetworkingFacade networking) {
		super(environment, preferences, networking);
	}

	@Override
	public void start() {
		environment.notifyEnv("entered scanning state");
		networking
				.setOnAccessPointScanListener(new INetworkingFacade.OnAccessPointScanListener() {

					@Override
					public void onScanTimeout() {
						environment.notifyEnv("t_scan timeout");
						new StateBeaconing(environment, preferences, networking)
								.start();
					}

					@Override
					public void onEmergencyAPConnected() {
						environment.notifyEnv("connected to AP!");
						new StateStation(environment, preferences, networking)
								.start();
					}
				});
		networking.scanForAP(preferences.getTScan(),
				preferences.getScanPeriod());
	}
}
