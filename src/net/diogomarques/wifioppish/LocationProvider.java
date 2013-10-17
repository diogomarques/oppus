package net.diogomarques.wifioppish;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Represents a Location Provider to obtain user location as precise 
 * as possible. It uses {@link android.content.SharedPreferences SharedPreferences} to store 
 * the value temporally.
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
	private SharedPreferences sharedPref;
	
	/**
	 * Preference key for the last known latitude
	 */
	public static final String LAST_LAT_KEY = "gps.lastLatitude";
	
	/**
	 * Preference key for the last known longitude
	 */
	public static final String LAST_LON_KEY = "gps.lastLongitude";
	
	/**
	 * Preference key for the last update
	 */
	public static final String LAST_TIME_KEY = "gps.lastUpdate";
	
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			Editor editor = sharedPref.edit();
			editor.putString(LAST_LAT_KEY, Double.toString(location.getLatitude()));
			editor.putString(LAST_LON_KEY, Double.toString(location.getLongitude()));
			editor.putLong(LAST_TIME_KEY, System.currentTimeMillis());
			editor.commit();
		}
	};
	
	/**
	 * Creates a new Location Provider. It starts the location gathering process.
	 * @param c Android context
	 */
	public LocationProvider(Context c) {
		context = c;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		// Clean previous location, if any
		Editor prefEditor = sharedPref.edit();
		prefEditor.remove(LAST_LAT_KEY);
		prefEditor.remove(LAST_LON_KEY);
		prefEditor.remove(LAST_TIME_KEY);
		prefEditor.commit();
		
		// FIXME começar o processo de recolha aqui?
		registerLocationListener(locationListener);
	}
	
	/**
	 * Registers an event listener to get GPS updates
	 * @param mLocListener location listener to receive coordinate updates
	 */
	public void registerLocationListener(LocationListener mLocListener) {
		if(mLocManager == null)
			mLocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
		
		unregisterLocationListener(mLocListener);
		mLocManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 0, 0, mLocListener);		
	}
	
	/**
	 * Unregisters a previously registered location listener
	 * @param mLocationListener location listener to remove
	 */
	public void unregisterLocationListener(LocationListener mLocationListener) {
		if(mLocManager != null)
			mLocManager.removeUpdates(mLocationListener);
	}
}