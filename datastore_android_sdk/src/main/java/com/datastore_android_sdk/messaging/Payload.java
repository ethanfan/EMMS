/**
 * Payload.java
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
package com.datastore_android_sdk.messaging;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.datastore_android_sdk.serialization.DateTypeAdapter;
import com.datastore_android_sdk.serialization.ExceptionSerializer;
import com.datastore_android_sdk.serialization.PayloadTypeAdapterFactory;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;


/**
 * Contains information about how the system should alert the user as well as
 * any custom data you provide.
 * 
 * Note: The maximum size allowed for a notification payload in iOS is 256 bytes
 * and a limit of 4096 bytes for GCM
 */
public class Payload {
	
	/**
	 * The message to display as an alert.
	 */
	public static final String ALERT_FIELD_NAME = "alert";
	
	/**
	 * The field that specifies the name of the sound file to play as an alert.
	 */
	public static final String SOUND_FIELD_NAME = "sound";
	
	/**
	 * The field that specifies the number to display as badge in the application icon.
	 * This field is specific to payload in iOS.
	 */
	public static final String BADGE_FIELD_NAME = "badge";

	/**
	 * The sender of the message. The sender does not need to correspond to a
	 * valid object in the application. In fact, it can be {@code null} if the
	 * notification was a broadcast message.
	 */
	public static final String SENDER_FIELD_NAME = "sender";
	
	/**
	 * The conversation that this {@link Payload} belongs to. A payload does not
	 * need to be associated with a conversation (e.g. a broadcast notification).
	 * 
	 * @see Conversation
	 */
	public static final String CONVERSATION_FIELD_NAME = "conversation";
	
	/**
	 * The content type of this payload. It indicates the Internet media type
	 * of the payload content
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/MIME">MIME types on Wikipedia</a>
	 */
	public static final String CONTENT_TYPE_FIELD_NAME = "content_type";
	
	@SerializedName(ALERT_FIELD_NAME)
	@Expose
	private String alert;
	
	@SerializedName(SOUND_FIELD_NAME)
	@Expose
	private String sound;
	
	@SerializedName(BADGE_FIELD_NAME)
	@Expose
	private int badge;
	
	// Internally used
	private Map<String, Object> properties;
	
	public Payload() {
		properties = new HashMap<String, Object>();
	}

	public String getAlert() {
		return alert;
	}

	public void setAlert(String alert) {
		this.alert = alert;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public int getBadge() {
		return badge;
	}

	public void setBadge(int badge) {
		this.badge = badge;
	}
	
	/**
	 * Returns the sender of this payload. This should be the identifier of
	 * a user object. 
	 */
	public Object getSender() {
		return getProperty(SENDER_FIELD_NAME);
	}
	
	/**
	 * Specifies the sender of this payload.
	 * 
	 * @param value The identifier of the sender of this payload
	 */
	public void setSender(Object value) {
		addProperty(SENDER_FIELD_NAME, value);
	}
	
	/**
	 * Returns the identifier of the {@link Conversation} that this payload 
	 * sends to. This should be the identifier of a {@link Conversation}
	 * 
	 * @see Conversation
	 */
	public Object getConversation() {
		return getProperty(CONVERSATION_FIELD_NAME);
	}
	
	/**
	 * Specifies the to which {@link Conversation} this payload sends to.
	 * 
	 * @param value The {@link Conversation}
	 */
	public void setConversation(Object value) {
		addProperty(CONVERSATION_FIELD_NAME, value);
	}
	
	/**
	 * Returns the Internet media type of this payload.
	 */
	public Object getContentType() {
		return getProperty(CONTENT_TYPE_FIELD_NAME);
	}
	
	/**
	 * Specifies the Internet media type of this payload.
	 * 
	 * @param value A {@link String} representation of the media type of this payload
	 */
	public void setContentType(String value) {
		addProperty(CONTENT_TYPE_FIELD_NAME, value);
	}
	
	/**
	 * Add an object to the properties HashMap.
	 * 
	 * @param name The name of the property
	 * @param value The value of the property
	 */
	public synchronized void addProperty(String name, Object value) {
		if (name != null) {
			if (value == null) {
				removeProperty(name);
			} else {
				if (properties == null) {
					properties = new HashMap<String, Object>();
				}
				properties.put(name, value);
			}
		}
	}
	
	/**
	 * Get the property from properties HashMap with name.
	 * 
	 * @param name The name of the property
	 * @return The value of the property, or <code>null</code> if no mapping for the specified key is found
	 */
	public Object getProperty(String name) {
		Object result = null;
		if (name != null && properties != null) {
			result = properties.get(name);
		}
		return result;
	}
	
	/**
	 * Returns an unmodifiable view of the properties of the {@link Message}.
	 */
	@SuppressWarnings("unchecked")
	public Map<String, ? extends Object> getProperties() {
		return (Map<String, ? extends Object>) (properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties));
	}
	
	/**
	 * Remove the property from properties HashMap with name.
	 * 
	 * @param name The name of the property
	 */
	public synchronized void removeProperty(String name) {
		if (name != null && properties != null) {
			properties.remove(name);
		}
	}
	
	/**
	 * Deserializes the JSON element into a {@link Payload}.
	 * 
	 * @param json JSON element to deserialize
	 * @return The deserialized {@link Payload} object
	 */
	public static Payload fromJson(String json) {
		TypeAdapterFactory factory = new PayloadTypeAdapterFactory(
				new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap()),
				FieldNamingPolicy.IDENTITY, 
				Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());
		
		// Creates the JSON builder
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.registerTypeHierarchyAdapter(Exception.class, new ExceptionSerializer())
				.registerTypeAdapterFactory(factory)
				.serializeNulls()
				.create();
		
		return gson.fromJson(json, Payload.class);
	}
	
	/**
	 * Converts this {@link Payload} into its equivalent JSON representation.
	 * 
	 * @return JSON string representation of the {@link Payload}
	 */
	public String toJson() {
		TypeAdapterFactory factory = new PayloadTypeAdapterFactory(
				new ConstructorConstructor(Collections.<Type, InstanceCreator<?>>emptyMap()),
				FieldNamingPolicy.IDENTITY, 
				Excluder.DEFAULT.excludeFieldsWithoutExposeAnnotation());

		// Creates the JSON builder
		Gson gson = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.registerTypeHierarchyAdapter(Exception.class, new ExceptionSerializer())
				.registerTypeAdapterFactory(factory)
				.serializeNulls()
				.create();
		
		// Serialize the data model into a JSON representation
		return gson.toJson(this);
	}
	
	@Override
	public String toString() {
		return toJson();
	}

}
