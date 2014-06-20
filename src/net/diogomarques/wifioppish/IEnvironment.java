package net.diogomarques.wifioppish;

import java.util.List;

import net.diogomarques.wifioppish.networking.Message;
import net.diogomarques.wifioppish.sensors.SensorGroup;


/**
 * Interface for state machine environment implementations.
 * <p>
 * The environment is the top-level control and observation structure for the
 * networking state machine.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public interface IEnvironment {

	/**
	 * Enumeration of possible state machine states.
	 * 
	 * @author Diogo Marques <diogohomemmarques@gmail.com>
	 * 
	 */
	public enum State {
		/**
		 * Announcing to others availability of providing an access point.
		 */
		Beaconing,
		/**
		 * Actually providing an access point.
		 */
		Providing,
		/**
		 * Looking for access point to which to connect.
		 */
		Scanning,
		/**
		 * Being connected to an access point.
		 */
		Station,
		/**
		 * Checking if Internet is available
		 */
		InternetCheck,
		/**
		 * Connected to the internet
		 */
		InternetConn,
		/**
		 * Special state to force environment to free resources and stop
		 */
		Stopped
	}

	/**
	 * An helper for internal components to deliver status messages to the
	 * environment.
	 * 
	 * @param msg
	 *            a textual message
	 */
	public void deliverMessage(String msg);
	
	/**
	 * An helper for internal components to deliver object updates to the 
	 * environment.
	 * 
	 * @param object
	 * 			the object to be shared
	 * @param code
	 * 			common code between the receiver and the sender, representing
	 * 			the operation which could be started
	 */
	public void deliverCustomMessage(Object object, int code);

	/**
	 * Schedules a transition to a new state.
	 * 
	 * @param state
	 *            the destination state.
	 */
	public void gotoState(State state);
	
	/**
	 * Loops between states
	 * 
	 * @param first
	 * 			  the initial state of State Machine
	 */
	public void startStateLoop(State first);
	
	/**
	 * (Tries to) stop the state loop. It also should unregister 
	 * any sensors defined.
	 */
	public void stopStateLoop();

	/**
	 * Get preferences relating to this state machine.
	 * 
	 * @return preferences wrapped in an instance of IDomainPreferences
	 */
	public abstract IDomainPreferences getPreferences();

	/**
	 * Get the networking controller.
	 * 
	 * @return the system-specific facade for networking.
	 */
	public abstract INetworkingFacade getNetworkingFacade();

	/**
	 * Get the current state.
	 * 
	 * @return the current state if it is set; null otherwise.
	 */
	public abstract State getCurrentState();
	
	/**
	 * Pushes a Message to the forwarding queue
	 * @param m Message to push to the end of queue 
	 */
	public abstract void pushMessageToQueue(Message m);
	
	/**
	 * Gets all Messages from the sending queue
	 * @return Array of Messages to be sent
	 */
	public abstract List<Message> fetchMessagesFromQueue();
	
	/**
	 * Checks of the queue has messages
	 * @return True if queue has messages, false otherwise
	 */
	public abstract boolean hasMessages();
	
	/**
	 * Updates the statistics of sent and received network messages
	 * @param sent total of messages sent since last update
	 * @param received total of messages received since last update
	 */
	public abstract void updateStats(int sent, int received);
	
	/**
	 * Gets the ID associated with this node
	 * @return node ID
	 */
	public abstract String getMyNodeId();
	
	/**
	 * Gets the current node geographical location
	 * @return geographical location (latitude, longitude)
	 */
	public abstract double[] getMyLocation();
	
	/**
	 * Stores a Message received inside a log file
	 */
	public abstract void storeReceivedMessage(Message m);
	
	/**
	 * Creates a Message containing a custom text, ready to be sent 
	 * over the network.
	 * @param contents Custom text to append to this message
	 */
	public abstract Message createTextMessage(String contents);

	/**
	 * Empties the sending queue
	 */
	public abstract void clearQueue();

	/**
	 * Removes a single message from the sending queue
	 * @param msg Message to be removed
	 * @return True if the message was removed; False otherwise.
	 */
	public abstract boolean removeFromQueue(Message msg);

	/**
	 * Marks the victim as safe or not. The Messages will reflect this change. 
	 * Once marked as safe, a victim should not mark it self as needing help again.
	 * @param safe True, if the victim is safe; false otherwise
	 */
	void markVictimAsSafe(boolean safe);
	
	/**
	 * Gets the last non-internet state 
	 * @return last state 
	 */
	public abstract State getLastState();

	/**
	 * Get the boolean that represents if internet state is part of the state cycle 
	 * @return true if internet state is part of the cycle, false if not
	 */
	public boolean internetState();
	
	/**
	 * Gets the SensorGroup instance
	 * @return SensorGroup instance
	 */
	public abstract SensorGroup getSensorGroup();
}
