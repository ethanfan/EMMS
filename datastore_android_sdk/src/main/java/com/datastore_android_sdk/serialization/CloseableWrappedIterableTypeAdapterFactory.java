/**
 * CloseableWrappedIterableTypeAdapterFactory.java
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
package com.datastore_android_sdk.serialization;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.datastore_android_sdk.reflect.Types;


/**
 * A type adapter factory that creates a type adapter for {@link CloseableWrappedIterable}.
 */
public class CloseableWrappedIterableTypeAdapterFactory implements TypeAdapterFactory {
	
	private ModelSerializationStrategy serializationStrategy;
	
	public CloseableWrappedIterableTypeAdapterFactory() {
		this(ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization());
	}
	
	public CloseableWrappedIterableTypeAdapterFactory(ModelSerializationStrategy serializationStrategy) {
		this.serializationStrategy = serializationStrategy;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		Type type = typeToken.getType();
		
		Class<? super T> rawType = typeToken.getRawType();		
		if (!CloseableWrappedIterable.class.isAssignableFrom(rawType)) {
			return null;
		}
		
		// Specifies a TypeAdapter if a concrete type can be found for the element
		Type elementType = Types.getIterableParameter(type);
		TypeToken<?> elementTypeToken = TypeToken.get(elementType);
		TypeAdapter<?> elementTypeAdapter = elementType instanceof TypeVariable ? null : gson.getAdapter(elementTypeToken);
			
		return new Adapter(gson, elementType, elementTypeAdapter);
	}
	
	/**
	 * The type adapter to de/serialize a {@link CloseableWrappedIterable}.
	 * 
	 * @param <E> The type of the element in the {@link Iterable} 
	 */
	public class Adapter<E> extends TypeAdapter<CloseableWrappedIterable<E>> {
		
		private final TypeAdapterRuntimeTypeWrapper<E> elementTypeAdapter;
		
		public Adapter(
	    		Gson context, 
	    		Type elementType, 
	    		TypeAdapter<E> elementTypeAdapter) {
			 
			 this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, elementTypeAdapter, elementType);
		 }

		@SuppressWarnings("unchecked")
		@Override
		public void write(JsonWriter out, CloseableWrappedIterable<E> value) throws IOException {
			if (value == null) {
				out.nullValue();
				return;
			}
			
			out.beginArray();
			CloseableIterator<E> iterator = value.closeableIterator();
			try {
				while (iterator.hasNext()) {
					E element = iterator.next();
					if (elementTypeAdapter.getRuntimeTypeAdapter(element) instanceof DaoTypeAdapter) {
  					DaoTypeAdapter<E> adapter = (DaoTypeAdapter<E>) elementTypeAdapter.getRuntimeTypeAdapter(element);

  					// Field serialization policy takes precedence of the serialization
  					// policy specified for the type adapter factory
  					ModelSerializationStrategy strategy = adapter.setSerializationStrategy(serializationStrategy);
  					adapter.write(out, element);
  					adapter.setSerializationStrategy(strategy);
					} else {
						elementTypeAdapter.write(out, element);
					}
				}
			} finally {
				iterator.closeQuietly();
			}
			out.endArray();
		}

		@Override
		public CloseableWrappedIterable<E> read(JsonReader in) throws IOException {
			// TODO Not supported
			return null;
		}
		
	}

}
