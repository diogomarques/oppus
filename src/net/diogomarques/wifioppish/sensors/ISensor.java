package net.diogomarques.wifioppish.sensors;

import android.content.Context;

/**
 * Interface that defines a basic sensor.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 * 
 */
public interface ISensor {
	
	/**
	 * Tells the sensor to start obtaining data
	 * @param c Android Context
	 */
	public void startSensor(Context c);

	/**
	 * Gets the current value of this sensor
	 * @return Current sensor value
	 */
	public Object getCurrentValue();
	
}
