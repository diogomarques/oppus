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
import android.util.Log;

/**
 * Content Provider to access {@link Message Messages} received and set to 
 * opportunistic network.
 * 
 * @author André Silva <asilva@lasige.di.fc.ul.pt>
 */
public class MessagesProvider extends ContentProvider {
	
	private static final String TAG = "Messages Provider";
	
	// content provider
	public static final String PROVIDER = "net.diogomarques.wifioppish.MessagesProvider";
	public static final String PROVIDER_URL = "content://" + PROVIDER + "/";
	public static final String METHOD_RECEIVED = "received";
	public static final String METHOD_SENT = "sent";
	public static final String METHOD_STORE = "store";
	public static final Uri URI_RECEIVED = Uri.parse(PROVIDER_URL + METHOD_RECEIVED);
	public static final int URI_RECEIVED_CODE = 1;
	public static final Uri URI_SENT = Uri.parse(PROVIDER_URL + METHOD_SENT);
	public static final int URI_SENT_CODE = 2;
	public static final Uri URI_STORE = Uri.parse(PROVIDER_URL + METHOD_STORE);
	public static final int URI_STORE_CODE = 3;
	
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
	
	// constants
	public static final String MSG_SENT = "sent";
	public static final String MSG_REC = "received";
	
	
	private DBHelper dbHelper;
	static final UriMatcher uriMatcher;
	   static{
	      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	      uriMatcher.addURI(PROVIDER, METHOD_RECEIVED, URI_RECEIVED_CODE);
	      uriMatcher.addURI(PROVIDER, METHOD_SENT, URI_SENT_CODE);
	      uriMatcher.addURI(PROVIDER, METHOD_STORE, URI_STORE_CODE);
	   }
	   
	   // database declarations
	   private SQLiteDatabase database;
	   static final String DATABASE_NAME = "LOSTMessages";
	   static final String TABLE_NAME = "messages";
	   static final int DATABASE_VERSION = 1;
	   static final String CREATE_TABLE = 
	      " CREATE TABLE " + TABLE_NAME +
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
	      " " + COL_DIRECTION + " TEXT );";
	   
		// class that creates and manages the provider's database 
		private static class DBHelper extends SQLiteOpenHelper {

			public DBHelper(Context context) {
				super(context, DATABASE_NAME, null, DATABASE_VERSION);
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				 db.execSQL(CREATE_TABLE);
			}
			
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
				Log.i(TAG,
				        "Upgrading database from version " + oldVersion + " to "
			        + newVersion + ". Old data will be destroyed.");
			db.execSQL("DROP TABLE IF EXISTS " +  TABLE_NAME);
			    onCreate(db);
			}
			
	   }
	
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long row = database.insert(TABLE_NAME, "", values);
		
		if(row > 0) {
			Uri newUri = ContentUris.withAppendedId(URI_STORE, row);
			getContext().getContentResolver().notifyChange(newUri, null);
			return newUri;
		}
		
		return null;
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
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(TABLE_NAME);
		
		// select correct data based on URI
		switch(uriMatcher.match(uri)) {
			case URI_RECEIVED_CODE:
			queryBuilder.appendWhere(COL_DIRECTION + "='" + MSG_REC + "'");
			break;
			
			case URI_SENT_CODE:
			queryBuilder.appendWhere(COL_DIRECTION + "='" + MSG_SENT + "'");
			break;
			
			default:
			Log.w(TAG, "Unknown URI:" + uri);
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		Cursor cursor = queryBuilder.query(
			database, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		
		return cursor;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
