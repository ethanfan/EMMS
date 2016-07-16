/**
 * OrmCollectionElement.java
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
package com.datastore_android_sdk.sqlite;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.restlet.representation.Representation;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.reflect.Types;
import com.datastore_android_sdk.representation.CloseableWrappedIterableRepresentation;
import com.datastore_android_sdk.representation.GsonRepresentation;
import com.datastore_android_sdk.schema.Model;
import com.datastore_android_sdk.serialization.DateTypeAdapter;
import com.datastore_android_sdk.serialization.ForeignCollectionInstanceCreator;
import com.datastore_android_sdk.serialization.ForeignCollectionTypeAdapterFactory;
import com.datastore_android_sdk.serialization.ModelSerializationPolicy;
import com.datastore_android_sdk.serialization.ModelSerializationStrategy;
import com.datastore_android_sdk.sqlite.internal.CloseableIterator;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.internal.$Gson$Types;
import com.j256.ormlite.dao.BaseForeignCollection;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.LazyForeignCollection;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.serialization.ModelTypeAdapterFactory;
import com.datastore_android_sdk.sqlite.internal.$Orm$Preconditions;


/**
 * An implementatin of {@link ArrayElement} that is a wrapper around a
 * {@link List} or {@link ForeignCollection}.
 * 
 * @param <T> the type of data model wrapped by this element
 */
public class OrmCollectionElement<T extends Model<?, ?>> extends ArrayElement {
	
	/** The underlying {@link Collection}. */
	final Collection<T> col;
	
	/** The serialization policy of the {@link Model}. */
	private ModelSerializationStrategy serializationStrategy =
		ModelSerializationPolicy.DEFAULT.disableIdFieldOnlySerialization();
	
