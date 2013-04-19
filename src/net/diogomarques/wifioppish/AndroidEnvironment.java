package net.diogomarques.wifioppish;

import android.os.Handler;
import android.os.Message;

public class AndroidEnvironment implements IEnvironment {

	private final Handler mHandler;
	private final INetworkingFacade networkingFacade;
	private final IPreferences preferences;
	private StateBeaconing beaconing;
	private StateProviding providing;
	private StateScanning scanning;
	private StateStation station;

	public AndroidEnvironment(Handler consoleHandler,
			INetworkingFacade networkingFacade, IPreferences preferences) {
		this.mHandler = consoleHandler;
		this.networkingFacade = networkingFacade;
		this.preferences = preferences;
	}

	@Override
	public INetworkingFacade getNetworkingFacade() {
		return networkingFacade;
	}

	@Override
	public IPreferences getPreferences() {
		return preferences;
	}

	// TODO stuff that should be done in a factory
	@Override
	public void prepare() {
		beaconing = new StateBeaconing(this);
		providing = new StateProviding(this);
		scanning = new StateScanning(this);
		station = new StateStation(this);
	}

	@Override
	public void sendMessage(String msg) {
		mHandler.sendMessage(Message.obtain(mHandler, 0, msg));
	}

	@Override
	public void gotoState(State state) {
		networkingFacade.clearListeners();
		AState next = null;
		switch (state) {
		case Beaconing:
			next = beaconing;
			break;
		case Providing:
			next = providing;
			break;
		case Scanning:
			next = scanning;
		case Station:
			next = station;
		}
		next.start();
	}

}
