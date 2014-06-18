package net.diogomarques.wifioppish.sensors;

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
 * Updates geographical location positioning data. It uses {@link android.content.SharedPreferences SharedPreferences} 
 * to store the value temporally and allow shared access to other components.
 * <p>
 * The Location Provider uses the device's GPS to get the current location. 
 * Each location has an associated confidence level
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class LocationSensor extends AbstractSensor {
	
	private static final String TAG = "LocationSensor";
	private static final int CONFIDENCE_INTERVAL = 5;   // in seconds
	private Context context;
	private LocationManager mLocManager;
	private SharedPreferences sharedPref;
	private long lastUpdate;
	private ScheduledExecutorService schedulerConf;
	
	// data
	private double latitude, longitude;
	private int confidence;
	
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
	 * @see {@link #CONFIDENCE_APPROXIMATE} Value for reasonable confidence
	 * @see {@link #CONFIDENCE_UPDATED} Value for good confidence
	 */
	public static final String LAST_CONFIDENCE_KEY = "gps.confidence";
	
	/**
	 * Mediocre confidence level for location update, meaning that the location 
	 * refers to the previously successfully retrieved location
	 */
	public static final int CONFIDENCE_LAST_KNOWN = 0;
	
	/**
	 * Reasonable confidence level, tipically associated with coordinates exchanged by 
	 * peers geographicaly near the victim.
	 * <p>
	 * Reserved for future use.
	 */
	public static final int CONFIDENCE_APPROXIMATE = 5;
	
	/**
	 * Maximum confidence level for location update, meaning that the location 
	 * was just retrieved from GPS
	 */
	public static final int CONFIDENCE_UPDATED = 10;
	
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
			
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			confidence = CONFIDENCE_UPDATED;
			
			if(schedulerConf == null)
				initializeScheduler();
		}
	};
	
	public LocationSensor(Context c) {
		super(c);
		context = c;
		confidence = CONFIDENCE_LAST_KNOWN;
	}

	@Override
	public void startSensor() {
		registerLocationListener(locationListener);
	}

	@Override
	public Object getCurrentValue() {
		return new double[] { latitude, longitude, confidence };
	}

	@Override
	public void stopSensor() {
		unregisterLocationListener(locationListener);
	}
	
	/**
	 * Registers an event listener to get GPS updates
	 * @param mLocListener location listener to receive coordinate updates
	 */
	private void registerLocationListener(LocationListener mLocListener) {
		
		context.hashCode();
		
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
	private void unregisterLocationListener(LocationListener mLocationListener) {
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
					confidence = CONFIDENCE_LAST_KNOWN;
					Log.i(TAG, "No coordinates in the last " + CONFIDENCE_INTERVAL + " seconds, reducing confidence");
				} else {
					Log.i(TAG, "Receiving coordinates");
				}
			}
		}, 0, CONFIDENCE_INTERVAL, TimeUnit.SECONDS);
	}

}
