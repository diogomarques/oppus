package net.diogomarques.wifioppish;

import android.content.Context;

/**
 * An abstract state, forcing environment injection and all implementations to
 * have a start method.
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public abstract class AState {

	/**
	 * The state machine environment.
	 */
	protected IEnvironment environment;
	
	/**
	 * Android context
	 */
	protected Context context;

	/**
	 * Constructor.
	 * 
	 * @param env
	 *            the environment in which this state exists.
	 */
	public AState(IEnvironment env) {
		this.environment = env;
	}

	/**
	 * Start this state and keep it in operation until the timeout is reached.
	 * 
	 * @param timeout
	 *            the timeout in milliseconds
	 * @param context
	 * 			  Android current context      
	 */
	public abstract void start(int timeout, Context context);

}
