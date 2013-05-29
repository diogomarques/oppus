package net.diogomarques.wifioppish;

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
	 * Transition to new state.
	 * 
	 * @param state
	 *            the destination state.
	 */
	public void gotoState(State state);

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
}
