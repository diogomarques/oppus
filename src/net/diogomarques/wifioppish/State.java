package net.diogomarques.wifioppish;

public abstract class State {

	protected IPreferences preferences;

	protected INetworkingFacade networking;
	
	protected IEnvironment environment;

	public State(IEnvironment environment, IPreferences preferences,
			INetworkingFacade networking) {		
		this.environment = environment;
		this.preferences = preferences;
		this.networking = networking;
	}

	public abstract void start();

}
