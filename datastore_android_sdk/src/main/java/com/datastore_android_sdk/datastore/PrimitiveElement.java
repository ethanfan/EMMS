/**
 * PrimitiveElement.java
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
package com.datastore_android_sdk.datastore;

/**
 * A class representing a primitive data. A primitive value is either a
 * {@link String} or a Java primitive
 */
public abstract class PrimitiveElement extends DataElement {
	
	public static final Class<?>[] PRIMITIVE_TYPES = { 
		int.class, 
		long.class, 
		short.class,
	    float.class, 
	    double.class, 
	    byte.class, 
	    boolean.class, 
	    char.class, 
	    Integer.class, 
	    Long.class,
	    Short.class, 
	    Float.class, 
	    Double.class, 
	    Byte.class, 
	    Boolean.class, 
	    Character.class };

	/**
	 * Returns whether this primitive is a {@link String}.
	 * 
	 * @return {@code true} if this element is a {@link String}, {@code false} otherwise
	 */
	public abstract boolean isString();
	
	/**
	 * Returns whether or not this primitive is a {@link Number}.
	 * 
	 * @return {@code true} if this element is a number, {@code false} otherwise
	 */
	public abstract boolean isNumber();
	
	/**
	 * Returns whether or not this primitive contains a boolean value.
	 * @return {@code true} if this element contains a boolean value, {@code false} otherwise
	 */
	public abstract boolean isBoolean();
	
}
