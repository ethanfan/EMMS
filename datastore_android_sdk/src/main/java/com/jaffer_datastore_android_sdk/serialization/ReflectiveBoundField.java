/**
 * ReflectiveBoundField.java
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
package com.jaffer_datastore_android_sdk.serialization;

import java.io.IOException;
import java.lang.reflect.Field;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * A {@link BoundField} reflected on a Java class.
 */
public class ReflectiveBoundField extends BoundField {
	
	protected final Gson context;
	final TypeToken<?> fieldType;
	final TypeAdapter<?> typeAdapter;
	
	protected ReflectiveBoundField(
			Gson context,
			Field field,
			String name,
			TypeToken<?> fieldType,
			boolean serialize,
			boolean deserialize) {
		
		super(name, field, serialize, deserialize);
		
		this.context = context;
		this.fieldType = fieldType;
		typeAdapter = context.getAdapter(fieldType);
	}
	
	public static ReflectiveBoundField create(
			Gson context,
			Field field,
			String name,
			TypeToken<?> fieldType,
			boolean serialize,
			boolean deserialize) {
		
		return new ReflectiveBoundField(context, field, name, fieldType, serialize, deserialize);
	}
	
	// the type adapter and field type always agree
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
		Object fieldValue = field.get(value);
		TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(context, typeAdapter, fieldType.getType());
		writer.name(name);
		t.write(writer, fieldValue);
	}
	
	@Override
	public void read(JsonReader reader, final Object value) throws IOException, IllegalAccessException {
		boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
		
		Object fieldValue = typeAdapter.read(reader);
		if (fieldValue != null || !isPrimitive) {
			field.set(value, fieldValue);
		}
	}
	

}
