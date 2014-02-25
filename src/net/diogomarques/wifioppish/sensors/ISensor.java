package net.diogomarques.wifioppish.sensors;

/**
 * Interface that defines a basic sensor.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 * 
 */
public interface ISensor {

	/**
	 * Gets the current value of this sensor
	 * @return Current sensor value
	 */
	public Object getCurrentValue();
	
}
