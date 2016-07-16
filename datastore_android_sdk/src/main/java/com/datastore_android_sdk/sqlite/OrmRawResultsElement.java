/**
 * OrmRawResultsElement.java
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.datastore.DataElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.j256.ormlite.dao.GenericRawResults;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.representation.GsonRepresentation;
import com.datastore_android_sdk.sqlite.internal.$Orm$Preconditions;


/**
 * An implementation of {@link ArrayElement} that is a wrapper around a
 * {@link GenericRawResults}.
 */
public class OrmRawResultsElement extends ArrayElement {
	
	/** An array of column names in the collection of raw results. */
	private final String[] columnNames;
	
	private final List<String[]> results;
	
	public OrmRawResultsElement(String[] columnNames, List<String[]> results) {
		$Orm$Preconditions.checkNotNull(results);
		$Orm$Preconditions.checkNotNull(columnNames);
		this.results = results;
		this.columnNames = columnNames;
	}

	@Override
	public Iterator<DataElement> iterator() {
		return new OrmRawResultsElementIterator();
	}

	@Override
	public void add(DataElement element) {
		// A generic raw results can only add an {@link ObjectElement}
		if (element != null && element.isObject()) {
			String[] result = new String[columnNames.length];
			ObjectElement object = element.asObjectElement();
			
			for (int i = 0; i < result.length; i++) {
				DataElement e = object.get(columnNames[i]);
				if (e.isPrimitive()) {
					result[i] = e.asPrimitiveElement().valueAsString();
				}
			}
			results.add(result);
		}
	}

	@Override
	public int size() {
		return results.size();
	}

	@Override
	public DataElement get(int index) {
		String[] element = results.get(index);
		return new OrmRawResultElement(columnNames, element);
	}

	@Override
	public boolean isEmpty() {
		return results.isEmpty();
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
	/**
	 * Returns a {@link JsonArray} representation of this element.
	 * 
	 * @return The {@link JsonArray} representation of this element
	 */
	private JsonArray toJsonArray() {
		JsonArray array = new JsonArray();
		
		for (String[] result : results) {
			JsonObject object = new JsonObject();
			for (int i = 0; i < columnNames.length; i++) {
				object.addProperty(columnNames[i], result[i]);
			}
			array.add(object);
		}
		
		return array;
	}

	@Override
	public String toJson() {
		return toJsonArray().toString();
	}
	
	@Override
	public Representation toRepresentation() {
		return new GsonRepresentation(toJsonArray());
	}
	
	@Override
	public int hashCode() {
		int result = 0;
		final int prime = 31;
		if (results != null) {
			result = prime + results.hashCode();
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
		
		OrmRawResultsElement other = (OrmRawResultsElement) obj;
		if (results == null && columnNames == null) {
			return other.results == null && other.columnNames == null;
		} else if (results != null && columnNames == null) {
			return results.equals(other.results) && other.columnNames == null;
		} else if (results == null && columnNames != null) {
			return other.results == null && Arrays.equals(columnNames, other.columnNames);
		} else {
			return results.equals(other.results) && Arrays.equals(columnNames, other.columnNames);
		}
	}
	
	/**
	 * An internal implementation of an {@link Iterator} to navigate through the
	 * elements in the underlying {@link GenericRawResults}.
	 */
	private final class OrmRawResultsElementIterator implements Iterator<DataElement> {
		
		private final Iterator<String[]> itr;
		
		OrmRawResultsElementIterator() {
			itr = results.iterator();
		}

		@Override
		public boolean hasNext() {
			return itr.hasNext();
		}

		@Override
		public DataElement next() {
			String[] element = itr.next();
			return new OrmRawResultElement(columnNames, element);
		}

		@Override
		public void remove() {
			itr.remove();
		}
		
	}

}
