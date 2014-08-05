package net.diogomarques.wifioppish.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Encodes/decodes {@link Message Messages} and {@link MessageGroup MessageGroups} to other formats.
 * <p>
 * It supports the encode/decode of Message and MessageGroups to/from network, encode to CSV (Comma 
 * Separated Values) and encode to {@link JSONObject}.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class MessageFormatter {
	
	/**
	 * Receives a stream of bytes from the network and converts it 
	 * to a Message.
	 * @param stream The byte stream received from the network
	 * @return Message instance, if the Message was successfully extracted; null in 
	 * 			case of failed conversion
	 */
	public static Message networkToMessage(byte[] stream) {
		Message msg = null;
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(stream));
			msg = (Message) ois.readObject();
			
		} catch (ClassNotFoundException e) {
			Log.e("MessageSerializer", "toMsg: Class not found (different app versions?)", e);
			
		} catch (IOException e) {
			Log.e("MessageSerializer", "toMsg: IOException", e);
		}
		
		return msg;
	}
	
	/**
	 * Receives a stream of bytes from the network and converts it 
	 * to a MessageGroup.
	 * @param stream The byte stream received from the network
	 * @return MessageGroup instance, if the MessageGroup was successfully extracted; null in 
	 * 			case of failed conversion
	 */
	public static MessageGroup networkToMessageGroup(byte[] stream) {
		MessageGroup msg = null;
		try {
			
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(stream));
			msg = (MessageGroup) ois.readObject();
			
		} catch (ClassNotFoundException e) {
			Log.e("MessageSerializer", "toMsgGroup: Class not found (different app versions?)", e);
			
		} catch (IOException e) {
			Log.e("MessageSerializer", "toMsgGroup: IOException", e);
		}
		
		return msg;
	}
	
	/**
	 * Converts a Message to a streamable format (byte array)
	 * @param msg The Message to be converted
	 * @return stream to send over network if Message was successfully converted; null in 
	 * 			case of failed conversion
	 */
	public static byte[] messageToNetwork(Message msg) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] buffer = null;
		
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(msg);		  
			buffer = bos.toByteArray();
			out.close();
			bos.close();
		} catch (IOException e) {
			Log.e("MessageSerializer", "toNet: IOException " + e.getMessage());
		} 

		return buffer;
	}
	
	
	/**
	 * Converts a MessageGroup to a streamable format (byte array)
	 * @param msg The MessageGroup to be converted
	 * @return stream to send over network if Message was successfully converted; null in 
	 * 			case of failed conversion
	 */
	public static byte[] messageGroupToNetwork(MessageGroup msg) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] buffer = null;
		
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(msg);		  
			buffer = bos.toByteArray();
			out.close();
			bos.close();
		} catch (IOException e) {
			Log.e("MessageSerializer", "MsgGrouptoNet: IOException", e);
		} 

		return buffer;
	}
	
	/**
	 * Converts a Message to a JSON representation in Object format.
	 * @param msg The Message to be converted
	 * @return JSONObject instance if successful; null if failed conversion
	 */
	public static JSONObject messageToJsonObject(Message msg) {
		JSONObject json = new JSONObject();
		try {
			json.put("nodeid", msg.getNodeId());
			json.put("timestamp", msg.getTimestamp());
			json.put("msg", msg.getMessage());
			json.put("latitude", msg.getLatitude());
			json.put("longitude", msg.getLongitude());
			json.put("llconf", msg.getLocationConfidence());
			json.put("battery", msg.getBattery());
			json.put("steps", msg.getSteps());
			json.put("screen", msg.getScreenOn());
			json.put("distance", -1);
			json.put("safe", msg.isSafe() ? 1 : 0);
		} catch(JSONException e) {
			return null;
		}
		return json;
	}
	
	/**
	 * Converts a Message to a CSV String.
	 * @param msg The Message to be converted
	 * @return String in CSV format; null if failed convertion
	 */
	public static String messageToCSV(Message msg) {
		StringBuilder sb = new StringBuilder(String.format(
			"\"%s\";%s;%s;%s;%s;%d;%d;%d;%d;%d",
			(msg.getMessage() == null ? "" : msg.getMessage()),
			msg.getNodeId(),
			msg.getTimestamp(),
			msg.getLatitude(),
			msg.getLongitude(),
			msg.getLocationConfidence(),
			msg.getBattery(),
			msg.getSteps(),
			msg.getScreenOn(),
			msg.isSafe() ? 1 : 0
		));
		
		return sb.toString();
	}
}
