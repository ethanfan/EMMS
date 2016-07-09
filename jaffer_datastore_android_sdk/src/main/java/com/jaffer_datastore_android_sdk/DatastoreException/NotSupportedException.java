/**
 * NotSupportedException.java
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
package com.jaffer_datastore_android_sdk.DatastoreException;

/**
 * Thrown if the user was not logged in with the system.
 */
public class NotSupportedException extends DatastoreException {

	private static final long serialVersionUID = 5477247953930957977L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the message
	 */
	public NotSupportedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public NotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public NotSupportedException(Throwable cause) {
		super(cause);
	}
  
}
