/**
 * CloseableWrappedlterableRepresentation.java
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
package com.datastore_android_sdk.representation;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.schema.Model;
import com.datastore_android_sdk.serialization.CloseableWrappedIterableTypeAdapterFactory;
import com.datastore_android_sdk.serialization.ModelSerializationPolicy;
import com.datastore_android_sdk.serialization.ModelSerializationStrategy;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;

/**
 * Representation based on a {@link CloseableIterator}.
 */
@SuppressWarnings("rawtypes")
public class CloseableWrappedIterableRepresentation extends IterableRepresentation {
	
	/** The wrapped {@link CloseableWrappedIterable}. */
	private final CloseableWrappedIterable iterable;
	
	/** The serialization policy of the {@link Model}. */
	private ModelSerializationStrategy serializationStrategy;

	/**
	 * Creates a new instance of {@link CloseableWrappedIterableRepresentation} wrapping another
	 * instance of {@link CloseableWrappedIterableRepresentation}.
	 * 
	 * @param itr The {@link CloseableWrappedIterableRepresentation} wrapped
	 */
	public CloseableWrappedIterableRepresentation(CloseableWrappedIterableRepresentation itr) {
		this(itr == null ? null : itr.iterable);
	}
	
	/**
	 * Creates a new instance of {@link CloseableWrappedIterableRepresentation} wrapping the given {@link CloseableIterator}.
	 * 
	 * @param itr The underlying {@link CloseableIterator} of this {@link Representation}
	 */
	public CloseableWrappedIterableRepresentation(CloseableWrappedIterable itr) {
		this(itr, ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization());
	}
	
	public CloseableWrappedIterableRepresentation(CloseableWrappedIterable itr, ModelSerializationStrategy strategy) {
		super(itr, itr == null ? null : itr.getClass(), strategy);
		this.iterable = itr;		
	}
	
	/**
	 * Specifies the policy to use when serializing this element.
	 * 
	 * @param strategy The serialization policy to use to serialize this element
	 */
	public void setSerializationStrategy(ModelSerializationStrategy strategy) {
		serializationStrategy = strategy == null ? ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization() : strategy;
	}
	
	/**
	 * Returns the serialization strategy when serializing this {@link Representation}.
	 * 
	 * @return The serialization strategy to use to serialize this {@link Representation}
	 */
	public ModelSerializationStrategy getSerializationStrategy() {
		return serializationStrategy;
	}	
	
	/**
	 * Returns the {@link CloseableIterator} wrapped by this {@link Representation}.
	 * 
	 * @return The underlying {@link CloseableIterator} of this {@link Representation}
	 */
	public CloseableIterator iterator() {
		return iterable.closeableIterator();
	}
	
	/**
	 * Closes the underlying iterable.
	 */
	public void close() {
		try {
			// Closes the iterator
			if (iterable != null) {
				iterable.close();
			}
		} catch (Exception ex) {
			// Ignore
		}
	}
	
	@Override
	protected GsonBuilder getGsonBuilder() {
		GsonBuilder builder = super.getGsonBuilder();
		if (builder != null) {
			builder.registerTypeAdapterFactory(new CloseableWrappedIterableTypeAdapterFactory(getSerializationStrategy()));
		}
		return builder;
	}
	
	@Override
	protected Iterable<?> getIterable() {
		return iterable;
	}
	
	@Override
	protected long getLength() {		
		return UNKNOWN_SIZE;
	}
	
	@Override
	public void release() {
		super.release();
		close();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
		
		CloseableWrappedIterableRepresentation other = (CloseableWrappedIterableRepresentation) obj;
		if (iterable == null) {
			return other.iterable == null;
		}
		
		return iterable.equals(other.iterable);
	}
	
	@Override
	public int hashCode() {
		return iterable == null ? -1 : iterable.hashCode();
	}
	
	@Override
	public String toString() {
		return iterable == null ? "" : iterable.toString();
	}

}
