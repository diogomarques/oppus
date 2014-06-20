package net.diogomarques.wifioppish.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Sensor to obtain device's battery level
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class BatterySensor extends AbstractSensor {
	
	private Integer batteryLevel;
	private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context arg0, Intent intent) {
	    	int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	    	batteryLevel = (int) level / (scale / 100);
	    }
	};
	
	/**
	 * Creates a new Battery sensor to receive battery level updates
	 * @param c Android context
	 */
	public BatterySensor(Context c) {
		super(c);
		batteryLevel = -1;
	}
	
	@Override
	public void startSensor() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(mBatInfoReceiver, ifilter);
	}

	@Override
	public Object getCurrentValue() {
		return batteryLevel;
	}

	@Override
	public void stopSensor() {
		context.unregisterReceiver(mBatInfoReceiver);
	}

}
