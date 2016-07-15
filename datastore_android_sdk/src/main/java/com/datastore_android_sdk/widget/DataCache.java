/**
 * DataCache.java
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
package com.datastore_android_sdk.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Handler;
import android.util.Log;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.schema.Query;
import com.google.common.collect.Range;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.Build;
import com.datastore_android_sdk.datastore.Store;

/**
 * This class caches {@link Object} in memory. This cache is backed by a {@link HashMap}.
 */
public class DataCache {
	
	/**
	 * Interface definition for a callback to be invoked when a given range is cached.
	 */
	public interface OnCacheListener {
		
		/**
		 * Callback method to be invoked when the given range of data starts caching 
		 * 
		 * @param range the range of data to be cached
		 */
		void onDataStartCaching(Range<Long> range);
		
		/**
		 * Callback method to be invoked when the given range of data is cached
		 * 
		 * @param range the range of data cached
		 */
		void onDataFinishedCaching(Range<Long> range);
	}
	
	private static final String TAG = DataCache.class.getSimpleName();
	
	/** The default chunk size of the cache. */
	public static final long DEFAULT_CACHE_CHUNK_SIZE = 20;
	
	/** The default number of simultaneous caching requests. */
	public static final int DEFAULT_THREAD_POOL_SIZE = 5;
	
	/** The cached {@link Object}. */
	private final Map<Long, DataElement> cache;
	
	/** The cached size of this cache. */
	private Long cachedCount;
	private ListenableFuture<Long> countFuture = null;
	
	/** The cache chunk size. */
	private long chunkSize;
	
	/** The underlying data store. */
	private Store store;
	
	/** The schema to retrieve data from in the {@link Store}. */
	private String schema;
	
	/** The query to perform to retrieve the data. */
	private Query query;
	
	/** The synchronization lock. */
	private final Object lock;
	
	/** A {@link Map} of pending cache requests. */
	private final Map<Range<Long>, List<RequestFuture<Long, DataElement>>> cachingRanges;
	
	/** A value to decide need support offset */
	private boolean offset;
	
	/** The listener to invoke when cache is started/finished. */
	private volatile OnCacheListener listener;
	
	/** The hander to invoke callbacks. */
	private final Handler handler = new Handler();
	
	/** 
	 * Creates a new instance of {@link DataCache} with the default chunk size.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 */
	public DataCache(Store store, String schema) {
		this(store, schema, null);
	}
	
	/** 
	 * Creates a new instance of {@link DataCache} with the default chunk size.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 * @param query The query to perform against the {@code store}
	 */
	public DataCache(Store store, String schema, Query query) {
		this(store, schema, query, DEFAULT_CACHE_CHUNK_SIZE);
	}
	
	/** 
	 * Creates a new instance of {@link DataCache} with the default chunk size.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 * @param query The query to perform against the {@code store}
	 * @param offset The value to decide need support offset
	 */
	public DataCache(Store store, String schema, Query query, boolean offset) {
		this(store, schema, query, DEFAULT_CACHE_CHUNK_SIZE, offset);
	}
	
	/**
	 * Creates a new instance of {@link DataCache} with the given chunk size.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 * @param query The query to perform against the {@code store}
	 * @param chunkSize The chunk size of the cache
	 */
	public DataCache(Store store, String schema, Query query, long chunkSize) {
		this(store, schema, query, chunkSize, true);
	}
	
	/**
	 * Creates a new instance of {@link DataCache} with the given chunk size.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 * @param query The query to perform against the {@code store}
	 * @param chunkSize The chunk size of the cache
	 * @param offset The value to decide need support offset
	 */
	public DataCache(Store store, String schema, Query query, long chunkSize, boolean offset) {
		setStore(store);
		setSchema(schema);
		setQuery(query);
		setChunkSize(chunkSize);
		enableOffset(offset);
		
		lock = new Object();
		cache = new ConcurrentHashMap<Long, DataElement>(); 
		cachingRanges = new ConcurrentHashMap<Range<Long>, List<RequestFuture<Long, DataElement>>>();
	}
	
	/**
	 * Specifies the store to retrieve data from.
	 * 
	 * @param store The data store
	 */
	public synchronized void setStore(Store store) {
		clear();
		this.store = store;
	}
	
	/**
	 * Returns the data store to retrieve data from.
	 * 
	 * @return The data store
	 */
	public Store getStore() {
		return store;
	}
	
