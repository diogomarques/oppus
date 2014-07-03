package net.diogomarques.wifioppish.sensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a group of sensors. It allows the storage and 
 * retrievel of sensor like a key-value pair. Each key can have 
 * at most one sensor associated. You should use the keys provided 
 * by the {@link SensorGroupKey} enumerator.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class SensorGroup {
	
	private HashMap<SensorGroupKey, AbstractSensor> sensors;
	
	/**
	 * Set of keys to be used to access sensors
	 */
	public enum SensorGroupKey {
		
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
		sensors = new HashMap<SensorGroupKey, AbstractSensor>();
	}

	/**
	 * Gets the sensor associated with the key
	 * @param key Sensor key
	 * @return sensor instance if exists; null otherwise
	 */
	public AbstractSensor getSensor(SensorGroupKey key) {
		return sensors.get(key);
	}
	
	/**
	 * Gets the sensor value for the selected key
	 * @param key Sensor key
	 * @return value for selected sensor; null if no sensor exists with that key
	 */
	public Object getSensorCurrentValue(SensorGroupKey key) {
		AbstractSensor curSensor = getSensor(key);
		if(curSensor == null)
			return null;
		
		return curSensor.getCurrentValue();
	}
	
	/**
	 * Removes a sensor from this group
	 * @param key Sensor key
	 * @param stop If true, the sensor is stopped
	 * @return Removed sensor
	 */
	public AbstractSensor removeSensor(SensorGroupKey key, boolean stop) {
		AbstractSensor sensor = sensors.remove(key);
		if(stop && sensor != null)
			sensor.stopSensor();
		
		return sensor;
	}
	
	/**
	 * Removes all sensors registered
	 * @param stop If true, all sensors are stopped
	 * @return List of removed sensors
	 */
	public List<AbstractSensor> removeAllSensors(boolean stop) {
		List<AbstractSensor> removed = new ArrayList<AbstractSensor>();
		
		for(SensorGroupKey k : SensorGroupKey.values())
			removed.add(removeSensor(k, stop));
		
		return removed;
	}
	
	/**
	 * Adds a new sensor to this sensor group and starts it 
	 * @param key Sensor key
	 * @param sensor New Sensor to add
	 * @param start Starts the sensor, if successfully registered
	 * @return true if sensor was added; false otherwise (likely there is already a 
	 * sensor associated with that key)
	 */
	public boolean addSensor(SensorGroupKey key, AbstractSensor sensor, boolean start) {
		if(sensors.containsKey(key))
			return false;
		
		sensors.put(key, sensor);
		
		if(start)
			sensor.startSensor();
		
		return true;
	}
	
}
