/**
 * Message.java
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

import java.io.File;

import org.apache.commons.io.FilenameUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.service.MetadataService;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.jaffer_datastore_android_sdk.datastore.DataElement;
import com.jaffer_datastore_android_sdk.representation.GsonRepresentation;


/**
 * This class represents a message in a conversation.
 */
public class Message {
	
	/** Field names. */
	public static final String BODY_FIELD_NAME = "body";
	public static final String CONTENT_TYPE_FIELD_NAME = "content_type";
	public static final String CONVERSATION_FIELD_NAME = "conversation";
	public static final String SENDER_FIELD_NAME = "sender";
	
	/** The underlying value holder. */
	private final JsonObject object = new JsonObject();
	
	/**
	 * Creates a plain text {@link Message}.
	 * 
	 * @param body The message body
	 */
	public Message(String body) {
		setBody(body);
	}

	/**
	 * Returns the body of this message.
	 * 
	 * @return The body of this message
	 */
	public String getBody() {
		JsonElement element = object.get(BODY_FIELD_NAME);
		return element == null ? null : element.getAsJsonPrimitive().getAsString(); 
	}
	
	/**
	 * Specifies the body of this message. 
	 * 
	 * @param body The string body of this message.
	 */
	public void setBody(String body) {
		if (body != null) {
			object.addProperty(BODY_FIELD_NAME, body);
			object.addProperty(CONTENT_TYPE_FIELD_NAME, MediaType.TEXT_PLAIN.toString());
		}
	}
	
	/**
	 * Specifies the body of this message.
	 * 
	 * @param body The file body of this message
	 */
	public void setBody(File body) {
		if (body != null) {
			MetadataService metadata = new MetadataService();
			String extension = FilenameUtils.getExtension(body.getName());
			object.addProperty(BODY_FIELD_NAME, body.toURI().toString());
			object.addProperty(CONTENT_TYPE_FIELD_NAME, metadata.getMediaType(extension).toString());
		}		
	}

	/**
	 * Returns the conversation that this message is to send to.
	 * 
	 * @return The conversation of this message is to send to
	 */
	public Object getConversation() {
		JsonElement element = object.get(CONVERSATION_FIELD_NAME);
		if (element == null) {
			return null;
		}
		
		JsonPrimitive primitive = element.getAsJsonPrimitive();
		if (primitive.isNumber()) {
			return primitive.getAsLong();
		} else if (primitive.isString()) {
			return primitive.getAsString();
		} else {
			return null;
		}
	}
	
	/**
	 * Specifies the conversation this message is to send to. This can be the
	 * identifier of the conversation.
	 * 
	 * @param conversation
	 *          The conversation this message is to send to.
	 */
	public void setConversation(String conversation) {
		if (conversation != null) {
			object.addProperty(CONVERSATION_FIELD_NAME, conversation);
		}
	}
	
	/**
	 * Specifies the conversation this message is to send to. This can be the
	 * identifier of the conversation.
	 * 
	 * @param conversation
	 *          The conversation this message is to send to.
	 */
	public void setConversation(Number conversation) {
		if (conversation != null) {
			object.addProperty(CONVERSATION_FIELD_NAME, conversation);
		}
	}
	
	/**
	 * Specifies the conversation this message is to send to.
	 * 
	 * @param conversation
	 *          The conversation this message is to send to.
	 */
	public void setConversation(DataElement conversation) {
		if (conversation != null && conversation.isObject()) {
			object.add(CONVERSATION_FIELD_NAME, new JsonParser().parse(conversation.toJson()));
		}
	}
	
	/**
	 * Specifies the sender of this message. This can be the identifier of the sender
	 * 
	 * @param sender The sender of this message
	 */
	public void setSender(String sender) {
		if (sender != null) {
			object.addProperty(SENDER_FIELD_NAME, sender);
		}
	}
	
	/**
	 * Specifies the sender of this message. This can be the identifier of the sender
	 * 
	 * @param sender The sender of this message
	 */
	public void setSender(Number sender) {
		if (sender != null) {
			object.addProperty(SENDER_FIELD_NAME, sender);
		}
	}
	
	/**
	 * Specifies the sender of this message.
	 * 
	 * @param sender The sender of this message
	 */
	public void setSender(DataElement sender) {
		if (sender != null && sender.isObject()) {
			object.add(SENDER_FIELD_NAME, new JsonParser().parse(sender.toJson()));
		}
	}
	
	/**
	 * Returns a {@link Representation} of this message.
	 * 
	 * @return The {@link Representation} of this message
	 */
	public Representation toRepresentation() {
		return new GsonRepresentation(object);
	}

}
