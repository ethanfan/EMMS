/**
 * Error.java
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
package com.jaffer_datastore_android_sdk.rest;

/**
 * The error occurred during a RESTful call
 */
public interface Error {
	
	/**
	 * Returns the description of the error
	 * 
	 * @return the description of the error occurred
	 */
	public String getDescription();

}
