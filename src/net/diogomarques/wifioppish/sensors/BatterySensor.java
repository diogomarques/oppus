package net.diogomarques.wifioppish.sensors;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Sensor to gather device's battery charge level
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class BatterySensor extends AbstractSensor {
	
	private Intent batteryStatus;
	private Integer batteryLevel;
	
	public BatterySensor(Context c) {
		super(c);
		batteryLevel = -1;
	}
	
	@Override
	public void startSensor() {
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		batteryStatus = context.registerReceiver(null, ifilter);
		
		// get current level
		/*int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		batteryLevel = (int) level / (scale / 100);*/
	}

	@Override
	public Object getCurrentValue() {
		int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		batteryLevel = (int) level / (scale / 100);
		
		return batteryLevel;
	}

	@Override
	public void stopSensor() {
		// TODO implementar método de paragem
	}

}
