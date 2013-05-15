package net.diogomarques.wifioppish;

public interface IDomainPreferences {

	/**
	 * Get port to use in broadcasts.
	 * 
	 * @return a port number
	 */
	public int getPort();

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
	public int getTBeac();

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
	public int getTPro();

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
	public int getTScan();

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
	public int getTCon();

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
	public int getScanPeriod();

	public String getBroadcastAddress();

	public String getWifiSSID();

	public String getWifiPassword();

}