package net.diogomarques.wifioppish;

public interface IEnvironment {
	
	public enum State {
		Beaconing, Providing, Scanning, Station;
	}
	
	public void sendMessage(String msg);
	
	public void gotoState(State state);

	public abstract void prepare();

	public abstract IPreferences getPreferences();

	public abstract INetworkingFacade getNetworkingFacade();
}
