/**
 * QueryDeserializer.java
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

import com.datastore_android_sdk.DatastoreException.QueryParseException;

import java.lang.reflect.Type;



/**
 * An abstract class to deserialize the data into the appropriate type in the
 * data model being queried.
 * 
 * @param <T> the Java type of the object to deserialize
 */
public abstract class QueryDeserializer<T> {
	
	/**
	 * Query parser invokes this call-back method during parsing when it
	 * encounters a field of the specified type.
	 * 
	 * @param data
	 *            the data being deserialized
	 * @param typeOfT
	 *            the type of the object to deserialize to
	 * @return a deserialized object of the specified type which is a subclass
	 *         of T
	 * @throws QueryParseException
	 *             thrown if there is a serious issue that occurs
	 */
	public abstract T deserialize(Object data, Type typeOfT) throws QueryParseException;

}
