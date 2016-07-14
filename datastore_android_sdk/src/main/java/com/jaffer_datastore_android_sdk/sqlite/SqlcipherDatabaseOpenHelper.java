/**
 * SqlcipherDatabaseOpenHelper.java
 *
 * Copyright (c) 2008-2014 Joy Aether Limited. All rights reserved.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * 
 * This unpublished material is proprietary to Joy Aether Limited.
 * All rights reserved. The methods and
 * techniques described herein are considered trade secrets
 * and/or confidential. Reproduction or distribution, in whole
 * or in part, is forbidden except by express written permission
 * of Joy Aether Limited.
 */
package com.jaffer_datastore_android_sdk.sqlite;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.Context;

import com.j256.ormlite.support.ConnectionSource;
import com.jaffer_datastore_android_sdk.sqlite.internal.SqlcipherOpenHelper;


/**
 * A wrapper class to the encrypted {@link SQLiteOpenHelper}.
 */
public class SqlcipherDatabaseOpenHelper extends DatabaseOpenHelper {
	
	/** The SQLiteOpenHelper object to manage database. */
	private final DatabaseOpenHelper openHelper;
	
	/**
	 * Construct a new instance of SQLite database open helper to manage database
	 * creation and version management.
	 * 
	 * @param helper The store helper to manage schema
	 */
	public SqlcipherDatabaseOpenHelper(Context context, SqliteStoreHelper helper) {
		super(context);
		this.openHelper = helper == null ? null : new DatabaseOpenHelper(context, helper);
	}
	
	@Override
	public SqliteStoreHelper getStoreHelper() {
		return openHelper == null ? null : openHelper.helper;
	}
	
	@Override
	public void close() {
		if (openHelper != null) {
			openHelper.close();
		}
	}
	
	@Override
	public ConnectionSource getConnectionSource() {
		if (openHelper != null) {
			return openHelper.getConnectionSource();
		}
		return null;
	}
	
	/**
	 * Specifies the password to de/encrypt the database file. This method is a call
	 * through to {@link SqlcipherOpenHelper#setKey(String)}.
	 * 
	 * @param key
	 *            The new password to de/encrypt the database file
	 * @see {@link SqlcipherOpenHelper#setKey(String)}
	 */
	public void setKey(String key) {
		if (openHelper != null) {
			openHelper.setKey(key);
		}
	}
	
	/**
	 * An internal class to wrap the underlying {@link SQLiteOpenHelper}.
	 */
	private final class DatabaseOpenHelper extends SqlcipherOpenHelper {
		
		/** The helper to class to manage schema. */
		private final SqliteStoreHelper helper;
		
		DatabaseOpenHelper(Context context, SqliteStoreHelper helper) {
			super(context, 
					helper == null ? null : helper.getDatabaseName(),
					null, 
					helper == null ? 1 : helper.getVersion(),
					helper == null ? "" : helper.getPassword());
			this.helper = helper;
		}
		
		@Override
		public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
			if (helper != null) {
				// Propagate call to the helper
				helper.onCreate(connectionSource);
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
			if (helper != null) {
				// Propagate call to the helper
				helper.onUpgrade(connectionSource, oldVersion, newVersion);
			}
		}
		
	}

}
