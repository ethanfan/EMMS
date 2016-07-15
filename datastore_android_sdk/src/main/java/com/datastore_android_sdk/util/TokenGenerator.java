/**
 * TokenGenerator.java
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
package com.datastore_android_sdk.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.restlet.engine.util.Base64;

/**
 * A generator that generates a strong random key.
 */
public final class TokenGenerator {
	
	/** The maximum number of tokens before the generator needs to be reseeded. */
	private static final long MAX_RESEED = 1000;
	
	/** The default length of the seed. */
	private static final int DEFAULT_SEED_LENGTH = 20;
	
	/** The singleton instance of {@link TokenGenerator}. */
	private static TokenGenerator instance; 
	
	/** The key generator. */
	private SecureRandom random;
	
	/** The number of times the generator is called. */
	private volatile long count = 0;
	
	/**
	 * Constructor.
	 */
	private TokenGenerator() {
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException(ex);
		}		
	}
	
	/**
	 * Randomly generates an array of bytes of the given length.
	 * 
	 * @param len the length of the resulting byte array
	 * @return The resulting byte array
	 */
	protected byte[] generateBytes(int len) {
		if (count++ > MAX_RESEED) {
			count = 0;
			random.setSeed(random.generateSeed(DEFAULT_SEED_LENGTH));
		}
		byte[] token = new byte[len];
		random.nextBytes(token);
		return token;
	}
	
	/**
	 * Returns a token generator. 
	 * 
	 * @return The token generator
	 */
	public static synchronized TokenGenerator getInstance() {
		if (instance == null) {
			instance = new TokenGenerator();
		}
		return instance;
	}
	
	/**
	 * Generates a base64 encoded token in the given size. 
	 * 
	 * @param length The size of the token to be generated
	 * @return The resulting token
	 */
	public String generate(int length) {
		byte[] bytes = generateBytes(length);
		return Base64.encode(bytes, false);
	}

}
