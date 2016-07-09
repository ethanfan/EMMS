/**
 * Notification.java
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
package com.jaffer_datastore_android_sdk.messaging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * A notification message to be pushed to client(s).
 */
public class Notification {
	
	public static final String PAYLOAD_FIELD_NAME = "payload";
	public static final String USERS_FIELD_NAME = "users";
	
	@SerializedName(PAYLOAD_FIELD_NAME)
	@Expose
	private Payload payload;
	
	@SerializedName(USERS_FIELD_NAME)
	@Expose
	private List<Object> users;
	
	/**
	 * Constructor.
	 */
	public Notification() {
		// Must have a no-arg constructor for JSON de/serialization
		this(null);
	}

	/**
	 * Creates a new instance of {@link Notification} with the given {@code payload}.
	 * 
	 * @param payload
	 *          The payload in this message.
	 */
	public Notification(Payload payload) {
		setPayload(payload);
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}
	
	public List<Object> getUsers() {
		return users == null ? null : Collections.unmodifiableList(users);
	}
	
	/**
	 * Adds the identifier of the user into the list of recipient of this {@link Notification}.
	 * 
	 * @param user The identifier of the user to add to the recipient list
	 * @return {@code true} if the user is added to the list of recipients, {@code false} otherwise
	 */
	public synchronized boolean addUser(Object user) {
		if (users == null) {
			users = new ArrayList<Object>();
		}
		
		boolean result = false;
		if (user != null) {
			if (user instanceof Collection) {
				users.addAll(users);
			} else {
				if (!users.contains(user)) {
					result = users.add(user);
				}
			}
		}
		return result;
	}
	
	/**
	 * Removes the given {@code user} from the list of recipients of this {@link Notification}.
	 * 
	 * @param user The identifier of the user to remove
	 * @return {@code true} if the given {@code user} is removed, {@code false} otherwise
	 */
	public synchronized boolean removeUser(Object user) {
		boolean result = false;
		if (users != null && user != null) {
			result = users.remove(user);
		}
		return result;
	}
	
	/**
	 * Converts this {@link Notification} into its equivalent JSON representation.
	 * 
	 * @return JSON string representation of the {@link Notification}
	 */
	public String toJson() {
		// Creates the JSON builder
		GsonBuilder gsonb = new GsonBuilder();
		Gson gson = gsonb.serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
		
		// Serialize the data model into a JSON representation
		return gson.toJson(this);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

}
