package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.networking.SoftAPDelegate;
import net.diogomarques.wifioppish.networking.UDPDelegate;
import net.diogomarques.wifioppish.networking.WiFi;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;

/**
 * Networking façade.
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
				new SoftAPDelegate(context), new WiFi(context, environment.getPreferences()),
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
		mWiFi.scanForAP(timeoutMilis,
				listener, this);
		
	}
}
