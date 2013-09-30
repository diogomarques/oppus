package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.networking.Message;
import net.diogomarques.wifioppish.networking.SoftAPDelegate;
import net.diogomarques.wifioppish.networking.UDPDelegate;
import net.diogomarques.wifioppish.networking.WiFiDelegate;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;

/**
 * The Android-specific networking controller facade.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public class AndroidNetworkingFacade implements INetworkingFacade {

	/*
	 * Dependencies.
	 */
	private final Context mContext;
	private final IEnvironment mEnvironment;
	private final SoftAPDelegate mSoftAP;
	private final WiFiDelegate mWiFi;
	private final UDPDelegate mUdp;

	/**
	 * Static factory that creates instances of networking controllers.
	 * 
	 * @param c
	 *            the context
	 * @param env
	 *            the state machine environment
	 * @return a new instance will all dependencies set
	 */
	public static AndroidNetworkingFacade createInstance(Context c,
			IEnvironment env) {
		return new AndroidNetworkingFacade(c, env, new SoftAPDelegate(c),
				new WiFiDelegate(c, env), new UDPDelegate(c, env));
	}

	private AndroidNetworkingFacade(Context context, IEnvironment environment,
			SoftAPDelegate softAP, WiFiDelegate wiFi, UDPDelegate udp) {
		this.mContext = context;
		this.mEnvironment = environment;
		this.mSoftAP = softAP;
		this.mWiFi = wiFi;
		this.mUdp = udp;
	}

	protected Context getContext() {
		return mContext;
	}

	@Override
	public void startAcessPoint() {
		mSoftAP.startWifiAP(this);
	}

	@Override
	public void stopAccessPoint() {
		mSoftAP.stopWifiAP(this);
	}

	/**
	 * Get the WifiConfiguration based on the SSID and password set on the
	 * domain parameters in {@link IDomainPreferences}.
	 * 
	 * @return a WifiConfiguration for a WPA access point with the SSID in
	 *         {@link IDomainPreferences#getWifiSSID()} and the password on
	 *         {@link IDomainPreferences#getWifiPassword()}.
	 */
	public WifiConfiguration getWifiSoftAPConfiguration() {
		IDomainPreferences preferences = mEnvironment.getPreferences();
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = preferences.getWifiSSID();
		wc.preSharedKey = preferences.getWifiPassword();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();
		return wc;
	}

	@Override
	public void send(Message msg, OnSendListener listener) {
		mUdp.send(msg, listener);
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
		mWiFi.scanForAP(timeoutMilis, listener, this);

	}
}
