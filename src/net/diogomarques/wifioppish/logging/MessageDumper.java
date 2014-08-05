package net.diogomarques.wifioppish.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Environment;

import net.diogomarques.wifioppish.networking.Message;
import net.diogomarques.wifioppish.networking.MessageFormatter;

/**
 * Dumps Messages to a text file for future analysis
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class MessageDumper {
	
	private FileOutputStream fos; 
	private final String LINE_BREAK = "\r\n"; 
	
	/**
	 * Creates a new instance of this Message Dumper
	 * @param prefix Filename of the log
	 * @throws IOException If file cannot be opened
	 */
	public MessageDumper(String prefix) throws IOException {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
		Calendar c = Calendar.getInstance();
		
		String filename = prefix + formatter.format(c.getTime()) +  ".txt";
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath());
		File file = new File(dir.getAbsolutePath(),  filename);
		
		fos = new FileOutputStream(file);
	}
	
	/**
	 * Adds a Message to be written in the log
	 * @param m Message do be stored
	 * @throws IOException File inaccessible, SD card removed
	 */
	public void addMessage(Message m) throws IOException {
		fos.write(MessageFormatter.messageToCSV(m).getBytes());
		fos.write(LINE_BREAK.getBytes());
		fos.flush();
	}

}
