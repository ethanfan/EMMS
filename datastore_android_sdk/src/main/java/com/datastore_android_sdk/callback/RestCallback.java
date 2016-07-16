/** 
 * RestCallback.java
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

import org.restlet.representation.Representation;



/**
 * Defines the callback interfaces to be invoked after REST call to remote
 * resource.
 */
public interface RestCallback {
	
	/**
	 * Called after a call to the remote resource completed successfully. Note:
	 * normally, you do not release the {@link Representation} here. The
	 * {@link Representation} returned may be used in other callback. Releasing
	 * the {@link Representation} will result in incorrect state for the other
	 * registered callback.
	 * 
	 * @param entity
	 *            The entity returned
	 */
	void success(Representation entity);
	
	/**
	 * Called if there was an error accessing the remote resource.
	 * 
	 * @param ex The exception caused
	 */
	void failure(DatastoreException ex);

}
