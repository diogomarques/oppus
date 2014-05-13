package net.diogomarques.wifioppish.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Sensor to detect when the device's screen is turned on
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class ScreenOnSensor implements ISensor {
	
	private static final String TAG = "ScreenOn Sensor";
	
	private Integer totalActivations;
	
	/**
	 * Creates a new ScreenOn sensor to monitor the device's screen activity
	 */
	public ScreenOnSensor() {
		totalActivations = 0;
	}

	@Override
	public void startSensor(Context c) {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		c.registerReceiver(new BroadcastReceiver() {
	
			    @Override
			    public void onReceive(Context context, Intent intent) {
			        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
			        	totalActivations++;
			        	Log.i(TAG, "Activated " + totalActivations + " times");
			        }
			    }
			},
			ifilter
		);
	}

	@Override
	public Object getCurrentValue() {
		return totalActivations;
	}

}
