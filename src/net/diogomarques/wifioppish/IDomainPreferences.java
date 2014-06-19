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
	 * Get t_beac (minimum 10s).
	 * 
	 * <blockquote> "t_beac the time it advertises its presence in AP mode by
	 * sending SSID beacons."
	 * <p>
	 * "If not speciﬁed otherwise, the times t_scan min and t_beac min are set
	 * to 10 seconds".
	 * <p>
	 * "Increasing the beaconing time in AP mode has a negative impact on the
	 * performance". <footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_beac
	 */
	public int getTBeac();

	/**
	 * Get t_pro (minimum 40s).
	 * 
	 * <blockquote> "t_pro the time a mobile device is providing AP
	 * functionality for connected STAs"
	 * <p>
	 * "The performance of the Flexible STA and Flexible AP variants of WiFi-Opp
	 * is largely independent of the connection times (if ≥
	 * 40s)".<footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_pro
	 */
	public int getTPro();

	/**
	 * Get t_scan (minimum 5 minutes).
	 * 
	 * <blockquote> "t_scan is the time a mobile device scans for APs".
	 * <p>
	 * "The WiFi-Opp performance is almost independent of the scanning time,
	 * thus permitting long scanning times of 5-15 minutes".<footer>Trifunovic et
	 * al, 2011, <a href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_scan
	 */
	public int getTScan();

	/**
	 * Get t_con (minimum 40s).
	 * 
	 * <blockquote> "t_con the time a STA stays connected with a specific AP".
	 * <p>
	 * "The performance of the Flexible STA and Flexible AP variants of WiFi-Opp
	 * is largely independent of the connection times (if ≥ 40s)"
	 * <footer>Trifunovic et al, 2011, <a
	 * href="http://202.194.20.8/proc/MOBICOM2011/chants/p37.pdf"
	 * >PDF</a></footer></blockquote>
	 * 
	 * @return t_con
	 */
	public int getTCon();
	/**
	 * Get t_int (TODO).
	 * 
	 * "t_int is the time a mobile device scans for a internet connection".
	 * 
	 * @return t_int
	 */
	public int getTInt();

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
	public State getStartState();
	
	/**
	 * Get if the internet state is enable or disabled
	 */
	public boolean checkInternetMode();
	
	/**
	 * Get the preferred API webservice endpoint. 
	 * The method will be appended to this address.
	 * @return API endpoint web address
	 */
	public String getApiEndpoint(); 
	
	/**
	 * Get the period to send Messages to the opportunistic network.
	 * @return Time in milisseconds to wait before resend a Message to network
	 */
	public int getSendPeriod();

}