/**
 * FieldExclusionStrategy.java
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

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * A strategy definition that defines the field(s) to be serialized in a class. 
 */
public abstract class FieldExclusionStrategy implements ExclusionStrategy {

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		// Do not exclude a property based on its Type
		return false;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		return shouldSkipField(f.getName());
	}
	
	/**
	 * Determines whether or not a field with the given name should be omitted.
	 * 
	 * @param serializedName the name of the field
	 * @return true if the Field should be omitted, false otherwise
	 */
	public abstract boolean shouldSkipField(String serializedName);

}
