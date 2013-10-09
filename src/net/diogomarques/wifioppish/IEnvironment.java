package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.networking.Message;


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
		Station;
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
	public abstract void pushMessage(Message m);
	
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
}
