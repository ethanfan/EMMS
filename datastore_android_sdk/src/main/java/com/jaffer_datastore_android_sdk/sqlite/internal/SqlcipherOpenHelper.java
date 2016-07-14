/**
 * SqlcipherOpenHelper.java
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
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;

/**
 * SQLite open helper that can be extended by your application. 
 */
public abstract class SqlcipherOpenHelper extends SQLiteOpenHelper {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(SqlcipherOpenHelper.class);
	
	/** The underlying connection to the SQLite database. */
	protected SqlcipherConnectionSource connectionSource;
	
	protected boolean cancelQueriesEnabled;
	private volatile boolean isOpen = true;
	
	/** The key to de/encrypt the database file. */
	private volatile String key;

	/**
	 * Create a helper object to create, open, and/or manage a database.
	 * 
	 * @param context
	 *          Associated content from the application. This is needed to locate
	 *          the database.
	 * @param name
	 *          The file name of the database
	 * @param factory
	 *          The cursor factory to use for creating cursor objects, or
	 *          {@code null} for the default
	 * @param version
	 *          The version number of the database. This is used to determine
	 *          whether to upgrade or downgrade the database by Android
	 */
	public SqlcipherOpenHelper(Context context, String name, CursorFactory factory, int version) {
		this(context, name, factory, version, null);
	}
	
	/**
	 * Create a helper object to create, open, and/or manage a database.
	 * 
	 * @param context
	 *          Associated content from the application. This is needed to locate
	 *          the database.
	 * @param name
	 *          The file name of the database
	 * @param factory
	 *          The cursor factory to use for creating cursor objects, or
	 *          {@code null} for the default
	 * @param version
	 *          The version number of the database. This is used to determine
	 *          whether to upgrade or downgrade the database by Android
	 * @param encryptKey The key to de/encrypt the database file.
	 */
	public SqlcipherOpenHelper(Context context, String name, CursorFactory factory, int version, String encryptKey) {
		super(context, name, factory, version);
		setKey(encryptKey);
		SQLiteDatabase.loadLibs(context);
	}
	
	/**
	 * Returns the password to de/encrypt the database file.
	 * 
	 * @return The password to de/encrypt the database file
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Specifies the password to de/encrypt the database file.
	 * 
	 * @param key
	 *            The new password to de/encrypt the database file. An empty key
	 *            or {@code null} will use an empty passphrase.
	 */
	public void setKey(String key) {
		String newKey = key == null ? null : key.isEmpty() ? null : key;
		if (this.key != null && !this.key.equals(newKey)) {
			try {
				SQLiteDatabase db = getWritableDatabase();
				// Keying the database using the old key
				String[] args = { getKey() };
				db.execSQL("PRAGMA key = '" + getKey() + "'");
				
				// Changes the key to the database. Uses an empty if the given
				// new key is {@code null}. Empty key will disable encryption.
				args[0] = newKey == null ? "" : newKey;
				db.execSQL("PRAGMA rekey = '" + newKey + "'");
				db.close();
				
				// Reset the connection source
				connectionSource = null;
			} catch (Exception ex) {
				// The key should remain the same
				ex.printStackTrace();
				newKey = getKey();
			}
		}
		this.key = newKey;
	}

	/**
	 * Create and/or open a database.
	 * 
	 * @return A database object valid until {@link #getWritableDatabase()},
	 *         {@link #getWritableDatabase(String)} or {@link #close()} is called.
	 */
	public synchronized SQLiteDatabase getReadableDatabase() {
		return getReadableDatabase(getKey());
	}
	
	/**
	 * Create and/or open a database that will be used for reading and writing.
	 * 
	 * @return A read/write database object valid until {@link #close()} is called
	 */
	public synchronized SQLiteDatabase getWritableDatabase() {
		return getWritableDatabase(getKey());
	}
	
	/**
	 * What to do when your database needs to be created. Usually this entails creating the tables and loading any
	 * initial data.
	 * 
	 * <p>
	 * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
	 * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
	 * </p>
	 * 
	 * @param database
	 *            Database being created.
	 * @param connectionSource
	 *            To use get connections to the database to be created.
	 */
	public abstract void onCreate(SQLiteDatabase database, ConnectionSource connectionSource);

