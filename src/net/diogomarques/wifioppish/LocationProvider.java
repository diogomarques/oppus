package net.diogomarques.wifioppish;

import net.diogomarques.utils.CountDownTimer;
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
	private int failedInteractions;
	
	/**
	 * Limit of the backoff formula (minutes)
	 */
	private final int BACKOFF_LIMIT = 30; 
	
	/**
	 * Maximum time to wait for a GPS reply (minutes)
	 */
	private final int GPS_RESPONSE_LIMIT = 2;
	
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
			
			Log.w("LocationProvider", "Location records updated! (" +
					location.getLatitude() + "," + location.getLongitude() + ")");
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
		failedInteractions = 0;
		
		// Clean previous location, if any
		Editor prefEditor = sharedPref.edit();
		prefEditor.remove(LAST_LAT_KEY);
		prefEditor.remove(LAST_LON_KEY);
		prefEditor.remove(LAST_TIME_KEY);
		prefEditor.commit();
	}
	
	/**
	 * Starts the process of discovering the current and future locations
	 */
	public void startLocationDiscovery() {
		/*CountDownTimer gpsDelay = new CountDownTimer(
				calculateNextInteraction(failedInteractions) * 60 * 1000, 1000) {
			
			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}
		};*/
		registerLocationListener(locationListener);
	}
	
	private void startGPS() {
		int responseLimit = GPS_RESPONSE_LIMIT * 60 * 1000;
		
		CountDownTimer gpsDelay = new CountDownTimer(
				responseLimit, responseLimit) {
			
			@Override
			public void onTick(long arg0) { }
			
			@Override
			public void onFinish() {
				// TODO não foi obtida resposta do GPS: pedidofalhado++ e esperar novo processo
			}
		};
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
	 * Gives the time in minutes to wait before searching for a location 
	 * (using the formula 2^(n-1) with a limit of {@link #BACKOFF_LIMIT} minutes)
	 * @param seq The position of the sequence (intended to use a number of trials after last success)
	 * @return Number of minutes to wait before next interaction
	 */
	private int calculateNextInteraction(int seq) {
		if(seq <= 0)
			return 0;
		
		return Math.min( (int)Math.pow(2, seq - 1) , BACKOFF_LIMIT );
	}
}