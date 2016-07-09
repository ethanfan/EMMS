/**
 * BoundField.java
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

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * A field in a {@link Class} to be de/serialized to JSON.
 */
public abstract class BoundField {
	
	/** The name of the field. */
	final String name;
	
	/** The underlying {@link Field} this bound field represents. */
	final Field field;
	
	/** Determines whether this field should be serialized. */
	final boolean shouldSerialize;
	
	/** Determines whether this field should be deserialized. */
	final boolean shouldDeserialize;

	protected BoundField(
			String name, 
			Field field,
			boolean shouldSerialize, 
			boolean shouldDeserialize) {
		
		this.name = name;
		this.field = field;
		this.shouldSerialize = shouldSerialize;
		this.shouldDeserialize = shouldDeserialize;
	}
	
	/**
	 * Writes {@code value} to JSON.
	 * 
	 * @param writer The writer to write the JSON value to
	 * @param value The object to retrieve the value
	 * @throws IOException thrown if there was an error writing to JSON
	 * @throws IllegalAccessException thrown if the object cannot be accessed
	 */
	public abstract void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException;

	/**
	 * Reads one JSON value.
	 * 
	 * @param reader The reader to read the JSON value from
	 * @param value The object to receive the read value
	 * @throws IOException thrown if there was an error reading the JSON value
	 * @throws IllegalAccessException thrown if the object value cannot be set
	 */
	public abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;

}

