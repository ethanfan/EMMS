/**
 * Datastore.java
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
package com.datastore_android_sdk.datastore;

import com.datastore_android_sdk.sqlite.DatabaseOpenHelper;
import com.datastore_android_sdk.sqlite.SqliteStoreOpenHelperManager;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.datastore_android_sdk.sqlite.SqliteStoreHelper;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.restlet.Client;
import org.restlet.data.Protocol;


/**
 * The centeral entry point to access the {@link Store}.
 */
public final class Datastore {

	/** The SQLite store cache. */
	private Map<Class<?>, SqliteStore> stores;
	/** The singleton instance of {@link Datastore}. */
	private static Datastore instance = null;

	/**
	 * A class that describes a datastore atomic operation.
	 */
	public static final class AtomicOperation {

		/** The field to operate on. */
		private final String field;

		/** The atomic operation.*/
		private final Operator operation;

		/** The value to atomically update the field by. */
		private final int value;

		/**
		 * An enumeration of operators that an {@link AtomicOperation} supports.
		 */
		public static enum Operator {
			INCREMENT("inc");

			/** The name of the operation. */
			private String operation;

			Operator(String operation) {
				this.operation = operation;
			}

			public String toString() {
				return operation;
			}

			public String toOperator() {
				return "[" + operation + "]";
			}

		}

		/**
		 * Creates a new instance of an {@link AtomicOperation}.
		 *
		 * @param field
		 *            the field this operation to operate against
		 * @param operation
		 *            the operation to operate against the {@code field}
		 * @param value
		 *            the value of apply to the {@code field}
		 */
		public AtomicOperation(String field, Operator operation, int value) {
			this.field = field;
			this.operation = operation;
			this.value = value;
		}

		/**
		 * Returns the field to operate on.
		 *
		 * @return The name of the field to operate on
		 */
		public String getField() {
			return field;
		}

		/**
		 * Returns the operation this operand operates.
		 *
		 * @return The operation
		 */
		public Operator getOperation() {
			return operation;
		}

		/**
		 * Returns the amount to operate on the field atomically.
		 *
		 * @return The amount to operate on the field
		 */
		public int getValue() {
			return value;
		}

		/**
		 * Returns the operand of this operation.
		 *
		 * @return the operand of this operation.
		 */
		public String getOperand() {
			return field == null ? "" : field
					+ operation == null ? "" : operation.toOperator();
		}

		@Override
		public String toString() {
			return getOperand() + "=" + value;
		}

	}

	/**
	 * Returns the singleton instance of {@link Datastore}. If this is the first
	 * time {@link Datastore} is retrieved, this method will disable cloud
	 * messaging.
	 *
	 * <p>
	 * To enable cloud messaging, you can call {@link #close()} to close all
	 *
	 * method.
	 * </p>
	 *
	 * @return The singleton instance of the {@link Datastore}
	 */
	public static Datastore getInstance() {
		if (instance == null) {
			instance = new Datastore();

			// We register a client connector extension to handle the connection
			Client client = new Client(new org.restlet.Context(), Arrays.asList(Protocol.HTTP, Protocol.HTTPS));
			client.getContext().getParameters().add("followRedirects", "true");
			client.getContext().getParameters().add("idleTimeout", "60000");
			client.getContext().getParameters().add("stopIdleTimeout", "10000");
//			Engine.getInstance().getRegisteredClients().add(new HttpClientHelper(client));
		}
		return instance;
	}

	/**
	 * Prevent this class from instantiating from outside of the package.
	 */
	private Datastore() {
	}

	/**
	 * Returns a {@link SqliteStore} that provides access to a SQLite database.
	 *
	 * @param helper
	 *          The helper object that defines the database configuration
	 * @return The {@link SqliteStore} that provides access to the given SQLite
	 *         database. Returns {@code null} if the {@code helper} is
	 *         {@code null}
	 */
	public synchronized SqliteStore getSqliteStore(SqliteStoreHelper helper) {
		return getSqliteStore(helper, Executors.newCachedThreadPool());
	}

