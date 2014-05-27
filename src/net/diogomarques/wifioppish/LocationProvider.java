package net.diogomarques.wifioppish;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Represents a Location Provider to obtain user location as precise 
 * as possible. It uses {@link android.content.SharedPreferences SharedPreferences} to store 
 * the value temporally and allow shared access to other components.
 * <p>
 * The Location Provider uses the device's GPS to get the current location.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class LocationProvider {
	
	private static final String TAG = "LocationProvider";
	private static final int CONFIDENCE_INTERVAL = 5;   // in seconds
	private Context context;
	private LocationManager mLocManager;
	private SharedPreferences sharedPref;
	private long lastUpdate;
	private ScheduledExecutorService schedulerConf;
	
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
	
	/**
	 * Preference key for the location confidence
	 * @see {@link #CONFIDENCE_LAST_KNOWN} Value for poor confidence
	 * @see {@link #CONFIDENCE_UPDATED} Value for good confidence
	 */
	public static final String LAST_CONFIDENCE_KEY = "gps.confidence";
	
	/**
	 * Mediocre confidence level for location update, meaning that the location 
	 * refers to the previously successfully retrieved location
	 */
	public static final int CONFIDENCE_LAST_KNOWN = 0;
	
	/**
	 * Maximum confidence level for location update, meaning that the location 
	 * was just retrieved from GPS
	 */
	public static final int CONFIDENCE_UPDATED = 1;
	
	private LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			Log.i(TAG, "Status change: #status=" + status + " for " + provider);
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			Log.i(TAG, "Provider enabled: " + provider);
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			Log.i(TAG, "Provider disabled: " + provider);
		}
		
		@Override
		public void onLocationChanged(Location location) {
			lastUpdate = System.currentTimeMillis();
			
			Editor editor = sharedPref.edit();
			editor.putString(LAST_LAT_KEY, Double.toString(location.getLatitude()));
			editor.putString(LAST_LON_KEY, Double.toString(location.getLongitude()));
			editor.putLong(LAST_TIME_KEY, lastUpdate);
			editor.putInt(LAST_CONFIDENCE_KEY, CONFIDENCE_UPDATED);
			editor.commit();
			
			if(schedulerConf == null)
				initializeScheduler();
			
			/*Log.d(TAG, "Location update (" +
					location.getLatitude() + "," + location.getLongitude() + ")");*/
		}
	};
	
	/**
	 * Creates a new Location Provider. To start the location gathering process, invoke the
	 * {@link #startLocationDiscovery()} method 
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
		prefEditor.remove(LAST_CONFIDENCE_KEY);
		prefEditor.commit();
	}
	
	/**
	 * Starts the process of discovering the current and future locations
	 */
	public void startLocationDiscovery() {
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
	
	/**
	 * Initializes the scheduler to check for valid locations
	 */
	private void initializeScheduler() {
		schedulerConf = Executors.newSingleThreadScheduledExecutor();
		schedulerConf.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				if((lastUpdate + CONFIDENCE_INTERVAL*1000) < System.currentTimeMillis()) {
					Editor editor = sharedPref.edit();
					editor.putInt(LAST_CONFIDENCE_KEY, CONFIDENCE_LAST_KNOWN);
					editor.commit();
					Log.i(TAG, "No coordinates in the last " + CONFIDENCE_INTERVAL + " seconds, reducing confidence");
				} else {
					Log.i(TAG, "Receiving coordinates");
				}
			}
		}, 0, CONFIDENCE_INTERVAL, TimeUnit.SECONDS);
	}
}