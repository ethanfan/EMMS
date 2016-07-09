/**
 * Omit.java
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
package com.jaffer_datastore_android_sdk.schema;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.gson.annotations.Expose;

/**
 * An annotation that indicates this member should be omitted when the object is
 * serialized. This annotation is different from {@link Expose}. The
 * {@link Expose} annotation is used for JSON serialization while this
 * annotation is used for object serialization.
 * 
 * @author Stanley Lam
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Omit {

	/**
	 * If {@code true}, the field marked with this annotation will be omitted in
	 * serialization. If {@code false}, the field marked with this annotation will
	 * be omitted. Defaults to {@code true}.
	 */
	boolean value() default true;
}
