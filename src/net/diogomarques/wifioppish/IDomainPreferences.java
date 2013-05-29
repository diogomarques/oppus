package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;

/**
 * Interface that defines access domain-specific parameters (as opposed to users
 * preferences).
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public interface IDomainPreferences {

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
	 * "t_con the time a STA stays connected with a specific AP".<footer>Trifunovic et
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

	/**
	 * Get port to use in broadcasts.
	 * 
	 * @return a port number.
	 */
	public int getPort();

	/**
	 * Get the access point's SSID.
	 * 
	 * @return the SSID.
	 */
	public String getWifiSSID();

	/**
	 * Get the access point's password.
	 * 
	 * @return the password.
	 */
	public String getWifiPassword();

	/**
	 * Get the state in which the machine should start.
	 * 
	 * @return the start state.
	 */
	public abstract State getStartState();

}