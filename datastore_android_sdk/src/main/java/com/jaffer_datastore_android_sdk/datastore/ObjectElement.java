/**
 * ObjectElement.java
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

import com.jaffer_datastore_android_sdk.schema.Query;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A class representing a data object.
 */
public abstract class ObjectElement extends DataElement {

	/**
	 * Returns whether or not a member with the given name is present in the data
	 * object.
	 * 
	 * @param memberName
	 *          The name of the member to check for existence. (e.g. user/role)
	 * @return {@code true} if a member with the given name exists in the data
	 *         object, {@code false} otherwise
	 */
	public boolean has(String memberName) {
		List<String> path = memberName == null ? null : Arrays.asList(memberName.split("/"));
		return has(path);
	}

	/**
	 * Returns whether or not a member identified by the path components is
	 * present in the data object.
	 * 
	 * @param memberPath
	 *          The path components of the member requested
	 * @return {@code true} if the member identified by the path components exists
	 *         in the data object, {@code false} otherwise
	 */
	public abstract boolean has(List<String> memberPath);

	/**
	 * Returns the member with the specified name.
	 * 
	 * @param memberName
	 *          The name of the member requested.
	 * @return The data element of the member requested, {@code null} if the
	 *         member requested does not exist
	 */
	public DataElement get(String memberName) {
		List<String> path = memberName == null ? null : Arrays.asList(memberName.split(Query.FIELD_SEPARATOR));
		return get(path);
	}

	/**
	 * Returns the member with the identified by the path components.
	 * 
	 * @param memberPath
	 *          The path components
	 * @return The data element of the member requested, {@code null} if the
	 *         member requested does not exist
	 */
	public abstract DataElement get(List<String> memberPath);
	
	/**
	 * Returns an array of keys in this data object.
	 * 
	 * @return an array of keys in this data object
	 */
	public abstract String[] allKeys();
	
	/**
	 * Sets value of the member.
	 * 
	 * @param property
	 *          The name of the member
	 * @param value
	 *          The value of the member
	 */
	public abstract void set(String property, String value);
	
	/**
	 * Sets value of the member.
	 * 
	 * @param property
	 *          The name of the member
	 * @param value
	 *          The number value associated with the member.
	 */
	public abstract void set(String property, Number value);
	
	/**
	 * Sets value of the member.
	 * 
	 * @param property
	 *          The name of the member
	 * @param value
	 *          The boolean value associated with the member.
	 */
	public abstract void set(String property, Boolean value);
	
	/**
	 * Sets value of the member.
	 * 
	 * @param property
	 *          The name of the member
	 * @param value
	 *          The character value associated with the member.
	 */
	public abstract void set(String property, Character value);
	
	/**
	 * Sets value of the member.
	 * 
	 * @param property The name of the member
	 * @param value The date value associated with the member;
	 */
	public abstract void set(String property, Date value);

	/**
	 * Sets value of the member.
	 * 
	 * @param property
	 *          The name of the member
	 * @param value
	 *          The file value associated with the member
	 */
	public abstract void set(String property, File value);

	/**
	 * Sets value of the member.
	 * 
	 * @param property
	 *          The name of the member
	 * @param element
	 *          The data object value associated with the member
	 */
	public abstract void set(String property, DataElement element);
	
	/**
	 * Returns the member with the given name as a {@link PrimitiveElement}.
	 * 
	 * @param memberName The name of the member requested.
	 * @return The {@link PrimitiveElement} corresponding to the specified member,
	 *         {@code null} if the member requested does not exist or if it is not
	 *         an {@link PrimitiveElement}
	 */
	public PrimitiveElement getAsPrimitiveElement(String memberName) {
		DataElement result = get(memberName);
		if (result != null && result.isPrimitive()) {
			return (PrimitiveElement) result;
		}
		return null;
	}

	/**
	 * Returns the member with the given name as a {@link ArrayElement}.
	 * 
	 * @param memberName
	 *          The name of the member requested.
	 * @return The {@link ArrayElement} corresponding to the specified member,
	 *         {@code null} if the member requested does not exist or if it is not
	 *         an {@link ArrayElement}
	 */
	public ArrayElement getAsArrayElement(String memberName) {
		DataElement result = get(memberName);
		if (result != null && result.isArray()) {
			return (ArrayElement) result;
		}
		return null;
	}

	/**
	 * Returns the member with the given name as a {@link ArrayElement}.
	 * 
	 * @param memberName
	 *          The name of the member requested.
	 * @return The {@link ObjectElement} corresponding to the specified member,
	 *         {@code null} if the member requested does not exist or if it is not
	 *         an {@link ObjectElement}
	 */
	public ObjectElement getAsObjectElement(String memberName) {
		DataElement result = get(memberName);
		if (result != null && result.isObject()) {
			return (ObjectElement) result;
		}
		return null;
	}

}
