/*
 * Adopted from Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastore_android_sdk.serialization;

import java.io.IOException;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.ForeignCollection;

/**
 * Type adapter that reflects over the fields and methods of a 'ForeignCollection' class.
 * 
 * @author Stanley Lam
 */
public class ForeignCollectionTypeAdapterFactory implements TypeAdapterFactory {
	
	private final ForeignCollectionInstanceCreator creator;
	
	/**
	 * Creates a new instance of {@link ForeignCollectionTypeAdapterFactory}
	 * without an instance creator. Without an instance creator,
	 * {@link ForeignCollection} cannot be deserialized
	 */
	public ForeignCollectionTypeAdapterFactory() {
		this(null);
	}
	
	/**
	 * Creates a new instance of {@link ForeignCollectionTypeAdapterFactory} with
	 * the given {@link ForeignCollection} instance creator.
	 * 
	 * @param creator
	 *          The instance creator that creates {@link ForeignCollection}
	 */
	public ForeignCollectionTypeAdapterFactory(ForeignCollectionInstanceCreator creator) {
		this.creator = creator;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gson.TypeAdapterFactory#create(com.google.gson.Gson, com.google.gson.reflect.TypeToken)
	 */
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
		Type type = typeToken.getType();
		
		Class<? super T> rawType = typeToken.getRawType();		
		if (!ForeignCollection.class.isAssignableFrom(rawType)) {
			return null;
		}
		
		final Type elementType = $Gson$Types.getCollectionElementType(type, rawType);
		TypeToken<?> elementTypeToken = TypeToken.get(elementType);
		
		// Specify a TypeAdapter for the element only if we can retreive a concrete type element type
		TypeAdapter<?> elementTypeAdapter = elementType instanceof TypeVariable ? null
				: gson.getAdapter(elementTypeToken);
    
		@SuppressWarnings({ "unchecked", "rawtypes" })
		TypeAdapter<T> result = (TypeAdapter<T>) new Adapter(gson, elementType, elementTypeAdapter) {
    	
    	@Override
    	public ObjectConstructor<T> getConstructor(final ForeignCollectionDeserializationContext context) {
    		return new ObjectConstructor<T>() {
    			public T construct() {
						if (creator == null) {
							return null;
						} else {
							return (T) creator.createInstance(
									elementType,
									context.getParent(),
									context.getColumnName(),
									context.getOrderColumnName(),
									context.getOrderAscending());
						}
					}
				};
			}

		};
		return result;
	}
	
	/**
	 * The adapter that de/serializes a {@link ForeignCollection}.
	 * 
	 * @author Stanley Lam
	 *
	 * @param <E>
	 */
	public abstract class Adapter<E> extends ForeignCollectionTypeAdapter<ForeignCollection<E>> {
		
		private final TypeAdapterRuntimeTypeWrapper<E> elementTypeAdapter;
		private ModelSerializationStrategy serializationStrategy;
		private ForeignCollectionDeserializationContext context;
    
		public Adapter(
				Gson context, 
				Type elementType,
				TypeAdapter<E> elementTypeAdapter) {
    	
    		this.serializationStrategy = ModelSerializationPolicy.DEFAULT;
    		this.elementTypeAdapter = new TypeAdapterRuntimeTypeWrapper<E>(context, elementTypeAdapter, elementType);
		}
    
		public abstract ObjectConstructor<? extends ForeignCollection<E>> getConstructor(
				ForeignCollectionDeserializationContext context);
    
		@Override
		public ModelSerializationStrategy setSerializationStrategy(
				ModelSerializationStrategy strategy) {

			ModelSerializationStrategy returnValue = this.serializationStrategy;
			this.serializationStrategy = strategy == null ? ModelSerializationPolicy.DEFAULT : strategy;
			return returnValue;
		}
    
		@Override
		public void setDeserializationContext(
				ForeignCollectionDeserializationContext ctx) {
			context = ctx;
		}

		public ForeignCollection<E> read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}

			ObjectConstructor<? extends ForeignCollection<E>> constructor = getConstructor(context);
			ForeignCollection<E> collection = constructor == null ? null : constructor.construct();
			if (collection != null) {
				in.beginArray();
				while (in.hasNext()) {
					E instance = elementTypeAdapter.read(in);
					collection.add(instance);
				}
				in.endArray();
			}
			return collection;
		}
    
		@SuppressWarnings("unchecked")
		public void write(JsonWriter out, ForeignCollection<E> collection)
				throws IOException {
			if (collection == null) {
				out.nullValue();
				return;
			}

			out.beginArray();
			CloseableIterator<E> iterator = collection.closeableIterator();
			try {
				while (iterator.hasNext()) {
					E element = iterator.next();
					if (elementTypeAdapter.getRuntimeTypeAdapter(element) instanceof DaoTypeAdapter) {
						DaoTypeAdapter<E> adapter = (DaoTypeAdapter<E>) elementTypeAdapter.getRuntimeTypeAdapter(element);

						// Field serialization policy takes precedence of the
						// serialization policy specified for the type adapter factory
						ModelSerializationStrategy strategy = adapter.setSerializationStrategy(serializationStrategy);
						adapter.write(out, element);
						adapter.setSerializationStrategy(strategy);
					} else {
						elementTypeAdapter.write(out, element);
					}
				}
			} finally {
				try {
					iterator.close();
				} catch (SQLException ex) {
					// Ignored
				}
			}
			out.endArray();
		}
	}
}

