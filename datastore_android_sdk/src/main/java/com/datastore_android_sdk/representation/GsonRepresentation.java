/**
 * GsonRepresentation.java
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
package com.datastore_android_sdk.representation;



import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.NullElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rest.JsonPrimitiveElement;
import com.datastore_android_sdk.serialization.GsonTypeAdpaters;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.datastore_android_sdk.datastore.data.Range;
import com.datastore_android_sdk.rest.JsonArrayElement;

/**
 * Representation based on a JSON document using GSON.
 * 
 * @see <a href="https://code.google.com/p/google-gson/">Google GSON</a>
 */
public class GsonRepresentation extends JsonDataRepresentation {
	
	/** The wrapped JSON element. */
	private JsonElement json;
	
	/**
	 * Creates a new instance of {@link GsonRepresentation} from another {@link Representation}.
	 * 
	 * @param representation The source {@link Representation}
	 * @throws IOException Thrown if there was error reader from the communication channel
	 */
	public GsonRepresentation(Representation representation) throws IOException {
		this(representation == null ? null : representation.getReader());
	}
	
	/**
	 * Creates a new instance of {@link GsonRepresentation} from a {@link Reader}.
	 * 
	 * @param reader The reader to read the JSON from
	 */
	public GsonRepresentation(Reader reader) {
		this(reader == null ? null : new JsonParser().parse(reader));
	}

	/**
	 * Creates a new instance of {@link GsonRepresentation} wrapping another
	 * instance of {@link GsonRepresentation}.
	 * 
	 * @param gson The {@link GsonRepresentation} wrapped
	 */
	public GsonRepresentation(GsonRepresentation gson) {
		this(gson == null ? null : gson.json);
	}
	
	/**
	 * Creates a new instance of {@link GsonRepresentation} wrapping the given {@link JsonElement}.
	 * 
	 * @param element The underlying JSON element of this {@link Representation}
	 */
	public GsonRepresentation(JsonElement element) {
		super();
		json = element;
		updateCount();
		setRange(new Range());
	}
	
	/**
	 * Returns the JSON value wrapped by this {@link Representation}.
	 * 
	 * @return The underlying JSON value of this {@link Representation}
	 */
	public JsonElement getJsonElement() {
		return json;
	}
	
	/**
	 * Returns the number of items in JSON element
	 * 
	 * @return the number of items in the JSON element
	 */
	@Override
	public long getCount() {
		long result = 0;
		
		if (getJsonElement() != null) {
			if (getJsonElement().isJsonArray()) {
				return getJsonElement().getAsJsonArray().size();
			} else {
				return 1;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns the underlying {@link DataElement} that this representation wraps.
	 * 
	 * @return The wrapped {@link DataElement}
	 */
	public DataElement getDataElement() {
		if (getJsonElement() != null) {
			if (getJsonElement().isJsonArray()) {
				return new JsonArrayElement(getJsonElement().getAsJsonArray());
			} else if (getJsonElement().isJsonObject()) {
				return new JsonObjectElement(getJsonElement().getAsJsonObject());
			} else if (getJsonElement().isJsonPrimitive()) {
				return new JsonPrimitiveElement(getJsonElement().getAsJsonPrimitive());
			}
		} 
		return NullElement.INSTANCE;
	};
	
	/**
	 * Updates the expected number of items in the representation
	 */
	protected void updateCount() {		
		long count = Range.SIZE_MAX;
		if (getJsonElement() != null) {
			if (getJsonElement().isJsonArray()) {
				count = getJsonElement().getAsJsonArray().size();
			} else if (getJsonElement().isJsonObject()) {
				// There is only 1 item
				count = 1;
			} else if (getJsonElement().isJsonNull()) {
				// A JSON null has no item
				count = 0;
			}
		}
		super.setCount(count);
	}
	
	@Override
	public void setRange(org.restlet.data.Range range) {
		if (range != null) {
			if (getJsonElement() != null && !getJsonElement().isJsonPrimitive()) {
				range.setIndex(Range.INDEX_FIRST);
				range.setSize(getCount());
			}
		}
		super.setRange(range);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
		
		GsonRepresentation other = (GsonRepresentation) obj;
		if (json == null) {
			return other.json == null;
		}
		
		return json.equals(other.json);
	}
	
	@Override
	public int hashCode() {
		return json == null ? -1 : json.hashCode();
	}
	
	@Override
	public String toString() {
		return json == null ? "" : json.toString();
	}
	
	@Override
	public void write(Writer writer) throws IOException {
		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setLenient(true);
		GsonTypeAdpaters.JSON_ELEMENT.write(jsonWriter, getJsonElement());
	}

}
