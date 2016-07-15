/**
 * PayloadTypeAdapter.java
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
package com.datastore_android_sdk.serialization;

import java.io.IOException;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.datastore_android_sdk.messaging.Payload;


/**
 * The adapter that serializes/deserializes a Payload object.
 * 
 * @param <T> the class type of the payload
 */
public class PayloadTypeAdapter<T extends Payload> extends TypeAdapter<Payload> {
	
	protected final ObjectConstructor<T> constructor;
	protected final Map<String, BoundField> boundFields;
	
	PayloadTypeAdapter(ObjectConstructor<T> constructor, Map<String, BoundField> boundFields) {
		this.constructor = constructor;
		this.boundFields = boundFields;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Payload read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}
		
		Payload instance = constructor.construct();
		try {
			in.beginObject();
			while (in.hasNext()) {
				String name = null;
				JsonToken jsonToken = in.peek();
				switch (jsonToken) {
				case NAME:
					name = in.nextName();
				default:
					break;
				}
				
				BoundField field = name == null ? null : boundFields.get(name);
				if (field == null) {
					field = boundFields.get(PayloadTypeAdapterFactory.PROPERTIES_FIELD_NAME);
					if (field == null) {
						in.skipValue();
					} else {
						InternalPropertyMapBoundField fld = (InternalPropertyMapBoundField) field;
						fld.read(in, instance, name);
					}
				} else if (!field.shouldDeserialize) {
					in.skipValue();
				} else {
					field.read(in, instance);
				}
			}
			in.endObject();
		} catch (IllegalStateException e) {
			throw new JsonSyntaxException(e);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
		
		return instance;
	}

	@Override
	public void write(JsonWriter out, Payload value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		
		try {
			out.beginObject();
			for (BoundField boundField : boundFields.values()) {
				if (boundField.shouldSerialize) {
					boundField.write(out, value);
				}
			}
			out.endObject();
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}
