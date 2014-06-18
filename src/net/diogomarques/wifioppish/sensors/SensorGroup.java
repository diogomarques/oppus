package net.diogomarques.wifioppish.sensors;

import java.util.HashMap;

/**
 * Represents a group of sensors. It allows the storage and 
 * retrievel of sensor like a key-value pair. Each key can have 
 * at most one sensor associated. You should use the keys provided 
 * by the {@link GroupKey} enumerator.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class SensorGroup {
	
	private HashMap<GroupKey, AbstractSensor> sensors;
	
	/**
	 * Set of keys to be used to access sensors
	 * 
	 * @author André Silva <asilva@lasige.di.fc.ul.pt>
	 */
	public enum GroupKey {
		
		/**
		 * Battery level sensor
		 */
		Battery,
		
		/**
		 * Victim steps/movement sensor
		 */
		MicroMovements,
		
		/**
		 * User-screen interaction
		 */
		ScreenOn,
		
		/**
		 * Geographical location sensor
		 */
		Location
	}
	
	/**
	 * Creates and initializes a new {@link SensorGroup}
	 */
	public SensorGroup() {
		sensors = new HashMap<GroupKey, AbstractSensor>();
	}

	/**
	 * Gets the sensor associated with the key
	 * @param key Sensor key
	 * @return sensor instance if exists; null otherwise
	 */
	public AbstractSensor getSensor(GroupKey key) {
		return sensors.get(key);
	}
	
	/**
	 * Gets the sensor value for the selected key
	 * @param key Sensor key
	 * @return value for selected sensor; null if no sensor exists with that key
	 */
	public Object getSensorCurrentValue(GroupKey key) {
		AbstractSensor curSensor = getSensor(key);
		if(curSensor == null)
			return null;
		
		return curSensor.getCurrentValue();
	}
	
	/**
	 * Removes a sensor from this group
	 * @param key Sensor key
	 */
	public void removeSensor(GroupKey key) {
		sensors.remove(key);
	}
	
	/**
	 * Adds a new sensor to this sensor group and starts it 
	 * @param key Sensor key
	 * @param sensor New Sensor to add
	 * @return true if sensor was added; false otherwise (likely there is already a 
	 * sensor associated with that key)
	 */
	public boolean addSensor(GroupKey key, AbstractSensor sensor) {
		if(sensors.containsKey(key))
			return false;
		
		sensors.put(key, sensor);
		sensor.startSensor();
		return true;
	}
}
