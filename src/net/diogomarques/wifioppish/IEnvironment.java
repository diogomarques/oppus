package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;

public interface IEnvironment {
	
	public enum State {
		Beaconing, Providing, Scanning, Station;
	}
	
	public void sendMessage(String msg);
	
	public void gotoState(State state);

	public abstract IDomainPreferences getPreferences();

	public abstract INetworkingFacade getNetworkingFacade();

	public State getCurrentState();
}
