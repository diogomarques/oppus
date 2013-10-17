package net.diogomarques.wifioppish;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

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
	
	private State nextState;
	private Semaphore semNextState;
	private Context context;
	
	private ConcurrentForwardingQueue mQueue;
	
	private String myNodeID;
	
	// stats
	private int totalReceived;
	private int totalSent;
	
	/**
	 * Period to look for messages to forward
	 */
	private final int FORWARD_PERIOD = 1000;

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
	private AndroidEnvironment() { }

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
		// allow on state transition (to the first one)
		environment.semNextState = new Semaphore(1);
		// allowing the gathering of shared preferences
		environment.context = c;
		// save the node ID
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
		String nodeID = sharedPref.getString("nodeID", "unknown");
		environment.myNodeID = nodeID;
		Log.w("NodeID", "My node id is: " + environment.myNodeID);
		// start forwarding task
		environment.mQueue = new ConcurrentForwardingQueue();
		environment.forwardMessages();
		// stats 
		environment.totalReceived = environment.totalSent = 0; 
		
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
		mHandler.sendMessage(Message.obtain(mHandler, MainActivity.ConsoleHandler.LOG_MSG, msg));
	}

	@Override
	public void gotoState(State state) {
		semNextState.release();
		nextState = state;
		mHandler.sendMessage(Message.obtain(mHandler, MainActivity.ConsoleHandler.ROLE, state.toString()));
	}

	@Override
	public void startStateLoop(State first) {
		nextState = first;
		mHandler.sendMessage(Message.obtain(mHandler, MainActivity.ConsoleHandler.ROLE, first.toString()));
		
		while (true) {
			
			try {
				semNextState.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			mCurrentState = nextState;
			AState next = null;
			int timeout = 0;
			switch (nextState) {
			case Beaconing:
				next = mBeaconing;
				timeout = mPreferences.getTBeac();
				break;
			case Providing:
				next = mProviding;
				timeout = mPreferences.getTPro();
				break;
			case Scanning:
				next = mScanning;
				timeout = mPreferences.getTScan();
				break;
			case Station:
				next = mStation;
				timeout = mPreferences.getTCon();
				break;
			}
			
			synchronized(next) {
				next.start(timeout, context);
			}
		}
		
	}

	@Override
	public void pushMessage(net.diogomarques.wifioppish.networking.Message m) {
		// don't forward messages sent by this node
		if(!m.isNodeinTrace(myNodeID)) {
			mQueue.add(m);
			Log.w("ForwardingQueue", "New message to queue: " + m + " (received by " + myNodeID + ")");
		}
	}

	@Override
	public boolean hasMessages() {
		return !mQueue.isEmpty();
	}
	
	private void forwardMessages() {
		TimerTask job = new TimerTask() {
			
			@Override
			public void run() {
				if(!mQueue.isEmpty()) {
					net.diogomarques.wifioppish.networking.Message m;
					
					while( (m = mQueue.poll()) != null ) {
						m.addTraceNode(myNodeID, System.currentTimeMillis(), getMyLocation());
						deliverMessage("Forwarding message from " + m.getAuthor());
						mNetworkingFacade.send(m, null);
					}
				}
			}
		};
		
		
		Timer timer = new Timer();
		timer.schedule(job, 1000, FORWARD_PERIOD);
	}

	@Override
	public void updateStats(int sent, int received) {
		totalSent += sent;
		totalReceived += received;
		
		Message stats = Message.obtain(mHandler, MainActivity.ConsoleHandler.MSG_COUNT);
		Bundle b = new Bundle();
		b.putIntArray("stats", new int[] { totalSent, totalReceived });
		stats.setData(b);
		mHandler.sendMessage(stats);
	}

	@Override
	public String getMyNodeId() {
		return myNodeID;
	}

	@Override
	public double[] getMyLocation() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		double lat = Double.parseDouble(sharedPref.getString("gps.lastLatitude", "0"));
		double lon = Double.parseDouble(sharedPref.getString("gps.lastLongitude", "0"));
		
		return new double[] { lat, lon };
	}
}
