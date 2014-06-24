package net.diogomarques.wifioppish;

import net.diogomarques.wifioppish.networking.Message;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
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
	
	public static final String METHOD_SENT = "sent";
	public static final Uri URI_SENT = Uri.parse(PROVIDER_URL + METHOD_SENT);
	public static final int URI_SENT_CODE = 2;
	
	public static final String METHOD_CUSTOM = "customsend";
	public static final Uri URI_CUSTOM = Uri.parse(PROVIDER_URL + METHOD_CUSTOM);
	public static final int URI_CUSTOM_CODE = 3;
	public static final Uri URI_CUSTOM_ID = Uri.parse(PROVIDER_URL + METHOD_CUSTOM + "/#");
	public static final int URI_CUSTOM_ID_CODE = 4;
	
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
	
	// constants - direction
	public static final String MSG_SENT = "sent";
	public static final String MSG_REC = "received";
	
	// constants - status
	public static final String OUT_WAIT = "waiting";
	public static final String OUT_NET = "sentNet";
	public static final String OUT_WS = "sentWS";

	private DBHelper dbHelper;
	static final UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER, METHOD_RECEIVED, URI_RECEIVED_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_SENT, URI_SENT_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_CUSTOM, URI_CUSTOM_CODE);
		uriMatcher.addURI(PROVIDER, METHOD_CUSTOM + "/#", URI_CUSTOM_ID_CODE);
	}

	// database declarations
	private SQLiteDatabase database;
	static final String DATABASE_NAME = "LOSTMessages";
	static final String TABLE_OUTGOING = "outgoing";
	static final String TABLE_INCOMING = "incoming";
	static final String TABLE_TOSEND = "tosend";
	static final int DATABASE_VERSION = 1;
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
			" " + COL_ADDED + " DOUBLE);";
	static final String CREATE_TABLE_TOSEND = 
			" CREATE TABLE " + TABLE_TOSEND +
			" (customMessage TEXT PRIMARY KEY);"; 

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
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(TAG,
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ". Old data will be destroyed.");

			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_INCOMING);
			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_OUTGOING);
			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_TOSEND);

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
		
		switch(uriMatcher.match(uri)) {
			case URI_CUSTOM_CODE:
			row = database.insert(TABLE_TOSEND, "", values);
			break;
			
			case URI_RECEIVED_CODE:
			row = database.insert(TABLE_INCOMING, "", values);
			break;
			
			case URI_SENT_CODE:
			row = database.insert(TABLE_OUTGOING, "", values);
			break;
		}
		
		if(row > 0) {
			Uri newUri = ContentUris.withAppendedId(uri, row);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		
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
			
			case URI_SENT_CODE:
			queryBuilder.setTables(TABLE_OUTGOING);
			break;
			
			// TODO obter mensagens outgoing dos 3 tipos (wait, sentnet, sentws)
			
			case URI_CUSTOM_CODE:
			queryBuilder.setTables(TABLE_TOSEND);
			break;
			
			// TODO obter mensagens custom com o id único
			case URI_CUSTOM_ID_CODE:
			queryBuilder.setTables(TABLE_TOSEND);
			String id = uri.getLastPathSegment();
			queryBuilder.appendWhere("rowid = " + id);
			break;
			
			default:
			Log.w(TAG, "Unknown URI to query:" + uri);
			return null;
		}
		
		Cursor cursor = queryBuilder.query(
			database, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		switch(uriMatcher.match(uri)) {
			case URI_SENT_CODE:
			database.update(TABLE_OUTGOING, values, selection, selectionArgs);
			// TODO ter em conta o id da mensagem outgoing na atualização
		}
		return 0;
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
