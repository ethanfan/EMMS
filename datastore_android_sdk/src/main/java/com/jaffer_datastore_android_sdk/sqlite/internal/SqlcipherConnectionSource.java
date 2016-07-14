/**
 * SqlcipherConnectionSource.java
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
package com.jaffer_datastore_android_sdk.sqlite.internal;

import java.sql.SQLException;

import net.sqlcipher.database.SQLiteDatabase;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.SqliteAndroidDatabaseType;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.support.BaseConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
/**
 * A SqliteStore version of database connection source on Android.
 */
public class SqlcipherConnectionSource extends BaseConnectionSource implements ConnectionSource {
	
	/** The logger to use to log events. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SqlcipherConnectionSource.class);
	
	/** The helper that manages database connection. */
	private final SqlcipherOpenHelper helper;
	private final SQLiteDatabase sqliteDatabase;
	private SqlcipherDatabaseConnection connection = null;
	private volatile boolean isOpen = true;
	private final DatabaseType databaseType = new SqliteAndroidDatabaseType();
	private boolean cancelQueriesEnabled = false;

	/**
	 * Creates a new instance of {@link SqlcipherConnectionSource} with the
	 * given SQLite helper class to manage database creation and version.
	 * 
	 * @param helper
	 *          The SQLite helper to manage database create and version
	 */
	public SqlcipherConnectionSource(SqlcipherOpenHelper helper) {
		this.helper = helper;
		this.sqliteDatabase = null;
	}

	/**
	 * Creates a new instance of {@link SqlcipherConnectionSource} with the given SQLite database.
	 * 
	 * @param sqliteDatabase The SQLite database
	 */
	public SqlcipherConnectionSource(SQLiteDatabase sqliteDatabase) {
		this.helper = null;
		this.sqliteDatabase = sqliteDatabase;
	}

	@Override
	public DatabaseConnection getReadOnlyConnection() throws SQLException {
		/*
		 * We have to use the read-write connection because getWritableDatabase() can call close on
		 * getReadableDatabase() in the future. This has something to do with Android's SQLite connection management.
		 * 
		 * See android docs: http://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper.html
		 */
		return getReadWriteConnection();
	}

	@Override
	public DatabaseConnection getReadWriteConnection() throws SQLException {
		DatabaseConnection conn = getSavedConnection();
		if (conn != null) {
			return conn;
		}
		if (connection == null) {
			SQLiteDatabase db;
			if (sqliteDatabase == null) {
				try {
					db = helper.getWritableDatabase();
				} catch (android.database.SQLException e) {
					throw SqlExceptionUtil.create("Getting a writable database from helper " + helper + " failed", e);
				}
			} else {
				db = sqliteDatabase;
			}
			connection = new SqlcipherDatabaseConnection(db, true, cancelQueriesEnabled);
			LOGGER.trace("created connection {} for db {}, helper {}", connection, db, helper);
		} else {
			LOGGER.trace("{}: returning read-write connection {}, helper {}", this, connection, helper);
		}
		return connection;
	}

	@Override
	public void releaseConnection(DatabaseConnection conn) {
		// noop since connection management is handled by AndroidOS
	}

	@Override
	public boolean saveSpecialConnection(DatabaseConnection conn) throws SQLException {
		return saveSpecial(conn);
	}

	@Override
	public void clearSpecialConnection(DatabaseConnection conn) {
		clearSpecial(conn, LOGGER);
	}

	@Override
	public void close() {
		// the helper is closed so it calls close here, so this CANNOT be a call back to helper.close()
		isOpen = false;
	}

	@Override
	public void closeQuietly() {
		close();
	}

	@Override
	public DatabaseType getDatabaseType() {
		return databaseType;
	}

	@Override
	public boolean isOpen() {
		return isOpen;
	}
	
	public boolean isCancelQueriesEnabled() {
		return cancelQueriesEnabled;
	}

	/**
	 * Set to true to enable the canceling of queries.
	 * 
	 * <p>
	 * <b>NOTE:</b> This will incur a slight memory increase for all Cursor based queries -- even if cancel is not
	 * called for them.
	 * </p>
	 */
	public void setCancelQueriesEnabled(boolean cancelQueriesEnabled) {
		this.cancelQueriesEnabled = cancelQueriesEnabled;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
	}

}
