package net.diogomarques.wifioppish.networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * Encodes/decodes Messages to/from network.
 * 
 * <p>
 * Currently, it accepts bytes stream and decodes the corresponding JSON 
 * string, which will be converted to a Message, using the GSON library.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class MessageSerializer {
	
	private static final String MSG_EOT = String.valueOf(0x0004);

	
	/**
	 * Receives a json string inside a strem of bytes from the network and converts it 
	 * to a Message.
	 * @param json The byte strea, containing the json string received from the network
	 * @return Message instance, if the Message was successfully extracted; null in 
	 * 			case of failed conversion
	 */
	public static Message networkToMessage(byte[] stream) {
		GsonBuilder b = new GsonBuilder();
		String json = getMessageIn(stream);
		Gson gson = new Gson();
		Message msg = null;
		
		try {
			msg = gson.fromJson(json, Message.class);
		} catch(JsonSyntaxException e) {
			Log.w("MessageSerializer", "Wrong convertion (to Message): " + json );
		}
		
		return msg;
	}
	
	/**
	 * Converts a Message to a Json format
	 * @param msg The message to be converted
	 * @return Json string if the Message was successfully converted; null in 
	 * 			case of failed conversion
	 */
	public static String messageToNetwork(Message msg) {
		Gson gson = new Gson();
		String json = gson.toJson(msg)/*.concat(MSG_EOT)*/; 
		Log.w("MessageSerializer", "toNet:" + json);
		return json;
	}
	
	private static String getMessageIn(byte[] buffer) {
		String msg = new String(buffer);
		int i = msg.length() - 1;
		while( msg.charAt(i) == '\0' ) {
			i--;
		}
		return msg.substring(0, i + 1);
	}
}