	/**
	 * Specifies a new callback to be invoked when data is cached 
	 * 
	 * @param cacheListener the listener to be invoked when data is cached
	 */
	public void setOnCacheListener(OnCacheListener cacheListener) {
		listener = cacheListener;
	}
	
	/**
	 * Returns the callback to be invoked when data is cached.
	 * 
	 * @return the callback to be invoked when data is cached
	 */
	protected OnCacheListener getCacheListener() {
		return listener;
	}
	
	/**
	 * Specifies the schema to retrieve data from.
	 * 
	 * @param schema The name of the schema
	 */
	public synchronized void setSchema(String schema) {
		clear();
		this.schema = schema;
	}
	
	/**
	 * Returns the name of the schema to retrieve data from in the {@link Store}.
	 * 
	 * @return The name of the schema
	 */
	public String getSchema() {
		return schema;
	}
	
	/**
	 * Specifies the {@link Query} to perform against the {@link Store}.
	 * 
	 * @param query The query to perform
	 */
	public synchronized void setQuery(Query query) {
		clear();
		this.query = query;
	}
	
	/**
	 * Returns the {@link Query} to perform against the {@link Store}.
	 * 
	 * @return The query to perform
	 */
	public Query getQuery() {
		return query;
	}
	
	/**
	 * Returns a the {@link Query} if one is set by calling
	 * {@link #setQuery(Query)}. Returns a new {@link Query} if no query is
	 * provided
	 * 
	 * @return The promised {@link Query}
	 */
	protected Query getPromisedQuery() {
		if (query == null) {
			return new Query();
		}
		return query;
	}
	
	/**
	 * Specifies the chunk size of the cache.
	 * 
	 * @param chunkSize The chunk size of the cache
	 */
	public void setChunkSize(long chunkSize) {
		if (chunkSize > 0) {			
			this.chunkSize = chunkSize;
		}
	}
	
	/**
	 * Returns the chunk size of the cache.
	 * 
	 * @return The chunk size of this cache
	 */
	public long getChunkSize() {
		return chunkSize;
	}
	
	/**
	 * Clears the cache.
	 */
	public void clear() {
		// Clears the cached values
		cachedCount = null;
		if (cache != null) {			
			cache.clear();
		}
		
		// Clears any pending caching request
		if (cachingRanges != null) {
			for (List<RequestFuture<Long, DataElement>> range : cachingRanges.values()) {
				for (RequestFuture<Long, DataElement> request : range) {
					request.cancel(true);
				}
			}
			cachingRanges.clear();
		}
	}
	
	/**
	 * Determines whether the item identified by {@code identity} is in the
	 * process of cached.
	 * 
	 * @param identity
	 *            The identifier of the item
	 * @return The {@link Range} that the item is caching in, {@code null} if
	 *         the item is not in the process of being cached
	 */
	protected Range<Long> getCachingRange(Long identity) {
		if (identity != null) {			
			for (Range<Long> range : cachingRanges.keySet()) {
				if (range.contains(identity)) {
					return range;
				}
			}
		}
		return null;
	}
	
	/**
	 * Adds the given {@code future} into the pending requests.
	 * 
	 * @param range The {@link Range} requested
	 * @param future The pending request
	 */
	protected void addPendingRequest(Range<Long> range, RequestFuture<Long, DataElement> future) {
		if (range != null && future != null) {			
			List<RequestFuture<Long, DataElement>> pendings = cachingRanges.get(range);
			if (pendings == null) {
				pendings = new ArrayList<RequestFuture<Long, DataElement>>();
				cachingRanges.put(range, pendings);
			}
			
			synchronized (lock) {				
				if (!pendings.contains(future)) {
					pendings.add(future);
				}
			}
		}
	}
	
