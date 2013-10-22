package net.diogomarques.wifioppish.networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.util.Log;

/**
 * Encodes/decodes Messages to/from network.
 * 
 * <p>
 * Currently, it accepts bytes stream and decodes the Java object, which will 
 * be converted to a Message.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class MessageSerializer {
	
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
			Log.e("MessageSerializer", "toMsg: Class not found (different app versions?) " + e.getMessage());
			
		} catch (IOException e) {
			Log.e("MessageSerializer", "toMsg: IOException " + e.getMessage());
		}
		
		return msg;
	}
	
	/**
	 * Converts a Message to a stream format
	 * @param msg The message to be converted
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
}
