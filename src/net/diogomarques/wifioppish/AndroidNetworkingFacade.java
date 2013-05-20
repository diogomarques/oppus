package net.diogomarques.wifioppish;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import net.diogomarques.wifioppish.networking.SoftAPDelegate;
import net.diogomarques.wifioppish.networking.UDPDelegate;
import net.diogomarques.wifioppish.networking.WiFi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Networking façade for interaction with softAP functionality & message passing
 * (UDP)
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public class AndroidNetworkingFacade implements INetworkingFacade {

	private static final String TAG = AndroidNetworkingFacade.class
			.getSimpleName();

	/*
	 * Dependencies.
	 */
	private final Context mContext;
	private final IDomainPreferences mPreferences;
	private final SoftAPDelegate mSoftAP;
	private final WiFi mWiFi;
	private final UDPDelegate mUdp;

	public static AndroidNetworkingFacade createInstance(Context context,
			IEnvironment environment) {
		return new AndroidNetworkingFacade(context, environment,
				new SoftAPDelegate(context), new WiFi(context),
				new UDPDelegate(context, environment.getPreferences()));
	}

	private AndroidNetworkingFacade(Context context, IEnvironment environment,
			SoftAPDelegate softAP, WiFi wiFi, UDPDelegate udp) {
		this.mContext = context;
		this.mPreferences = environment.getPreferences();
		this.mSoftAP = softAP;
		this.mWiFi = wiFi;
		this.mUdp = udp;
	}

	@Override
	public void startWifiAP() {
		mSoftAP.startWifiAP(this);
	}

	@Override
	public void stopWifiAP() {
		mSoftAP.stopWifiAP(this);
	}

	public WifiConfiguration getWifiSoftAPConfiguration() {
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = mPreferences.getWifiSSID();
		wc.preSharedKey = mPreferences.getWifiPassword();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();
		return wc;
	}

	@Override
	public void send(String msg, OnSendListener listener) {
		mUdp.send( msg, listener);
	}

	@Override
	public void receiveFirst(int timeoutMilis, OnReceiveListener listener) {
		mUdp.receiveFirst(timeoutMilis, listener);
	}

	@Override
	public void receive(int timeoutMilis, OnReceiveListener listener) {
		mUdp.receive(timeoutMilis, listener);
	}

	@Override
	public void scanForAP(int timeoutMilis,
			final OnAccessPointScanListener listener) {
		// TODO wake & wifi locks may be needed. check.
		// best case, a lock WifiManager.WIFI_MODE_SCAN_ONLY will suffice
		// worst case, set wifi to never sleep:
		// <uses-permission android:name="android.permission.WRITE_SETTINGS"/>
		// Settings.System.putInt(getContentResolver(),
		// Settings.System.WIFI_SLEEP_POLICY,
		// Settings.System.WIFI_SLEEP_POLICY_NEVER);

		// control flag to prevent scan after connection
		final AtomicBoolean connected = new AtomicBoolean(false);

		// activate WiFi, otherwise other methods may behave strangely, e.g.
		// returning nulls
		// TODO check if this always works. enabling takes some time, but can be
		// checked by the receiver, using the WIFI_STATE_ENABLED extra
		final WifiManager manager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		manager.setWifiEnabled(true);

		// start receiver for scans
		BroadcastReceiver scanReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				List<ScanResult> results = manager.getScanResults();
				// Best signal check:
				// http://marakana.com/forums/android/examples/40.html
				ScanResult bestSignal = null;
				for (ScanResult result : results) {
					if (result.SSID.equals(mPreferences.getWifiSSID())) {
						Log.w(TAG, "Found " + mPreferences.getWifiSSID()
								+ " AP, signal " + result.level);
						if (bestSignal == null
								|| WifiManager.compareSignalLevel(
										bestSignal.level, result.level) < 0)
							bestSignal = result;
					}
				}
				if (bestSignal != null) {
					Log.w(TAG, "Settled for AP with signal  "
							+ bestSignal.level);
					WifiConfiguration configuration = getWifiSoftAPConfiguration();
					configuration.SSID = "\"" + configuration.SSID + "\"";
					configuration.preSharedKey = "\""
							+ configuration.preSharedKey + "\"";
					int netId = manager.addNetwork(configuration);
					Log.w(TAG, "addNetwork -> netId = " + netId);
					if (netId == -1) {
						throw new IllegalStateException("Add network error.");
					}
					boolean disconnect = manager.disconnect();
					Log.w(TAG, "disconnect -> " + disconnect);
					boolean enableNetwork = manager.enableNetwork(netId, true);
					Log.w(TAG, "enableNetwork -> " + enableNetwork);
					boolean reconnect = manager.reconnect();
					Log.w(TAG, "reconnect -> " + reconnect);
					safeUnregisterReceiver(this);
					connected.set(true);
					listener.onEmergencyAPConnected();
				}
			}
		};
		;
		mContext.registerReceiver(scanReceiver, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
		// scan immediately one time - fast reconnect
		// "In this case nevertheless, the node will not scan for t_scan seconds (which might be very long) but immediately try to ﬁnd a new AP or becomes one within a few seconds (fast reconnect)."
		manager.startScan();
		// every scanPeriod, scan again
		long startTime = new Date().getTime();
		while (!connected.get()) {
			long tick = new Date().getTime();
			if (tick > startTime + timeoutMilis) {
				Log.w("", "Scan timeout");
				safeUnregisterReceiver(scanReceiver);
				listener.onScanTimeout();
				break;
			}
			while (true) {
				if (new Date().getTime() > tick + mPreferences.getScanPeriod()) {
					// Log.w("", "Scan " + countScans++);
					manager.startScan();
					break;
				}
			}
		}
	}

	/*
	 * There is not API for checking if a receiver is registered. This is a
	 * workaround.
	 */
	private boolean safeUnregisterReceiver(BroadcastReceiver receiver) {
		boolean sucess = false;
		try {
			mContext.unregisterReceiver(receiver);
			sucess = true;
		} catch (IllegalArgumentException exception) {
			sucess = false;
			Log.w("", "Trying to unregister a receiver that isn't registered.");
		}
		return sucess;
	}

	// // TODO: is this really needed? everyone in range is supposed to have got
	// // it.
	// private void relayPacket(DatagramPacket packet) {
	// Log.w(TAG, "Relaying message");
	// String msg = new String(packet.getData());
	// send(msg);
	// }

}
