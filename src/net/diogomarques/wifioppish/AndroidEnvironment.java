package net.diogomarques.wifioppish;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class AndroidEnvironment implements IEnvironment {
	

	private Handler mHandler;
	private INetworkingFacade networkingFacade;
	private IDomainPreferences preferences;
	private StateBeaconing beaconing;
	private StateProviding providing;
	private StateScanning scanning;
	private StateStation station;
	
	void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	void setNetworkingFacade(INetworkingFacade networkingFacade) {
		this.networkingFacade = networkingFacade;
	}

	void setPreferences(IDomainPreferences preferences) {
		this.preferences = preferences;
	}

	void setBeaconingState(StateBeaconing beaconing) {
		this.beaconing = beaconing;
	}

	void setProvidingState(StateProviding providing) {
		this.providing = providing;
	}

	void setScanningState(StateScanning scanning) {
		this.scanning = scanning;
	}

	void setStationState(StateStation station) {
		this.station = station;
	}

	/**
	 * Use factory instead.
	 * 
	 * @param mHandler
	 * @param networkingFacade
	 * @param preferences
	 * @param beaconing
	 * @param providing
	 * @param scanning
	 * @param station
	 */
	public AndroidEnvironment(Handler mHandler,
			INetworkingFacade networkingFacade, IDomainPreferences preferences,
			StateBeaconing beaconing, StateProviding providing,
			StateScanning scanning, StateStation station) {
		super();
		this.mHandler = mHandler;
		this.networkingFacade = networkingFacade;
		this.preferences = preferences;
		this.beaconing = beaconing;
		this.providing = providing;
		this.scanning = scanning;
		this.station = station;
	}
	
	private AndroidEnvironment() {
		
	}
	
	public static IEnvironment createInstance(Context c, Handler handler) {
		AndroidEnvironment environment = new AndroidEnvironment();
		// states
		StateBeaconing beaconing = new StateBeaconing(environment);
		StateProviding providing = new StateProviding(environment);
		StateScanning scanning = new StateScanning(environment);
		StateStation station = new StateStation(environment);
		INetworkingFacade networkingFacade = AndroidNetworkingFacade.createInstance(c, environment);		
		IDomainPreferences preferences = new AndroidPreferences(c);		
		// networking
		environment.setHandler(handler);
		environment.setNetworkingFacade(networkingFacade);
		environment.setPreferences(preferences);
		environment.setBeaconingState(beaconing);
		environment.setProvidingState(providing);
		environment.setScanningState(scanning);
		environment.setStationState(station);
		return environment;
	}


	@Override
	public INetworkingFacade getNetworkingFacade() {
		return networkingFacade;
	}

	@Override
	public IDomainPreferences getPreferences() {
		return preferences;
	}
	
	@Override
	public void sendMessage(String msg) {
		mHandler.sendMessage(Message.obtain(mHandler, 0, msg));
	}

	@Override
	public void gotoState(State state) {		
		// TODO now creates new instances; delete if no effect on bugs
		AState next = null;
		switch (state) {
		case Beaconing:
			next = beaconing;
			next.start(preferences.getTBeac());
			break;
		case Providing:
			next = providing;
			next.start(preferences.getTPro());
			break;
		case Scanning:
			next = scanning;
			next.start(preferences.getTScan());
			break;
		case Station:
			next = station;
			next.start(preferences.getTCon());
			break;
		}		
	}

}
