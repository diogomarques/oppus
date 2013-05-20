package net.diogomarques.wifioppish;

public abstract class AState {

	protected IEnvironment environment;

	public AState(IEnvironment environment) {		
		this.environment = environment;				
	}

	public abstract void start(int timeout);

}
