package net.diogomarques.wifioppish;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * An Android-specific implementation of the state machine environment. To
 * create an instance, use {@link #createInstance(Context, Handler)}.
 * <p>
 * Since the state machine will typically run outside the UI thread, an {@link Handler}
 * is used to forward status messages obtained through
 * {@link #deliverMessage(String)};
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 * 
 */
public class AndroidEnvironment implements IEnvironment {

	/* Dependencies */
	private Handler mHandler;
	private INetworkingFacade mNetworkingFacade;
	private IDomainPreferences mPreferences;
	private StateBeaconing mBeaconing;
	private StateProviding mProviding;
	private StateScanning mScanning;
	private StateStation mStation;
	private State mCurrentState;

	/**
	 * Constructor with all dependencies. Use
	 * {@link #createInstance(Context, Handler)} instead.
	 * 
	 * @param mHandler
	 * @param networkingFacade
	 * @param preferences
	 * @param beaconing
	 * @param providing
	 * @param scanning
	 * @param station
	 */
	private AndroidEnvironment(Handler mHandler,
			INetworkingFacade networkingFacade, IDomainPreferences preferences,
			StateBeaconing beaconing, StateProviding providing,
			StateScanning scanning, StateStation station) {
		super();
		this.mHandler = mHandler;
		this.mNetworkingFacade = networkingFacade;
		this.mPreferences = preferences;
		this.mBeaconing = beaconing;
		this.mProviding = providing;
		this.mScanning = scanning;
		this.mStation = station;
	}

	/**
	 * Convenience constructor for {@link #createInstance(Context, Handler)}.
	 */
	private AndroidEnvironment() {
	}

	/**
	 * Static factory that creates instances of state machine environments.
	 * 
	 * @param c
	 *            the context
	 * @param h
	 *            handler to send messages to the UI
	 * @return a new instance with all dependencies set
	 */
	public static IEnvironment createInstance(Context c, Handler h) {
		AndroidEnvironment environment = new AndroidEnvironment();
		// states
		StateBeaconing beaconing = new StateBeaconing(environment);
		StateProviding providing = new StateProviding(environment);
		StateScanning scanning = new StateScanning(environment);
		StateStation station = new StateStation(environment);
		INetworkingFacade networkingFacade = AndroidNetworkingFacade
				.createInstance(c, environment);
		IDomainPreferences preferences = new AndroidPreferences(c);
		// networking
		environment.mHandler = h;
		environment.mNetworkingFacade = networkingFacade;
		environment.mPreferences = preferences;
		environment.mBeaconing = beaconing;
		environment.mProviding = providing;
		environment.mScanning = scanning;
		environment.mStation = station;
		return environment;
	}

	@Override
	public State getCurrentState() {
		return mCurrentState;
	}

	@Override
	public INetworkingFacade getNetworkingFacade() {
		return mNetworkingFacade;
	}

	@Override
	public IDomainPreferences getPreferences() {
		return mPreferences;
	}

	@Override
	public void deliverMessage(String msg) {
		mHandler.sendMessage(Message.obtain(mHandler, 0, msg));
	}

	@Override
	public void gotoState(State state) {
		mCurrentState = state;
		AState next = null;
		switch (state) {
		case Beaconing:
			next = mBeaconing;
			next.start(mPreferences.getTBeac());
			break;
		case Providing:
			next = mProviding;
			next.start(mPreferences.getTPro());
			break;
		case Scanning:
			next = mScanning;
			next.start(mPreferences.getTScan());
			break;
		case Station:
			next = mStation;
			next.start(mPreferences.getTCon());
			break;
		}
	}
}
