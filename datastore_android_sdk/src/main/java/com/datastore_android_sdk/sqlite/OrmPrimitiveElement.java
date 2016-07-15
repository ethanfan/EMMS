/**
 * OrmPrimitiveElement.java
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
package com.datastore_android_sdk.sqlite;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.UUID;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.google.gson.internal.LazilyParsedNumber;
import com.datastore_android_sdk.datastore.PrimitiveElement;
import com.datastore_android_sdk.sqlite.internal.$Orm$Preconditions;


/**
 * An implementation of {@link PrimitiveElement} that wraps a primitive data type in OrmLite.
 * 
 * @see <a href="http://ormlite.com/data_types.shtml">OrmLite SQL Data Types - Lightweight Java ORM</a>
 */
public class OrmPrimitiveElement extends PrimitiveElement {
	
	/** An array of primitive types supported in ORM. */
	private static final Class<?>[] ORM_TYPES = { String.class, Date.class, UUID.class };
	
	private Object value;
	
	/**
	 * Construct a primitive containing a boolean value.
	 * 
	 * @param value The underlying primitive value
	 */
	public OrmPrimitiveElement(Boolean value) {
		setValue(value);
	}
	
	/**
	 * Construct a primitive containing a {@link Number}.
	 * 
	 * @param value The underlying number value
	 */
	public OrmPrimitiveElement(Number value) {
		setValue(value);
	}
	
	/**
	 * Construct a primitive containing a {@link String}.
	 * 
	 * @param value The underlying string value
	 */
	public OrmPrimitiveElement(String value) {
		setValue(value);
	}
	
	/**
	 * Construct a primitive containing a {@link Character}.
	 * 
	 * @param value The underlying character value
	 */
	public OrmPrimitiveElement(Character value) {
		setValue(value);
	}
	
	/**
	 * Construct a primitive containing a {@link Date}.
	 * 
	 * @param value The underlying date value
	 */
	public OrmPrimitiveElement(Date value) {
		setValue(value);
	}

	/**
	 * Construct a primitive containing the given {@code value}. {@code value}
	 * must be one of the ORM supported types i.e. String, UUID, Date, or Java
	 * primitive types
	 * 
	 * @param value The underlying value of this primitive
	 */
	public OrmPrimitiveElement(Object value) {
		setValue(value);
	}
	
	/**
	 * Construct a primitive containing a {@link UUID}.
	 * 
	 * @param value The underlying UUID value
	 */
	public OrmPrimitiveElement(UUID value) {
		setValue(value);
	}
	
	void setValue(Object primitive) {
		$Orm$Preconditions.checkArgument(primitive instanceof Number || isOrmPrimitive(primitive));
		this.value = primitive;
	}

