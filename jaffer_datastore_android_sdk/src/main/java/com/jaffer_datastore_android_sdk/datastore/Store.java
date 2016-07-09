/**
 * Store.java
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
package com.jaffer_datastore_android_sdk.datastore;

import java.util.List;

import com.google.common.util.concurrent.ListenableFuture;
import com.jaffer_datastore_android_sdk.callback.AuthorizationCallback;
import com.jaffer_datastore_android_sdk.callback.StoreCallback;
import com.jaffer_datastore_android_sdk.schema.Query;

/**
 * Defines the interfaces a {@link Store} must implement.
 */
public interface Store {
	
	/**
	 * Creates the given {@link DataElement} in the store.
	 * 
	 * @param element
	 *          The data element to create
	 * @param resource
	 *          The name of the data object to create
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 * @return 
	 */
	ListenableFuture<?> createElement(DataElement element, String resource, StoreCallback callback);

	/**
	 * Retrieves a data element with the given {@code id}.
	 * 
	 * @param id
	 *          The identity of the data element
	 * @param resource
	 *          The name of the data object
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 */
	ListenableFuture<?> readElement(Object id, String resource, StoreCallback callback);

	/**
	 * Updates the given {@link DataElement} in the store.
	 * 
	 * @param id
	 *          The identity of the element to update
	 * @param element
	 *          The data element to update
	 * @param resource
	 *          The name of the data object to create
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 */
	ListenableFuture<?> updateElement(Object id, DataElement element, String resource, StoreCallback callback);
	
	/**
	 * Updates data that matches the given {@code query} to fields to the data as specified in {@code element} 
	 * 
	 * @param query
	 *          The query to filter the data. Cannot be {@code null}
	 * @param element
	 *          The data element to update
	 * @param resource
	 *          The name of the data object to create
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 */
	ListenableFuture<DataElement> updateElements(Query query, DataElement element, String resource, StoreCallback callback);
	
	/**
	 * Deletes a data element with the given {@code id}.
	 * 
	 * @param id
	 *          The identity of the data element
	 * @param resource
	 *          The name of the data object
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 */
	ListenableFuture<?> deleteElement(Object id, String resource, StoreCallback callback);

	/**
	 * Deletes data that matches the given {@code query}. 
	 * 
	 * @param query
	 *          The query to filter the data. Cannot be {@code null}
	 * @param resource
	 *          The name of the data collection to delete elements
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 * @return 
	 */
	ListenableFuture<?> deleteElements(Query query, String resource, StoreCallback callback);
	
	/**
	 * Perform an query on the given data collection.
	 * 
	 * @param query
	 *          The query to perform
	 * @param resource
	 *          The name of the data collection to perform the query
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 * @return 
	 */
	ListenableFuture<?> performQuery(Query query, String resource, StoreCallback callback);

	/**
	 * Returns the number of objects returned from the query. The count is
	 * calculated after applying the filter but ignored the offset and limit
	 * options.
	 * 
	 * @param query
	 *          The query to perform
	 * @param resource
	 *          The name of the data collection to perfrom the query on
	 * @param callback
	 *          The callback to be invoked when the operation completes
	 * @return 
	 */
	ListenableFuture<?> count(Query query, String resource, StoreCallback callback);

	/**
	 * Perform an atomic update to a resource.
	 * 
	 * @param id
	 *          The identity of the resource
	 * @param resource
	 *          The name of the resource to update
	 * @param operation
	 *          The atomic operation to perform on the resource
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 */
	ListenableFuture<?> updateAtomicField(Object id, String resource, Datastore.AtomicOperation operation, StoreCallback callback);

	/**
	 * Perform an atomic update to a resource. If multiple operation is defined
	 * for a single field, only the last operation will be executed.
	 * 
	 * @param id
	 *          The identity of the resource
	 * @param resource
	 *          The name of the resource to update
	 * @param operations
	 *          The atomic operations to perform on the resource
	 * @param callback
	 *          The callback to be invoked when the operation complete
	 */
	ListenableFuture<?> updateAtomicFields(Object id, String resource, List<Datastore.AtomicOperation> operations, StoreCallback callback);
	
	/**
	 * Authenticate the given credentials with the store.
	 * 
	 * @param username
	 *          The user's logon name for the REST store
	 * @param password
	 *          The user's password
	 * @param callback
	 *          The callback to invoke when the operation completes
	 * @return 
	 */
	ListenableFuture<?> login(String username, String password, AuthorizationCallback callback);
	
	/**
	 * Sign-out from the store.
	 * 
	 * @param callback
	 *          The callback to invoke when the operation completes
	 */
	ListenableFuture<?> logout(AuthorizationCallback callback);
	
}
