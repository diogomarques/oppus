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
public class ScreenOnSensor extends AbstractSensor {
	
	private static final String TAG = ScreenOnSensor.class.getSimpleName();
	
	private Integer totalActivations;
	
	private BroadcastReceiver countScreenActivations = new BroadcastReceiver() {
		
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	        	totalActivations++;
	        	Log.i(TAG, "Activated " + totalActivations + " times");
	        }
	    }
	};
	
	/**
	 * Creates a new ScreenOn sensor to monitor the device's screen activity
	 * @param c Android context
	 */
	public ScreenOnSensor(Context c) {
		super(c);
		totalActivations = 0;
	}

	@Override
	public Object getCurrentValue() {
		return totalActivations;
	}

	@Override
	public void startSensor() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
		context.registerReceiver(countScreenActivations, ifilter);
	}

	@Override
	public void stopSensor() {
		context.unregisterReceiver(countScreenActivations);
	}

}
