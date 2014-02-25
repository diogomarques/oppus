package net.diogomarques.wifioppish.sensors;

/**
 * Defines a type of sensor which obtains data during a 
 * defined interval.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public interface IIntervalSensor extends ISensor {

	/**
	 * Gets the value accumulated during the interval. The 
	 * logic for obtaining the value depends on the sensor 
	 * (can be mean-value, max, min, etc)
	 * @return representative value for the interval
	 */
	public Object getIntervalValue();
	
	/**
	 * Tells the sensor to start a new interval, possibly 
	 * dicarding old data. Depending on sensor implementation, 
	 * it could be useful to save data from previous interval 
	 * using {@link #getCurrentValue()}
	 */
	public void resetInterval();
}
