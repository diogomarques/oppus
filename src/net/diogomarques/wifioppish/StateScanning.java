package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.networking.Message;
import android.content.Context;
import android.util.Log;

/**
 * Android implementation of state {@link IEnvironment.State#Scanning}
 * 
 * @author Diogo Marques <diogohomemmarques@gmail.com>
 */
public class StateScanning extends AState {

	/**
	 * Creates a new Scanning state
	 * @param environment Environment running the state machine
	 */
	public StateScanning(IEnvironment environment) {
		super(environment);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "Scanning");
		
		final INetworkingFacade networking = environment.getNetworkingFacade();
		
		// add auto-message to be accumulated
		Message autoMessage = environment.createTextMessage("");
		environment.pushMessageToQueue(autoMessage);
		environment.deliverMessage("entered scanning state");		
		
		networking.scanForAP(timeout, new INetworkingFacade.OnAccessPointScanListener() {

			@Override
			public void onScanTimeout() {
				environment.deliverMessage("t_scan timeout");
				
				// goes to internet state if enabled and has messages to send to webservice
				if(environment.internetState())
					environment.gotoState(State.InternetCheck);
				else
					environment.gotoState(State.Beaconing);				
			}

			@Override
			public void onAPConnection(String bSSID) {
				// calculate remote node ID
				String mac = bSSID;
				
				if(mac != null) {
					String remoteId = NodeIdentification.getNodeId(mac);
					environment.deliverMessage("connected to AP! (node ID is " + remoteId + " )");
				} else {
					environment.deliverMessage("connected to AP!");
				}
				
				environment.gotoState(State.Station);
			}
		});
	}
}