	/**
	 * Determines whether the given object is a primitive data type or object
	 * types natively supported in OrmLite.
	 * 
	 * @param target
	 *          The object to determine if it is a primitive data type or types
	 *          that are supported in OrmLite
	 * @return {@code true} if the object is a primitive data type or types that
	 *         are supported in OrmLite, {@code false} otherwise
	 */
	public static boolean isOrmPrimitive(Object target) {
		Class<?> classOfPrimitive = target.getClass();
		
		if (classOfPrimitive.isArray()) {
	    	classOfPrimitive = classOfPrimitive.getComponentType();
	    }
		
		for (Class<?> ormPrimitive : ORM_TYPES) {
			if (ormPrimitive.isAssignableFrom(classOfPrimitive)) {
				return true;
			}
		}
    
		for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
			if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the underlying value as a {@link Boolean}.
	 * 
	 * @return The underlying value as a {@link Boolean}
	 */
	private Boolean getBooleanWrapper() {
		return (Boolean) value;
	}
	
	@Override
	public boolean valueAsBoolean() {
		if (isBoolean()) {
			return ((Boolean) value).booleanValue();
		} else {
			// Check to see if the value as a String is "true" in any case.
      return Boolean.parseBoolean(valueAsString());
		}
	}
	
	@Override
	public Number valueAsNumber() {
		return isString() ? new LazilyParsedNumber(valueAsString()) : (Number) value;
	}
	
	@Override
	public String valueAsString() {
		if (isNumber()) {
			return valueAsNumber().toString();
		} else if (isBoolean()) {
			return getBooleanWrapper().toString();
		} else {
			return value == null ? null : value.toString();
		}
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid double.
	 */
	@Override
	public double valueAsDouble() {
		return isNumber() ? valueAsNumber().doubleValue() : Double.parseDouble(valueAsString());
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid {@link BigDecimal}.
	 */
	@Override
	public BigDecimal valueAsBigDecimal() {
		return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid {@link BigInteger}.
	 */
	@Override
	public BigInteger valueAsBigInteger() {
		 return value instanceof BigInteger ? (BigInteger) value : new BigInteger(value.toString());
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid float.
	 */
	@Override
	public float valueAsFloat() {
		return isNumber() ? valueAsNumber().floatValue() : Float.parseFloat(valueAsString());
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid long.
	 */
	@Override
	public long valueAsLong() {
		return isNumber() ? valueAsNumber().longValue() : Long.parseLong(valueAsString());
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid short.
	 */
	@Override
	public short valueAsShort() {
		return isNumber() ? valueAsNumber().shortValue() : Short.parseShort(valueAsString());
	}
	
	/**
	 * @throws NumberFormatException if the value contained is not a valid integer.
	 */
	@Override
	public int valueAsInt() {
		return isNumber() ? valueAsNumber().intValue() : Integer.parseInt(valueAsString());
	}
	
	/**
	 * @throws NumberFormatException if value contained is not a byte value
	 */
	@Override
	public byte valueAsByte() {
		return isNumber() ? valueAsNumber().byteValue() : Byte.parseByte(valueAsString());
	}
	
	@Override
	public char valueAsCharacter() {
		return valueAsString().charAt(0);
	}

	@Override
	public boolean isString() {
		// Characters and UUID are considered as string in ORM
		return value instanceof String 
				|| value instanceof Character
				|| value instanceof UUID;
	}

	@Override
	public boolean isNumber() {
		return value instanceof Number;
	}

	@Override
	public boolean isBoolean() {
		return value instanceof Boolean;
	}
	
	/**
	 * Returns whether this primitive is a date.
	 * 
	 * @return {@code true} if this primitive is a date, {@code false} otherwise
	 */
	public boolean isDate() {
		return value instanceof Date;
	}
		
	/**
	 * Convenience method to get this element as a {@link Date}.
	 * 
	 * @return This element as a {@link Date} or {@code null} if this element is
	 *         not a {@link PrimitiveElement} or is not a valid number
	 */
	public Date valueAsDate() {
		if (isDate()) {
			return (Date) value;
		} else if (isNumber()) {
			return new Date(valueAsLong());
		} else {
			return null;
		}
	}
	
	/**
	 * Convenience method to get this element as a byte[].
	 * 
	 * @return This element as a byte[] or {@code null} if this element is
	 *         not a {@link PrimitiveElement} or is not a Array
	 */
	@Override
	public byte[] valueAsByteArray() {
		Class<?> componentType = value.getClass().getComponentType();
		if (componentType != null && 
				byte.class.isAssignableFrom(componentType)) {
			return (byte[]) value;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
	@Override
	public int hashCode() {
		return value == null ? super.hashCode() : value.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		OrmPrimitiveElement other = (OrmPrimitiveElement) obj;
		if (value == null) {
			return other.value == null;
		}
		if (isIntegral(this) && isIntegral(other)) {
			return valueAsNumber().longValue() == other.valueAsNumber().longValue();
		}
		
		if (value instanceof Number && other.value instanceof Number) {
			double a = valueAsNumber().doubleValue();
			// Java standard types other than double return true for two NaN.
			// So, need special handling for double.
			double b = other.valueAsNumber().doubleValue();
			return a == b || Double.isNaN(a) && Double.isNaN(b);
		}
		return value.equals(other.value);
	}
	
	/**
	 * Returns true if the specified number is an integral type (Long, Integer,
	 * Short, Byte, BigInteger).
	 */
	private static boolean isIntegral(OrmPrimitiveElement primitive) {
		if (primitive.value instanceof Number) {
			Number number = (Number) primitive.value;
			return number instanceof BigInteger || number instanceof Long
					|| number instanceof Integer || number instanceof Short
					|| number instanceof Byte;
		}
		return false;
	}

	@Override
	public String toJson() {
		return valueAsString();
	}
	
	@Override
	public Representation toRepresentation() {
		return new StringRepresentation(valueAsString());
	}

}
