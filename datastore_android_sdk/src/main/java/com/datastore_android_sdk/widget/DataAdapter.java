/**
 * DataAdapter.java
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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.schema.Query;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.datastore_android_sdk.datastore.Build;
import com.datastore_android_sdk.datastore.Store;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;
/**
 * Implementation of {@BaseAdapter} that retrieves data from a data store.
 * 
 * @author Stanley Lam
 */
public abstract class DataAdapter extends BaseAdapter {
	
	private static final String TAG = DataAdapter.class.getSimpleName();
	
	/** Constants. */
	private static final long DEFAULT_TIMEOUT = 120000;
	
	/** The timeout period. */
	private volatile long timeout = DEFAULT_TIMEOUT;
	
	/** The underlying data cache. */
	private final DataCache cache;
	
	/** The main {@link Looper} to handle the message. */
	private final Handler handler = new Handler();
	
	/**
	 * Creates a new instance of {@link DataAdapter} with the given {@link DataCache}.
	 * 
	 * @param cache The data cache to retrieve data from
	 */
	public DataAdapter(DataCache cache) {
		checkNotNull(cache);
		this.cache = cache;
	}
	
	/**
	 * Creates a new instance of {@link DataAdapter} with the given {@link Store}.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 */
	public DataAdapter(Store store, String schema) {
		this(store, schema, null);
	}
	
	/**
	 * Creates a new instance of {@link DataAdapter} with the given {@link Store} and {@link Query}.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 * @param query The query to perform against the {@code store}
	 */
	public DataAdapter(Store store, String schema, Query query) {
		this(new DataCache(store, schema, query));
	}
	
	/**
	 * Creates a new instance of {@link DataAdapter} with the given {@link Store} and {@link Query}.
	 * 
	 * @param store The data store to retrieve data from
	 * @param schema The schema to retrieve data from in the {@code store}
	 * @param query The query to perform against the {@code store}
	 * @param offset The value to decide need support offset
	 */
	public DataAdapter(Store store, String schema, Query query, boolean offset) {
		this(new DataCache(store, schema, query, offset));
	}
	
	/**
	 * Returns the data cache this {@link Adapter} uses.
	 * 
	 * @return The data cache
	 */
	public DataCache getCache() {
		return cache;
	}
	
	/**
	 * Specifies a callback to be invoked when data is cached
	 * 
	 * @param listener the callback to be invoked when data is cached
	 */
	public void setCacheListener(DataCache.OnCacheListener listener) {
		if (cache != null) {
			cache.setOnCacheListener(listener);
		}
	}
	
	/**
	 * Specifies a new timeout period for requests.
	 * 
	 * @param timeout the new timeout in milliseconds
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	/**
	 * Returns the timeout period for requests in milliseconds
	 * 
	 * @return the timeout period in milliseconds
	 */
	public long getTimeout() {
		return timeout;
	}

	@Override
	public int getCount() {
		int result = 0;
		try {
			result = getCache().getCount().get(getTimeout(), TimeUnit.MILLISECONDS).intValue();
		} catch (Exception e) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to retrieve count of items", e);
			}
			onError(e);
		}
		return result;
	}
	
	@Override
	public Object getItem(int position) {
		Object result = null;
		try {
			result = getCache().getData((long) position).get(getTimeout(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to retrieve item", e);
			}
			onError(e);
		}
		return result;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View inflatedView = getInflatedView(position, convertView, parent);
		final WeakReference<View> viewReference = inflatedView == null ? null : new WeakReference<View>(inflatedView);
		try {
			final ListenableFuture<DataElement> future = getCache().getData((long) position);
			if (inflatedView != null) {
				inflatedView.setTag(future);
			}
			
			Futures.addCallback(future, new FutureCallback<DataElement>() {

				@Override
				public void onSuccess(final DataElement result) {
					final View view = viewReference == null ? null : viewReference.get();
					if (view != null) {						
						Object tag = view.getTag();
						// Only set data in the inflated view if the Future tagged
						// in the view is the same as the one we are requesting
						if (future.equals(tag) && handler != null) {
							view.setTag(null);
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									onDataAvailable(result, view);
								}
								
							});
						}
					}
				}

				@Override
				public void onFailure(Throwable t) {
					// Ignore
					if (Build.DEBUG) {
						Log.e(TAG, "Failed to set item in list", t);
					}
					onError(t);
				}
				
			});
		} catch (Exception e) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to retrieve item", e);
			}
			onError(e);
		}
		
		return inflatedView;
	}
	
	/**
	 * Called when the data in the adapter's data set is retrieved.
	 * 
	 * @param data The data retrieved
	 * @param view The view to display the data
	 */
	protected abstract void onDataAvailable(DataElement data, View view);
	
	/**
	 * Gets a {@link View} that displays the data at the specified position in
	 * the data set. You can either create a View manually or inflate it from an
	 * XML layout file. When the View is inflated, the parent View (GridView,
	 * ListView...) will apply default layout parameters unless you use
	 * inflate(int, android.view.ViewGroup, boolean) to specify a root view and
	 * to prevent attachment to the root.<br>
	 * <br>
	 * This method is used to obtain the view without populating it to display
	 * the data in the adapter's data set
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param convertView
	 *            The old view to reuse, if possible. Note: You should check
	 *            that this view is non-null and of an appropriate type before
	 *            using. If it is not possible to convert this view to display
	 *            the correct data, this method can create a new view.
	 *            Heterogeneous lists can specify their number of view types, so
	 *            that this View is always of the right type (see
	 *            getViewTypeCount() and getItemViewType(int)).
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return A View corresponding to the data at the specified position.
	 */
	public abstract View getInflatedView(int position, View convertView, ViewGroup parent);
	
	/**
	 * Called when the data in the adapter's data set is retrieved failure.
	 * @param t An exception
	 */
	protected void onError(Throwable t) {
		// Do nothing by default
	}

}
