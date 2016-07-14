/** 
 * IterableRepresentation.java
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
package com.jaffer_datastore_android_sdk.representation;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Iterator;

import org.restlet.data.CharacterSet;
import org.restlet.representation.Representation;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.stream.JsonWriter;
import com.jaffer_datastore_android_sdk.datastore.data.Range;
import com.jaffer_datastore_android_sdk.reflect.Types;
import com.jaffer_datastore_android_sdk.schema.Model;
import com.jaffer_datastore_android_sdk.serialization.DateTypeAdapter;
import com.jaffer_datastore_android_sdk.serialization.ForeignCollectionTypeAdapterFactory;
import com.jaffer_datastore_android_sdk.serialization.ModelSerializationPolicy;
import com.jaffer_datastore_android_sdk.serialization.ModelSerializationStrategy;
import com.jaffer_datastore_android_sdk.serialization.ModelTypeAdapterFactory;


/**
 * Representation based on {@link Iterable}
 */
public class IterableRepresentation extends JsonDataRepresentation {
	
	/** The {@link Iterable} this representation wraps */
	private final Iterable<?> iterable;
	
	/** The type of element in the iterable. */
	private final Type elementType;
	
	/** The serialization policy of the {@link Model} */
	private ModelSerializationStrategy serializationStrategy;
	
	/**
	 * Constructor
	 * 
	 * @param itr the {@link Iterable} this representation wraps
	 * @param typeOfIterable the type of the iterable
	 */
	public IterableRepresentation(Iterable<?> itr, Type typeOfIterable) {
		this(itr, typeOfIterable, ModelSerializationPolicy.DEFAULT);
	}
	
	/**
	 * Constructor
	 * 
	 * @param itr the {@link Iterable} this representation wraps
	 * @param typeOfIterable the type of the {@link Iterable}
	 * @param serializationStrategy the strategy to serialize the iterable
	 */
	public IterableRepresentation(Iterable<?> itr, Type typeOfIterable, ModelSerializationStrategy serializationStrategy) {
		super();
		TypeToken<?> typeToken = TypeToken.of(typeOfIterable);
		Class<?> rawType = typeToken.getRawType();
		if (!Iterable.class.isAssignableFrom(rawType)) {
			throw new RuntimeException("The type specified is invalid");
		}
				
		iterable = itr;
		elementType = Types.getIterableParameter(typeOfIterable);
		
		setCount(getLength());
		setRange(new Range());
		setSerializationStrategy(serializationStrategy);
	}
	
	/**
	 * Specifies the policy to use when serializing this element
	 * 
	 * @param strategy The serialization policy to use to serialize this element
	 */
	public void setSerializationStrategy(ModelSerializationStrategy strategy) {
		serializationStrategy = strategy == null ? ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization() : strategy;
	}
	
	/**
	 * Returns the strategy to use to serialize the {@link Iterable}
	 * 
	 * @return the strategy to use to serialize the {@link Iterable}
	 */
	public ModelSerializationStrategy getSerializationStrategy() {
		return serializationStrategy;
	}
	
	/**
	 * Returns the type of the elements in the {@link Iterable}
	 * 
	 * @return the type of the elements in the {@link Iterable}
	 */
	protected Type getElementType() {
		return elementType;
	}
	
	/**
	 * Returns a {@link GsonBuilder} to use to create a {@link Gson} to serialize the {@link Iterable} into JSON 
	 * 
	 * @return the {@link GsonBuilder} to build a {@link Gson}
	 */
	protected GsonBuilder getGsonBuilder() {
		GsonBuilder builder = new GsonBuilder()
				.registerTypeAdapter(Date.class, new DateTypeAdapter())
				.serializeNulls();
		
		if (getIterable() != null) {
			TypeToken<?> elementTypeToken = TypeToken.of(getElementType());
			final Class<?> elementRawType = elementTypeToken.getRawType();
			
			// Creates a custom type factory for data models
			ModelTypeAdapterFactory modelTypeAdapterFactory = Model.getTypeAdapterFactory(elementRawType);
			modelTypeAdapterFactory.registerSerializationAdapter(elementRawType, getSerializationStrategy());
			
			// Serialize the data model into a JSON representation
			builder.registerTypeAdapterFactory(modelTypeAdapterFactory)
					.registerTypeAdapterFactory(new ForeignCollectionTypeAdapterFactory());
		}
		
		return builder;
	}
	
	/**
	 * Returns the {@link Iterable} wrapped in this representation.
	 * 
	 * @return the {@link Iterable} wrapped in this representation.
	 */
	protected Iterable<?> getIterable() {
		return iterable;
	}
	
	/**
	 * Returns the number of items in the wrapped iterable
	 * 
	 * @return the number of items in the wrapped iterable
	 */
	protected long getLength() {
		long result = 0;
		
		Iterator<?> itr = getIterable() == null ? null : getIterable().iterator();
		while (itr != null && itr.hasNext()) {
			itr.next();
			result ++;
		}
		
		return result;
	}
	
	@Override
	public void setRange(org.restlet.data.Range range) {
		if (range != null) {
			long length = getLength();
			if (length != Representation.UNKNOWN_SIZE && range.getSize() > length) {
				range.setSize(length);
			}
			
			if (range.getIndex() < Range.INDEX_FIRST) {
				range.setIndex(Range.INDEX_FIRST);
			}
			if (length != Representation.UNKNOWN_SIZE && range.getIndex() > length) {
				range.setIndex(Range.INDEX_LAST);
			}
		}
		super.setRange(range);
	}

	@Override
	public void write(Writer writer) throws IOException {
		GsonBuilder builder = getGsonBuilder();
		builder = builder == null ? new GsonBuilder().serializeNulls() : builder;
		
		// Creates a Gson instance to serialize data objects into JSON
		Gson gson = builder.create();
		JsonWriter jsonWriter = new JsonWriter(writer);
		jsonWriter.setLenient(true);
		
		if (getIterable() != null) {
			TypeToken<?> typeToken = TypeToken.of(getIterable().getClass());
			gson.toJson(getIterable(), typeToken.getRawType(), jsonWriter);
		} else {
			gson.toJson(new JsonArray(), jsonWriter);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		IterableRepresentation other = (IterableRepresentation) obj;
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
