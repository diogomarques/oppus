package net.diogomarques.wifioppish;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
/**
 * Represents a Location Provider to obtain user location as precise 
 * as possible.
 * 
 * <p>
 * The Location Provider tries to use the 
 * device's GPS to get the current location.
 * </p>
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class LocationProvider {
	
	private Context context;
	private LocationManager mLocManager;
	
	/**
	 * Creates a new Location Provider
	 * @param c Android context
	 */
	public LocationProvider(Context c) {
		context = c;
	}
	
	/**
	 * Registers an event listener to get GPS updates
	 * @param mLocListener location listener to receive coordinate updates
	 */
	public void registerLocationListener(LocationListener mLocListener) {
		if(mLocManager == null)
			mLocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
		
		mLocManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, mLocListener);		
	}
}