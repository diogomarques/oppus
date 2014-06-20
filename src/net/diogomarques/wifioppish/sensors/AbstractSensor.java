package net.diogomarques.wifioppish.sensors;

import android.content.Context;

/**
 * Abstract class that defines a basic sensor. Each sensor is responsable for 
 * gathering the desired data and interrupt/resume execution of hardware sensors 
 * when requested. 
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 * 
 */
public abstract class AbstractSensor {
	
	/**
	 * Field containing the context passed to default public {@link #AbstractSensor(Context) constructor}.
	 */
	protected Context context;
	
	/**
	 * Creates a new sensor
	 * @param c Android context from which it is possible to obtain sensors.
	 */
	public AbstractSensor(Context c) {
		this.context = c;
	}
	
	/**
	 * Tells the sensor to start/resume data gathering and accumulate values
	 */
	public abstract void startSensor();

	/**
	 * Gets the current value of this sensor accumulated so far
	 * @return Current sensor value
	 */
	public abstract Object getCurrentValue();
	
	/**
	 * Suspends the sensor data gathering. To reactivate sensor, 
	 * {@link #startSensor(Context c)} must be called. This method should 
	 * fully stop the sensor and free any resources used but the accumulated value.
	 */
	public abstract void stopSensor();
}
