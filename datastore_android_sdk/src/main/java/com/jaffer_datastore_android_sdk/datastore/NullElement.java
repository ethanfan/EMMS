/**
 * NullElement.java
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

import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

/**
 * A class representing a {@code null} data value.
 */
public class NullElement extends DataElement {
	
	/**
	 * A singleton.
	 */
	public static final NullElement INSTANCE = new NullElement();
	
	@Override
	public int hashCode() {
		return NullElement.class.hashCode();
	}
	
	/**
	 * All instances of {@link NullElement} are the same.
	 * 
	 * @param other the object to verify against
	 * @return {@code true} if the {@code other} object is an
	 *         {@link NullElement}, {@code false} otherwise
	 */
	@Override
	public boolean equals(Object other) {
		return this == other || other instanceof NullElement;
	}
	
	@Override
	public String toString() {
		return "null";
	}
	
	@Override
	public String toJson() {
		return toString();
	}
	
	@Override
	public Representation toRepresentation() {
		return new EmptyRepresentation();
	}

}
