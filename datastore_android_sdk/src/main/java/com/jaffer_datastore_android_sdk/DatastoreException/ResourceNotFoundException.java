/**
 * ResoruceNotFoundException.java
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
 * Thrown if the requested resource was not found in the underlying datastore.
 */
public class ResourceNotFoundException extends DatastoreException {

	/** The error messages. */
	static final String ERROR_MESSAGE_RESOURCE = "The resource: %s does not exist.";
	static final String ERROR_MESSAGE_IDENTITY = "The resource: %s identified by %s does not exist.";

	private static final long serialVersionUID = 2032622765981298456L;

	/** The name of the resource. */
	private String resource;

	/** The identity of the resource. */
	private String id;

	/**
	 * Constructs a new resource not found exception.
	 * 
	 * @param resource
	 *            The resource that was not found
	 */
	public ResourceNotFoundException(String resource) {
		super(String.format(ERROR_MESSAGE_RESOURCE, resource));
		setResource(resource);
	}

	/**
	 * Constructs a new resource not found exception.
	 * 
	 * @param resource
	 *            The resource that was not found
	 * @param id
	 *            The identity of the resource
	 */
	public ResourceNotFoundException(String resource, String id) {
		super(String.format(ERROR_MESSAGE_IDENTITY, resource, id));
		setResource(resource);
		setId(id);
	}

	/**
	 * Constructs a new datastore exception with the specified detail message
	 * and cause.
	 * 
	 * @param resource
	 *            The resource that was not found
	 * @param cause
	 *            The cause of this exception
	 */
	public ResourceNotFoundException(String resource, Throwable cause) {
		super(String.format(ERROR_MESSAGE_RESOURCE, resource), cause);
		setResource(resource);
	}

	/**
	 * Constructs a new resource not found exception.
	 * 
	 * @param resource
	 *            The resource that was not found
	 * @param id
	 *            The identity of the resource
	 * @param cause
	 *            The cause of this exception
	 */
	public ResourceNotFoundException(String resource, String id, Throwable cause) {
		super(String.format(ERROR_MESSAGE_IDENTITY, resource, id), cause);
		setResource(resource);
		setId(id);
	}

	/**
	 * Specifies the identity of the resource that casued this exception.
	 * 
	 * @param id
	 *            The identity of the resource that caused this exception
	 */
	void setId(String id) {
		this.id = id;
	}

	/**
	 * Returns the identity of the resource that caused this exception.
	 * 
	 * @return The identity of the resource that cause this exception
	 */
	public String getId() {
		return id;
	}

	/**
	 * Specifies the name of the resource.
	 * 
	 * @param resource
	 *            The name of the resource
	 */
	void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * Returns the resource that was not found.
	 * 
	 * @return The resource that was not found
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Constructs a new datastore exception with the specified cause.
	 * 
	 * @param cause
	 *            the cause
	 */
	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

}
