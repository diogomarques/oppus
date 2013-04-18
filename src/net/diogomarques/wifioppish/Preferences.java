package net.diogomarques.wifioppish;

import android.content.Context;

// TODO use Android's preferences instead of hard-coded params & create prefs activity
// TODO times should not be fixed but instead have come variation to prevent locking & collisions
public class Preferences {

	private Context mContext;

	public Preferences(Context context) {
		mContext = context;
	}

	/**
	 * Get port to use in UDP broadcasts.
	 * 
	 * @return a port number
	 */
	public int getPort() {
		return 33333;
	}

	/**
	 * Get t_beac.
	 * 
	 * <blockquote> "t_beac the time it advertises its presence in AP mode by
	 * sending SSID beacons.".<footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_beac
	 */
	public int getTBeac() {
		// FIXME use recommended t
		return 10000;
	}

	/**
	 * Get t_pro.
	 * 
	 * <blockquote> "t_pro the time a mobile device is providing AP
	 * functionality for connected STAs.".<footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_pro
	 */
	public int getTPro() {
		// FIXME use recommended t
		return 5000;
	}

	/**
	 * Get t_scan.
	 * 
	 * <blockquote>
	 * "t_scan is the time a mobile device scans for APs".<footer>Trifunovic et
	 * al, 2011, <a href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_scan
	 */
	public int getTScan() {
		// FIXME use recommended t
		return 5000;
	}

	/**
	 * Get t_con.
	 * 
	 * <blockquote>
	 * "t_scan is the time a mobile device scans for APs".<footer>Trifunovic et
	 * al, 2011, <a href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_con
	 */
	public int getTCon() {
		// FIXME use recommended t
		return 5000;
	}

	/**
	 * Get the interval between scans when in scanning state.
	 * 
	 * <blockquote>
	 * "We assume that scans are triggered every 5s".<footer>Trifunovic et al,
	 * 2011, <a href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return the scanning period in ms
	 */
	public int scanPeriod() {
		return 5000;
	}

	public String getBroadcastAddress() {
		// TODO get from dhcp info
		return "192.168.43.255";
	}

	public String getWifiSSID() {
		return "emergencyAP";
	}

	public String getWifiPSK() {
		return "\"emergency\"";
	}

}
