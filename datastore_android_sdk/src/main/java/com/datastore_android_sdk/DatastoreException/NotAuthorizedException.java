/** NotAuthorizedException.java
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
package com.datastore_android_sdk.DatastoreException;

/**
 * Thrown when if the actor was not authorized to perform the given action.
 */
public class NotAuthorizedException extends DatastoreException {

	private static final long serialVersionUID = -3417482771900786210L;

	/**
	 * Constructs a new {@link NotAuthorizedException} with no detail message.
	 */
	public NotAuthorizedException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the message
	 */
	public NotAuthorizedException(String message) {
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
	public NotAuthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public NotAuthorizedException(Throwable cause) {
		super(cause);
	}

}
