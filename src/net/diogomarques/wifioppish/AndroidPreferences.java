package net.diogomarques.wifioppish;

import android.content.Context;

// TODO use Android's preferences instead of hard-coded params & create prefs activity
// TODO times should not be fixed but instead have variation to prevent locking & collisions
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
		return 1000 * 60 * 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTPro()
	 */
	@Override
	public int getTPro() {
		// FIXME use recommended t
		return 1000 * 60 * 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTScan()
	 */
	@Override
	public int getTScan() {
		// FIXME use recommended t
		return 5000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.diogomarques.wifioppish.IPreferences#getTCon()
	 */
	@Override
	public int getTCon() {
		// FIXME use recommended t
		return 5000;
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

}
