/**
 * IdToken.java
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
package com.jaffer_datastore_android_sdk.security;

/**
 * Defines the interfaces that an ID token must implement.
 */
public interface IdToken {

	/**
	 * Retrieves the JWT that contains identity information about the user. An ID
	 * Token is a cryptographically-signed JSON object encoded in base 64.
	 * 
	 * @return The JWT that contains identity information about the user
	 */
	String getIdToken();
	
}
