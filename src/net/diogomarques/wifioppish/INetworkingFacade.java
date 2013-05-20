package net.diogomarques.wifioppish;

public interface INetworkingFacade {

	public static interface OnSendListener {
		public void onMessageSent(String msg);

		public void onSendError(String errorMsg);
	}

	public static interface OnReceiveListener {
		public void onReceiveTimeout(boolean forced);

		public void onMessageReceived(String msg);
	}

	public static interface OnAccessPointScanListener {
		public void onScanTimeout();

		public void onEmergencyAPConnected();
	}

	public void startWifiAP();

	public void stopWifiAP();

	public void send(String string, OnSendListener listener);

	void receiveFirst(int timeoutMilis, OnReceiveListener listener);

	void receive(int timeoutMilis, OnReceiveListener listener);

	void scanForAP(int timeoutMilis, OnAccessPointScanListener listener);

}
