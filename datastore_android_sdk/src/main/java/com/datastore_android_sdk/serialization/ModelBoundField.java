/**
 * ModelBoundField.java
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;

import org.jasypt.util.password.PasswordEncryptor;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Primitives;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.datastore_android_sdk.schema.BlobDatabaseField;
import com.datastore_android_sdk.schema.Model;
import com.datastore_android_sdk.schema.PasswordField;


/**
 * A {@link BoundField} in a data model.
 */
public class ModelBoundField extends BoundField {
	
	protected final Gson context;
	final TypeToken<?> fieldType;
	final TypeAdapter<?> typeAdapter;
	
	protected ModelBoundField(
			Gson context,
			Field field, 
			String name, 
			TypeToken<?> fieldType, 
			boolean serialize,
			boolean deserialize) {
		
		super(name, field, serialize, deserialize);
		this.context = context;
		this.fieldType = fieldType;
		typeAdapter = context.getAdapter(fieldType);
	}
	
	public static ModelBoundField create(
			Gson context,
			Field field, 
			String name, 
			TypeToken<?> fieldType, 
			boolean serialize,
			boolean deserialize) {
		
		return new ModelBoundField(context, field, name, fieldType, serialize, deserialize);
	}
	
	protected boolean isIdField() {
		DatabaseField dbField = field.getAnnotation(DatabaseField.class);
		return dbField == null ? false : dbField.id() || dbField.generatedId() || !dbField.generatedIdSequence().isEmpty();
	}
	
	protected boolean isReadOnlyField() {
		DatabaseField dbField = field.getAnnotation(DatabaseField.class);
		return dbField == null ? false : dbField.readOnly();
	}
	
	protected boolean isForeignField() {
		DatabaseField dbField = field.getAnnotation(DatabaseField.class);
		return dbField == null ? false : dbField.foreign();
	}
	
	protected boolean isBlobField() {
		BlobDatabaseField blobField = field.getAnnotation(BlobDatabaseField.class);
		return blobField == null ? false : true;
	}
	
	protected boolean isForeignCollection() {
		ForeignCollectionField foreignCollectionField = field.getAnnotation(ForeignCollectionField.class);
		return foreignCollectionField != null;
	}
	
	protected boolean isPasswordField() {
		PasswordField passwordField = field.getAnnotation(PasswordField.class);
		return passwordField != null;
	}
	
	@Override
	public void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
		write(writer, value, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })	// the type adapter and field type always agree
	public void write(
			JsonWriter writer, 
			Object value, 
			ModelSerializationStrategy serializationStrategy) throws IOException, IllegalAccessException {
		
		Object fieldValue = field.get(value);
		boolean shouldExpand = serializationStrategy == null ? false : serializationStrategy.shouldExpandField(field);
		
		if (isBlobField() && serializationStrategy.shouldSerializeBlobField(field)) {
			if (fieldValue != null) {				
				BlobAdapter adapter = BlobAdapters.get(fieldValue.getClass());
				adapter.write(writer, fieldValue);
			} else {
				writer.nullValue();
			}
		} else if (isForeignField() && !shouldExpand && fieldValue != null) {
			Object val = null;
			try {
				Method method = fieldValue.getClass().getMethod("getIdentity");
				val = method.invoke(fieldValue);
			} catch (Exception ex) {
				// ignore the field if there was an error
			}
			
			if (val != null) {				
				TypeAdapter t = context.getAdapter(val.getClass());
				t.write(writer, val);
			} else {
				writer.nullValue();
			}
		} else if ((isForeignField() || isForeignCollection()) && fieldValue != null) {
			TypeAdapter t = context.getAdapter(fieldValue.getClass());
			
			if (t instanceof DaoTypeAdapter) {
				DaoTypeAdapter<?> adapter = (DaoTypeAdapter<?>) t;
				
				ModelSerializationStrategy policy = (ModelSerializationStrategy) serializationStrategy.getFieldSerializationStrategy(field);
				if (policy != null && policy.shouldRefreshField(field) && fieldValue instanceof Model) {
					try {
						((Model) fieldValue).refresh();
					} catch (SQLException e) {
						// Ignored
					}
				}
				
				// Field serialization policy takes precedence of the serialization
				// policy specified for the type adapter factory
				ModelSerializationStrategy strategy = adapter.setSerializationStrategy(policy);
				t.write(writer, fieldValue);
				adapter.setSerializationStrategy(strategy);
			} else {
				if (isPasswordField()) {
					PasswordField pwdField = field.getAnnotation(PasswordField.class);
					if (pwdField.encryptSerialize()) {
						try {
							PasswordEncryptor encryptor = pwdField.encryptorClass().newInstance();
							t.write(writer, encryptor.encryptPassword((String) fieldValue));
						} catch (InstantiationException e) {
							// Ignored
						} catch (ClassCastException ex) {
							// Ignored
						}
					} else {
						t.write(writer, fieldValue);
					}
				} else {
					t.write(writer, fieldValue);
				}
			}
		} else {
			TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(
					context, 
					typeAdapter,
					fieldType.getType());
			
			t.write(writer, fieldValue);
		}
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	public void read(JsonReader reader, final Object value) throws IOException, IllegalAccessException {
		boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
		if (typeAdapter instanceof ForeignCollectionTypeAdapter) {
			((ForeignCollectionTypeAdapter) typeAdapter).setDeserializationContext(new ForeignCollectionDeserializationContext() {
				
				@Override
				public Object getParent() {
					return value;
				}
				
				@Override
				public String getColumnName() {
					ForeignCollectionField fcField = field.getAnnotation(ForeignCollectionField.class);
					return fcField == null ? name : fcField.columnName();
				}
				
				@Override
				public String getOrderColumnName() {
					ForeignCollectionField fcField = field.getAnnotation(ForeignCollectionField.class);
					return fcField == null ? "" : fcField.orderColumnName();
				}
				
				@Override
				public boolean getOrderAscending() {
					ForeignCollectionField fcField = field.getAnnotation(ForeignCollectionField.class);
					return fcField == null ? true : fcField.orderAscending();
				}
				
			});
		}
		
		Object fieldValue = typeAdapter.read(reader);
		if (fieldValue != null || !isPrimitive) {
			if (isPasswordField()) {
				try {
					// Encrypt the password
					PasswordField pwdField = field.getAnnotation(PasswordField.class);
					if (pwdField.encryptDeserialize()) {
						PasswordEncryptor encryptor = pwdField.encryptorClass().newInstance();
						field.set(value, encryptor.encryptPassword((String) fieldValue));
					} else {
						field.set(value, fieldValue);
					}
				} catch (InstantiationException e) {
					// Ignored
				} catch (ClassCastException ex) {
					// Ignored
				}
			} else {
				field.set(value, fieldValue);
			}
		}
	}

}
