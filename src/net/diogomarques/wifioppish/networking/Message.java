package net.diogomarques.wifioppish.networking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Message envelope to be exchanged between devices.
 * 
 * <p>
 * Each Message contains a string message, a timestamp indicating the 
 * original message send time, a geographical location and the node ID which created 
 * the Message. It also contains a trace which allows to know the nodes where the message 
 * was temporarily stored.
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
	
	private String message;
	private List<TraceNode> trace;
	
	/**
	 * Represents an entry inside the Trace list
	 */
	private static class TraceNode implements Serializable {
		
		/**
		 * Generated class serial number
		 */
		private static final long serialVersionUID = 8019775040490610392L;
		
		private String author;
		private long timestamp;
		private double[] coordinates;
		
		/**
		 * Creates a new entry for the trace list
		 * @param middleman The node id which received the message
		 * @param timestamp The timestamp when the message was received by this node
		 * @param coordinates The geographical coordinates associated with the node location
		 */
		public TraceNode(String middleman, long timestamp, double[] coordinates) {
			super();
			this.author = middleman;
			this.timestamp = timestamp;
			this.coordinates = coordinates;
		}

		@Override
		public String toString() {
			return "[node=" + author + ", timestamp="
					+ timestamp + ", coordinates="
					+ Arrays.toString(coordinates) + "]";
		}	
	}
	
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
		trace = new ArrayList<TraceNode>();
		trace.add(new TraceNode(node, time, coords));
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
		TraceNode first = trace.get(0);
		return first.timestamp;
	}

	/**
	 * Gets the geographical coordinates associated with this Message
	 * @return Geographical coordinates
	 */
	public double[] getCoordinates() {
		TraceNode first = trace.get(0);
		return first.coordinates;
	}

	/**
	 * Gets the ID of the node who created the message
	 * @return Unique node ID
	 */
	public String getAuthor() {
		TraceNode first = trace.get(0);
		return first.author;
	}
	
	/**
	 * Adds a node to the message trace
	 * @param nodeID Node ID where this message arrived
	 */
	public void addTraceNode(String nodeID, long time, double[] coords) {
		trace.add(new TraceNode(nodeID, time, coords));
	}
	
	/**
	 * Checks if a node is in the Message trace
	 * @param node Node ID to check
	 * @return True if node is in the trace, false otherwise
	 */
	public boolean isNodeinTrace(String node) {
		for(TraceNode t : trace)
			if (t.author.equals(node)) return true;
		
		return false;
	}
	
	/**
	 * Gets the list of nodes where this message passed by
	 * @return Array containing the nodes, ordered by time
	 */
	private TraceNode[] getTrace() {
		TraceNode[] traceArray = new TraceNode[trace.size()];
		int i = 0;
		
		for(TraceNode t : trace)
			traceArray[i++] = t;
		
		return traceArray;
	}
	
	@Override
	public String toString() {
		return "Message [message=" + message + ", trace=" + Arrays.toString(getTrace()) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getAuthor() == null) ? 0 : getAuthor().hashCode());
		result = prime * result + Arrays.hashCode(getCoordinates());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
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
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (getTimestamp() != other.getTimestamp())
			return false;
		return true;
	}	
}
