package net.diogomarques.wifioppish;

import java.util.Random;

import net.diogomarques.wifioppish.IEnvironment.State;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;

// TODO use Android's preferences instead of hard-coded params & create prefs activity
public class AndroidPreferences implements IDomainPreferences {

	private Context mContext;

	public AndroidPreferences(Context context) {
		mContext = context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getPort()
	 */
	@Override
	public int getPort() {
		return 33333;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTBeac()
	 */
	@Override
	public int getTBeac() {
		// FIXME use recommended t
		int t_beac =  1000 * 30;
		return getBoundedRandom(t_beac, 0.5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTPro()
	 */
	@Override
	public int getTPro() {
		// FIXME use recommended t
		int t_pro =  1000 * 30;
		return getBoundedRandom(t_pro, 0.5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTScan()
	 */
	@Override
	public int getTScan() {
		// FIXME use recommended / prefs
		int t_scan = 1000 * 30;
		return getBoundedRandom(t_scan, 0.5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTCon()
	 */
	@Override
	public int getTCon() {
		// FIXME use recommended t
		int t_con =  1000 * 30;
		return getBoundedRandom(t_con, 0.5);
	}
	
	private int getBoundedRandom(int center, double deltaPercent) {
		if (center < 0 || deltaPercent > 1.0 || deltaPercent < 0.0)
			throw new IllegalArgumentException(
					"center < 0 || delta > 1.0 || delta < 0.0");
		int min = (int) (center * (1 - deltaPercent));
		int result = (int) (min + (center - min) * 2
				* new Random().nextDouble());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#scanPeriod()
	 */
	@Override
	public int getScanPeriod() {
		return 1000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getBroadcastAddress()
	 */
	@Override
	public String getBroadcastAddress() {
		// TODO get from dhcp info
		return "192.168.43.255";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getWifiSSID()
	 */
	@Override
	public String getWifiSSID() {
		return "emergencyAP";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getWifiPSK()
	 */
	@Override
	public String getWifiPassword() {
		return "emergency";
	}

	@Override
	public State getStartState() {
		return IEnvironment.State.Scanning;
	}

	public WifiConfiguration getWifiSoftAPConfiguration() {
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = getWifiSSID();
		wc.preSharedKey = getWifiPassword();
		wc.allowedGroupCiphers.clear();
		wc.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.clear();
		wc.allowedProtocols.clear();
		return wc;
	}

}
