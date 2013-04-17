package net.diogomarques.wifioppish;

public interface INetworking {

	public static interface OnSendListener {
		public void onMessageSent(String msg);

		public void onSendError(String errorMsg);
	}

	public static interface OnReceiveListener {
		public void onReceiveTimeout(boolean forced);

		public void onMessageReceived(String msg);
	}

	public OnReceiveListener getOnReceiveListener();

	public void setOnReceiveListener(OnReceiveListener listener);

	public OnSendListener getOnSendListener();

	public void setOnSendListener(OnSendListener listener);

	public void startWifiAP();

	public void stopWifiAP();

	public void send(String string);

	public void receiveFirst(int timeoutMilis);

	public void receive(int timeoutMilis);

}
