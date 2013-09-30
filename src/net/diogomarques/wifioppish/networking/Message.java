package net.diogomarques.wifioppish.networking;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Message envelope to be exchanged between devices.
 * 
 * <p>
 * Each Message contains a string message, a timestamp indicating the 
 * original message send time and a geographical location.
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
	
	private String author;
	private String message;
	private long timestamp;
	private double[] coordinates;
	
	/**
	 * Creates a new read-only Message
	 *  
	 * @param msg Text to be sent
	 * @param time Timestamp from when the Message was created 
	 * @param coords Geographical coordinates associated with this Message. The first position
	 * @param node Id of the node who sent the message
	 * should represent the latitude, and the second position of array should represent the longitude
	 */
	public Message(String msg, long time, double[] coords, String node) {
		message = msg;
		timestamp = time;
		coordinates = coords;
		author = node;
	}

	/**
	 * Gets the textual Message from the envelope
	 * @return Text Message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the timestamp associated with this Message
	 * @return Message creation timestamp
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Gets the geographical coordinates associated with this Message
	 * @return Geographical coordinates
	 */
	public double[] getCoordinates() {
		return coordinates;
	}

	/**
	 * Gets the ID of the node who created the message
	 * @return Unique node ID
	 */
	public String getAuthor() {
		return author;
	}

	@Override
	public String toString() {
		return "Message [message=" + message + ", timestamp=" + timestamp
				+ ", coordinates=" + Arrays.toString(coordinates)
				+ ", author=" + author + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + Arrays.hashCode(coordinates);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (!Arrays.equals(coordinates, other.coordinates))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}	
}
