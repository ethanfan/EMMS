/**
 * SerializationTypeAdapterFactory.java
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

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.gson.Gson;
import com.google.gson.TypeAdapterFactory;
import com.datastore_android_sdk.serialization.ModelTypeAdapterFactory;


/**
 * An annotation that marks the type adapter to use to de/serialize the class
 * using GSON.
 * 
 * @author Stanley Lam
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface SerializationTypeAdapterFactory {
	
	/**
	 * The TypeAdapterFactory class to use to de/serialize a given class. This is used by 
	 * the {@link Gson} during de/serialization
	 */
	Class<? extends TypeAdapterFactory> value() default ModelTypeAdapterFactory.class;

}
