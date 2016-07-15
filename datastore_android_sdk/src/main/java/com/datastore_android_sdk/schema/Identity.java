/**
 * Identity.java
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
package com.datastore_android_sdk.schema;

/**
 * This is an abstract API that data models implementations must conform to.
 * 
 * @author Stanley Lam
 * @param <T>
 *
 */
public interface Identity<T> {

	/**
	 * Returns a unique identifier for an item. The return value will be either a
	 * string or something that has a toString() method
	 * 
	 * @return The value of the identity
	 */
	T getIdentity();

	/**
	 * Returns the attribute name that is used to generate the identity. For most
	 * stores, this is a single attribute, but for some complex stores such as RDB
	 * backed stores that use compound (multi-attribute) identifiers it can be
	 * more than one. If the identity is not composed of attributes on the item,
	 * it will return null.
	 * 
	 * @return The attribute names that is used to generate the identity of a
	 *         model
	 */
	String getIdentityAttribute();

}
