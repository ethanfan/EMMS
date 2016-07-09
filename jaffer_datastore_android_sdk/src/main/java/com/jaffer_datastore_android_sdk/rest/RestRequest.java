/**
 * RestRequest.java
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

import com.jaffer_datastore_android_sdk.callback.RestCallback;

import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Cookie;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Header;
import org.restlet.representation.Representation;
import org.restlet.util.Series;


/**
 * A class that represents a REST request.
 */
public class RestRequest {
	
	/** The request HTTP method. */
	private final Method method;

	/** {@link Reference} to the request resource. */
	private final Reference ref;

	/**
	 * The entity to include in the methods with payload. This can be
	 * {@code null}
	 */
	private final Representation entity;

	/**
	 * The media type of the representation to retrieve. It is
	 * {@link MediaType#APPLICATION_JSON} by default
	 */
	private final MediaType mediaType;

	/** The headers to include in the request. */
	private List<Header> headers;
	
	/** The cookies to include in the request */
	private Series<Cookie> cookies;

	/** The callback to invoke when the request returned .*/
	private final RestCallback callback;

	/**
	 * Creates a new instance of {@link RestRequest} with the given parameters.
	 * 
	 * @param method
	 *          The request HTTP method
	 * @param ref
	 *          {@link Reference} to the request resource
	 * @param callback
	 *          The callback to invoke when the request returned
	 */
	public RestRequest(Method method, Reference ref, RestCallback callback) {
		this(method, ref, null, null, callback);
	}

	/**
	 * Creates a new instance of {@link RestRequest} with the given parameters.
	 * 
	 * @param method
	 *          The request HTTP method
	 * @param ref
	 *          {@link Reference} to the request resource
	 * @param headers
	 *          The list of headers to include in this request
	 * @param callback
	 *          The callback to invoke when the request returned
	 */
	public RestRequest(Method method, Reference ref, List<Header> headers, RestCallback callback) {
		this(method, ref, null, headers, callback);
	}

	/**
	 * Creates a new instance of {@link RestRequest} with the given parameters.
	 * 
	 * @param method
	 *          The request HTTP method
	 * @param ref
	 *          {@link Reference} to the request resource
	 * @param entity
	 *          The entity to include in the methods with payload.
	 * @param callback
	 *          The callback to invoke when the request returned
	 */
	public RestRequest(Method method, Reference ref, Representation entity, RestCallback callback) {
		this(method, ref, entity, null, callback);
	}

	/**
	 * Creates a new instance of {@link RestRequest} with the given parameters.
	 * 
	 * @param method
	 *          The request HTTP method
	 * @param ref
	 *          {@link Reference} to the request resource
	 * @param entity
	 *          The entity to include in the methods with payload.
	 * @param callback
	 *          The callback to invoke when the request returned
	 */
	public RestRequest(Method method, Reference ref, Representation entity, List<Header> headers, RestCallback callback) {
		this(method, ref, entity, MediaType.APPLICATION_JSON, headers, callback);
	}

	/**
	 * Creates a new instance of {@link RestRequest} with the given parameters.
	 * 
	 * @param method
	 *          The request HTTP method
	 * @param ref
	 *          {@link Reference} to the request resource
	 * @param entity
	 *          The entity to include in the methods with payload.
	 * @param mediaType
	 *          The media type of the representation to retrieve.
	 * @param callback
	 *          The callback to invoke when the request returned
	 */
	public RestRequest(Method method, Reference ref, Representation entity, MediaType mediaType, List<Header> headers, RestCallback callback) {
		this.method = method;
		this.ref = ref;
		this.entity = entity;
		this.mediaType = mediaType;
		this.headers = headers;
		this.callback = callback;
	}
	
	/**
	 * Returns the HTTP method of this request.
	 * 
	 * @return The HTTP method of this request
	 */
	public Method getMethod() {
		return method;
	}
	
	/**
	 * Returns the URI reference to the resource referenced in this request.
	 * 
	 * @return URI reference to the remote resource
	 */
	public Reference getReference() {
		return ref == null ? ref : ref.getTargetRef();
	}
	
	/**
	 * Returns the entity representation to include in the request body.
	 * 
	 * @return The entity representation to include in the request body
	 */
	public Representation getEntity() {
		return entity;
	}

	/**
	 * Returns the media type of the representation to retrieve in the response
	 * of this request.
	 * 
	 * @return The media type of the representation to retrieve
	 */
	public MediaType getMediaType() {
		return mediaType;
	}
	
	/**
	 * Adds a {@link Header} to be included in this request.
	 * 
	 * @param header The header to be included
	 */
	public void addHeader(Header header) {
		if (headers == null) {
			headers = new ArrayList<Header>();
		}
		headers.add(header);
	}
	
	/**
	 * Returns a list of headers to include in this request.
	 * 
	 * @return A list of headers
	 */
	public List<Header> getHeaders() {
		return headers;
	}
	
	/**
	 * Sets the cookies provided to the request.
	 * 
	 * @param cookies The cookies to in the request
	 */
	public void setCookies(Series<Cookie> cookies) {
		this.cookies = cookies;
	}
	
	/**
	 * Returns the modifiable series of cookies provided to the request.
	 * 
	 * @return A modifiable series of cookies provided to the request.
	 */
	public Series<Cookie> getCookies() {
		return cookies;
	}
	
	/**
	 * Returns the callback to invoke when the remote server respond to this request.
	 * 
	 * @return The callback to invoke
	 */
	public RestCallback getCallback() {
		return callback;
	}

}
