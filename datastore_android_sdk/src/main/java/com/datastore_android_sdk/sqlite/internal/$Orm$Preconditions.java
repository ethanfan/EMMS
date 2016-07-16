/**
 * $Orm$Preconditions.java
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
package com.datastore_android_sdk.sqlite.internal;

/**
 * A utility class to verify certain pre-conditions that must be satisfied or
 * else an {@link IllegalArgumentException} would be thrown.
 */
public final class $Orm$Preconditions {

	public static <T> T checkNotNull(T obj) {
		if (obj == null) {
			throw new NullPointerException();
		}
		return obj;
	}

	/**
	 * Verifies the {@code condition} to be {@code true}. Throw an exception if
	 * the assertion failed.
	 * 
	 * @param condition
	 *          The condition to verify
	 */
	public static void checkArgument(boolean condition) {
		if (!condition) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Private Constructor.
	 */
	private $Orm$Preconditions() {}
	
}
