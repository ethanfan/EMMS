/**
 * OrmRawResultElement.java
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

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.datastore.DataElement;
import com.google.gson.JsonObject;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.representation.GsonRepresentation;
import com.datastore_android_sdk.sqlite.internal.$Orm$Preconditions;


/**
 * An implementation of {@link ObjectElement} that is a wrapper around a raw result from ORMLite.
 */
public class OrmRawResultElement extends ObjectElement {
	
	/** An array of column names. */
	private final String[] columnNames;
	
	/** The underlying value of the raw result. */
	private final String[] values;
	
	public OrmRawResultElement(String[] columnNames, String[] values) {
		$Orm$Preconditions.checkNotNull(values);
		$Orm$Preconditions.checkNotNull(columnNames);
		$Orm$Preconditions.checkArgument(columnNames.length == values.length);
		
		this.values = values;
		this.columnNames = columnNames;
	}

	@Override
	public boolean has(List<String> memberPath) {
		return get(memberPath) != null;
	}

	/**
	 * <b>Note:</b> Unlike {@link OrmObjectElement}, the member name is the actual
	 * column name, not the serialized column name, returned from the query.
	 */
	@Override
	public DataElement get(List<String> memberPath) {
		DataElement result = null;
		
		// Raw result does not support nested objects 
		if (memberPath.size() == 1) {
			int index = getColumnIndex(memberPath.get(0));
			if (index >= 0 && index < values.length) {
				result = new OrmPrimitiveElement(values[index]);
			}
		}
			
		return result;
	}
	
	@Override
	public String[] allKeys() {
		return columnNames;
	}

	@Override
	public void set(String property, String value) {
		if (property != null) {
			int index = getColumnIndex(property);
			if (index >= 0 && index < values.length) {
				values[index] = value;
			}
		}
	}

	@Override
	public void set(String property, Number value) {
		set(property, value == null ? null : value.toString());
	}

	@Override
	public void set(String property, Boolean value) {
		set(property, value == null ? null : value.toString());
	}

	@Override
	public void set(String property, Character value) {
		set(property, value == null ? null : value.toString());
	}

	@Override
	public void set(String property, Date value) {
		set(property, value == null ? null : Long.valueOf(value.getTime()).toString());
	}
	
	@Override
	public void set(String property, File value) {
		set(property, value == null ? null : value.toURI().toString());
	}

	@Override
	public void set(String property, DataElement element) {
		set(property, element == null ? null : element.toJson());
	}
	
	/**
	 * Returns a {@link JsonObject} representation of this element.
	 * 
	 * @return The {@link JsonObject} representation of this element
	 */
	private JsonObject toJsonObject() {
		JsonObject result = new JsonObject();
		for (int i = 0; i < columnNames.length; i++) {
			result.addProperty(columnNames[i], values[i]);
		}
		return result;
	}

	@Override
	public String toJson() {
		return toJsonObject().toString();
	}
	
	@Override
	public Representation toRepresentation() {
		return new GsonRepresentation(toJsonObject());
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		final int prime = 31;
		if (values != null) {
			result = prime + values.hashCode();
		}
		if (columnNames != null) {
			int rst = result == 0 ? prime : prime * result;
			result = rst + columnNames.hashCode();
		}
		return result == 0 ? super.hashCode() : result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		OrmRawResultElement other = (OrmRawResultElement) obj;
		if (values == null && columnNames == null) {
			return other.values == null && other.columnNames == null;
		} else if (values != null && columnNames == null) {
			return values.equals(other.values) && other.columnNames == null;
		} else if (values == null && columnNames != null) {
			return other.values == null
					&& Arrays.equals(columnNames, other.columnNames);
		} else {
			return values.equals(other.values)
					&& Arrays.equals(columnNames, other.columnNames);
		}
	}
	
	/**
	 * Returns the index of the column identified by {@code columnName}.
	 * 
	 * @param columnName The name of the column to return the index
	 * @return The index of the column or -1 if the column is not found
	 */
	protected int getColumnIndex(String columnName) {
		int index = -1;
		for (int i = 0; i < columnNames.length; i++) {
			if (columnName.equals(columnNames[i])) {
				index = i;
				break;
			}
		}
		return index;
	}

}
