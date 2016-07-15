/**
 * BlobDatabaseField.java
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

/**
 * An annotation that identifies a blob data field in a class that corresponds to 
 * a column in the database.
 * 
 * @author Stanley Lam
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface BlobDatabaseField {
	
	/**
	 * The base URI where the blob data should be stored on the server when the field
	 * is persisted. An empty string denotes that the data should be stored at the 
	 * root of the running application
	 */
	String baseURI() default "";

}
