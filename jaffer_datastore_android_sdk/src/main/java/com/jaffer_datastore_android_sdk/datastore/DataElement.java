/**
 * DataElement.java
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
package com.jaffer_datastore_android_sdk.datastore;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.restlet.representation.Representation;

import com.google.gson.JsonPrimitive;

/**
 * A class representing an element of data. A data element can be an
 * {@link ObjectElement}, an {@link ArrayElement} or a {@link PrimitiveElement}
 */
public abstract class DataElement {

	/**
	 * Determines whether or not this element is a data object.
	 * 
	 * @return {@code true} if this element is a data object, {@code false}
	 *         otherwise
	 */
	public boolean isObject() {
		return this instanceof ObjectElement;
	}
	
	/**
	 * Determines whether or not this element is an array.
	 * 
	 * @return {@code true} if this element is of type {@link ArrayElement},
	 *         {@code false} otherwise
	 */
	public boolean isArray() {
		return this instanceof ArrayElement;
	}
	
	/**
	 * Determines whether or not this element is a primitive.
	 * 
	 * @return {@code true} if this element is of type {@link PrimitiveElement},
	 *         {@code false} otherwise
	 */
	public boolean isPrimitive() {
		return this instanceof PrimitiveElement;
	}
	
	/**
	 * Returns whether or not this data element is {@code null}.
	 * 
	 * @return {@code true} if this element is of type {@link NullElement},
	 *         {@code false} otherwise
	 */
	public boolean isNull() {
		return this instanceof NullElement;
	}

	/**
	 * Convenience method to return this element as an {@link ArrayElement}. If
	 * this element is not an {@link ArrayElement}, {@code null} will be returned.
	 * 
	 * @return This element as an {@link ArrayElement}
	 */
	public ArrayElement asArrayElement() {
		if (isArray()) {
			return (ArrayElement) this;
		}
		return null;
	}
	
	/**
	 * Convenience method to return this element as an {@link ObjectElement}. If
	 * this element is not an {@link ObjectElement}, {@code null} will be returned.
	 * 
	 * @return This element as an {@link ObjectElement}
	 */
	public ObjectElement asObjectElement() {
		if (isObject()) {
			return (ObjectElement) this;
		}
		return null;
	}
	
	/**
	 * Convenience method to return this element as an {@link PrimitiveElement}. If
	 * this element is not an {@link PrimitiveElement}, {@code null} will be returned.
	 * 
	 * @return This element as an {@link PrimitiveElement}
	 */
	public PrimitiveElement asPrimitiveElement() {
		if (isPrimitive()) {
			return (PrimitiveElement) this;
		}
		return null;
	}
	
	/**
	 * Convenience method to return this element as an {@link NullElement}. If
	 * this element is not an {@link NullElement}, {@code null} will be returned.
	 * 
	 * @return This element as an {@link NullElement}
	 */
	public NullElement asNullElement() {
		if (isNull()) {
			return (NullElement) this;
		}
		return null;
	}

	/**
	 * Convenience to method to get this element as a boolean value.
	 * 
	 * @return This element as a boolean value
	 */
	Boolean getAsBooleanWrapper() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a boolean value.
	 * 
	 * @return This element as a primitive boolean value
	 * @throws ClassCastException
	 *             Thrown if the element is of not a {@link JsonPrimitive} and
	 *             is not a valid boolean value.
	 */
	public boolean valueAsBoolean() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a {@link Number}.
	 * 
	 * @return This element as a {@link Number} or {@code null} if this element is
	 *         not a {@link PrimitiveElement} or is not a valid number
	 */
	public Number valueAsNumber() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a {@link Number}.
	 * 
	 * @return This element as a {@link Number} or {@code null} if this element is
	 *         not a {@link PrimitiveElement} or is not a valid number
	 */
	public String valueAsString() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a primitive double.
	 * 
	 * @return This element as a primitive double value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid double value.
	 */
	public double valueAsDouble() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a {@link BigDecimal}.
	 * 
	 * @return This element as a {@link BigDecimal}
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid {@link BigDecimal}.
	 */
	public BigDecimal valueAsBigDecimal() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a {@link BigInteger}.
	 * 
	 * @return This element as a {@link BigInteger}
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid {@link BigInteger}.
	 */
	public BigInteger valueAsBigInteger() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a primitive float value.
	 * 
	 * @return This element as a primitive float value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid float value.
	 */
	public float valueAsFloat() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a primitive long value.
	 * 
	 * @return This element as a primitive long value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid long value.
	 */
	public long valueAsLong() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a primitive short value.
	 * 
	 * @return This element as a primitive short value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid short value.
	 */
	public short valueAsShort() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a primitive integer value.
	 * 
	 * @return This element as a primitive integer value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid integer value.
	 */
	public int valueAsInt() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a primitive byte value.
	 * 
	 * @return This element as a primitive byte value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid byte value.
	 */
	public byte valueAsByte() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	/**
	 * Convenience method to get this element as a primitive byte array value
	 * 
	 * @return this element as a primitive byte array value
	 * @throws ClassCastException
	 *           Thrown if the element is of not a {@link PrimitiveElement} and is
	 *           not a valid byte value.
	 */
	public byte[] valueAsByteArray() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}

	/**
	 * Convenience method to get this element as a {@link Number}.
	 * 
	 * @return This element as a {@link Number} or {@code null} if this element is
	 *         not a {@link PrimitiveElement} or is not a valid number
	 */
	public char valueAsCharacter() {
		throw new UnsupportedOperationException(getClass().getSimpleName());
	}
	
	/**
	 * Converts this data element into its equivalent JSON representation.
	 * 
	 * @return JSON string representation of this data element
	 */
	public abstract String toJson();
	
	/**
	 * Converts this data element into a {@link Representation}.
	 * 
	 * @return Representation of this data element
	 */
	public abstract Representation toRepresentation();
	
}
