package net.diogomarques.wifioppish.service;

import android.os.Handler;
import android.os.Looper;

/**
 * Represents a generic {@link Handler Android Handler} with some default 
 * constants to help the identification of the incoming messages. This class 
 * is intended to be extended.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class LOSTHandler extends Handler {
	
	/**
	 * Generic log line indicating progress on the LOST logic
	 */
	public static final int LOG_MSG = 800;
	
	/**
	 * The node role (Beaconing, Providing, Scanning, Station) was changed
	 */
	public static final int ROLE = 801;
	
	/**
	 * A message was successfully sent to the network
	 */
	public static final int MSG_SENT = 804;
	
	/**
	 * A message was successfully sent directly to the webservice
	 */
	public static final int MSG_SENT_WS = 805;
	
	/**
	 * Environment started its execution
	 */
	public static final int ENV_START = 802;
	
	/**
	 * Environment stopped its execution
	 */
	public static final int ENV_STOP = 803;
	
	
	/**
	 * Creates a new Handler with the looper of current thread.
	 */
	public LOSTHandler() {
		super();
	}
	
	/**
	 * Creates a new Handler with the looper of a custom thread
	 * @param l Custom looper
	 */
	public LOSTHandler(Looper l) {
		super(l);
	}
}
