package net.diogomarques.wifioppish;

import java.util.ArrayList;
import java.util.List;

import net.diogomarques.wifioppish.IEnvironment.State;
import net.diogomarques.wifioppish.networking.Message;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;


/**
 * Android implementation of state {@link IEnvironment.State#InternetConn}
 * 
 * @author André Rodrigues
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class StateInternetConn extends AState {
	
	private final int HTTP_OK = 200;

	public StateInternetConn(IEnvironment env) {
		super(env);
	}

	@Override
	public void start(int timeout, Context c) {
		
		Log.w("Machine State", "Internet Connected");
		
		context = c;
		environment.deliverMessage("entered Internet connected state");
		
		// TODO move endpoint to preferences
		String endpoint = "http://192.168.1.3/wifioppish-map/index.php/rest/victims";
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(endpoint);
		
		// get messages from send queue
		List<Message> messages = environment.fetchMessagesFromQueue();
		JSONArray jsonArray = new JSONArray();
		
		for(Message m : messages) {
			JSONObject json = new JSONObject();
			json.put("nodeid", m.getNodeId());
			json.put("timestamp", m.getTimestamp());
			json.put("msg", m.getMessage());
			json.put("latitude", m.getLatitude());
			json.put("longitude", m.getLongitude());
			json.put("battery", m.getBattery());
			json.put("steps", m.getSteps());
			json.put("screen", m.getScreenOn());
			json.put("distance", -1);
			json.put("safe", m.isSafe() ? 1 : 0);
			jsonArray.put(json);
		}

		String contents = jsonArray.toString();
		
		Log.w("Webservice", "About to send: " + contents);
		
		try {
			// send request to webservice
		    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		    nameValuePairs.add(new BasicNameValuePair("data", contents));
		    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		    HttpResponse response = httpclient.execute(httppost);
		    HttpEntity entity = response.getEntity();
		    String body = EntityUtils.toString(entity, "UTF-8");
		    
		    Log.w("Webservice", "Response: " + response.getStatusLine().getStatusCode());
		    Log.w("Webservice", "Response body: " + body);
		    
		    // if messages were successfully inserted, clear the queue
		    if(response.getStatusLine().getStatusCode() == HTTP_OK)
		    	environment.clearQueue();
		    
		} catch (Exception e) {}

		if (environment.getLastState() == State.Scanning) {
			environment.deliverMessage("t_i_con timeout");
			environment.gotoState(State.Beaconing);
		} else {
			environment.deliverMessage("t_i_con timeout");
			environment.gotoState(State.Scanning);
		}
	}
}
