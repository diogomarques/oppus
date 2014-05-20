package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.networking.Message;
import net.diogomarques.wifioppish.networking.MessageGroup;

/**
 * The interface for networking control, defining operations and respective
 * callbacks.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public interface INetworkingFacade {

	/**
	 * Listener for message sending events.
	 */
	public static interface OnSendListener {
		/**
		 * Callback to be invoked when a message is successfully sent.
		 * 
		 * @param msg
		 *            the message that was sent (object format).
		 */
		public void onMessageSent(Message msg);

		/**
		 * Callback to be invoked when a message send fails.
		 * 
		 * @param errorMsg
		 *            an error message describing the cause of the failure.
		 */
		public void onSendError(String errorMsg);

		/**
		 * Callback to be invoked when a {@link MessageGroup} is successfully sent. 
		 * It invokes {@link #onMessageSent(Message)} for each {@link Message} inside 
		 * the MessageGroup.
		 * 
		 * @param msgs
		 * 				the sent MessageGroup
		 */
		public void onMessageSent(MessageGroup msgs);
	}

	/**
	 * Listener for message receiving events.
	 */
	public static interface OnReceiveListener {
		/**
		 * Callback to be invoked when receiving times out
		 * 
		 * TODO: remove forced, probs gone away after moving to TCP
		 */
		public void onReceiveTimeout(boolean forced);

		/**
		 * Callback to be invoked when a message is received.
		 * 
		 * @param msg
		 *            the message in the envelope format that was received.
		 */
		public void onMessageReceived(Message m);

		/**
		 * Callback to be invoked when a {@link MessageGroup} is successfully received. 
		 * It invokes {@link #onMessageReceived(Message)} for each {@link Message} inside 
		 * the MessageGroup.
		 * 
		 * @param msgs
		 * 				the received MessageGroup
		 */
		void onMessageReceived(MessageGroup msgs);
	}

	/**
	 * Listener for access point scanning events.
	 */
	public static interface OnAccessPointScanListener {
		/**
		 * Callback to be invoked when scan times out without finding an
		 * appropriate access point.
		 */
		public void onScanTimeout();

		/**
		 * Callback to be invoked when connection to AP is successful.
		 * 
		 * @param bSSID
		 * 				BSSID (MAC address) of the remote AP
		 */
		public void onAPConnection(String bSSID);
	}

	/**
	 * Start acting announcing to others the ability to be an access point.
	 */
	public void startAcessPoint();

	/**
	 * Sport announcing to other one the ability to be an access point.
	 */
	public void stopAccessPoint();

	/**
	 * Send a message to a shared channel.
	 * 
	 * @param msg
	 *            the message to send.
	 * @param listener
	 *            a listener for send-related events.
	 */
	public void send(Message msg, OnSendListener listener);
	
	/**
	 * Send a {@link MessageGroup} to a shared channel.
	 * 
	 * @param msgs
	 *            the MessageGroup to send.
	 * @param listener
	 *            a listener for send-related events.
	 */
	public void send(MessageGroup msgs, OnSendListener listener);

	/**
	 * Receive the first message available on a shared channel, if it comes
	 * before the timeout.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds.
	 * @param listener
	 *            a listener for receive-related events.
	 */
	void receiveFirst(int timeout, OnReceiveListener listener);

	/**
	 * Receive all incoming messages on a shared channel until the timeout is
	 * reached.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @param listener
	 *            a listener for receive-related events.
	 */
	void receive(int timeout, OnReceiveListener listener);

	/**
	 * Scan for access points provided by others until the timeout is reached.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds.
	 * @param listener
	 *            a listener for access point scanning events.
	 * 
	 */
	void scanForAP(int timeout, OnAccessPointScanListener listener);
	
	/**
	 * Listener for access point scanning events.
	 */
	public static interface OnInternetConnection {
		/**
		 * Callback to be invoked when scan times out without finding an
		 * appropriate access point.
		 */
		public void onScanTimeout();

		/**
		 * Callback to be invoked when a ping to www.google.pt is successful.
		 * 
		 */
		public void onInternetConnection();
	}
	
	/**
	 * Scan for internet connection until the timeout is reached.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds.
	 * @param listener
	 *            a listener for access point scanning events.
	 * 
	 */
	void scanForInternet(int timeout, OnInternetConnection listener);

}
