/**
 * QueryDeserializerFactory.java
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

import com.google.gson.reflect.TypeToken;

/**
 * Creates deserializer for set of related types.
 * 
 * @author Stanley Lam
 */
public interface QueryDeserializerFactory {
	
	/**
	 * Returns a query deserializer for {@code type}, or null if this factory
	 * does not support the given type.
	 * 
	 * @param <T> the Java type of the token to deserialize
	 * @param type the type to deserialize
	 * @return the deserializer to deserialize the given {@code type}
	 */
	<T> QueryDeserializer<T> create(TypeToken<T> type);

}
