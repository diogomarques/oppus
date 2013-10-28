package net.diogomarques.wifioppish;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Allows a node to get it's own ID and other ID based on their AP MAC address. 
 * A node identifier (node Id) is a string composed by alphanumeric characters [0-9A-F], 
 * with the exact length of 6. It is supposed to uniquely identify a device 
 * in the network.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class NodeIdentification {
	
	/**
	 * Gets this node's Id. This method is suitable to use once in the startup 
	 * since it uses {@link android.net.WifiManager} to turn on Wifi and get the 
	 * MAC address. The wifi remains enabled after the method ends
	 * 
	 * @param c Android Context of the current node
	 * @return This node's Id
	 */
	public static String getMyNodeId(Context c) {
		WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		manager.setWifiEnabled(true);
		WifiInfo info = manager.getConnectionInfo();
		String address = info.getMacAddress();
		
		return getNodeId(address);
	}
	
	/**
	 * Gets the node Id based on the Mac address
	 * 
	 * @param macAddress Mac address from which the Id will be generated
	 * @return Node's Id, or unknown if the MD5 algorithm is not implemented in the Java version 
	 */
	public static String getNodeId(String macAddress) {
		int maxLength = 6;
		
		// normalize MAC Address
		macAddress = macAddress.toUpperCase(Locale.US);
		
		try {
	        // Create MD5 Hash
	        MessageDigest digest;
			digest = java.security.MessageDigest
			        .getInstance("MD5");
		
	        digest.update(macAddress.getBytes());
	        byte messageDigest[] = digest.digest();
	
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i = 0; hexString.length() < maxLength; i++) {
	            String h = Integer.toHexString(0xFF & messageDigest[i]);
	            while (h.length() < 2)
	                h = "0" + h;
	            hexString.append(h);
	        }
	        return hexString.toString().toUpperCase(Locale.US);
        
		} catch (NoSuchAlgorithmException e) {
			Log.e("NodeIdentification", e.getMessage());
		}
		
		return "unknown";
	}
}
