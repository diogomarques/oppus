package net.diogomarques.wifioppish;

public class StateProviding extends State {

	public StateProviding(IEnvironment environment, IPreferences preferences,
			INetworkingFacade networking) {
		super(environment, preferences, networking);
	}

	@Override
	public void start() {
		environment.notifyEnv("entered providing state");
		networking
				.setOnReceiveListener(new INetworkingFacade.OnReceiveListener() {
					@Override
					public void onReceiveTimeout(boolean forced) {
						// t_pro reached
						if (forced) {
							environment.notifyEnv("t_pro timeout, stopping AP");
							networking.stopWifiAP();
							gotoScanning();
						}
						// no connections
						else {
							environment
									.notifyEnv("no connections to provide for");
							gotoBeaconing();
						}
					}

					@Override
					public void onMessageReceived(String msg) {
						environment.notifyEnv("message received: " + msg);
					}
				});
		networking.receive(preferences.getTBeac());

	}

	private void gotoBeaconing() {
		new StateBeaconing(environment, preferences, networking).start();
	}

	private void gotoScanning() {
		new StateScanning(environment, preferences, networking).start();
	}

}
