package net.diogomarques.wifioppish.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;

import net.diogomarques.wifioppish.IDomainPreferences;
import net.diogomarques.wifioppish.IEnvironment;
import net.diogomarques.wifioppish.INetworkingFacade.OnReceiveListener;
import net.diogomarques.wifioppish.INetworkingFacade.OnSendListener;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.util.Log;

public class UDPDelegate {

	private static final String TAG = UDPDelegate.class.getSimpleName();
	
	private static int MAX_UDP_DATAGRAM_SIZE = 65536;
	
	// TODO: move to msg serializer
	public static String MSG_EOT = String.valueOf(0x0004);	

	/**
	 * Single instance of lock
	 */
	private MulticastLock mLock;

	/**
	 * Single UDP socket for messaging i/o
	 */
	private DatagramSocket mSocket;

	/* Dependencies */
	private final Context mContext;
	private final IEnvironment mEnvironment;

	public UDPDelegate(Context context, IEnvironment environment) {
		mContext = context;
		mEnvironment = environment;
	}

	public void send(String msg, OnSendListener listener) {
		msg = msg + MSG_EOT;
		IDomainPreferences preferences = mEnvironment.getPreferences();
		try {
			DatagramPacket packet = new DatagramPacket(msg.getBytes(),
					msg.length(), InetAddress.getByName(preferences
							.getBroadcastAddress()), preferences.getPort());
			getBroadcastSocket().send(packet);
			listener.onMessageSent(msg);
		} catch (UnknownHostException e) {
			listener.onSendError("Unknown host");
		} catch (IOException e) {
			listener.onSendError("Network unavailable");
		}
	}
	
	private String getMessageIn(byte[] buffer) {
		String msg = new String(buffer);
		msg = msg.substring(0, msg.indexOf(MSG_EOT));
		return msg;		
	}

	public void receiveFirst(int timeoutMilis, OnReceiveListener listener) {
		byte[] buffer = new byte[MAX_UDP_DATAGRAM_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			DatagramSocket socket = getBroadcastSocket();
			socket.setSoTimeout(timeoutMilis);

			// blocks for t_beac
			socket.receive(packet);
			String received = getMessageIn(buffer);			
			Log.w(TAG,
					"Received packet! " + received + " from "
							+ packet.getAddress().getHostAddress() + ":"
							+ packet.getPort());
			releaseBroadcastSocket();
			listener.onMessageReceived(received);
		} catch (SocketTimeoutException e) {
			// t_beac timed out, go to scanning
			releaseBroadcastSocket();
			listener.onReceiveTimeout(false);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
			
		}
	}

	public void receive(int timeoutMilis, OnReceiveListener listener) {
		long now = new Date().getTime();
		byte[] buffer = new byte[MAX_UDP_DATAGRAM_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		try {
			// TODO: user countdowntimer and call receive first
			while (true) {
				// force finish, even if new connections are happening
				if (new Date().getTime() > (now + timeoutMilis)) {
					listener.onReceiveTimeout(true);
					break;
				}

				DatagramSocket socket = getBroadcastSocket();

				// blocks for t_beac
				socket.setSoTimeout(timeoutMilis);
				socket.receive(packet);
				String received = getMessageIn(buffer);
				Log.w(TAG,
						"Received packet! " + received + " from "
								+ packet.getAddress().getHostAddress() + ":"
								+ packet.getPort());
				releaseBroadcastSocket();
				listener.onMessageReceived(received); 

			}
		} catch (SocketTimeoutException e) {
			// no connections received
			releaseBroadcastSocket();
			listener.onReceiveTimeout(false);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}

	}

	private DatagramSocket getBroadcastSocket() {
		// Acquire multicast lock if not already acquired
		if (mLock == null || !mLock.isHeld()) {
			WifiManager manager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			mLock = manager.createMulticastLock(TAG);
			mLock.acquire();
		}
		// Open socket if not opened
		if (mSocket == null || mSocket.isClosed()) {
			try {
				mSocket = new DatagramSocket(mEnvironment.getPreferences()
						.getPort());
				mSocket.setBroadcast(true);
			} catch (SocketException e) {

				Log.e(TAG, e.getMessage());
			}
		}
		return mSocket;
	}

	private void releaseBroadcastSocket() {
		if (mSocket != null && !mSocket.isClosed())
			mSocket.close();
		mSocket = null;
		if (mLock != null && mLock.isHeld())
			mLock.release();
		mLock = null;
	}

}
