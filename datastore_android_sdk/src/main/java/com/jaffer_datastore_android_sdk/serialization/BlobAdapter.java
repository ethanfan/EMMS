/**
 * BlobAdapter.java
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
import java.io.Reader;
import java.lang.reflect.Field;

import org.restlet.data.Header;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.engine.header.HeaderReader;

import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jaffer_datastore_android_sdk.datastore.Build;


/**
 * De/serialize BLOB data.
 *
 * @param <T> the Java type the adapter de/serialize the data to
 */
public abstract class BlobAdapter<T> {

	protected static final String TAG = BlobAdapter.class.getSimpleName();

	/**
	 * Determines whether or not the given value is already in the desired
	 * deserialized form.
	 *
	 * @return true if the value was already in the desired deserialized format,
	 *         false otherwise
	 */
	protected boolean isDeserialized(Reader reader, int pos) {
		boolean result = true;
		try {
			String line = readToEndOfLine(reader, pos);
			Header header = HeaderReader.readHeader(line);
			if (HeaderConstants.HEADER_CONTENT_TYPE.equalsIgnoreCase(header.getName())
					|| HeaderConstants.HEADER_CONTENT_DISPOSITION.equalsIgnoreCase(header.getName())) {

				result = false;
			}
		} catch (Exception ex) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to read header from JSON", ex);
			}
		}
		return result;
	}

	/**
	 * Returns the file representation of the data.
	 *
	 * @param value
	 *          The value of the data
	 * @return The file representation the data, {@code null} if the data should
	 *         not be serialized.
	 */
	protected abstract File serialize(T value);

	/**
	 * Returns the deserialized value of the data.
	 *
	 * @param file
	 *          The deserialized BLOB data as a {@link File}
	 * @return The deserialized value of the data
	 */
	protected abstract T deserialize(File file);

	/**
	 * Writes the JSON value for {@code value}.
	 *
	 * @param writer
	 *          The {@link JsonWriter} to write to
	 * @param value
	 *          The Java value to write
	 * @throws IOException
	 *           Thrown if there was error writing to {@code writer}
	 */
	public void write(JsonWriter writer, T value) throws IOException {
		File file = serialize(value);
		GsonTypeAdpaters.FILE.write(writer, file);
	}

	/**
	 * Reads the JSON value and converts it into Java object of the given type.
	 *
	 * @param reader
	 *          The {@link JsonReader} to read data from
	 * @return The converted Java object
	 * @throws IOException
	 *           Thrown if there was error reading from {@code reader}
	 */
	public T read(JsonReader reader) throws IOException {
		if (!isDeserialized(getReader(reader), getCurrentReadPos(reader))) {
			File file = GsonTypeAdpaters.FILE.read(reader);
			return deserialize(file);
		} else {
			return deserialize(new File(reader.nextString()));
		}
	}

	/**
	 * Reads from the {@link Reader} until the next newline character.
	 *
	 * @param reader The {@link Reader} to read from
	 * @param pos The position to start reading from
	 * @return The string read
	 */
	private String readToEndOfLine(Reader reader, int pos) {
		StringBuilder builder = new StringBuilder();

		try {
			char[] buffer = new char[1];
			int position = pos;

			while (reader.read(buffer, position, 1) != -1) {
				builder.append(buffer);
				position++;
			}
		} catch (Exception ex) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to read from reader.", ex);
			}
		}

		return builder.toString();
	}

	/**
	 * Uses reflection to retrieve the underlying {@link Reader} in
	 * {@link JsonReader}.
	 *
	 * @param in
	 *          The {@link JsonReader} to retrieve the {@link Reader} object
	 * @return The underlying {@link Reader} used in {@link JsonReader},
	 *         {@code null} if the {@link Reader} cannot be retrieved
	 */
	private Reader getReader(JsonReader in) {
		Reader result = null;
		try {
			Field field = JsonWriter.class.getDeclaredField("in");
			field.setAccessible(true);
			result = (Reader) field.get(in);
		} catch (Exception ex) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to retrieve the underlying reader", ex);
			}
		}
		return result;
	}

	private int getCurrentReadPos(JsonReader in) {
		int pos = -1;
		try {
			Field field = JsonWriter.class.getDeclaredField("pos");
			field.setAccessible(true);
			pos = field.getInt(in);
		} catch (Exception ex) {
			if (Build.DEBUG) {
				Log.e(TAG, "Failed to retrieve the current reading position in reader", ex);
			}
		}
		return pos;
	}

}
