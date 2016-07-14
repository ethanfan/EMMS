/**
 * GsonTypeAdpaters.java
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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.google.gson.JsonFile;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LazilyParsedNumber;
import com.google.gson.internal.bind.JsonTreeWriter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.jaffer_datastore_android_sdk.reflect.$Class;

/**
 * Type adapters for types supported in the datastore.
 */
public final class GsonTypeAdpaters {
	
	static final String TAG = TypeAdapters.class.getSimpleName();
	
	public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter<JsonElement>() {
		
		@Override
		public void write(JsonWriter out, JsonElement value) throws IOException {

			if (value == null || value.isJsonNull()) {
				out.nullValue();
			} else if (value.isJsonPrimitive()) {
				if (isJsonFile(value)) {
					JsonFile file = (JsonFile) value;
					FILE.write(out, file.getAsFile());
				} else {					
					JsonPrimitive primitive = value.getAsJsonPrimitive();
					if (primitive.isNumber()) {
						out.value(primitive.getAsNumber());
					} else if (primitive.isBoolean()) {
						out.value(primitive.getAsBoolean());
					} else {
						out.value(primitive.getAsString());
					}
				}
			} else if (value.isJsonArray()) {
				out.beginArray();
				for (JsonElement e : value.getAsJsonArray()) {
					write(out, e);
				}
				out.endArray();

			} else if (value.isJsonObject()) {
				out.beginObject();
				for (Map.Entry<String, JsonElement> e : value.getAsJsonObject().entrySet()) {
					out.name(e.getKey());
					write(out, e.getValue());
				}
				out.endObject();
			} else {
				throw new IllegalArgumentException("Couldn't write " + value.getClass());
			}
		}

		@Override
		public JsonElement read(JsonReader in) throws IOException {
			switch (in.peek()) {
			case STRING:
				// FIXME: deserialize file
				return new JsonPrimitive(in.nextString());
			case NUMBER:
				String number = in.nextString();
				return new JsonPrimitive(new LazilyParsedNumber(number));
			case BOOLEAN:
				return new JsonPrimitive(in.nextBoolean());
			case NULL:
				in.nextNull();
				return JsonNull.INSTANCE;
			case BEGIN_ARRAY:
				JsonArray array = new JsonArray();
				in.beginArray();
				while (in.hasNext()) {
					array.add(read(in));
				}
				in.endArray();
				return array;
			case BEGIN_OBJECT:
				JsonObject object = new JsonObject();
				in.beginObject();
				while (in.hasNext()) {
					object.add(in.nextName(), read(in));
				}
				in.endObject();
				return object;
			case END_DOCUMENT:
			case NAME:
			case END_OBJECT:
			case END_ARRAY:
			default:
				throw new IllegalArgumentException();
			}
		}

		/**
		 * Returns whether the given {@link JsonElement} is a {@link File}
		 * 
		 * @param element
		 *          The {@link JsonElement} to examine
		 * @return {@code true} if the given {@code element} if a {@link JsonFile},
		 *         {@code false} otherwise
		 */
		private boolean isJsonFile(JsonElement element) {
			return element instanceof JsonFile;
		}
		
	};
	
	public static final TypeAdapter<File> FILE = new TypeAdapter<File>() {
		
		/** The method names to invoke in {@link JsonWriter} using reflection */
		static final String METHOD_WRITE_NAME = "writeDeferredName";
		static final String METHOD_BEFORE_VALUE = "beforeValue";
		
		static final String FIELD_WRITER = "out";
		
		@Override
		public void write(JsonWriter out, File value) throws IOException {
			if (value != null && value.exists()) {
				$Class.invokeMethod(METHOD_WRITE_NAME, out);
				$Class.invokeMethod(METHOD_BEFORE_VALUE, out, false);
				
				if (out instanceof JsonTreeWriter) {
					StringWriter writer = new StringWriter();
					streamValue(writer, value);
					out.value(writer.toString());
				} else {					
					Writer writer = (Writer) $Class.getFieldValue(FIELD_WRITER, out);
					streamValue(writer, value);
				}
			} else {
				out.nullValue();
			}
		}

		@Override
		public File read(JsonReader in) throws IOException {
			// FIXME: deserialize the base64 encoded string value into a file
			return null;
		}
		
		/**
		 * Streams the serialized data to {@code writer} 
		 * 
		 * @param writer The writer to write the serialized value to
		 * @param value The value to serialize
		 */
		private void streamValue(Writer writer, File value) throws IOException {
			if (writer != null) {
				writer.write("\"");
				
				Base64FileEncoder encoder = new Base64FileEncoder(value);
				encoder.encode(writer);
				
				writer.write("\"");
			}
		}
		
	};
	
	public static final TypeAdapterFactory FILE_FACTORY = TypeAdapters.newFactory(File.class, FILE);
	
	/** Prevent this class from being instantiated. */
	private GsonTypeAdpaters() {}

}
