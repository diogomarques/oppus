package net.diogomarques.wifioppish.networking;

import java.io.Serializable;

import net.diogomarques.wifioppish.sensors.LocationSensor;

/**
 * Message envelope to be exchanged between devices.
 * 
 * <p>
 * Each Message contains a node ID, a timestamp indicating the 
 * original message send time, a geographical location and an optional text message. 
 * Messages also contain another attributes like device battery, number of steps given 
 * by victim among others.
 * 
 * <p>
 * This envelope was created to be suitable to be transmitted over a network. It 
 * uses Java Serialization to ensure that the contents are formatted correctly. 
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class Message implements Serializable {

	/**
	 * Generated class serial number
	 */
	private static final long serialVersionUID = 4793280315313094725L;
	
	/* Essentials attributes */
	private String nodeId;
	private long timestamp;
	private double latitude;
	private double longitude;
	private int llconfidence;
	private String message;
	
	/* Victim status attributes */
	private int battery;
	private int steps;
	private int screenOn;
	private boolean safe;
	
	/**
	 * Creates a new Message envelope with information regarding external conditions
	 * 
	 * @param message Text to be sent
	 * @param timestamp Timestamp from when the Message was created 
	 * @param coords Geographical coordinates associated with this Message. The first position 
	 * 	should represent the latitude, and the second position of array should represent the longitude
	 * @param nodeId Identificator of the node who sent the message
	 * 
	 */
	public Message(String nodeId, long timestamp, double[] coords, String message) {
		this.nodeId = nodeId;
		this.timestamp = timestamp;
		this.latitude = coords[0];
		this.longitude = coords[1];
		this.llconfidence = (int) coords[2];
		this.message = message;
		this.battery = -1;
		this.safe = false;
		this.screenOn = -1;
		this.steps = -1;
	}
	
	/**
	 * Creates a new Message envelope with information regarding external conditions
	 * 
	 * @param message Text to be sent
	 * @param timestamp Timestamp from when the Message was created 
	 * @param coords Geographical coordinates associated with this Message. The first position 
	 * 	should represent the latitude, and the second position of array should represent the longitude
	 * @param nodeId Identificator of the node who sent the message
	 * @param battery Battery level [0-100] of the device
	 * @param safe True if the victim is currently marked as safe; false otherwise
	 * @param screen Total number of screen activations
	 * @param steps Total number of micro-movements done measured
	 */
	public Message(String nodeId, long timestamp, double[] coords, String message, int battery, int safe, int screen, int steps) {
		this.nodeId = nodeId;
		this.timestamp = timestamp;
		this.latitude = coords[0];
		this.longitude = coords[1];
		this.llconfidence = (int) coords[2];
		this.message = message;
		this.battery = battery;
		this.safe = (safe == 1);
		this.screenOn = screen;
		this.steps = steps;
	}
	
	/**
	 * Gets the device battery when this message was sent
	 * @return the battery
	 */
	public int getBattery() {
		return battery;
	}

	/**
	 * Sets the device battery information to be sent
	 * @param battery the battery to set
	 */
	public void setBattery(int battery) {
		this.battery = battery;
	}

	/**
	 * Gets the total of steps the victims made until the message was sent
	 * @return the steps
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * Sets the total number of steps for the victim
	 * @param steps the steps to set
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}

	/**
	 * Gets the total times the screen as turned on
	 * @return the screenOn
	 */
	public int getScreenOn() {
		return screenOn;
	}

	/**
	 * Sets the total times the screen as turned on
	 * @param screenOn the screenOn to set
	 */
	public void setScreenOn(int screenOn) {
		this.screenOn = screenOn;
	}

	/**
	 * Tells whenever the victim marked itself as safe
	 * @return the safe value
	 */
	public boolean isSafe() {
		return safe;
	}

	/**
	 * Sets the safe victim status
	 * @param safe the safe to set
	 */
	public void setSafe(boolean safe) {
		this.safe = safe;
	}

	/**
	 * Gets the sender node identificator
	 * @return the nodeId
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * Gets the time when the message was sent
	 * @return the timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the latitude of the victim
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * Gets the longitude of the victim
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	/**
	 * Gets the confidence associated with the location
	 * @return location confidence
	 * @see {@link LocationSensor#CONFIDENCE_LAST_KNOWN}
	 * 		Value for poor confidence
	 * @see {@link LocationSensor#CONFIDENCE_UPDATED}
	 * 		Value for good confidence
	 */
	public int getLocationConfidence() {
		return llconfidence;
	}

	/**
	 * Gets the textual message sent by the victim, if any
	 * @return the text message; empty if no text message was sent
	 */
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "Message [nodeId=" + nodeId + ", timestamp=" + timestamp
				+ ", latitude=" + latitude + ", longitude=" + longitude
				+ ", llconfidence=" + llconfidence
				+ ", message=" + message + ", battery=" + battery + ", steps="
				+ steps + ", screenOn=" + screenOn + ", safe=" + safe + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + battery;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + llconfidence;
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
		result = prime * result + (safe ? 1231 : 1237);
		result = prime * result + screenOn;
		result = prime * result + steps;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Message))
			return false;
		Message other = (Message) obj;
		if (battery != other.battery)
			return false;
		if (Double.doubleToLongBits(latitude) != Double
				.doubleToLongBits(other.latitude))
			return false;
		if (llconfidence != other.llconfidence)
			return false;
		if (Double.doubleToLongBits(longitude) != Double
				.doubleToLongBits(other.longitude))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (nodeId == null) {
			if (other.nodeId != null)
				return false;
		} else if (!nodeId.equals(other.nodeId))
			return false;
		if (safe != other.safe)
			return false;
		if (screenOn != other.screenOn)
			return false;
		if (steps != other.steps)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}
}
