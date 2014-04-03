package net.diogomarques.wifioppish.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Environment;

/**
 * Simple text log to store message traces in the SD card root.
 *  
 * <p>
 * This class allows the creation of a log file inside the SD card. Each 
 * run is stored inside an independent file. The file name follows this convention:
 * <code>[LOG_PREFIX][yyyyMMdd-HHmmss][.txt]</code>
 * 
 * <p>
 * This file name convention tries to ensure the uniqueness of each run.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 *
 */
public class TextLog {

	/**
	 * Prefix of the file name
	 */
	private final static String LOG_PREFIX = "wifioppish-";	
	private FileOutputStream fos;
	
	/**
	 * Creates a new instance of the logger
	 * @throws IOException The file cannot be opened, no SD card present
	 */
	public TextLog() throws IOException {
		this(LOG_PREFIX);
	}
	
	/**
	 * Creates a new instance of the logger, with a custom prefix for the log file
	 * @param prefix Initial part of the log file name
	 * @throws IOException The file cannot be opened, no SD card present
	 */
	public TextLog(String prefix) throws IOException {		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
		Calendar c = Calendar.getInstance();

		String name = prefix + formatter.format(c.getTime()) +  ".txt";
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File (sdCard.getAbsolutePath());
		File file = new File(dir.getAbsolutePath(),  name);
		fos = new FileOutputStream(file);
	}
	
	/**
	 * Stores a line in the log file, using a custom timestamp.
	 * 
	 * <p>
	 * Each file is stored in the follow way:
	 * <code>[formatted timestamp][TAB][line][NEW_LINE]</code>
	 * 
	 * @param line Line of text to store inside file
	 * @param timestamp Time of the event. When the value -1 is passed, the current time is used
	 * @throws IOException File cannot be written (SD card full, 
	 * 			Insufficient permissions, SD card in use by other app)
	 */
	public void storeLine(String line, long timestamp) throws IOException {
		StringBuilder sb = new StringBuilder();
		Calendar c = Calendar.getInstance();
				
		if (timestamp != -1)
			c.setTimeInMillis(timestamp);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd;HHmmssSSS", Locale.US);
		sb.append(formatter.format(c.getTime()));
		sb.append(";");
		sb.append("\"" + line + "\"");
		sb.append("\r\n");
		
		fos.write(sb.toString().getBytes());
	}
	
	/**
	 * Stores a line in the log file, with the current timestamp 
	 * (from mobile device's clock)
	 * 
	 * <p>
	 * Each file is stored in the follow way:
	 * <code>[formatted timestamp][TAB][line][NEW_LINE]</code>
	 * 
	 * @param line Line of text to store inside file
	 * @throws IOException File cannot be written (SD card full, 
	 * 			Insufficient permissions, SD card in use by other app)
	 */
	public void storeLine(String line) throws IOException {
		storeLine(line, -1);
	}
	
	/**
	 * Closes the file log
	 * @throws IOException File already closed, lost link due SD card external operation
	 */
	public void close() throws IOException {
		fos.close();
	}
}
