/** 
 * StoreCallback.java
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
package com.datastore_android_sdk.callback;


import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.Store;

/**
 * Defines the callback interfaces you can implement to be notified after an
 * operation is performed on the {@link Store}.
 */
public interface StoreCallback {

	/**
	 * Called when an operation is performed successfully on the {@link Store}.
	 * 
	 * @param element
	 *          The data element returned as a result of the operation performed
	 * @param resource
	 *          The name of the resource the operation performed on
	 */
	void success(DataElement element, String resource);

	/**
	 * Called if there were error performing the operation.
	 * 
	 * @param ex
	 *          The exception caused
	 * @param resource
	 *          The name of the resource the operation performed on
	 */
	void failure(DatastoreException ex, String resource);

}
