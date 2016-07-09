/**
 * DaoTypeAdapter.java
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
package com.jaffer_datastore_android_sdk.serialization;

import com.google.gson.TypeAdapter;

/**
 * Base type adapter for ORMLite DAO objects in GSON serialization/deserialization.
 * 
 * @param <T> the class type that ORMListe DAO object operates on
 * @author Stanley Lam 
 */
public abstract class DaoTypeAdapter<T> extends TypeAdapter<T> {

	/**
	 * Overrides the serialization strategy with the given serialization policy. The strategy should be
	 * omitted once {@link DaoTypeAdapter#write(com.google.gson.stream.JsonWriter, Object)} is called
	 * @param serializationStrategy
	 */
	abstract ModelSerializationStrategy setSerializationStrategy(ModelSerializationStrategy serializationStrategy);

}
