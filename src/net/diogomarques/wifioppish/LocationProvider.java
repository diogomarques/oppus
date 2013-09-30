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
	
	private final String LAST_LAT_KEY = "gps.lastLatitude";
	private final String LAST_LON_KEY = "gps.lastLongitude";
	private final String LAST_TIME_KEY = "gps.lastUpdate";
	
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
	 * Represents a location with a associated refreshness
	 * 
	 * @author André Silva <asilva@lasige.di.fc.ul.pt>
	 */
	public class CachedLocation extends Location {
		
		private long lastUpdate;

		/**
		 * Create a default Location object
		 * @param l
		 * @see android.location.Location#Location(Location)
		 */
		public CachedLocation(Location l) {
			super(l);
		}
		
		public CachedLocation() {
			super("");
		}

		/**
		 * Gets the last update time
		 * @return Last update time
		 */
		public long getLastUpdate() {
			return lastUpdate;
		}

		/**
		 * Sets the last update time
		 * @param lastUpdate Last update time
		 */
		public void setLastUpdate(long lastUpdate) {
			this.lastUpdate = lastUpdate;
		}
		
	}
	
	/**
	 * Creates a new Location Provider. It starts the location gathering process.
	 * @param c Android context
	 */
	public LocationProvider(Context c) {
		context = c;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
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
	 * Gets the current location (note that the location 
	 * @return location object with the most recent collected data 
	 */
	public CachedLocation getCurrentLocation() {
		/*long reqTime = System.currentTimeMillis();
		
		try {
			registerLocationListener(locationListener);
			semGPSTimeout.tryAcquire(timeout, TimeUnit.MILLISECONDS);
			return lastKnownLocation;
			
		} catch (InterruptedException e) {
			Log.e("LocationProvider", "Error acquiring lock: " + e.getMessage(), e);
		}
		
		return null;*/
		if(!sharedPref.contains(LAST_TIME_KEY) ||
				!sharedPref.contains(LAST_LON_KEY) ||
				!sharedPref.contains(LAST_LAT_KEY) )
			return null;
		
		double lat = Double.parseDouble(sharedPref.getString(LAST_LAT_KEY, "0"));
		double lon = Double.parseDouble(sharedPref.getString(LAST_LON_KEY, "0"));
		long time = sharedPref.getLong(LAST_TIME_KEY, 0);
		
		CachedLocation loc = new CachedLocation();
		loc.setLatitude(lat);
		loc.setLongitude(lon);
		loc.setLastUpdate(time);
		
		return loc;
	}
	
	/**
	 * Unregisters a previously registered location listener
	 * @param mLocationListener location listener to remove
	 */
	public void unregisterLocationListener(LocationListener mLocationListener) {
		if(mLocManager != null) {
			mLocManager.removeUpdates(mLocationListener);
		}
	}
}