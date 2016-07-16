/**
 * QueryParseException.java
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
 * This exception is raised if there is a serious issue that occurs during
 * parsing of a {@link Query}.
 */
public class QueryParseException extends DatastoreException {

	private static final long serialVersionUID = -5922777274521052658L;

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message
	 *            the message
	 */
	public QueryParseException(String message) {
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
	public QueryParseException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public QueryParseException(Throwable cause) {
		super(cause);
	}

}
