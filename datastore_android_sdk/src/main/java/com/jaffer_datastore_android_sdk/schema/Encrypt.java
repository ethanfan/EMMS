/**
 * Encrypt.java
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

/**
 * An annotation that indicates this member should be digested.
 * 
 * @author Stanley Lam
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Encrypt {
	
	/**
	 * The default algorithm to be used for digesting.
	 */
	String DEFAULT_ALGORITHM = "MD5";
	
	/**
	 * The default number of iterations to apply to the encryption algorithm.
	 */
	int DEFAULT_ITERATIONS = 1000;
	
	/**
	 * The algorithm algorithm to be used for digesting, like MD5 or SHA-1. 
	 * By default, the system uses MD5.
	 */
	String algorithm() default DEFAULT_ALGORITHM;
	
	/**
	 * Whether the plain (not hashed) salt bytes are to be appended after the
	 * digest operation result bytes. Default is to insert plain salt before
	 * digest result.
	 */
	boolean invertPositionOfPlainSaltInEncryptionResults() default false;
	
	/**
	 * Whether the salt bytes are to be appended after the message ones before 
	 * performing the digest operation on the whole. Default is to insert before it.
	 */
	boolean invertPositionOfSaltInMessageBeforeDigesting() default false;
	
	/**
	 * The number of times the hash function will be applied recursively.
	 */
	int iterations() default DEFAULT_ITERATIONS;
	
	/**
	 * Sets the prefix to be added at the beginning of encryption results, 
	 * and also to be expected at the beginning of plain messages provided 
	 * for matching operations.
	 */
	String prefix() default "";
	
	/**
	 * Sets the suffix to be added at the end of encryption results, and 
	 * also to be expected at the end of plain messages provided for matching 
	 * operations.
	 */
	String suffix() default "";
	
	/**
	 * Sets the size of the salt to be used to compute the digest. Default is 0,
	 * which means no salt will be used.
	 */
	int saltSizeBytes() default 0;
	
	/**
	 * Whether digest matching operations will allow matching digests with a 
	 * salt size different to the one configured in the "saltSizeBytes" property.
	 * Default is true.
	 */
	boolean useLenientSaltSizeCheck() default true;

}
