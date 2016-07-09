/**
 * CloseableIterator.java
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
package com.jaffer_datastore_android_sdk.sqlite.internal;

import java.util.Iterator;

/**
 * Defines the interfaces of an iterator over a ORM collection.
 * 
 * @param <T> The type of the element to iterate
 */
public interface CloseableIterator<T> extends Iterator<T> {
	
	/**
	 * Closes the iterator.
	 */
	void close();

}