	/**
	 * Returns the pending request identified by the given identifier 
	 * 
	 * @param identity the identity of the pending request (i.e. the identity of the data requested)
	 * @return the pending request, {@code null} if there is no pending request identified by the given identifier
	 */
	protected RequestFuture<Long, DataElement> getPendingRequest(Long identity) {
		Range<Long> range = getCachingRange(identity);
		if (range != null) {
			List<RequestFuture<Long, DataElement>> pendings = cachingRanges.get(range);
			if (pendings != null) {
				synchronized (lock) {
					for (RequestFuture<Long, DataElement> future : pendings) {
						if (future.id.equals(identity)) {
							return future;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns the number of items represented by the {@link Query}.
	 * 
	 * @return The number of items represented by the {@link Query}
	 */
	public ListenableFuture<Long> getCount() {
		if (cachedCount != null) {
			return Futures.immediateFuture(cachedCount);
		} else if (countFuture != null) {
			return countFuture;
		} else {			
			if (getStore() != null && getSchema() != null) {
				final SettableFuture<Long> future = SettableFuture.create();
				getStore().count(getQuery(), schema, new StoreCallback() {
					
					@Override
					public void success(DataElement element, String resource) {
						if (element != null && element.isPrimitive()) {
							cachedCount = element.asPrimitiveElement().valueAsLong();
							future.set(cachedCount);
						} else {
							future.setException(new DatastoreException("Failed to determine the size of cache."));
						}
						countFuture = null;
					}
					
					@Override
					public void failure(DatastoreException ex, String resource) {
						future.setException(ex);
						countFuture = null;
					}
					
				});
				countFuture = future;
				return future;
			} else {
				return Futures.immediateFailedFuture(new DatastoreException("Failed to determine the size of cache. No store or schema specified"));
			}
		}
	}
	
	/**
	 * Returns whether or not the data item identified by the given {@code identity} is cached 
	 * 
	 * @param identity the identity of the object
	 * @return {@code true} if the data item is already cached, {@code false} otherwise
	 */
	public boolean isAvailable(Long identity) {
		return cache.get(identity) != null;
	}
	
	/**
	 * Returns whether or not the given range of data is being loaded into cache
	 * 
	 * @param range the range of data to examine
	 * @return {@code true} if the given range of data is being loaded into cache, {@code false} otherwise
	 */
	public boolean isLoading(Range<Long> range) {
		if (range != null) {			
			for (Range<Long> cachingRange : cachingRanges.keySet()) {
				if (cachingRange.isConnected(range)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the data object at the given position.
	 * 
	 * @param identity
	 *            The identity of the object
	 * @return The data object at the given {@code position}, {@code null} if
	 *         the position exceeded the size of the cache
	 */
	public ListenableFuture<DataElement> getData(Long identity) {
		DataElement value = cache.get(identity);
		if (value == null) {
			if (getStore() != null && getSchema() != null) {
				// Determines whether or not the requested value is in the
				// process of caching. We submit a task to retrieve cache only
				// if the value is not cached and is not in the process of being
				// cached
				Range<Long> cachingRange = getCachingRange(identity);
				if (cachingRange == null) {
					final RequestFuture<Long, DataElement> future = new RequestFuture<Long, DataElement>(identity);
					cachingRange = Range.closedOpen(identity, identity + getChunkSize());
					addPendingRequest(cachingRange, future);
					
					Query q = getPromisedQuery().limitResultsTo(getChunkSize());
					if (hasOffset()) {
						q.offsetResultsBy(cachingRange.lowerEndpoint());
					}
					if (Build.DEBUG) {
						Log.i(TAG, "Issuing call to retrieve data for: " + cachingRange);
					}
					
					final Range<Long> range = cachingRange;
					
					// Invoke listener
					if (getCacheListener() != null) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								getCacheListener().onDataStartCaching(range);								
							}
							
						});
					}
					
					getStore().performQuery(q, schema, new StoreCallback() {
						
						@Override
						public void success(DataElement element, String resource) {
							if (Build.DEBUG) {
								Log.i(TAG, "Received result for: " + range);
							}
							List<RequestFuture<Long, DataElement>> futures = cachingRanges.get(range);
							
							if (element != null) {
								if (element.isArray()) {
									ArrayElement array = element.asArrayElement();
									
									// Cache the values returned
									long index = range.lowerEndpoint();
									for (int i = 0; i < array.size(); i++) {
										cache.put(index, array.get(i));
										index ++;
									}
									
									// Invokes callback that data is cached
									if (getCacheListener() != null) {
										handler.post(new Runnable() {

											@Override
											public void run() {
												getCacheListener().onDataFinishedCaching(range);
											}
											
										});
									}
									
									// Informs the ListenableFutures the cached values are available										
									if (futures != null) {										
										synchronized (lock) {											
											for (RequestFuture<Long, DataElement> future : futures) {
												future.set(cache.get(future.getId()));
											}
										}
									}
								} else {
									// Caches the value returned
									cache.put(range.lowerEndpoint(), element);
									
									// Invokes callback that data is cached
									if (getCacheListener() != null) {
										handler.post(new Runnable() {

											@Override
											public void run() {
												getCacheListener().onDataFinishedCaching(range);
											}
											
										});
									}
									
									// Informs ListenableFutures the cached value is available
									if (futures != null) {
										for (RequestFuture<Long, DataElement> future : futures) {
											if (future.getId() == range.lowerEndpoint()) {												
												future.set(element);
											} else {
												future.setException(new DatastoreException("The requested item is not available"));
											}
										}
									}
								}
								
								// Removes the range being cached
								cachingRanges.remove(range);
								return;
							}
							
							if (futures != null) {
								// Informs the ListenableFutures there was error									
								for (RequestFuture<Long, DataElement> future : futures) {									
									future.setException(new DatastoreException("Unexpected value returned from query"));
								}
							}
						}
						
						@Override
						public void failure(DatastoreException ex, String resource) {
							if (Build.DEBUG) {
								Log.e(TAG, ex.getMessage(), ex);
							}
							
							List<RequestFuture<Long, DataElement>> futures = cachingRanges.get(range);
							if (futures != null) {
								// Informs the ListenableFutures the cached values are available										
								for (RequestFuture<Long, DataElement> future : futures) {
									future.setException(ex);
								}
							}
						}
						
					});
					
					return future;
				} else {
					RequestFuture<Long, DataElement> result = getPendingRequest(identity);
					if (result == null) {
						result = new RequestFuture<Long, DataElement>(identity);
						addPendingRequest(cachingRange, result);
					}
					return result;
				}
			} else {
				// Invalid parameters in cache settings, we return a failed
				// future immediately
				return Futures.immediateFailedFuture(new DatastoreException("Invalid parameter in cache"));
			}
		} else {
			// The value is cached, we return a future that executes immediately
			return Futures.immediateFuture(value);
		}
	}
	
	public boolean hasOffset() {
		return offset;
	}

	public void enableOffset(boolean offset) {
		this.offset = offset;
	}

	/**
	 * A {@link ListenableFuture} whose result may be set by a
	 * {@link #set(Object)} or {@link #setException(Throwable)} call. It may
	 * also be cancelled.
	 * 
	 * @param <ID> The identity of the requested value
	 * @param <V> The value requested
	 */
	private class RequestFuture<ID, V> extends AbstractFuture<V> {
		
		private final ID id;
		
		/**
		 * Creates a new instance of {@link RequestFuture} with the given {@code id}.
		 * 
		 * @param id The identity of the value requested
		 */
		public RequestFuture(ID id) {
			this.id = id;
		}
		
		public ID getId() {
			return id;
		}
		
		/**
		 * Sets the value of this future. This method will return {@code true}
		 * if the value was successfully set, or {@code false} if the future has
		 * already been set or cancelled.
		 * 
		 * @param value
		 *            the value the future should hold.
		 * @return true if the value was successfully set.
		 */
		@Override
		public boolean set(V value) { // NOPMD
			return super.set(value);
		}
		
		/**
		 * Sets the future to having failed with the given exception. This
		 * exception will be wrapped in an {@code ExecutionException} and thrown
		 * from the {@code get} methods. This method will return {@code true} if
		 * the exception was successfully set, or {@code false} if the future
		 * has already been set or cancelled.
		 * 
		 * @param throwable
		 *            the exception the future should hold.
		 * @return true if the exception was successfully set.
		 */
		@Override
		public boolean setException(Throwable throwable) { // NOPMD
			return super.setException(throwable);
		}
		
		@Override
		public int hashCode() {
			return getId() == null ? super.hashCode() : getId().hashCode();
		}
		
		/**
		 * Returns {@code true} if {@code object} is a {@link RequestFuture}.
		 * requesting the same identity as this {@link RequestFuture}
		 */
		@Override
		public boolean equals(Object object) {
			if (object instanceof RequestFuture) {
				RequestFuture<?, ?> other = (RequestFuture<?, ?>) object;
				if (getId() == null) {
					return other.getId() == null;
				} else {
					return getId().equals(other.getId());
				}
			}
			return false;
		}
		
	}

}
