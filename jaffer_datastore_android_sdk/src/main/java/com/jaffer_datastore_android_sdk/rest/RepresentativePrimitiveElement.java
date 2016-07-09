/**
 * RepresentativePrimitiveElement.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jaffer_datastore_android_sdk.datastore.PrimitiveElement;
import com.jaffer_datastore_android_sdk.representation.BufferedRepresentation;


/**
 * An implementation of {@link PrimitiveElement} that wraps a
 * {@link Representation}. This allows other types of data to be retrieved via
 * {@link Store#readElement}.
 */
public class RepresentativePrimitiveElement extends PrimitiveElement {
	
	private static final List<MediaType> TEXT_MEDIA_TYPES = ImmutableList.of(
			MediaType.TEXT_ALL,
			MediaType.APPLICATION_JSON,
			MediaType.APPLICATION_RTF,
			MediaType.APPLICATION_WADL,
			MediaType.APPLICATION_XMI,
			MediaType.APPLICATION_XML,
			MediaType.APPLICATION_YAML);
	
	/** The underlying representation of a remote resource. */
	private final Representation value;
	
	/**
	 * Constructor
	 * 
	 * @param representation the wrapped {@link Representation}
	 */
	public RepresentativePrimitiveElement(Representation representation) {
		// Uses a buffered representation to cache the content
		value = new BufferedRepresentation(representation, BufferedRepresentation.DEFAULT_SIZE_THRESHOLD, null, true);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		RepresentativePrimitiveElement other = (RepresentativePrimitiveElement) obj;
		if (value == null) {
			return other.value == null;
		}
		
		return value.equals(other.value);
	}
	
	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}

	@Override
	public boolean isString() {
		if (value != null) {
			for (MediaType textType : TEXT_MEDIA_TYPES) {
				if (textType.includes(value.getMediaType())) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean isNumber() {
		return false;
	}

	@Override
	public boolean isBoolean() {
		return false;
	}

	@Override
	public String toJson() {
		if (value != null && MediaType.APPLICATION_JSON.includes(value.getMediaType())) {
			try {
				Reader reader = value.getReader();
				JsonReader jsonReader = new JsonReader(reader);
				JsonElement element = new JsonParser().parse(jsonReader);
				return element.toString();
			} catch (Exception e) {
				// Ignored
			}
		}
		
		// Returns an empty string by default
		return "";
	}

	@Override
	public Representation toRepresentation() {
		return value;
	}
	
	@Override
	public byte valueAsByte() {
		byte[] bytes = valueAsByteArray();
		return bytes == null ? null : bytes[0];
	}
	
	@Override
	public byte[] valueAsByteArray() {
		InputStream stream = null;
		try {
			stream = value == null ? null : value.getStream();
			return stream == null ? null : IOUtils.toByteArray(stream);
		} catch (IOException e) {
			// Ignored
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// Ignore
				}
			}
		}
		return null;
	}
	
	@Override
	public String valueAsString() {
		if (isString()) {
			try {
				return value.getText();
			} catch (IOException e) {
				// Ignored
			}
		}
		return null;
	}

}
