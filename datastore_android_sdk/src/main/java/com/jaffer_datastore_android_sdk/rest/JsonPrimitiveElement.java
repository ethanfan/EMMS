/**
 * JsonPrimitiveElement.java
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
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.restlet.representation.Representation;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonWriter;
import com.jaffer_datastore_android_sdk.datastore.DataElement;
import com.jaffer_datastore_android_sdk.datastore.NullElement;
import com.jaffer_datastore_android_sdk.datastore.PrimitiveElement;
import com.jaffer_datastore_android_sdk.representation.GsonRepresentation;
import com.jaffer_datastore_android_sdk.serialization.GsonTypeAdpaters;


/**
 * An implementation of {@link PrimitiveElement} that wraps a {@link JsonPrimitive}.
 */
public final class JsonPrimitiveElement extends PrimitiveElement implements JsonDataElement {
	
	/** The primitive value this element wraps. */
	private final JsonPrimitive value;
	
	/**
	 * Creates a new instance of {@link PrimitiveElement} that wraps {@code primitive}.
	 * 
	 * @param primitive The underlying {@link JsonPrimitive} this data element represents. Cannot be {@code null}
	 */
	public JsonPrimitiveElement(JsonPrimitive primitive) {
		if (primitive == null) {
			throw new NullPointerException("The wrapped value cannot be null");
		}
		value = primitive;
	}

	@Override
	public boolean isString() {
		return value.isString();
	}

	@Override
	public boolean isNumber() {
		return value.isNumber();
	}

	@Override
	public boolean isBoolean() {
		return value.isBoolean();
	}
	
	@Override
	public JsonElement getData() {
		return value;
	}
	
	@Override
	public boolean valueAsBoolean() {
		return value.getAsBoolean();
	}
	
	@Override
	public Number valueAsNumber() {
		return value.getAsNumber();
	}
	
	@Override
	public String valueAsString() {
		return value.getAsString();
	}
	
	@Override
	public double valueAsDouble() {
		return value.getAsDouble();
	}
	
	@Override
	public BigDecimal valueAsBigDecimal() {
		return value.getAsBigDecimal();
	}
	
	@Override
	public BigInteger valueAsBigInteger() {
		return value.getAsBigInteger();
	}
	
	@Override
	public float valueAsFloat() {
		return value.getAsFloat();
	}
	
	@Override
	public long valueAsLong() {
		return value.getAsLong();
	}
	
	@Override
	public short valueAsShort() {
		return value.getAsShort();
	}
	
	@Override
	public int valueAsInt() {
		return value.getAsInt();
	}
	
	@Override
	public byte valueAsByte() {
		return value.getAsByte();
	}
	
	@Override
	public byte[] valueAsByteArray() {
		return value.getAsString().getBytes();
	}
	
	@Override
	public char valueAsCharacter() {
		return value.getAsCharacter();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		JsonPrimitiveElement other = (JsonPrimitiveElement) obj;
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
	public String toString() {
		try {
			StringWriter stringWriter = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(stringWriter);
			jsonWriter.setLenient(true);
			GsonTypeAdpaters.JSON_ELEMENT.write(jsonWriter, value);
			return stringWriter.toString();
		} catch (IOException ex) {
			return NullElement.INSTANCE.toString();
		}
	}
	
	@Override
	public Representation toRepresentation() {
		return new GsonRepresentation(value) {

			@Override
			public DataElement getDataElement() {
				return JsonPrimitiveElement.this;
			}
			
		};
	}
	
	@Override
	public String toJson() {
		return toString();
	}

}
