package net.diogomarques.wifioppish;

public abstract class AState {

	protected IDomainPreferences preferences;

	protected INetworkingFacade networking;
	
	protected IEnvironment environment;

	public AState(IEnvironment environment) {		
		this.environment = environment;
		this.preferences = environment.getPreferences();
		this.networking = environment.getNetworkingFacade();
	}

	public abstract void start(int timeout);

}