	/**
	 * What to do when your database needs to be updated. This could mean careful migration of old data to new data.
	 * Maybe adding or deleting database columns, etc..
	 * 
	 * <p>
	 * <b>NOTE:</b> You should use the connectionSource argument that is passed into this method call or the one
	 * returned by getConnectionSource(). If you use your own, a recursive call or other unexpected results may result.
	 * </p>
	 * 
	 * @param database
	 *            Database being upgraded.
	 * @param connectionSource
	 *            To use get connections to the database to be updated.
	 * @param oldVersion
	 *            The version of the current database so we can know what to do to the database.
	 * @param newVersion
	 *            The version that we are upgrading the database to.
	 */
	public abstract void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion);
	
	/**
	 * Returns the connection source associated with this helper.
	 */
	public ConnectionSource getConnectionSource() {
		if (!isOpen) {
			// We don't throw this exception, but log it for debugging purposes
			LOGGER.warn(new IllegalStateException(), "Getting connectionSource was called after closed");
		}
		
		if (connectionSource == null) {
			connectionSource = new SqlcipherConnectionSource(this);
		}
		return connectionSource;
	}


	/**
	 * Satisfies the {@link SQLiteOpenHelper#onCreate(SQLiteDatabase)} interface method.
	 */
	@Override
	public final void onCreate(SQLiteDatabase db) {
		ConnectionSource cs = getConnectionSource();
		
		/*
		 * The method is called by Android database helper's get-database calls when Android detects that we need to
		 * create or update the database. So we have to use the database argument and save a connection to it on the
		 * AndroidConnectionSource, otherwise it will go recursive if the subclass calls getConnectionSource().
		 */
		DatabaseConnection conn = cs.getSpecialConnection();
		boolean clearSpecial = false;
		if (conn == null) {
			conn = new SqlcipherDatabaseConnection(db, true, cancelQueriesEnabled);
			try {
				cs.saveSpecialConnection(conn);
				clearSpecial = true;
			} catch (SQLException e) {
				throw new IllegalStateException("Could not save special connection", e);
			}
		}
		try {
			onCreate(db, cs);
		} finally {
			if (clearSpecial) {
				cs.clearSpecialConnection(conn);
			}
		}
	}

	/**
	 * Satisfies the {@link SQLiteOpenHelper#onUpgrade(SQLiteDatabase, int, int)} interface method.
	 */
	@Override
	public final void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ConnectionSource cs = getConnectionSource();
		/*
		 * The method is called by Android database helper's get-database calls when Android detects that we need to
		 * create or update the database. So we have to use the database argument and save a connection to it on the
		 * AndroidConnectionSource, otherwise it will go recursive if the subclass calls getConnectionSource().
		 */
		DatabaseConnection conn = cs.getSpecialConnection();
		boolean clearSpecial = false;
		if (conn == null) {
			conn = new SqlcipherDatabaseConnection(db, true, cancelQueriesEnabled);
			try {
				cs.saveSpecialConnection(conn);
				clearSpecial = true;
			} catch (SQLException e) {
				throw new IllegalStateException("Could not save special connection", e);
			}
		}
		try {
			onUpgrade(db, cs, oldVersion, newVersion);
		} finally {
			if (clearSpecial) {
				cs.clearSpecialConnection(conn);
			}
		}
	}

	/**
	 * Close any open connections.
	 */
	@Override
	public void close() {
		super.close();
		
		connectionSource.close();
		/*
		 * We used to set connectionSource to null here but now we just set the closed flag and then log heavily if
		 * someone uses getConectionSource() after this point.
		 */
		isOpen = false;
	}

	/**
	 * Return true if the helper is still open. Once {@link #close()} is called then this will return false.
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Get a DAO for our class. This uses the {@link DaoManager} to cache the DAO for future gets.
	 * 
	 * <p>
	 * NOTE: This routing does not return Dao<T, ID> because of casting issues if we are assigning it to a custom DAO.
	 * Grumble.
	 * </p>
	 */
	@SuppressWarnings("unchecked")
	public <D extends Dao<T, ?>, T> D getDao(Class<T> clazz) throws SQLException {
		// special reflection fu is now handled internally by create dao calling the database type
		return (D) DaoManager.createDao(getConnectionSource(), clazz);
	}

	/**
	 * Get a RuntimeExceptionDao for our class. This uses the {@link DaoManager} to cache the DAO for future gets.
	 * 
	 * <p>
	 * NOTE: This routing does not return RuntimeExceptionDao<T, ID> because of casting issues if we are assigning it to
	 * a custom DAO. Grumble.
	 * </p>
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <D extends RuntimeExceptionDao<T, ?>, T> D getRuntimeExceptionDao(Class<T> clazz) {
		try {
			Dao<T, ?> dao = getDao(clazz);
			return (D) new RuntimeExceptionDao(dao);
		} catch (SQLException e) {
			throw new RuntimeException("Could not create RuntimeExcepitionDao for class " + clazz, e);
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + Integer.toHexString(super.hashCode());
	}

}
