/**
 * VersionConflictException.java
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
 * Thrown if the state of the posted resource was in conflict with the current
 * state of the resource.
 */
public class VersionConflictException extends DatastoreException {

	private static final long serialVersionUID = 3425468399103567798L;

	/**
	 * Constructs a new datastore exception with the specified detail message.
	 * 
	 * @param message
	 *            the message
	 */
	public VersionConflictException(String message) {
		super(message);
	}

	/**
	 * Constructs a new datastore exception with the specified detail message
	 * and cause.
	 * 
	 * @param message
	 *            the message
	 * @param cause
	 *            the cause
	 */
	public VersionConflictException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new datastore exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public VersionConflictException(Throwable cause) {
		super(cause);
	}

}
