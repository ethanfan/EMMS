/**
 * PayloadTypeAdapterFactory.java
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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import com.datastore_android_sdk.messaging.Notification;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.ConstructorConstructor;
import com.google.gson.internal.Excluder;
import com.google.gson.internal.ObjectConstructor;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.datastore_android_sdk.messaging.Payload;


/**
 * Type adapter that reflects over the fields and methods of a {@link Payload} class.
 */
public class PayloadTypeAdapterFactory implements TypeAdapterFactory {
	
	/** The field name to store internal properties of the object. */ 
	static final String PROPERTIES_FIELD_NAME = "properties";
	
	private final ConstructorConstructor constructorConstructor;
	private final FieldNamingStrategy fieldNamingPolicy;
	protected final Excluder excluder;
	
	private TypeAdapterFactory internalPropertiesTypeAdapterFactory;
	
	/**
	 * Constructor a {@TypeAdapterFactory} without an {@code Excluder}.
	 * 
	 * @param constructorConstructor The constructor to construct a {@link Payload}
	 * @param fieldNamingPolicy The naming policy to use when a field is de/serialized
	 */
	public PayloadTypeAdapterFactory(ConstructorConstructor constructorConstructor,	FieldNamingStrategy fieldNamingPolicy) {
		this(constructorConstructor, fieldNamingPolicy, null);
	}
	
	public PayloadTypeAdapterFactory(
			ConstructorConstructor constructorConstructor,
			FieldNamingStrategy fieldNamingPolicy, 
			Excluder excluder) {
		
		this.constructorConstructor = constructorConstructor;
		this.fieldNamingPolicy = fieldNamingPolicy;
		this.excluder = excluder;
	}
	
	/**
	 * Tells GSON to use {@code factory} to retrieve the {@link TypeAdapter} for parsing properties
	 * in a {@link Notification}.
	 * 
	 * @param factory the {@link PropertyTypeAdapterFactory} to use
	 * @return the newly configured {@link MessageTypeAdapterFactory}
	 */
	public PayloadTypeAdapterFactory usePropertyTypeAdapterFactory(TypeAdapterFactory factory) {
		internalPropertiesTypeAdapterFactory = factory;
		return this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
		Class<? super T> raw = type.getRawType();

		if (!Payload.class.isAssignableFrom(raw)) {
			return null; // it's not a Message!
		}
		
		ObjectConstructor<T> constructor = constructorConstructor.get(type);
		return (TypeAdapter<T>) new PayloadTypeAdapter(constructor, getBoundFields(gson, type, raw));
	}
	
	/**
	 * Returns true if the given field should be excluded in serialization, false otherwise.
	 * 
	 * @param f the field to serialize
	 * @param serialize true if it was serializing, false if it was deserializing
	 * @return true if the field should be excluded
	 */
	public boolean includeField(Field f, boolean serialize) {
		// The internal properties field should never be excluded unless {@link
		// parseRequiredFieldsOnly} is set to {@code true}
		if (PROPERTIES_FIELD_NAME.equals(f.getName())) {
			return true;
		}
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
	protected BoundField createBoundField(
			final Gson context,
			final Field field,
			final String name,
			final TypeToken<?> fieldType, 
			boolean serialize,
			boolean deserialize) {
		
		if (PROPERTIES_FIELD_NAME.equals(name)) {
			return InternalPropertyMapBoundField.create(
					context, 
					internalPropertiesTypeAdapterFactory, 
					field, 
					name, 
					fieldType, 
					serialize, 
					deserialize);
		} else {
			return ReflectiveBoundField.create(context, field, name, fieldType, serialize, deserialize);
		}
	}
	
	/**
	 * Creates the bound fields for the given type.
	 * 
	 * @param context the Gson context
	 * @param type the type of the class
	 * @param raw the raw type of the class
	 * @return a map of bound fields
	 */
	private Map<String, BoundField> getBoundFields(
			Gson context,
			TypeToken<?> type,
			Class<?> raw) {
		
		Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
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
				BoundField boundField = createBoundField(
						context, 
						field, 
						getFieldName(field), 
						TypeToken.get(fieldType), 
						serialize, 
						deserialize);
				
				BoundField previous = result.put(boundField.name, boundField);
				if (previous != null) {
					throw new IllegalArgumentException(declaredType + " declared multiple JSON fields named " + previous.name);
				}
			}
			
			typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
			rawType = typeToken.getRawType();
		}
		
		return result;
	}

}
