/** DatastoreException.java
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

import org.restlet.data.Status;

/**
 * The {@link DatastoreException} class is the base exception class for data
 * access API runtime exceptions.
 */
public class DatastoreException extends RuntimeException {

	private static final long serialVersionUID = -78795686244809384L;
	
	/** The status associated to this exception. */
    private Status status;

	/**
	 * Consturcts a {@link DatastoreException} with no detail message.
	 */
	public DatastoreException() {
		super();
	}

	/**
	 * Constructs a new datastore exception with the specified detail message.
	 * 
	 * @param message
	 *            the message
	 */
	public DatastoreException(String message) {
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
	public DatastoreException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new datastore exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public DatastoreException(Throwable cause) {
		super(cause);
	}
	
	/**
	 * Constructor
	 * 
	 * @param status the status associated with the {@link Exception}
	 * @param message the detailed message of the error
	 */
	public DatastoreException(Status status, String message) {
		super(message);
		this.status = status;
	}
	
	/**
     * Returns the status associated to this exception.
     * 
     * @return The status associated to this exception.
     */
    public Status getStatus() {
        return status;
    }
	
	/**
	 * Returns the root cause of this exception
	 * 
	 * @return the root cause of this exception
	 */
	public Throwable getRootCause() {
		Throwable cause = getCause();
		Throwable prev = null;
		
		while (cause != null) {
			prev = cause;
			cause = prev.getCause();
		}
		return prev;
	}

}
