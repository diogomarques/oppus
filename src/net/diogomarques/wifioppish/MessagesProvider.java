package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.networking.Message;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Content Provider to access {@link Message Messages} received and sent to 
 * opportunistic network.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class MessagesProvider extends ContentProvider {
	
	private static final String TAG = "Messages Provider";
	
	// content provider
	public static final String PROVIDER = "net.diogomarques.wifioppish.MessagesProvider";
	public static final String PROVIDER_URL = "content://" + PROVIDER + "/";
	
	// methods available
	public static final String METHOD_RECEIVED = "received";
	public static final Uri URI_RECEIVED = Uri.parse(PROVIDER_URL + METHOD_RECEIVED);
	public static final int URI_RECEIVED_CODE = 1;
	public static final Uri URI_RECEIVED_ID = Uri.parse(PROVIDER_URL + METHOD_RECEIVED + "/*");
	public static final int URI_RECEIVED_ID_CODE = 5;
	
	public static final String METHOD_SENT = "sent";
	public static final Uri URI_SENT = Uri.parse(PROVIDER_URL + METHOD_SENT);
	public static final int URI_SENT_CODE = 2;
	public static final Uri URI_SENT_ID = Uri.parse(PROVIDER_URL + METHOD_SENT + "/*");
	public static final int URI_SENT_ID_CODE = 8;
	
	public static final String METHOD_CUSTOM = "customsend";
	public static final Uri URI_CUSTOM = Uri.parse(PROVIDER_URL + METHOD_CUSTOM);
	public static final int URI_CUSTOM_CODE = 3;
	public static final Uri URI_CUSTOM_ID = Uri.parse(PROVIDER_URL + METHOD_CUSTOM + "/#");
	public static final int URI_CUSTOM_ID_CODE = 4;
	
	public static final String METHOD_STATUS = "status";
	public static final Uri URI_STATUS = Uri.parse(PROVIDER_URL + METHOD_STATUS);
	public static final int URI_STATUS_CODE = 6;
	public static final Uri URI_STATUS_CUSTOM = Uri.parse(PROVIDER_URL + METHOD_STATUS + "/*");
	public static final int URI_STATUS_CUSTOM_CODE = 7;
	
	// database fields
	public static final String COL_ID = "_id";
	public static final String COL_NODE = "nodeid";
	public static final String COL_TIME = "timestamp";
	public static final String COL_MSG = "message";
	public static final String COL_LAT = "latitude";
	public static final String COL_LON = "longitude";
	public static final String COL_CONF = "llconf";
	public static final String COL_BATTERY = "battery";
	public static final String COL_STEPS = "steps";
	public static final String COL_SCREEN = "screen";
	public static final String COL_DISTANCE = "distance";
	public static final String COL_SAFE = "safe";
	public static final String COL_ADDED = "local_added";
	public static final String COL_DIRECTION = "direction";
	public static final String COL_STATUS = "status";
	public static final String COL_ORIGIN = "origin";
	public static final String COL_STATUSKEY = "statuskey";
	public static final String COL_STATUSVALUE = "statusvalue";
	
	// constants - direction
	public static final String MSG_SENT = "sent";
	public static final String MSG_REC = "received";
	
	// constants - message status
	public static final String OUT_WAIT = "waiting";
	public static final String OUT_NET = "sentNet";
	public static final String OUT_WS = "sentWS";

	private DBHelper dbHelper;
	static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER, METHOD_RECEIVED, URI_RECEIVED_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_SENT, URI_SENT_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_CUSTOM, URI_CUSTOM_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_CUSTOM + "/#", URI_CUSTOM_ID_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_RECEIVED + "/*", URI_RECEIVED_ID_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_STATUS, URI_STATUS_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_STATUS + "/*", URI_STATUS_CUSTOM_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_SENT + "/*",  URI_SENT_ID_CODE);
	}

	// database declarations
	private SQLiteDatabase database;
	static final String DATABASE_NAME = "LOSTMessages";
	static final String TABLE_OUTGOING = "outgoing";
	static final String TABLE_INCOMING = "incoming";
	static final String TABLE_TOSEND = "tosend";
	static final String TABLE_STATUS = "status";
	static final int DATABASE_VERSION = 2;
	static final String CREATE_TABLE_OUTGOING = 
			" CREATE TABLE " + TABLE_OUTGOING +
			" (" + COL_ID + " TEXT PRIMARY KEY, " + 
			" " + COL_NODE + " TEXT," +
			" " + COL_TIME + " DOUBLE," +
			" " + COL_MSG + " TEXT," +
			" " + COL_LAT + " DOUBLE," + 
			" " + COL_LON + " DOUBLE," +
			" " + COL_CONF + " INTEGER," +
			" " + COL_BATTERY + " INTEGER," + 
			" " + COL_STEPS + " INTEGER," + 
			" " + COL_SCREEN + " INTEGER," + 
			" " + COL_DISTANCE + " INTEGER," + 
			" " + COL_SAFE + " INTEGER," +
			" " + COL_ADDED + " DOUBLE," +
			" " + COL_STATUS + " TEXT );";
	static final String CREATE_TABLE_INCOMING = 
			" CREATE TABLE " + TABLE_INCOMING +
			" (" + COL_ID + " TEXT PRIMARY KEY, " + 
			" " + COL_NODE + " TEXT," +
			" " + COL_TIME + " DOUBLE," +
			" " + COL_MSG + " TEXT," +
			" " + COL_LAT + " DOUBLE," + 
			" " + COL_LON + " DOUBLE," +
			" " + COL_CONF + " INTEGER," +
			" " + COL_BATTERY + " INTEGER," + 
			" " + COL_STEPS + " INTEGER," + 
			" " + COL_SCREEN + " INTEGER," + 
			" " + COL_DISTANCE + " INTEGER," + 
			" " + COL_SAFE + " INTEGER," +
			" " + COL_ORIGIN + " TEXT," + 
			" " + COL_ADDED + " DOUBLE);";
	static final String CREATE_TABLE_TOSEND = 
			" CREATE TABLE " + TABLE_TOSEND +
			" (customMessage TEXT PRIMARY KEY);";
	static final String CREATE_TABLE_STATUS =
			" CREATE TABLE " + TABLE_STATUS + 
			" (" + COL_STATUSKEY + " TEXT," + 
			" " + COL_STATUSVALUE + " TEXT)";

	// class that creates and manages the provider's database 
	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE_INCOMING);
			db.execSQL(CREATE_TABLE_OUTGOING);
			db.execSQL(CREATE_TABLE_TOSEND);
			db.execSQL(CREATE_TABLE_STATUS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG,
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ". Old data will be destroyed.");

			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_INCOMING);
			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_OUTGOING);
			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_TOSEND);
			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_STATUS);

			onCreate(db);
		}

	}
		
	@Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new DBHelper(context);
		// permissions to be writable
		database = dbHelper.getWritableDatabase();

	    return database != null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long row = -1;
		boolean received = false;
		boolean status = false;
		
		try {
			switch(uriMatcher.match(uri)) {
				case URI_CUSTOM_CODE:
				row = database.insertOrThrow(TABLE_TOSEND, "", values);
				break;
				
				case URI_RECEIVED_CODE:
				row = database.insertOrThrow(TABLE_INCOMING, "", values);
				received = true;
				break;
				
				case URI_SENT_CODE:
				row = database.insertOrThrow(TABLE_OUTGOING, "", values);
				break;
				
				case URI_STATUS_CODE:
				try {
					row = database.insert(TABLE_STATUS, "", values);
					status = true;
				} catch(SQLException e) {
					// key already exists, fallback to update
					update(uri, values, COL_STATUSKEY + "=\"" + values.getAsString(COL_STATUSKEY) + "\"", null);
				}
				break;
			}
		} catch(SQLException e) {
			Log.w(TAG, "Tried to insert duplicate data, records not changed", e);
			row = -1;
		}
		
		if(row > 0) {
			Uri newUri;
			
			if(received) {
				String id = String.format("%s%s", values.getAsString(COL_NODE), values.getAsString(COL_TIME));
				newUri = Uri.withAppendedPath(uri, id);
			} else if(status) {
				newUri =  Uri.withAppendedPath(URI_STATUS, values.getAsString(COL_STATUSKEY));
			} else {
				newUri = ContentUris.withAppendedId(uri, row);
			}
			
			Log.d(TAG, "Generating notification for " + newUri);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		
		Log.d(TAG, "No notification for " + uri);
		return null;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		
		// select correct table based on URI
		switch(uriMatcher.match(uri)) {
			case URI_RECEIVED_CODE:
			queryBuilder.setTables(TABLE_INCOMING);
			break;
			
			case URI_RECEIVED_ID_CODE:
			queryBuilder.setTables(TABLE_INCOMING);
			String receivedId = uri.getLastPathSegment();
			queryBuilder.appendWhere( COL_ID + "=\"" + receivedId + "\"");
			break;
			
			case URI_SENT_CODE:
			queryBuilder.setTables(TABLE_OUTGOING);
			break;
			
			case URI_SENT_ID_CODE:
			queryBuilder.setTables(TABLE_OUTGOING);
			String sentId = uri.getLastPathSegment();
			queryBuilder.appendWhere("_id = " + sentId);
			break;
			
			case URI_CUSTOM_CODE:
			queryBuilder.setTables(TABLE_TOSEND);
			break;
			
			case URI_CUSTOM_ID_CODE:
			queryBuilder.setTables(TABLE_TOSEND);
			String toSendId = uri.getLastPathSegment();
			queryBuilder.appendWhere("rowid = " + toSendId);
			break;
			
			case URI_STATUS_CODE:
			queryBuilder.setTables(TABLE_STATUS);
			break;
			
			case URI_STATUS_CUSTOM_CODE:
			queryBuilder.setTables(TABLE_STATUS);
			String key = uri.getLastPathSegment();
			queryBuilder.appendWhere(COL_STATUSKEY + "=\"" + key + "\"");
			break;
			
			default:
			Log.w(TAG, "Unknown URI to query:" + uri);
			return null;
		}
		
		Cursor cursor = queryBuilder.query(
			database, projection, selection, selectionArgs, null, null, sortOrder);
		
		return cursor;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		int rows = -1;
		
		switch(uriMatcher.match(uri)) {
		
			case URI_SENT_ID_CODE:
			String id = uri.getLastPathSegment();
			selection = "_id = \"" + id + "\"";
			rows = database.update(TABLE_OUTGOING, values, selection, selectionArgs);
			if(rows > 0) {
				Log.i(TAG, "Generating notification for " + uri);
				getContext().getContentResolver().notifyChange(uri, null);
			}
			break;
		
			case URI_STATUS_CODE:
			rows = database.update(TABLE_STATUS, values, selection, selectionArgs);
			if(rows > 0) {
				Uri newUri = Uri.withAppendedPath(URI_STATUS, values.getAsString(COL_STATUSKEY));
				getContext().getContentResolver().notifyChange(newUri, null);
			}
			break;
		}
		
		return rows;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int count = 0;
		
		switch(uriMatcher.match(uri)) {
			case URI_CUSTOM_CODE: 
			count = database.delete(TABLE_TOSEND, selection, selectionArgs);
			break;
		
			case URI_CUSTOM_ID_CODE:
			String id = uri.getLastPathSegment();
			count = database.delete( TABLE_TOSEND, "rowid = " + id +
	                    (!TextUtils.isEmpty(selection) ? " AND (" +
	                    selection + ')' : ""), selectionArgs);
			break;
		}
		
		if(count > 0)
			getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
