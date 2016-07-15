/**
 * ModelSerializationStrategy.java
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
package com.datastore_android_sdk.serialization;

import java.lang.reflect.Field;

/**
 * A strategy (or policy) definition that is used to decide whether or not a field or top-level
 * class should be expanded as part of the JSON output. For serialization,
 * if the {@link #shouldExpandField(Field)} method returns false then only the id of of the foreign 
 * field will be serialized. 
 * 
 * @author Stanley Lam
 */
public interface ModelSerializationStrategy {
	
	/**
	 * Returns whether or not the only the ID field of the model should be
	 * serialized.
	 * 
	 * @return true if only the ID field of the model should be serialized,
	 *         false otherwise
	 */
	boolean shouldSerializeIdFieldOnly();
	
	/**
	 * Returns whether or not the given field should be ignored during
	 * serialization.
	 * 
	 * @return true if the field should be ignored, false otherwise
	 */
	boolean shouldSkipField(Field field);
	
	/**
	 * @param field
	 *            the field to determine whether or not it should be expanded.
	 * @return true if the field should be expanded in the JSON output
	 */
	boolean shouldExpandField(Field field);
	
	/**
	 * Returns whether or not a given field should be refreshed when
	 * de/serialized.
	 * 
	 * @param field
	 *            the field to determine whether or not it should be refreshed
	 * @return true if the field should be refreshed, false otherwise
	 */
	boolean shouldRefreshField(Field field);
	
	/**
	 * Returns whether or not a given field is a {@link BlobDatabaseField} and
	 * whether it should be serialized from the underlying source as a Base64
	 * {@link String}.
	 * 
	 * @param field
	 *            the field to determine whether or not it should be serialized
	 * @return {@code true} if the field should be serialized, {@code false}
	 *         otherwise
	 */
	boolean shouldSerializeBlobField(Field field);
	
	/**
	 * Returns the inner expansion strategy for the given field.
	 * 
	 * @param field
	 *            the field to return the expansion strategy
	 * @return the expansion strategy for the given field in a data model
	 */
	ModelSerializationStrategy getFieldSerializationStrategy(Field field);

}
