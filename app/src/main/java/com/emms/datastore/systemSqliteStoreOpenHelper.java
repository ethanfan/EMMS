package com.emms.datastore;

import android.content.Context;
import android.util.Log;

import com.datastore_android_sdk.schema.Model;
import com.datastore_android_sdk.schema.Schema;
import com.datastore_android_sdk.sqlite.SqliteStoreHelper;
import com.emms.schema.Message;
import com.j256.ormlite.support.ConnectionSource;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Defines the interfaces that a helper must implement to be notified when a
 * SQLite store is created or upgraded.
 *
 * 
 */
public class systemSqliteStoreOpenHelper extends SqliteStoreHelper {
	// define the constant of table name
	public static final String SCHEMA_MESSAGE = Schema.getAttributes(
			Message.class).getTableName();


	/**
	 * A HashMap store a key pair a schema object class.
	 */
	private final Map<String, Class<? extends Model<?, ?>>> schema = new ConcurrentHashMap<String, Class<? extends Model<?, ?>>>() {
		private static final long serialVersionUID = 9084349657357243355L;
	};

	public systemSqliteStoreOpenHelper(Context context,int version) {
		super(context);
		setTables();
	}

	@Override
	public String getDatabaseName() {
		return new File(getContext().getExternalFilesDir(null), "system.db")
					.getAbsolutePath();

	}


	@Override
	public int getVersion() {
		return 1001;
	}

	@Override
	public Class<? extends Model<?, ?>> getSchema(String name) {
		return schema.get(name);
	}

	@Override
	public void onCreate(ConnectionSource connectionSource) {
//		try {
//			//create the Application Download State table for first time open the app.
//			TableUtils.createTableIfNotExists(connectionSource, ApplicationDownloadState.class);
//		} catch (Exception ex) {
//			Log.e("onCreate", ex.getMessage());
//
//		}
//			Collection<Class<? extends Model<?, ?>>> classes = schema.values();
//			for (Class<?> clazz : classes) {
//				try {
//					TableUtils.createTableIfNotExists(connectionSource, clazz);
//				} catch (Exception ex) {
//					Log.e("onCreate", ex.getMessage());
//
//				}
//			}
		
	}

	@Override
	public void onUpgrade(ConnectionSource connectionSource, int oldVersion,
			int newVersion) {
		Log.d("old version", oldVersion + "");
		Log.d("new version", newVersion + "");

		// Do nothing
	}

	private void setTables() {
		schema.put(SCHEMA_MESSAGE, Message.class);

	}

}
