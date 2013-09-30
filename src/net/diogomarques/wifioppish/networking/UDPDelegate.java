package net.diogomarques.wifioppish.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import net.diogomarques.wifioppish.ConcurrentForwardingQueue;
import net.diogomarques.wifioppish.IDomainPreferences;
import net.diogomarques.wifioppish.IEnvironment;
import net.diogomarques.wifioppish.INetworkingFacade.OnReceiveListener;
import net.diogomarques.wifioppish.INetworkingFacade.OnSendListener;
import net.diogomarques.wifioppish.LocationProvider;
import android.content.Context;
import android.location.Location;
import android.net.DhcpInfo;
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
	
	private ConcurrentForwardingQueue mQueue;

	/* Dependencies */
	private final Context mContext;
	private final IEnvironment mEnvironment;

	public UDPDelegate(Context context, IEnvironment environment) {
		mContext = context;
		mEnvironment = environment;
		mQueue = new ConcurrentForwardingQueue();
	}

	private InetAddress getBroadcastAddress() throws UnknownHostException {
		WifiManager mWifi = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		// from
		// http://code.google.com/p/boxeeremote
		DhcpInfo dhcp = mWifi.getDhcpInfo();
	    if (dhcp == null) {
	      Log.d(TAG, "Could not get dhcp info");
	      return null;
	    }

	    int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
	    byte[] quads = new byte[4];
	    for (int k = 0; k < 4; k++)
	      quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
	    return InetAddress.getByAddress(quads);
	}

	public void send(Message msg, OnSendListener listener) {
		byte[] netMessage = messageToNetwork(msg); 
		
		IDomainPreferences preferences = mEnvironment.getPreferences();
		try {
			DatagramPacket packet = new DatagramPacket(netMessage,
					netMessage.length, getBroadcastAddress(), preferences.getPort());
			getBroadcastSocket().send(packet);
			listener.onMessageSent(msg.toString());
		} catch (UnknownHostException e) {
			listener.onSendError("Unknown host\n\t" + e.getMessage());
		} catch (SocketException e) {
			// Connection to softAP not available
			Log.w(TAG, e.getMessage(), e);
			listener.onSendError("lost connection to AP\n\t" + e.getMessage());
		} catch (IOException e) {
			// wtf - e comes up null sometimes
			if (e != null) {
				Log.e(TAG, e.getMessage(), e);
				listener.onSendError(e.getMessage());

			} else {
				listener.onSendError("Freak IO exception\n\t" + e.getMessage());
			}
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
			//String received = getMessageIn(buffer);
			Message m = networkToMessage(buffer);
			mQueue.offer(m);
			String received = m.toString();
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
				//String received = getMessageIn(buffer);
				Message m = networkToMessage(buffer);
				mQueue.offer(m);
				String received = m.toString();
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
	
	/**
	 * Prepares a Message envelope to be sent over the network
	 * @param m Message to send
	 * @return Byte buffer ready to send over a socket, or null in case of failed conversion
	 */
	public byte[] messageToNetwork(Message m) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] buffer = null;
		
		try {
		  out = new ObjectOutputStream(bos); 
		  out.writeObject(m);		  
		  buffer = bos.toByteArray();
		  out.close();
		  bos.close();
		  
		} catch(IOException e) {
			Log.e("messageToNetwork", "Error converting message: " + e.getMessage());			
		} 
		
		return buffer;
	}
	
	/**
	 * Extracts a Message envelope from a byte array
	 * @param buffer Byte array which contains the Message envelope
	 * @return Message instance of extracted message, or null in case of failed extraction
	 */
	public Message networkToMessage(byte[] buffer) {
		ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
		ObjectInput in = null;
		Message message = null;
		
		try {
		  in = new ObjectInputStream(bis);
		  Object o = in.readObject(); 
		  
		  if( o instanceof Message )
			  message = (Message) o;
		  
		  bis.close();
		  in.close();
		  
		} catch (StreamCorruptedException e) {
			Log.e("networkToMessage", "Corrupted stream: " + e.getMessage());
			
		} catch (IOException e) {
			Log.e("networkToMessage", "Error converting message: " + e.getMessage());
			
		} catch (ClassNotFoundException e) {
			Log.e("networkToMessage", "Class not found (different app versions?): " + e.getMessage());
			
		}

		return message;
	}

}
