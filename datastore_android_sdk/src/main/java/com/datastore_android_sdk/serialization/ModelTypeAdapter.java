/**
 * ModelTypeAdapter.java
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
import java.util.Collection;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * The adapter that serializes/deserializes a data model.
 *  
 * @author Stanley Lam
 *
 * @param <T>
 */
public class ModelTypeAdapter<T> extends DaoTypeAdapter<T> {
	
	protected final ObjectConstructor<T> constructor;
	protected final Map<String, ModelBoundField> boundFields;
	protected ModelSerializationStrategy serializationStrategy;
	
	public ModelTypeAdapter(
			ObjectConstructor<T> constructor, 
			ModelSerializationStrategy serializationStrategy, 
			Map<String, ModelBoundField> boundFields) {
		
		this.constructor = constructor;
		this.serializationStrategy = serializationStrategy;
		this.boundFields = boundFields;
	}
	
	protected ModelBoundField getIdField() {
		Collection<ModelBoundField> fields = boundFields.values();
		for (ModelBoundField field : fields) {
			if (field.isIdField()) {
				return field;
			}
		}
		return null;
	}
	
	@Override
	public ModelSerializationStrategy setSerializationStrategy(
			ModelSerializationStrategy strategy) {
		
		ModelSerializationStrategy returnValue = this.serializationStrategy;
		serializationStrategy = strategy;
		return returnValue;
	}
	
	@Override
	public T read(JsonReader in) throws IOException {
		if (in.peek() == JsonToken.NULL) {
			in.nextNull();
			return null;
		}

		T instance = constructor.construct();

		try {
			JsonToken token = in.peek();
			switch (token) {
			case BEGIN_OBJECT:
				in.beginObject();
				while (in.hasNext()) {
					String name = in.nextName();
					BoundField field = boundFields.get(name);
					if (field == null || !field.shouldDeserialize) {
						in.skipValue();
					} else {
						field.read(in, instance);
					}
				}
				in.endObject();
				break;
			case NUMBER:
			case STRING:
				BoundField field = getIdField();
				if (field != null) {
					field.read(in, instance);
				}
				break;
			default:
				break;
			}
		} catch (IllegalStateException e) {
			throw new JsonSyntaxException(e);
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}

		return instance;
	}

	@Override
	public void write(JsonWriter out, T value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		// should serialize the ID field only if no serialization policy was 
		// specified for the 'Model'
		boolean serializeIdFieldOnly = serializationStrategy == null ? true
				: serializationStrategy.shouldSerializeIdFieldOnly();
		
		try {
			if (!serializeIdFieldOnly) {
				out.beginObject();
				for (ModelBoundField boundField : boundFields.values()) {
					boolean shouldSkipField = serializationStrategy == null ? false
							: serializationStrategy.shouldSkipField(boundField.field);
					if (boundField.shouldSerialize && !shouldSkipField) {
						out.name(boundField.name);
						boundField.write(out, value, serializationStrategy);
					}
				}
				out.endObject();
			} else {
				ModelBoundField field = getIdField();
				if (field != null) {
					field.write(out, value, serializationStrategy);
				}
			}
		} catch (IllegalAccessException e) {
			throw new AssertionError(e);
		}
	}
	
}
