/**
 * JsonObjectElement.java
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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.restlet.representation.Representation;

import com.google.gson.JsonElement;
import com.google.gson.JsonFile;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jaffer_datastore_android_sdk.datastore.DataElement;
import com.jaffer_datastore_android_sdk.datastore.NullElement;
import com.jaffer_datastore_android_sdk.datastore.ObjectElement;

import com.jaffer_datastore_android_sdk.representation.GsonRepresentation;
import com.jaffer_datastore_android_sdk.serialization.GsonTypeAdpaters;

/**
 * An implementation of {@link ObjectElement} that is a wrapper around
 * a {@link JsonObject}.
 */
public final class JsonObjectElement extends ObjectElement implements JsonDataElement {
	
	/** The underlying JSON object this element wraps. */
	final JsonObject object;
	
	/**
	 * Creates a new instance of {@link JsonObjectElement}.
	 */
	public JsonObjectElement() {
		this(new JsonObject());
	}

	/**
	 * Creates a new instance of {@link JsonObjectElement} with the given JSON.
	 * 
	 * @param json
	 *          The JSON text
	 * @throws JsonParseException
	 *           Thrown if {@code json} is not a valid JSON
	 * @throws JsonSyntaxException
	 *           Thrown if {@code json} contains invalid syntax
	 * @throws IllegalStateException
	 *           Thrown if {@code json} is not an object
	 */
	public JsonObjectElement(String json) throws JsonParseException, JsonSyntaxException, IllegalStateException {
		this(new JsonParser().parse(json).getAsJsonObject());
	}
	
	/**
	 * Creates a new instance of {@link JsonObjectElement} with the given
	 * {@link Representation}.
	 * 
	 * @param representation
	 *          The entity representation to create this JSON object from
	 * @throws IOException
	 *           Thrown if the IO channel cannot be read
	 * @throws JsonSyntaxException
	 *           Thrown if the entity is not a valid JSON
	 * @throws JsonIOException
	 *           Thrown if the entity cannot be read
	 * @throws IllegalStateException
	 *           Thrown if {@code json} is not an object
	 */
	public JsonObjectElement(Representation representation) throws JsonIOException, JsonSyntaxException, IOException, IllegalStateException {
		this(representation == null ? new JsonObject() : new JsonParser().parse(new JsonReader(representation.getReader())).getAsJsonObject());
	}
	
	/**
	 * Creates a new instance of {@link JsonObjectElement} with the given.
	 * {@link JsonObject}
	 * 
	 * @param object
	 *          The underlying {@link JsonObject}
	 */
	public JsonObjectElement(JsonObject object) {
		this.object = object;
	}
	
	@Override 
	public boolean has(List<String> path) {
		return get(path) != null;
	}
	
	@Override
	public DataElement get(List<String> path) {
		String fieldName = path == null ? null : path.get(0);
		int index = -1;
		Pattern p = Pattern.compile("(.+?)\\[(.*?)\\]");
		Matcher m = fieldName == null ? null : p.matcher(fieldName);
		
		// If the pattern: paramName[index] was found
		if (m != null && m.find() && m.groupCount() == 2) {
			fieldName = m.group(1);
			index = Integer.valueOf(m.group(2));
		}
		
		DataElement result = getDataElement(fieldName);
		if (result != null && index > -1) {
			if (result.isArray()) {
				result = result.asArrayElement().get(index);
			} else {
				// The element found is not an array but the path requested refers to
				// an array 
				result = null;
			}
		}
		
		if (path != null && path.size() > 1) {
			if (result != null && result.isObject()) {
				result = result.asObjectElement().get(path.subList(1, path.size()));
			} else {
				// The element found is not a data element but the path requested refers
				// to a sub-element
				result = null;
			}
		}
		
		return result;
	}
	
	@Override
	public String[] allKeys() {
		List<String> keys = new ArrayList<String>();
		if (object != null) {			
			Iterator<Entry<String, JsonElement>> itr = object.entrySet().iterator();
			while (itr.hasNext()) {
				Entry<String, JsonElement> entry = itr.next();
				keys.add(entry.getKey());
			}
		}
		
		String[] result = new String[keys.size()];
		return keys.toArray(result);
	}
	
	@Override
	public void set(String property, String value) {
		if (object != null && property != null) {
			object.addProperty(property, value);
		}
	}
	
	@Override
	public void set(String property, Boolean value) {
		if (object != null && property != null) {
			object.addProperty(property, value);
		}
	}
	
	@Override
	public void set(String property, Number value) {
		if (object != null && property != null) {
			object.addProperty(property, value);
		}
	}
	
	@Override
	public void set(String property, Character value) {
		if (object != null && property != null) {
			object.addProperty(property, value);
		}
	}
	
	@Override
	public void set(String property, Date value) {
		if (object != null && value != null) {
			// We serialize date objects a {@link Long}
			object.addProperty(property, value.getTime());
		}
	}
	
	@Override
	public void set(String property, File value) {
		if (object != null && value != null) {
			object.add(property, new JsonFile(value));
		}
	}
	
	@Override
	public void set(String property, DataElement element) {
		if (object != null && property != null) {
			if (element instanceof JsonDataElement) {
				JsonElement data = ((JsonDataElement) element).getData();
				object.add(property, data);
			} else {
				String json = element == null ? null : element.toJson();
				JsonElement data = json == null ? JsonNull.INSTANCE : new JsonParser().parse(json);
				object.add(property, data);
			}
		}
	}

	/**
	 * Returns a {@link DataElement} representation of the underlying
	 * {@link JsonElement} identified by {@code memberName}.
	 * 
	 * @param memberName
	 *          The name of the member requested.
	 * @return The data element of the member requested, {@code null} if the
	 *         member requested does not exist
	 */
	private DataElement getDataElement(String memberName) {
		if (object != null && memberName != null) {
			JsonElement element = object.get(memberName);
			if (element != null) {
				if (element.isJsonArray()) {
					return new JsonArrayElement(element.getAsJsonArray());
				} else if (element.isJsonNull()) {
					return NullElement.INSTANCE;
				} else if (element.isJsonPrimitive()) {
					return new JsonPrimitiveElement(element.getAsJsonPrimitive());
				} else if (element.isJsonObject()) {
					return new JsonObjectElement(element.getAsJsonObject());
				}
			}
		}
		
		// The requested member does not exist
		return null;
	}
	
	@Override
	public JsonElement getData() {
		return object;
	}
	
	@Override
	public String toString() {
		try {
			StringWriter stringWriter = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			GsonTypeAdpaters.JSON_ELEMENT.write(jsonWriter, object);
			return stringWriter.toString();
		} catch (IOException ex) {
			return NullElement.INSTANCE.toString();
		}
	}
	
	@Override
	public Representation toRepresentation() {
		return new GsonRepresentation(object) {
			
			@Override
			public DataElement getDataElement() {
				return JsonObjectElement.this;
			}
			
		};
	}
	
	@Override
	public String toJson() {
		return toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		JsonObjectElement other = (JsonObjectElement) obj;
		if (object == null) {
			return other.object == null;
		}
		
		return object.equals(other.object);
	}
	
	@Override
	public int hashCode() {
		return object == null ? super.hashCode() : object.hashCode();
	}

}
