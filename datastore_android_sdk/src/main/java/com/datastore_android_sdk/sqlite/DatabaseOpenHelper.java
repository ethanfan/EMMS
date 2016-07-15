/**
 * DatabaseOpenHelper.java
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
package com.datastore_android_sdk.sqlite;

import android.content.Context;

import com.j256.ormlite.support.ConnectionSource;

/**
 * Defines the interfaces a database helper must implement.
 */
public abstract class DatabaseOpenHelper {
	
	/** The context the help runs in. */
	private final Context context;
	
	public DatabaseOpenHelper(Context context) {
		this.context = context;
	}
	
	/**
	 * Returns the context this helper runs in .
	 * 
	 * @return The context that this helper runs in
	 */
	public Context getContext() {
		return context;
	}
	
	/**
	 * Returns the helper that manages database creation and upgrade .
	 * 
	 * @return The helper that manages database creation and upgrade
	 */
	public abstract SqliteStoreHelper getStoreHelper();
	
	/**
	 * Close any open database object.
	 */
	public abstract void close();

	/**
	 * Returns the connection source associated with this helper.
	 * 
	 * @return The connection source associated with this helper or {@code null}
	 *         if there is no database defined
	 */
	public abstract ConnectionSource getConnectionSource();

}
