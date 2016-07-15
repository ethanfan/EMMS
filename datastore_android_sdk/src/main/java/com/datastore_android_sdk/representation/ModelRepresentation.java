/**
 * ModelRepresentation.java
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

import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.datastore.data.Range;
import com.datastore_android_sdk.schema.Model;
import com.datastore_android_sdk.serialization.DateTypeAdapter;
import com.datastore_android_sdk.serialization.ForeignCollectionInstanceCreator;
import com.datastore_android_sdk.serialization.ForeignCollectionTypeAdapterFactory;
import com.datastore_android_sdk.serialization.ModelSerializationPolicy;
import com.datastore_android_sdk.serialization.ModelSerializationStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import com.j256.ormlite.dao.ForeignCollection;
import com.datastore_android_sdk.serialization.ModelTypeAdapterFactory;

/**
 * Representation based on a JSON document using {@link Model}.
 */
@SuppressWarnings("rawtypes")
public class ModelRepresentation extends JsonDataRepresentation {
	
	/** The wrapped data Model. */
	private final Model model;
	
	/** The serialization policy of the {@link Model}. */
	private ModelSerializationStrategy serializationStrategy;
	
	/**
	 * Creates a new instance of {@link ModelRepresentation} from another
	 * {@link Representation}.
	 * 
	 * @param representation
	 *          The source {@link Representation}
	 * @param blobFactories
	 *          The BLOB data factories
	 * @param creator
	 *          The creator to create {@link ForeignCollection} instances
	 * @param type
	 *          The class type of the data model
	 * @throws JsonIOException
	 *           Thrown if the given {@code reader} cannot be read
	 * @throws JsonSyntaxException
	 *           Thrown if the given {@code representation} is no a valid JSON
	 * @throws IOException
	 *           Thrown if there was error reading from the {@code reader}
	 */
	public ModelRepresentation(
			Representation representation, 
			ForeignCollectionInstanceCreator creator,
			Class<?> type) throws JsonIOException, JsonSyntaxException, IOException {
		
		this(representation == null ? null : (Model) Model.fromJson(representation.getReader(), creator, type));
	}
	
	/**
	 * Creates a new instance of {@link ModelRepresentation} wrapping another
	 * instance of {@link ModelRepresentation}.
	 * 
	 * @param model The {@link ModelRepresentation} wrapped
	 */
	public ModelRepresentation(ModelRepresentation model) {
		this(model == null ? null : model.model);
	}
	
	/**
	 * Creates a new instance of {@link ModelRepresentation} wrapping the given {@link Model}.
	 * 
	 * @param model The underlying {@link Model} of this {@link Representation}
	 */
	public ModelRepresentation(Model model) {
		this(model, ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization());
	}
	
	/**
	 * Creates a new instance of {@link ModelRepresentation} around a given
	 * {@link Model} using the given serialization strategy.
	 * 
	 * @param model
	 *          The underlying data model of this {@link Representation}
	 * @param strategy
	 *          The serialization strategy to use when de/serializing this
	 *          {@link Representation}
	 */
	public ModelRepresentation(Model model, ModelSerializationStrategy strategy) {
		super();
		this.model = model;
		setSerializationStrategy(strategy);
		setCount(1);
		setRange(new Range());
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
	 * Returns the {@link Model} wrapped by this {@link Representation}.
	 * 
	 * @return The underlying model of this {@link Representation}
	 */
	public Model getModel() {
		return model;
	}
	
	@Override
	public void setRange(org.restlet.data.Range range) {
		if (range != null) {
			range.setIndex(Range.INDEX_FIRST);
			range.setSize(getCount());
		}
		super.setRange(range);
	}

	@Override
	public void write(Writer writer) throws IOException {
		if (model != null) {
			// Creates a custom type factory for data models
			ModelTypeAdapterFactory modelTypeAdapterFactory = Model.getTypeAdapterFactory(model.getClass());
			modelTypeAdapterFactory.registerSerializationAdapter(model.getClass(), getSerializationStrategy());
			// Creates a type factor for serializing foreign collections
			ForeignCollectionTypeAdapterFactory foreignCollectionTypeAdapterFactory = new ForeignCollectionTypeAdapterFactory();
			
			// Serialize the data model into a JSON representation 
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(Date.class, new DateTypeAdapter())
					.registerTypeAdapterFactory(modelTypeAdapterFactory)
					.registerTypeAdapterFactory(foreignCollectionTypeAdapterFactory)
					.serializeNulls()
					.create();
			
			JsonWriter jsonWriter = new JsonWriter(writer);
			jsonWriter.setLenient(true);
			gson.toJson(model, model.getClass(), jsonWriter);
		} else {
			// An empty representation with no content
			setAvailable(false);
			setTransient(true);
			setSize(0);
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
		
		ModelRepresentation other = (ModelRepresentation) obj;
		if (model == null) {
			return other.model == null;
		}
		
		return model.equals(other.model);
	}
	
	@Override
	public int hashCode() {
		return model == null ? -1 : model.hashCode();
	}
	
	@Override
	public String toString() {
		return model == null ? "" : model.toString();
	}

}
