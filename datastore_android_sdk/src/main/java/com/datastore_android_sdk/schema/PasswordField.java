/**
 * PasswordField.java
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.password.PasswordEncryptor;

/**
 * An annotation that identifies a password field in a class that corresponds to 
 * a column in the database.
 * 
 * @author Stanley Lam
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface PasswordField {

	/**
	 * Allows user to set a custom password encryptor class. This class contain a
	 * constructor that takes no argument
	 */
	Class<? extends PasswordEncryptor> encryptorClass() default BasicPasswordEncryptor.class;

	/**
	 * Whether or not this password field is a temporary password field. Default
	 * is {@code false}
	 * 
	 * @return {@code true} if this field is a temporary password field,
	 *         {@code false} otherwise
	 */
	boolean isTemporary() default false;

	/**
	 * Indicates whether or not encryption should be performed on this field while
	 * serializing.
	 * 
	 * @return {@code true} if this encryption should be performed while
	 *         serializing, {@code false} otherwise. Defaults to {@code false}
	 */
	boolean encryptSerialize() default false;
	
	/**
	 * Indicates whether or not encryption should be performed on this field while
	 * deserializing.
	 * 
	 * @return {@code true} if this encryption should be performed while
	 *         deserializing, {@code false} otherwise. Defaults to {@code false}
	 */
	boolean encryptDeserialize() default false;

}
