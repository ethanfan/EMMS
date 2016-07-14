/**
 * SqliteStoreHelper.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.os.Environment;

import com.j256.ormlite.support.ConnectionSource;
import com.jaffer_datastore_android_sdk.datastore.Build;
import com.jaffer_datastore_android_sdk.schema.Log;
import com.jaffer_datastore_android_sdk.schema.Manifest;
import com.jaffer_datastore_android_sdk.schema.Model;
import com.jaffer_datastore_android_sdk.schema.ModelAttributes;
import com.jaffer_datastore_android_sdk.schema.Operation;
import com.jaffer_datastore_android_sdk.schema.Schema;

import org.restlet.security.User;


/**
 * Defines the interfaces that a helper must implement to be notified when a
 * SQLite store is created or upgraded.
 */
public abstract class SqliteStoreHelper {
	
	static final String TAG = SqliteStoreHelper.class.getSimpleName();
	
	/** Data schemas. */
	public static final String SCHEMA_LOG = Schema.getAttributes(Log.class).getTableName(); // NOPMD
	public static final String SCHEMA_MANIFEST = Schema.getAttributes(Manifest.class).getTableName();
	public static final String SCHEMA_OPERATION = Schema.getAttributes(Operation.class).getTableName();
	
	/** The context this helper runs in. */
	private final Context ctx;
	
	private static final Map<String, Class<? extends Model<?, ?>>> SCHEMA = new ConcurrentHashMap<String, Class<? extends Model<?, ?>>>();
	static {
		SCHEMA.put(SCHEMA_LOG, Log.class);
		SCHEMA.put(SCHEMA_MANIFEST, Manifest.class);
		SCHEMA.put(SCHEMA_OPERATION, Operation.class);
	}
	
	/**
	 * Construct a new instance of {@link SqliteStoreHelper} with in the given {@link Context}.
	 * 
	 * @param context The context this helper attaches to
	 */
	public SqliteStoreHelper(Context context) {
		ctx = context;
	}
	
	/**
	 * Retunrs the context this helper runs in.
	 * 
	 * @return The {@link Context} that this helper runs in
	 */
	public Context getContext() {
		return ctx;
	}
	
	/**
	 * Returns the name of the database file.
	 * 
	 * @return The name of the database file or {@code null} for an in-memory database
	 */
	public abstract String getDatabaseName();
	
	/**
	 * Returns the version number of the database (starting at 1).
	 * 
	 * @return The version number of the database
	 */
	public abstract int getVersion();

	/**
	 * Returns the password to access an encrypted database. Returns {@code null}
	 * if the database is not encrypted. This method returns {@code null} by
	 * default.
	 * 
	 * @return The password to access an encrypted database.
	 */
	public String getPassword() {
		return null;
	}

	/**
	 * Returns whether database operation log is enabled. If {@code true}, create,
	 * update and deleting data will be logged. Logging is disabled by default.
	 * 
	 * <p>
	 * Logging must be enabled in order for schema synchronization to function.
	 * {@link SqliteStore} queries the logs to determine records that have been
	 * created, updated or deleted locally.
	 * </p>
	 * 
	 * <p>
	 * 
	 * </p>
	 * 
	 * @return {@code ture} to enable logging, {@code false} otherwise
	 */
	public boolean isLoggingEnabled() {
		return false;
	}
	
	/**
	 * Returns the system schema class identified by the given {@code name}.
	 * 
	 * @param name The name of the system schema
	 * @return The Java class, {@code null} if the given {@code name} is not a valid system schema
	 */
	public Class<? extends Model<?, ?>> getSystemSchema(String name) {
		return SCHEMA.get(name);
	}

	/**
	 * Returns the credentials schema in the database. Returns {@code null} if the
	 * local store does not support credentials. With no credential support,
	 * offline credential authentication is disabled.
	 * 
	 * @return The credentials data model that extends {@link User}
	 */
	public Class<? extends User> getCredentialSchema() {
		return null;
	}
	
	/**
	 * Returns the schema class identified by the given resource name.
	 * 
	 * @param name The name of the resource
	 * @return The Java class, {@code null} if the given {@code name} is not known
	 */
	public abstract Class<? extends Model<?, ?>> getSchema(String name);
	
	/**
	 * Logs the given {@code action} in the datastore.
	 * 
	 * @param model The data model the action performed on
	 * @param action The action performed
	 */
	public Log getLog(Model<?, ?> model, Log.Action action) {
		if (isLoggingEnabled() && model != null && action != null) {
			ModelAttributes attrs = Schema.getAttributes(model.getClass());
			String tableName = attrs.getTableName();
			String idFieldName = attrs.getIdField();
			
			// Creates the log entry
			Object id = model.getFieldValue(idFieldName);
			return id == null ? null : new Log(tableName, action, id.toString());
		}
		return null;
	}
	
	/**
	 * Called when the store is created for the first time.
	 * 
	 * @param connectionSource The connection to the underlying database
	 */
	public abstract void onCreate(ConnectionSource connectionSource);

	/**
	 * Called when the database needs to be upgraded.
	 * 
	 * @param connectionSource
	 *          The connection to the underlying database
	 * @param oldVersion
	 *          The version of the current database so we can know what to do to
	 *          the database.
	 * @param newVersion
	 *          The version that we are upgrading the database to.
	 */
	public abstract void onUpgrade(ConnectionSource connectionSource, int oldVersion, int newVersion);
	
	/**
	 * Copies the SQLite database file to the given {@code destination}.
	 * 
	 * @param target The destination to which to copy the database file
	 */
	public void copyDatabase(File target) {
		try {
			File destination = target;
			File data = Environment.getDataDirectory();
			String packageName = getContext().getPackageName();
			File pack = new File(data, "data" + File.separator + packageName);
			File database = new File(pack, "databases" + File.separator + getDatabaseName());
			
			if (!destination.exists()) {
				File parent = null;
				if (destination.isDirectory()) {
					destination = new File(destination, getDatabaseName());
				}
				if (destination.isFile()) {
					parent = destination.getParentFile();
				}
				if (parent != null && parent.isDirectory() && !parent.exists()) {
					destination.mkdirs();
				}
				
				destination.createNewFile();
			}
			
			if (database.exists()) {
				FileInputStream fis = new FileInputStream(database);
				FileOutputStream fos = new FileOutputStream(destination);
				
				FileChannel src = fis.getChannel();
				FileChannel dst = fos.getChannel();
				
				dst.transferFrom(src, 0, src.size());
				
				src.close();
				dst.close();
				
				fis.close();
				fos.close();
			}
		} catch (Exception ex) {
			if (Build.DEBUG) {
				android.util.Log.e(TAG, "Failed to copy database file to: " + target, ex);
			}
		}
	}

}
