package net.diogomarques.wifioppish;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import net.diogomarques.wifioppish.logging.MessageDumper;
import net.diogomarques.wifioppish.sensors.SensorGroup;
import net.diogomarques.wifioppish.sensors.SensorGroup.GroupKey;
import net.diogomarques.wifioppish.structs.ConcurrentForwardingQueue;
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
	private StateInternetCheck mInternet;
	private StateInternetConn mConnected;
	
	private State mCurrentState;
	
	//last state, used to determine the next state after the internet state
	private State lastState;

	
	private State nextState;
	private Semaphore semNextState;
	private Context context;
	
	private ConcurrentForwardingQueue mQueue;
	
	private String myNodeID;
	
	// stats
	private int totalReceived;
	private int totalSent;
	
	private MessageDumper dumper;
	
	// duplicates prevention
	private List<Integer> duplicates;
	
	private boolean victimSafe;
	
	private SensorGroup sensorGroup;

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
			StateScanning scanning, StateStation station, StateInternetCheck internet, StateInternetConn sconnect) {
		super();
		this.mHandler = mHandler;
		this.mNetworkingFacade = networkingFacade;
		this.mPreferences = preferences;
		this.mBeaconing = beaconing;
		this.mProviding = providing;
		this.mScanning = scanning;
		this.mStation = station;
		this.mInternet = internet;
		this.mConnected = sconnect;
		
		
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
		StateInternetCheck internet = new StateInternetCheck(environment);
		StateInternetConn sconnect = new StateInternetConn(environment);
		
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
		environment.mInternet = internet;
		environment.mConnected = sconnect;
		
		// allow one state transition (to the first one)
		environment.semNextState = new Semaphore(1);
		// allowing the gathering of shared preferences
		environment.context = c;
		// save the node ID
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
		String nodeID = sharedPref.getString("nodeID", "unknown");
		environment.myNodeID = nodeID;
		Log.w("NodeID", "My node id is: " + environment.myNodeID);
		// start forwarding sending queue
		environment.mQueue = new ConcurrentForwardingQueue();
		// stats 
		environment.totalReceived = environment.totalSent = 0;
		// message dumper
		try {
			environment.dumper = new MessageDumper("msg-dump");
		} catch (IOException e) {
			Log.e("AndroidEnvironment", "Cannot start Dumper: " + e.getMessage());
		}
		// duplicate hashes
		environment.duplicates = new ArrayList<Integer>();
		
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
			
			if(mCurrentState!=null && mCurrentState != State.InternetCheck &&  mCurrentState != State.InternetConn)
				lastState=mCurrentState;
			
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
			case InternetCheck:
				next = mInternet;
				timeout = mPreferences.getTInt();
				break;
			case InternetConn:
				next = mConnected;
				timeout = mPreferences.getTInt();
				break;
			}
			
			synchronized(next) {
				next.start(timeout, context);
			}
		}
		
	}

	@Override
	public void pushMessageToQueue(net.diogomarques.wifioppish.networking.Message m) {
		// duplicate check
		Integer msgHashcode = Integer.valueOf(m.hashCode());
		if( !duplicates.contains(msgHashcode) ) {
			mQueue.add(m);
			Log.w("SendingQueue", "New message to queue: " + m + " (received by " + myNodeID + ")");
			Log.w("SendingQueue", "Added message to queue, " + mQueue.size() + " messages remaining");
			duplicates.add(msgHashcode);
		}
	}
	
	@Override
	public List<net.diogomarques.wifioppish.networking.Message> fetchMessagesFromQueue() {
		ArrayList<net.diogomarques.wifioppish.networking.Message> messages =
				new ArrayList<net.diogomarques.wifioppish.networking.Message>();
		
		for(net.diogomarques.wifioppish.networking.Message m : mQueue)
			messages.add(m);
		
		return messages;
	}

	@Override
	public boolean hasMessages() {
		return !mQueue.isEmpty();
	}
	
	@Override
	public void clearQueue() {
		mQueue.clear();
		Log.w("SendingQueue", "Queue cleared");
	}
	
	@Override
	public boolean removeFromQueue(net.diogomarques.wifioppish.networking.Message msg) {
		boolean removed = mQueue.remove(msg);
		if(removed)
			Log.w("SendingQueue", "Removed message from queue, " + mQueue.size() + " messages remaining");
			
		return removed;
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

	@Override
	public void storeReceivedMessage(
			net.diogomarques.wifioppish.networking.Message m) {
		try {
			dumper.addMessage(m);
		} catch (IOException e) {
			Log.e("AndroidEnvironment", "Cannot store message into Dumper: " + e.getMessage());
		}
	}

	@Override
	public net.diogomarques.wifioppish.networking.Message createTextMessage(
			String contents) {
		double[] location = getMyLocation();
		String nodeID = getMyNodeId();
		
		net.diogomarques.wifioppish.networking.Message newMsg =
			new net.diogomarques.wifioppish.networking.Message(
				nodeID, System.currentTimeMillis(), location, contents);
		
		// set other attributes
		newMsg.setSafe(victimSafe);
		
		if(sensorGroup != null) {
			Integer battery = (Integer) sensorGroup.getSensorCurrentValue(GroupKey.Battery);
			if(battery != null)
				newMsg.setBattery(battery);
			
			Integer steps = (Integer) sensorGroup.getSensorCurrentValue(GroupKey.Steps);
			if(steps != null)
				newMsg.setSteps(steps);
			
			Integer screen = (Integer) sensorGroup.getSensorCurrentValue(GroupKey.ScreenOn);
			if(screen != null)
				newMsg.setScreenOn(screen);
		}
		
		return newMsg;
	}

	@Override
	public void deliverCustomMessage(Object object, int code) {
		mHandler.sendMessage(Message.obtain(mHandler, code, object));
	}
	
	@Override
	public void markVictimAsSafe(boolean safe) {
		victimSafe = safe;
	}
	@Override
	public State getLastState() {
		return lastState;
	}
	@Override
	public boolean internetState(){
		return  mPreferences.checkInternetMode();
	}

	@Override
	public SensorGroup getSensorGroup() {
		if(sensorGroup == null)
			sensorGroup = new SensorGroup();
		
		return sensorGroup;
	}
	
}
