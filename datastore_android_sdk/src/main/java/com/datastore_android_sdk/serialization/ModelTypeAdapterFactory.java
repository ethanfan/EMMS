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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datastore_android_sdk.schema.Model;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.reflect.TypeToken;


/**
 * Type adapter that reflects over the fields and methods of a 'Model' class.
 */
public class ModelTypeAdapterFactory implements TypeAdapterFactory {

	protected final ConstructorConstructor constructorConstructor;
	protected final FieldNamingStrategy fieldNamingPolicy;
	protected final Excluder excluder;
	protected final Map<Type, ModelSerializationStrategy> serializationStrategies =
		new HashMap<Type, ModelSerializationStrategy>();
	
	/**
	 * Construct a {@link TypeAdapterFactory} that includes all fields in a {@link Model}.
	 * 
	 * @param constructorConstructor The consturctor to create {@link Model} object
	 * @param fieldNamingPolicy The naming policy to use during de/serialization of JSON
	 */
	public ModelTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy) {
		this(constructorConstructor, fieldNamingPolicy, null);
	}

	public ModelTypeAdapterFactory(
			ConstructorConstructor constructorConstructor,
			FieldNamingStrategy fieldNamingPolicy, 
			Excluder excluder) {

		this.constructorConstructor = constructorConstructor;
		this.fieldNamingPolicy = fieldNamingPolicy;
		this.excluder = excluder;
	}

	/**
	 * Configures the type adapter factory for custom serialization and
	 * deserialization. This method combines the registration of
	 * {@link BlobDeserializer} and {@link ModelSerializationStrategy}. This
	 * method registers the type specified and no other types. Users must register
	 * related types.
	 * 
	 * @param type
	 *          the type definition for the adapter being registered
	 * @param adapter
	 *          The object must implement at least one of the
	 *          {@link BlobDeserializer}, or {@link ModelSerializationStrategy}
	 *          interfaces.
	 */
	public void registerSerializationAdapter(Type type, Object adapter) {
		if (adapter instanceof ModelSerializationStrategy) {
			serializationStrategies.put(type, (ModelSerializationStrategy) adapter);
		}
	}

	/**
	 * Returns true if the given field should be excluded in serialization, false
	 * otherwise. If no {@link Excluder} was specified, none of the {@link Field}
	 * in should be ignored
	 * 
	 * @param f
	 *          the field to serialize
	 * @param serialize
	 *          true if it was serializing, false if it was deserializing
	 * @return true if the field should be excluded
	 */
	public boolean includeField(Field f, boolean serialize) {
		return excluder == null ? true : !excluder.excludeClass(f.getType(), serialize) && !excluder.excludeField(f, serialize);
	}
	
	/**
	 * Retrieves the name of the given field.
	 * 
	 * @param f the field to retrieve the name for serialization
	 * @return The name of to use for the given field during serialization
	 */
	private String getFieldName(Field f) {
		SerializedName serializedName = f.getAnnotation(SerializedName.class);
		return serializedName == null ? fieldNamingPolicy.translateName(f) : serializedName.value();
	}
	
	@Override
	public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
		Class<? super T> raw = type.getRawType();

		if (!Model.class.isAssignableFrom(raw)) {
			return null; // it's not a Model!
		}

		ObjectConstructor<T> constructor = constructorConstructor.get(type);
		ModelSerializationStrategy serializationStrategy = findSerializationStrategy(raw);
		return new ModelTypeAdapter<T>(
				constructor, 
				serializationStrategy,
				getBoundFields(gson, type, raw));
	}
	
	/**
	 * Searches the registered serialization strategies to find the best matching
	 * strategy for the given {@code type}.
	 * 
	 * @param type
	 *          The raw type of the object to find a serialization strategy
	 * @return The best matching serialization strategy or {@code null} if
	 *         serialization strategy registered for the given {@code type}
	 */
	private ModelSerializationStrategy findSerializationStrategy(Class<?> type) {
		if (type != null) {			
			Class<?> rawType = type;
			TypeToken<?> typeToken = TypeToken.get(rawType);
			
			// Find if there is a serialization strategy registered for the super
			// classes
			while (rawType != Object.class) {
				if (serializationStrategies.containsKey(rawType)) {
					return serializationStrategies.get(rawType);
				}
				typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
				rawType = typeToken.getRawType();
			}
			
			// Find if there is a serialization strategy registered for an interface
			// the type implement
			Type[] interfaces = type.getGenericInterfaces();
			for (Type i : interfaces) {
				if (serializationStrategies.containsKey(i)) {
					return serializationStrategies.get(i);
				}
			}
		}
		return null;
	}

	/**
	 * Creates a bound field.
	 * 
	 * @param context the gson context
	 * @param field the field associated with the bound field
	 * @param name the name of the bound field
	 * @param fieldType the type of the field
	 * @param serialize true to serialize the field, false otherwise
	 * @param deserialize true to deserialzie the field, false otherwise
	 * @return the BoundField object associated with the given Field
	 */
	protected ModelBoundField createBoundField(
			final Gson context,
			final Field field, 
			final String name, 
			final TypeToken<?> fieldType, 
			boolean serialize,
			boolean deserialize) {
		
		return ModelBoundField.create(
				context,
				field,
				name,
				fieldType,
				serialize,
				deserialize);
	}

	/**
	 * Creates the bound fields for the given type.
	 * 
	 * @param context the Gson context
	 * @param type the type of the class
	 * @param raw the raw type of the class
	 * @return a map of bound fields
	 */
	protected Map<String, ModelBoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
		Map<String, ModelBoundField> result = new LinkedHashMap<String, ModelBoundField>();
		if (raw.isInterface()) {
			return result;
		}

		Class<?> rawType = raw;
		TypeToken<?> typeToken = type;
		Type declaredType = typeToken.getType();
		
		while (rawType != Object.class) {
			Field[] fields = rawType.getDeclaredFields();
			for (Field field : fields) {
				boolean serialize = includeField(field, true);
				boolean deserialize = includeField(field, false);
				
				if (!serialize && !deserialize) {
					continue;
				}

				field.setAccessible(true);
				Type fieldType = $Gson$Types.resolve(typeToken.getType(), rawType, field.getGenericType());
				ModelBoundField boundField = createBoundField(
						context, 
						field,
						getFieldName(field),
						TypeToken.get(fieldType), 
						serialize, 
						deserialize);
				
				BoundField previous = result.put(boundField.name, boundField);
				if (previous != null) {
					throw new IllegalArgumentException(declaredType + " declared multiple JSON fields named "
							+ previous.name);
				}
			}
			typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
			rawType = typeToken.getRawType();
		}
		return result;
	}

}