	/**
	 * Returns a {@link SqliteStore} that provides access to a SQLite database.
	 *
	 * @param helper
	 *            The helper object that defines the database configuration
	 * @param executors
	 *            the {@link ExecutorService} to run the query tasks
	 * @return The {@link SqliteStore} that provides access to the given SQLite
	 *         database. Returns {@code null} if the {@code helper} is
	 *         {@code null}
	 */
	public synchronized SqliteStore getSqliteStore(SqliteStoreHelper helper, ExecutorService executors) {
		SqliteStore result = null;

		if (helper != null) {
			result = lookupStore(helper.getClass());
			if (result == null) {
				DatabaseOpenHelper openHelper = SqliteStoreOpenHelperManager.getHelper(helper.getContext(), helper.getClass());
				result = new SqliteStore(openHelper, executors == null ? Executors.newCachedThreadPool() : executors);
			}

			if (result != null) {
				addStoreToCache(helper.getClass(), result);
			}
		}

		return result;
	}

//	/**
//	 * Returns a {@link RestStore} that provides access to a REST datastore server.
//	 *
//	 * @param client
//	 *          The client the store uses to make requests to the REST datastore
//	 *          server
//	 * @return The {@link RestStore} object that provides API access to the given
//	 *         REST datastore server. Returns {@code null} if the {@code client} is
//	 *         {@code null}
//	 */
//	public RestStore getRestStore(RestStoreClient client) {
//		RestStore result = null;
//
//		if (client != null) {
//			// FIXME: caches a REST store?
//			result = new RestStore(client);
//		}
//
//		return result;
//	}

	/**
	 * Releases the given {@code store}. This will remove the store from cache,
	 * decrement the reference count, and closes the helper
	 *
	 * @param store The {@link Store} to release
	 */
	public synchronized void releaseStore(Store store) {
		// Releases the cached {@link SqliteStore}
		if (store instanceof SqliteStore) {
			SqliteStore sqlite = (SqliteStore) store;
			DatabaseOpenHelper helper = sqlite.getHelper();
			SqliteStoreHelper storeHelper = helper == null ? null : helper.getStoreHelper();
			if (storeHelper != null) {
				removeStoreFromCache(storeHelper.getClass());
				SqliteStoreOpenHelperManager.releaseHelper(storeHelper.getClass());
			}
		}
	}

	/**
	 * Closes all {@link SqliteStore}. This removes all {@link SqliteStore} from
	 * cache and closes the helpers
	 */
	public synchronized void close() {
		if (stores != null) {
			for (SqliteStore store : stores.values()) {
				releaseStore(store);
			}
		}
	}

	/**
	 * Lookup a {@link SqliteStore} from cache.
	 *
	 * @param helperClass
	 *          The class of the {@link SqliteStoreHelper} the store accesses
	 * @return The {@link SqliteStore} that accesses the database identified by the
	 *         given {@code helper}, {@code null} if the store is not found in  cache
	 */
	private SqliteStore lookupStore(Class<? extends SqliteStoreHelper> helperClass) {
		if (stores == null) {
			return null;
		}

		return stores.get(helperClass);
	}

	/**
	 * Adds the given {@code store} to cache.
	 *
	 * @param helperClass The class of the {@link SqliteStoreHelper} the store accesses
	 * @param store The {@link SqliteStore} to cache
	 */
	private void addStoreToCache(Class<? extends SqliteStoreHelper> helperClass, SqliteStore store) {
		if (helperClass != null && store != null) {
			if (stores == null) {
				stores = new ConcurrentHashMap<Class<?>, SqliteStore>();
			}
			stores.put(helperClass, store);
		}
	}

	/**
	 * Removes cached instance of the store.
	 *
	 * @param helperClass The helper class that provided access to the database
	 */
	private void removeStoreFromCache(Class<? extends SqliteStoreHelper> helperClass) {
		if (stores != null && helperClass != null) {
			stores.remove(helperClass);
		}
	}

}
