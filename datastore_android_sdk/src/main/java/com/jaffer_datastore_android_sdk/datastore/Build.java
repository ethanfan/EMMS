/**
 * Build.java
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
package com.jaffer_datastore_android_sdk.datastore;

/**
 * The class that defines the running environment.
 */
public final class Build {
	
	/** The runtime environment. */
	public static final Environment ENVIRONMENT = Environment.PRODUCTION;
	
	/** Returns {@code true} if we are running a debug build. */
	public static final boolean DEBUG = true;
	
	/**
	 * For debugging.
	 */
	public static enum Environment {
		PRODUCTION,
		SANDBOX;
		
		/**
		 * Converts an integer to an {@link Environment} enumeration.
		 * 
		 * @param value
		 *            the integer representation of the runtime environment
		 * @return {@link Environment#SANDBOX} if {@code value} is 1,
		 *         {@link Environment#PRODUCTION} otherwise
		 */
		public static Environment fromInteger(int value) {
			return value == 1 ? SANDBOX : PRODUCTION;
		}
	};
	
	/**
	 * Prevents this utility from being instantiated.
	 */
	private Build() {}

}
