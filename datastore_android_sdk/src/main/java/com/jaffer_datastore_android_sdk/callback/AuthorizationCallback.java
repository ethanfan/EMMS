/** 
 * AuthorizationCallback.java
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
package com.jaffer_datastore_android_sdk.callback;


import com.jaffer_datastore_android_sdk.DatastoreException.DatastoreException;
import com.jaffer_datastore_android_sdk.security.IdToken;

/**
 * Defines the callback interfaces to be invoked after an authorization request
 * is fulfilled.
 */
public interface AuthorizationCallback {
	
	/**
	 * Invoked after an authorization request is completed successfully.
	 * 
	 * @param token The {@link IdToken} issued
	 */
	void success(IdToken token);
	
	/**
	 * Invoked after an authorization request failed.
	 * 
	 * @param ex The exception caused
	 */
	void failure(DatastoreException ex);

}
