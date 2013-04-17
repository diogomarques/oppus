package net.diogomarques.wifioppish;

import android.content.Context;

// TODO use Android's preferences and create prefs activity
public class Preferences {

	private Context mContext;

	public Preferences(Context context) {
		mContext = context;
	}

	public int getPort() {
		return 33333;
	}
	
	public int getTBeac() {
		// FIXME check in paper
		return 10000;
	}
	
	public int getTPro() {
		// FIXME
		return 5000;
	}

	public String getBroadcastAddress() {
		return "192.168.43.255";
	}

	public String getWifiSSID() {
		return "emergencyAP";
	}

	public String getWifiPSK() {
		return "\"emergency\"";
	}

}
