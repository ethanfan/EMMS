/** 
 * JsonFile.java
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
package com.google.gson;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.jaffer_datastore_android_sdk.datastore.Build;
import com.jaffer_datastore_android_sdk.serialization.Base64FileEncoder;


/**
 * An JSON element that represents a {@link File}. When serialized, the file is
 * encoded in a base64 encoded string.
 */
public  class JsonFile extends JsonElement {
	
	/** The Content headers supported. */
	public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
	
	/** The wrapped File. */
	private final File file;
	
	/**
	 * Creates a new instance of {@link JsonFile} wrapping the given {@code file}.
	 * 
	 * @param file The {@link File} wrapped by this element
	 */
	public JsonFile(File file) {
		this.file = file;
	}

	@Override
	JsonElement deepCopy() {
		return new JsonFile(file);
	}
	
	/**
	 * A {@link JsonFile} is a special type of primitive.
	 * 
	 * @return Always return {@code true} 
	 */
	@Override
	public boolean isJsonPrimitive() {
		return true;
	}
	
	@Override
	public JsonPrimitive getAsJsonPrimitive() {
		return new JsonPrimitive(getAsString());
	}
	
	@Override
	public String getAsString() {
		return toString();
	}
	
	/**
	 * Returns this element as a {@link File}.
	 * 
	 * @return {@link File} representation of this element
	 */
	public File getAsFile() {
		return file;
	}
	
	@Override
	public String toString() {
		if (getAsFile() != null) {
			try {
				StringWriter writer = new StringWriter();
				Base64FileEncoder encoder = new Base64FileEncoder(getAsFile());
				encoder.encode(writer);
				return writer.toString();
			} catch (IOException ex) {
				if (Build.DEBUG) {
					Log.e(getClass().getSimpleName(), "Failed to serialize the file.", ex);
				}
			}
		}
		return JsonNull.INSTANCE.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		JsonFile other = (JsonFile) obj;
		if (file == null) {
			return other.file == null;
		}

		return file.equals(other.file);
	}

	@Override
	public int hashCode() {
		return file == null ? super.hashCode() : file.hashCode();
	}

}
