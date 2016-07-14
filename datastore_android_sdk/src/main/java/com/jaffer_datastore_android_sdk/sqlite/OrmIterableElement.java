/**
 * OrmIterableElement.java
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
package com.jaffer_datastore_android_sdk.sqlite;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;

import org.restlet.representation.Representation;

import com.google.common.collect.Iterators;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.j256.ormlite.dao.CloseableIterable;
import com.j256.ormlite.dao.CloseableWrappedIterableImpl;
import com.jaffer_datastore_android_sdk.datastore.ArrayElement;
import com.jaffer_datastore_android_sdk.datastore.DataElement;
import com.jaffer_datastore_android_sdk.reflect.Types;
import com.jaffer_datastore_android_sdk.representation.CloseableWrappedIterableRepresentation;
import com.jaffer_datastore_android_sdk.schema.Model;
import com.jaffer_datastore_android_sdk.serialization.DateTypeAdapter;
import com.jaffer_datastore_android_sdk.serialization.ForeignCollectionTypeAdapterFactory;
import com.jaffer_datastore_android_sdk.serialization.ModelSerializationPolicy;
import com.jaffer_datastore_android_sdk.serialization.ModelSerializationStrategy;
import com.jaffer_datastore_android_sdk.serialization.ModelTypeAdapterFactory;
import com.jaffer_datastore_android_sdk.sqlite.internal.$Orm$Preconditions;
import com.jaffer_datastore_android_sdk.sqlite.internal.CloseableIterator;


/**
 * An implementation of {@link ArrayElement} that is a wraps around an
 * {@link CloseableIterator}.
 * 
 * @param <T> The {@link Model} class type
 */
public class OrmIterableElement<T extends Model<T, ?>> extends ArrayElement {
	
	/** The wrapped {@link Iterable}. */
	private final CloseableIterable<T> iterable;
	
	/** The serialization policy of the {@link Model}. */
	private ModelSerializationStrategy serializationStrategy =
		ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization();
	
	public OrmIterableElement(CloseableIterable<T> itr) {
		$Orm$Preconditions.checkNotNull(itr);
		iterable = itr;
	}
	
	/**
	 * Specifies the policy to use when serializing this element.
	 * 
	 * @param strategy The serialization policy to use to serialize this element
	 */
	public void setSerializationStrategy(ModelSerializationStrategy strategy) {
		serializationStrategy = strategy;
	}
	
	/**
	 * Returns the serialization strategy when serializing this {@link Representation}.
	 * 
	 * @return The serialization strategy to use to serialize this {@link Representation}
	 */
	protected ModelSerializationStrategy getSerializationStrategy() {
		return serializationStrategy;
	}

	@Override
	public Iterator<DataElement> iterator() {
		return new OrmIterableElementIterator();
	}

	@Override
	public void add(DataElement element) {
		// FIXME: not implemented. No element can be added to an iterable
	}

	@Override
	public int size() {
		int result = 0;
		if (iterable != null) {
			com.j256.ormlite.dao.CloseableIterator<T> iterator = iterable.closeableIterator();
			try {
				result = Iterators.size(iterator);
			} finally {
				iterator.closeQuietly();
			}
		}
		return result;
	}

	@Override
	public DataElement get(int index) {
		DataElement result = null;
		if (iterable != null) {			
			com.j256.ormlite.dao.CloseableIterator<T> iterator = iterable.closeableIterator();
			try {
				T model = Iterators.get(iterator, index);
				OrmObjectElement<T> orm = new OrmObjectElement<T>(model);
				orm.setSerializationStrategy(getSerializationStrategy());
				result = orm;
			} finally {
				iterator.closeQuietly();
			}
		}
		return result;
	}

	@Override
	public boolean isEmpty() {
		boolean result = true;
		if (iterable != null) {
			com.j256.ormlite.dao.CloseableIterator<T> iterator = iterable.closeableIterator();
			try {
				result = !iterator.hasNext();
			} finally {
				iterator.closeQuietly();
			}
		}
		return result;
	}
	
	/**
	 * Returns a {@link JsonElement} representation of this element.
	 * 
	 * @return The {@link JsonElement} representation of this element
	 */
	@SuppressWarnings("rawtypes")
	private JsonElement toJsonElement() {
		if (iterable != null) {
			// Creates a custom type factory for data models
			TypeToken typeToken = TypeToken.of(iterable.getClass());
			Type type = typeToken.getType();
			
			final Type elementType = Types.getIterableParameter(type);
			TypeToken<?> elementTypeToken = TypeToken.of(elementType);
			final Class<?> elementRawType = elementTypeToken.getRawType();
			
			ModelTypeAdapterFactory factory = Model.getTypeAdapterFactory(elementRawType);
			factory.registerSerializationAdapter(Model.class, getSerializationStrategy());
			
			Gson gson = new GsonBuilder()
					.registerTypeAdapterFactory(factory)
					.registerTypeAdapterFactory(new ForeignCollectionTypeAdapterFactory())
					.registerTypeAdapter(Date.class, new DateTypeAdapter())
					.serializeNulls()
					.create();
			
			return gson.toJsonTree(iterable, iterable.getClass());
		}
		return JsonNull.INSTANCE;
	}

	@Override
	public String toJson() {
		return toJsonElement().toString();
	}

	@Override
	public Representation toRepresentation() {
		CloseableWrappedIterableImpl<T> wrapped = new CloseableWrappedIterableImpl<T>(iterable);
		return new CloseableWrappedIterableRepresentation(wrapped);
	}
	
	/**
	 * An internal implementation of an {@link Iterator} to navigate through the
	 * elements in the underlying {@link CloseableIterable}.
	 */
	private final class OrmIterableElementIterator implements CloseableIterator<DataElement> {
		
		/** The underlying iterator. */
		private final Iterator<T> iterator;
		
		OrmIterableElementIterator() {
			iterator = iterable == null ? null : iterable.closeableIterator(); 
		}

		@Override
		public boolean hasNext() {
			return iterator == null ? false : iterator.hasNext();
		}

		@Override
		public DataElement next() {
			T model = iterator.next();
			return new OrmObjectElement<T>(model);
		}

		@Override
		public void remove() {
			if (iterator != null) {
				iterator.remove();
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		public void close() {
			if (iterator instanceof com.j256.ormlite.dao.CloseableIterator) {
				((com.j256.ormlite.dao.CloseableIterator) iterator).closeQuietly();
			}
		}
		
	}

}
