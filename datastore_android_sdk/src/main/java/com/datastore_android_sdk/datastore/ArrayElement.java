/**
 * ArrayElement.java
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
 * A class representing a collection of data.
 */
public abstract class ArrayElement extends DataElement implements Iterable<DataElement> {

	/**
	 * Adds the given {@code element} into the array.
	 *  
	 * @param element The element to add to the array
	 */
	public abstract void add(DataElement element);

	/**
	 * Adds all of the elements in the given data collection into this data
	 * collection.
	 * 
	 * @param elements
	 *          The data collection whose elements are to be added to this data
	 *          collection.
	 */
	public void addAll(ArrayElement elements) {
		if (elements != null) {
			for (DataElement element : elements) {
				add(element);
			}
		}
	}
	
	/**
	 * Returns the number of elements in this collection.
	 * 
	 * @return The number of elements in this data collection
	 */
	public abstract int size();

	/**
	 * Returns the element at the specified position in the data collection.
	 * 
	 * @param index
	 *          Index of the element to return
	 * @return The element at the given position in this data collection
	 */
	public abstract DataElement get(int index);
	
	/**
	 * Returns {@code true} if this data collection contains no element.
	 * 
	 * @return {@code true} if this data collection contains no element,
	 *         {@code false} otherwise
	 */
	public abstract boolean isEmpty();
	
}
