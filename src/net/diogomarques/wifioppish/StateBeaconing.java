package net.diogomarques.wifioppish;

/* NOTE: in this implementation, beaconing lasts t_beac when it first starts and also when StateProviding finishes due to disconnection.
 * 
 */
public class StateBeaconing extends State {

	public StateBeaconing(IEnvironment environment, IPreferences preferences,
			INetworkingFacade networking) {
		super(environment, preferences, networking);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void start() {
		environment.notifyEnv("entered beaconing state");
		environment.notifyEnv("(re) starting AP");
		networking.startWifiAP();
		networking
				.setOnReceiveListener(new INetworkingFacade.OnReceiveListener() {
					@Override
					public void onReceiveTimeout(boolean forced) {
						environment.notifyEnv("t_beac timeout, stopping AP");
						// stop ap and go to scanning
						networking.stopWifiAP();
						gotoScanning();
					}

					@Override
					public void onMessageReceived(String msg) {
						environment.notifyEnv("message received: " + msg);
						gotoProviding();
					}
				});
		networking.receiveFirst(preferences.getTBeac());
	}

	private void gotoProviding() {
		new StateProviding(environment, preferences, networking).start();
	}

	private void gotoScanning() {
		new StateScanning(environment, preferences, networking).start();
	}
}
