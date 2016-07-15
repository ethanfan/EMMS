/**
 * JsonArrayElement.java
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
package com.datastore_android_sdk.rest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.NullElement;
import com.datastore_android_sdk.representation.GsonRepresentation;
import com.datastore_android_sdk.serialization.GsonTypeAdpaters;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.datastore_android_sdk.datastore.ArrayElement;


/**
 * An implementation of {@link ArrayElement} that is a wrapper around {@link JsonArray}.
 */
public class JsonArrayElement extends ArrayElement implements JsonDataElement {
	
	private final JsonArray array;

	/**
	 * Creates a new instance of {@link JsonArrayElement} with the given JSON.
	 * 
	 * @param json
	 *          The JSON text
	 * @throws IllegalStateException
	 *           Thrown if {@code json} is not a JSON array
	 * @throws JsonParseException
	 *           Thrown if {@code json} is not a valid JSON
	 */
	public JsonArrayElement(String json) {
		this(new JsonParser().parse(json).getAsJsonArray());
	}

	/**
	 * Creates a new instance of {@link JsonArrayElement} that wraps around the
	 * given {@link JsonArray}.
	 * 
	 * @param array The underlying {@link JsonArray}. Cannot be {@code null}
	 */
	public JsonArrayElement(JsonArray array) {
		if (array == null) {
			throw new NullPointerException("The wrapped object cannot be null.");
		}
		this.array = array;
	}

	@Override
	public Iterator<DataElement> iterator() {
		return new JsonArrayElementIterator();
	}

	@Override
	public void add(DataElement element) {
		if (element != null) {
			if (element instanceof JsonDataElement) {
				JsonDataElement json = (JsonDataElement) element;
				array.add(json.getData());
			} else if (element.isNull()) {
				array.add(JsonNull.INSTANCE);
			} else {
				String json = element.toJson();
				try {
					array.add(new JsonParser().parse(json));
				} catch (Exception e) {
					// Ignores
				} 
			}
		}
	}

	@Override
	public int size() {
		return array.size();
	}

	@Override
	public DataElement get(int index) {
		JsonElement element = array == null ? null : array.get(index);
		return getDataElement(element);
	}

	@Override
	public boolean isEmpty() {
		return array.size() == 0;
	}
	
	@Override
	public JsonElement getData() {
		return array;
	}
	
	@Override
	public String toString() {
		try {
			StringWriter stringWriter = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			GsonTypeAdpaters.JSON_ELEMENT.write(jsonWriter, array);
			return stringWriter.toString();
		} catch (IOException ex) {
			return NullElement.INSTANCE.toString();
		}
	}
	
	@Override
	public String toJson() {
		return toString();
	}
	
	@Override
	public Representation toRepresentation() {
		return new GsonRepresentation(array) {

			@Override
			public DataElement getDataElement() {
				return JsonArrayElement.this;
			}
			
		};
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		JsonArrayElement other = (JsonArrayElement) obj;
		if (array == null) {
			return other.array == null;
		}

		return array.equals(other.array);
	}
	
	@Override
	public int hashCode() {
		return array == null ? super.hashCode() : array.hashCode();
	}
	
	/**
	 * Returns a {@link DataElement} representation of a given {@link JsonElement}.
	 * 
	 * @param element The {@link JsonElement} to return the {@link DataElement} representation
	 * @return The {@link DataElement} representation of {@code element}
	 */
	private DataElement getDataElement(JsonElement element) {
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
		return null;
	}

	/**
	 * An internal implementation of an {@link Iterator} to navigate through the
	 * elements in the underlying {@link JsonArray}.
	 */
	private final class JsonArrayElementIterator implements Iterator<DataElement> {
		
		private final Iterator<JsonElement> itr;
		
		JsonArrayElementIterator() {
			itr = array.iterator();
		}

		@Override
		public boolean hasNext() {
			return itr.hasNext();
		}

		@Override
		public DataElement next() {
			JsonElement element = itr.next();
			return getDataElement(element);
		}

		@Override
		public void remove() {
			itr.remove();
		}
		
	}

}