	/**
	 * Construct a new instance of {@link OrmCollectionElement} with the given {@link Collection}.
	 * 
	 * @param col The underlying {@link Collection}. Cannot be {@code null}
	 */
	public OrmCollectionElement(Collection<T> col) {
		$Orm$Preconditions.checkNotNull(col);
		this.col = col;
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
		return new OrmCollectionElementIterator();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void add(DataElement element) {
		// An ORM collection can only add {@link ObjectElement}
		if (element != null && element.isObject()) {
			try {
				if (element instanceof OrmObjectElement) {
					OrmObjectElement object = (OrmObjectElement) element;
					col.add((T) object.model);
				} else {
					// Retrieves the type information of the element from the collection
					TypeToken typeToken = TypeToken.of(col.getClass());
					Type elementType = $Gson$Types.getCollectionElementType(typeToken.getType(), typeToken.getRawType());
					Class<?> rawType = $Gson$Types.getRawType(elementType);
					
					// We retrieve the DAO associated with collection to create a
					// foreign collection instance creator. Without a foreign collection
					// instance creator, foreign collections will not be created.
					String json = element.toJson();
					Dao<T, ?> dao = getDao();
					ForeignCollectionInstanceCreator creator = dao == null ? null : new ForeignCollectionInstanceCreator(dao.getConnectionSource());
					col.add((T) Model.fromJson(json, creator, rawType));
				}
			} catch (Exception ex) {
				// Ignore exceptions caused. Just omit the element 
			}
		}
	}

	@Override
	public int size() {
		return col.size();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public DataElement get(int index) {
		DataElement result = null;
		
		if (index > -1 && index < col.size()) {
			Iterator<T> itr = null;
			CloseableWrappedIterable<T> iterable = null;
			int i = 0;
			
			// Obtain an iterator for the collection. We obtain a wrapped iterable for
			// foreign collections so that if this method is called in a thread, the
			// iterator can be closed independently
			if (col instanceof ForeignCollection) {
				iterable = ((ForeignCollection<T>) col).getWrappedIterable();
				itr = iterable.iterator();
			} else {
				itr = col.iterator();
			}
			
			// Iterate through the collection to find the element referenced
			try {
				while (itr.hasNext()) {
					T model = itr.next();
					
					// We only refresh the model if the underlying collection is a lazy
					// foreign collection
					if (col instanceof LazyForeignCollection) {
						model.refresh();
					}
					
					if (i == index) {
						result = new OrmObjectElement(model);
						break;
					}
					i++;
				}
			} catch (Exception ex) {
				// Ignores
			}
			
			// Closes the iterator if the underlying collection is a foreign collection
			if (iterable != null) {
				try {
					iterable.close();
				} catch (Exception ex) {
					// Ignores
				}
			}
		}
		
		return result;
	}

	@Override
	public boolean isEmpty() {
		return col.isEmpty();
	}
	
	@Override
	public String toString() {
		return toJson();
	}
	
	/**
	 * Returns a {@link JsonElement} representation of this element.
	 * 
	 * @return The {@link JsonElement} representation of this element
	 */
	@SuppressWarnings("rawtypes")
	private JsonElement toJsonElement() {
		if (col != null) {
			TypeToken typeToken = TypeToken.of(col.getClass());
			Type type = typeToken.getType();
			
			final Type elementType = Types.getIterableParameter(type);
			TypeToken<?> elementTypeToken = TypeToken.of(elementType);
			Class<?> elementRawType = elementTypeToken.getRawType();
			
			ModelTypeAdapterFactory factory = Model.getTypeAdapterFactory(elementRawType);
			factory.registerSerializationAdapter(Model.class, getSerializationStrategy());
			
			Gson gson = new GsonBuilder()
					.registerTypeAdapterFactory(Model.getTypeAdapterFactory(elementRawType))
					.registerTypeAdapterFactory(new ForeignCollectionTypeAdapterFactory())
					.registerTypeAdapter(Date.class, new DateTypeAdapter())
					.serializeNulls()
					.create();
			
			return gson.toJsonTree(col, col.getClass());
		}
		return JsonNull.INSTANCE;
	}

	@Override
	public String toJson() {
		return toJsonElement().toString();
	}
	
	@Override
	public Representation toRepresentation() {
		if (col instanceof ForeignCollection) {
			return new CloseableWrappedIterableRepresentation(((ForeignCollection<T>) col).getWrappedIterable(), getSerializationStrategy());
		} else {			
			final DataElement self = this;
			return new GsonRepresentation(toJsonElement()) {
				
				@Override
				public DataElement getDataElement() {
					return self;
				}
				
			};
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		OrmCollectionElement other = (OrmCollectionElement) obj;
		if (col == null) {
			return other.col == null;
		}
		
		return col.equals(other.col);
	}
	
	@Override
	public int hashCode() {
		return col == null ? super.hashCode() : col.hashCode();
	}

	/**
	 * If the underlying collection is a {@link ForeignCollection}, this function
	 * returns the database access object (DAO) associated with the collection.
	 * Returns {@code null} if the underlying collection is not a foreign
	 * collection.
	 * 
	 * @return {@link Dao} associated with the collection, {@code null} if the
	 *         underlying collection is not a foreign collection
	 */
	@SuppressWarnings("unchecked")
	private Dao<T, ?> getDao() {
		Class<?> clazz = col == null ? null : col.getClass();
		Dao<T, ?> result = null;
		
		if (clazz != null && BaseForeignCollection.class.isAssignableFrom(clazz)) {
			try {
				Field field = BaseForeignCollection.class.getField("dao");
				field.setAccessible(true);
				result = (Dao<T, ?>) field.get(col);
			} catch (Exception ex) {
				// We ignore exceptions as a result of reflections
			}
		} 
		
		return result;
	}
	
	/**
	 * An internal implementation of an {@link Iterator} to navigate through the
	 * elements in the underlying {@link Collection}.
	 */
	private final class OrmCollectionElementIterator implements CloseableIterator<DataElement> {
		
		/** A closeable iterable that can be closed. */
		private final CloseableWrappedIterable<T> iterable;
		
		/** The underlying iterator. */
		private final Iterator<T> iterator;
		
		OrmCollectionElementIterator() {
			// Obtain an iterator for the collection. We obtain a wrapped iterable for
			// foreign collections so that if this method is called in a thread, the
			// iterator can be closed independently
			if (col instanceof ForeignCollection) {
				iterable = ((ForeignCollection<T>) col).getWrappedIterable();
				iterator = iterable.iterator();
			} else {
				iterable = null;
				iterator = col.iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public DataElement next() {
			T model = iterator.next();
			return new OrmObjectElement(model);
		}

		@Override
		public void remove() {
			iterator.remove();
		}

		@Override
		public void close() {
			if (iterable != null) {
				try {
					iterable.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
		
	}

}
