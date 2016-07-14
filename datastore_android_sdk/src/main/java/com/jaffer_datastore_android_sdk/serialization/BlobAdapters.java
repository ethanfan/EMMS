/**
 * BlobAdapters.java
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

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.restlet.data.Protocol;
import org.restlet.data.Reference;

/**
 * Blob adapters for de/serializing BLOB data.
 */
public final class BlobAdapters {
	
	public static final BlobAdapter<java.net.URL> URL = new BlobAdapter<java.net.URL>() {

		@Override
		protected File serialize(java.net.URL value) {
			try {
				if (Protocol.FILE.getSchemeName().equalsIgnoreCase(value.getProtocol())) {
					return new File(value.toURI());
				}
			} catch (Exception ex) {
				// Ignored
			}
			return null;
		}

		@Override
		protected java.net.URL deserialize(File file) {
			try {
				return file.toURI().toURL();
			} catch (Exception ex) {
				// Ignored
				return null;
			}
		}
		
	};
	
	public static final BlobAdapter<java.net.URI> URI = new BlobAdapter<java.net.URI>() {

		@Override
		protected File serialize(java.net.URI value) {
			if (value != null && Protocol.FILE.getSchemeName().equalsIgnoreCase(value.getScheme())) {
				return new File(value);
			}
			return null;
		}

		@Override
		protected java.net.URI deserialize(File file) {
			return file == null ? null : file.toURI();
		}
		
	};
	
	public static final BlobAdapter<String> STRING = new BlobAdapter<String>() {

		@Override
		protected File serialize(String value) {
			Reference ref = new Reference(value);
			if (Protocol.FILE.equals(ref.getSchemeProtocol())) {
				return new File(ref.getTargetRef().toUri());
			}
			return null;
		}
		
		@Override
		protected String deserialize(File file) {
			return file == null ? null : file.toURI().toString();
		}
		
	};
	
	/**
	 * A list of supported {@link BlobAdapter}.
	 */
	private static final Map<Class<?>, BlobAdapter<?>> ADAPTERS = new ConcurrentHashMap<Class<?>, BlobAdapter<?>>();
	static {
		ADAPTERS.put(String.class, STRING);
		ADAPTERS.put(java.net.URL.class, URL);
		ADAPTERS.put(java.net.URI.class, URI);
	}

	/** Prevent this class from being instantiated. */
	private BlobAdapters() {}
	
	/**
	 * Returns the {@link BlobAdapter} for the given Java type.
	 * 
	 * @param clazz The Java type to return the {@link BlobAdapter}
	 */
	public static BlobAdapter<?> get(Class<?> clazz) {
		return ADAPTERS.get(clazz);
	}
	
}
