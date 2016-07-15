/** 
 * ModelAttributes.java
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
package com.datastore_android_sdk.schema;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datastore_android_sdk.reflect.Iterables;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


public final class ModelAttributes {
	
	/** The class type of the model. */
	private final Class<?> raw;
	
	/** The naming strategy to use translate the field name into serialized name. */
	private final FieldNamingStrategy fieldNamingPolicy;
	
	/** The database fields in the model. */
	private Map<String, Field> columns;
	
	/** All {@link Fields} in a class. */
	private Map<String, Field> fields;
	
	/** All {@link Fields} that are marked with {@link Exposed} annotation. */
	private Map<String, Field> exposedFields;
	
	/**
	 * Constructs a Model Attributes object from the {@code type}
	 * @param type the model to pull attributes from
	 */
	public ModelAttributes(Class<?> type) {
		this(type, FieldNamingPolicy.IDENTITY);
	}
	
	/**
	 * Constructs a Model Attributes object from the {@code type} with the 
	 * given {@code namingPolicy} 
	 * @param type the model to pull attributes from
	 * @param namingPolicy the naming strategy to use for naming the Field(s) 
	 */
	public ModelAttributes(Class<?> type, FieldNamingStrategy namingPolicy) {
		raw = type;
		fieldNamingPolicy = namingPolicy;
		exposedFields = new HashMap<String, Field>();
		fields = new HashMap<String, Field>();
		columns = new HashMap<String, Field>();
		
		if (!type.isInterface()) {
			Class<?> rawType = raw;
			TypeToken<?> typeToken = TypeToken.get(rawType);
			while (rawType != Object.class) {
				Field[] rawFields = rawType.getDeclaredFields();
				for (Field field : rawFields) {
					String serializedName = getFieldSerializedName(field);
					if (!fields.containsKey(serializedName)) {						
						fields.put(serializedName, field);
					}
					if (isFieldExposed(field) && !exposedFields.containsKey(serializedName)) {
						exposedFields.put(serializedName, field);
					}
					if ((isDatabaseField(field) || isForeignCollection(field)) && !columns.containsKey(serializedName)) {
						columns.put(serializedName, field);
					}
				}
				
				typeToken = TypeToken.get($Gson$Types.resolve(typeToken.getType(), rawType, rawType.getGenericSuperclass()));
				rawType = typeToken.getRawType();
	  		}
		}
	}
	
	/**
	 * Returns all fields that are exposed (i.e. fields that are marked with
	 * {@link Expose} annotation) in a data model
	 * 
	 * @return an unmodifiable map of all fields with the serialized name of the
	 *         fields as keys and {@link Field} as the value that are exposed in
	 *         a data model.
	 * @see {@link #getAllFields(boolean)}
	 */
	public Map<String, Field> getAllExposedFields() {
		return Collections.unmodifiableMap(exposedFields);
	}
	
	/**
	 * Returns all fields in a data model.
	 */
	public Map<String, Field> getAllFields() {
		return Collections.unmodifiableMap(fields);
	}
	
	/**
	 * Returns the name of the database table the class Model represents
	 * @return the name of the database table
	 */
	public String getTableName() {
		DatabaseTable table = raw == null ? null : raw.getAnnotation(DatabaseTable.class);
		return table == null ? raw.getSimpleName() : table.tableName();
	}
	
	/**
	 * Returns a Class object that identifies the declared type for the 
	 * field represented by this Field object. If it was a foreign field, the 
	 * declared type for the ID field associated with the foreign object is
	 * returned
	 * @param fieldName the name of the Field
	 * @return a Class object identifying the declared type of the field
	 * represented by this object 
	 */
	public Class<?> getColumnType(String fieldName) {
		Field field = columns.get(fieldName);
		if (isForeignField(field)) {
			ModelAttributes attr = new ModelAttributes(field.getType());
			return attr.getIdField() == null ? null : attr.getColumns().get(attr.getIdField()).getType();
		}
		
		return field == null ? null : field.getType();
	}
	
	/**
	 * Returns the serialized name in the foreign collection that defines the relationship
	 * between the type of the ModelAttribute and the objects on the foreign collection
	 * 
	 * @param fieldName the serialzied name of the foreign collection
	 * @return the column name that defines the relationship, null if the field specified
	 * was not a foreign collection
	 */
	public String getForeignCollectionForeignName(String fieldName) {
		String result = null;
		String foreignFieldName = null;
		
		ModelAttributes attrs = Schema.getAttributes((Class<?>) getFieldGenericType(fieldName));
		if (attrs == null || !isForeignCollection(fieldName)) {
			return result;
		}
		
		// Determines whether or not the a foreign collection field name is 
		// specified in the annotation
		ForeignCollectionField fcField = columns.get(fieldName).getAnnotation(ForeignCollectionField.class);
		foreignFieldName = fcField == null ? null : fcField.foreignFieldName();
		
		// Loop through the foreign collection object to find the field
		// that corresponds to the {@code} raw type of the model attributes
		for (String name : attrs.columns.keySet()) {
			Field field = attrs.columns.get(name);
			boolean isRequestedField = false;
			if (foreignFieldName == null || foreignFieldName.isEmpty()) {
				isRequestedField = field.getType().equals(raw);
			} else {
				isRequestedField = field.getName().equals(foreignFieldName);
			}
			
			// Found the foreign field
			if (isRequestedField) {
				result = name;
				break;
			}
		}
		
		return result;
	}
	
	/**
	 * Returns a Type object that represents the declared type of the given column. 
	 * If the column was a foreign collection, the Type object returned reflects the 
	 * type of the containing element
	 * 
	 * @param fieldName the serialized name of the database column
	 * @return the type of the given column, null if the column was not found
	 */
	@Deprecated
	public Type getColumnGenericType(String fieldName) {
		return getFieldGenericType(fieldName);
	}
	
	/**
	 * Returns a Type object that represents the declared type of the given field. 
	 * If the field was a collection, the Type object returned reflects the 
	 * type of the containing element
	 * 
	 * @param fieldName the serialized name of the field
	 * @return the type of the given column, null if the column was not found
	 */
	public Type getFieldGenericType(String fieldName) {
		Field field = fields.get(fieldName);
		
		if (isForeignCollection(field) || (field != null && Iterables.isIterable(field.getGenericType()))) {
			Type genericFieldType = field.getGenericType();
				
			if (genericFieldType instanceof ParameterizedType) {
				ParameterizedType foreignCollectionType = (ParameterizedType) genericFieldType;
				Type[] fieldArgTypes = foreignCollectionType.getActualTypeArguments();
				return fieldArgTypes.length > 0 ? fieldArgTypes[0] : null; 
			}
		} else {
			return field == null ? null : field.getGenericType();
		}
		
		return null;
	}

	/**
	 * Retrieves the database column name of a {@link Field} in the {@link Model}
	 * with the given serialized name
	 * 
	 * @param fieldName
	 *          the name of the Field
	 * @return The database column name of a {@link Field} in the {@link Model}
	 *         with the given serialized name
	 */
	public String getColumnName(String fieldName) {
		Field field = columns.get(fieldName);
		return getColumnName(field);
	}

	/**
	 * Returns the database column name of a {@link Field} in the {@link Model} of
	 * a given type
	 * 
	 * @param type
	 *          The type of the {@link Field}
	 * @return The database column name of a {@link Field} in the {@link Model}
	 *         with the given serialized name. An empty {@link String} will be
	 *         returned if no {@link Field} in the {@link Model} is of the given
	 *         {@code type}
	 */
	public String getColumnName(Class<?> type) {
		for (Field field : columns.values()) {
			if (field.getType().equals(type)) {
				return getColumnName(field);
			}
		}
		return "";
	}
	
	/**
	 * Returns whether or not the given field can be {@code null}
	 * 
	 * @param fieldName the serialized name of the field to examine
	 * @return {@code true} if the field can be {@code null} or if the {@link Field} identified is not a database field, {@code false} otherwise
	 */
	public boolean canBeNull(String fieldName) {
		Field field = columns.get(fieldName);
		return canBeNull(field);
	}
	
	/**
	 * Returns the name of the ID field of the data class
	 * @return The name of the ID field, null if no ID field is found
	 */
	public String getIdField() {
		for (String fieldName : columns.keySet()) {
			if (isIdField(columns.get(fieldName))) {
				return fieldName;
			}
		}
		// Can be null if there was no ID field in a data class
		return null;
	}

	/**
	 * Returns a list of names that are annotated as password in the model
	 * 
	 * @return A list of column names that are annotated as password in the model.
	 *         If no password field is defined in the model, an empty list will be
	 *         returned
	 */
	public List<String> getPasswordFields() {
		List<String> result = new ArrayList<String>();
		
		for (String name : fields.keySet()) {
			if (isPasswordField(fields.get(name))) {
				result.add(name);
			}
		}
		
		return result;
	}
	
	/**
	 * Returns whether or not the ID field of the data class is generated
	 * @return true if the ID column was auto generated, false otherwise
	 */
	public boolean isIdFieldGenerated() {
		String idFieldName = getIdField();
		return idFieldName == null ? false : isGeneratedId(columns.get(idFieldName));
	}
	
	/**
	 * Returns the columns in a data model
	 * @return a map of columns the data model represents.
	 */
	public Map<String, Field> getColumns() {
		return Collections.unmodifiableMap(columns);
	}
	
	/**
	 * Determines whether or not a Field represents an ID column
	 * @param columnName the column name of field being examined
	 * @return true if the Field represents an ID column, false otherwise
	 */
	public boolean isIdColumn(String columnName) {
		return isIdField(columns.get(columnName));
	}
	
	/**
	 * Determines whether a given column represents a foreign field
	 * @param columnName the column name of field being examined
	 * @return true if the Field represents a foreign field, false otherwise
	 */
	public boolean isForeignField(String columnName) {
		return columnName == null ? false : isForeignField(columns.get(columnName));
	}
	
	/**
	 * Determines whether or not a given column is ready only. A read-only field will be returned in queries but ignored
	 * in {@link Model#update()} or {@link Model#create()}.
	 * 
	 * @param columnName The name of the field being examined
	 * @return {@code true} if the field is read-only, {@code false} otherwise
	 */
	public boolean isReadOnly(String columnName) {
		return columnName == null ? false : isReadOnlyField(columns.get(columnName));
	}
	
	/**
   * Determines whether or not a given column is a foreign collection
   * @param columnName the column name of field to determine whether it is a foreign collection
   * @return true if the field is a foreign collection, false otherwise
   */
	public boolean isForeignCollection(String columnName) {
		return columnName == null ? false : isForeignCollection(columns.get(columnName));
	}
	
	/**
	 * Determines whether or not a given field's value should be encrypted
	 * 
	 * @param columnName
	 *            the name of the database column
	 * @return true if the field should be encrypted, false otherwise
	 */
	@Deprecated
	public boolean shouldEncryptColumn(String columnName) {
		return shouldEncryptField(columnName);
	}
	
	/**
	 * Determines whether or not a given field's value should be encrypted
	 * 
	 * @param fieldName
	 *            the serialized name of the field
	 * @return true if the field should be encrypted, false otherwise
	 */
	public boolean shouldEncryptField(String fieldName) {
		return fieldName == null ? false : shouldEncryptField(fields.get(fieldName));
	}

	/**
	 * Returns whether or not the given field is a BLOB data
	 * 
	 * @param columnName
	 *            The serialized name of the model
	 * @return {@code true} if the column is a BLOB field, {@code false}
	 *         otherwise
	 */
	@Deprecated
	public boolean isBlobColumn(String columnName) {
		return isBlobField(columnName);
	}
	
	/**
	 * Returns whether or not the given field is a BLOB data
	 * 
	 * @param fieldName
	 *            The serialized name of the field
	 * @return {@code true} if the column is a BLOB field, {@code false}
	 *         otherwise
	 */
	public boolean isBlobField(String fieldName) {
		return fieldName == null ? null : isBlobField(fields.get(fieldName));
	}
  
	/**
	 * Returns whether or not a given field should be omitted
	 * 
	 * @param columnName
	 *            The serialized name of a field in a data model
	 * @return {@code true} if the field should be omitted, {@code false}
	 *         otherwise
	 */
	@Deprecated
	public boolean shouldOmit(String columnName) {
		return shouldOmitField(columnName);
	}
	
	/**
	 * Returns whether or not a given field should be omitted
	 * 
	 * @param fieldName
	 *            The serialized name of the field
	 * @return {@code true} if the field should be omitted, {@code false}
	 *         otherwise
	 */
	public boolean shouldOmitField(String fieldName) {
		return fieldName == null ? false : shouldOmit(fields.get(fieldName));
	}
	
	/**
	 * Determines whether or not a given column is exposed for de/serialization
	 * 
	 * @param columnName
	 *            the column name of field to determine whether it is expose
	 * @return true if the field was exposed, false otherwise
	 */
	@Deprecated
	public boolean isColumnExposed(String columnName) {
		return isFieldExposed(columnName);
	}
	
	/**
	 * Determines whether or not a given column is exposed for de/serialization
	 * 
	 * @param fieldName
	 *            The serialized name of the field
	 * @return true if the field was exposed, false otherwise
	 */
	public boolean isFieldExposed(String fieldName) {
		return fieldName == null ? false : isFieldExposed(fields.get(fieldName));
	}

	/**
	 * Determines whether or not a given field is a password field
	 * 
	 * @param columnName
	 *            the column name of field to determine whether it is a password
	 *            field
	 * @return true if the field is a password field, false otherwise
	 */
	@Deprecated
	public boolean isPasswordColumn(String columnName) {
		return isPasswordField(columnName);
	}
	
	/**
	 * Determines whether or not a given field is a password field
	 * 
	 * @param fieldName
	 *            The serialized name of the field
	 * @return true if the field is a password field, false otherwise
	 */
	public boolean isPasswordField(String serializedName) {
		return serializedName == null ? false : isPasswordField(fields.get(serializedName));
	}

	/**
	 * Returns whether the {@link Field} identified by {@code columnName} is a
	 * temporary password field
	 * 
	 * @param columnName
	 *            The serialized name of the {@link Field} in the data model
	 * @return {@code true} if the {@link Field} is a temporary password field,
	 *         {@code false} otherwise
	 */
	@Deprecated
	public boolean isTemporaryPasswordColumn(String columnName) {
		return isTemporaryPasswordField(columnName);
	}
	
	/**
	 * Returns whether the {@link Field} identified by {@code columnName} is a
	 * temporary password field
	 * 
	 * @param fieldName
	 *            The serialized name of the field
	 * @return {@code true} if the {@link Field} is a temporary password field,
	 *         {@code false} otherwise
	 */
	public boolean isTemporaryPasswordField(String fieldName) {
		return isTemporaryPasswordField(fields.get(fieldName));
	}
	
	/**
	 * Determines whether or not a given field is a password field
	 * 
	 * @param f
	 *            the field to determine whether it is a password field
	 * @return true if the field is a password field, false otherwise
	 */
	private boolean isPasswordField(Field f) {
		PasswordField pwdField = f == null ? null : f.getAnnotation(PasswordField.class);
		return pwdField != null;
	}
  
	/**
	 * Returns whether the {@link Field} identified by {@code columnName} is a
	 * temporary password field
	 * 
	 * @param field
	 *            The {@link Field} to determine if it is a temporary password
	 *            field
	 * @return {@code true} if the {@link Field} is a temporary password field,
	 *         {@code false} otherwise
	 */
	private boolean isTemporaryPasswordField(Field f) {
		PasswordField pwdField = f == null ? null : f.getAnnotation(PasswordField.class);
		return pwdField == null ? false : pwdField.isTemporary();
	}

	/**
	 * Determines whether or not a given field is exposed for de/serialization
	 * 
	 * @param f
	 *            the field to determine whether it is expose
	 * @return true if the field was exposed, false otherwise
	 */
	private boolean isFieldExposed(Field f) {
		Expose expose = f == null ? null : f.getAnnotation(Expose.class);
		return expose == null ? false : true;
	}
  
  /**
	 * Determines whether a Field object is a foreign field
	 * @param f the field being examined
	 * @return true if the Field represents a foreign field, false otherwise
	 */
	private boolean isForeignField(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		return databaseField == null ? false : databaseField.foreign();
	}
	
	/**
	 * Determines whether or not a given field is a foreign collection
	 * 
	 * @param f
	 *            the field to determine whether it is a foreign collection
	 * @return true if the field is a foreign collection, false otherwise
	 */
	private boolean isForeignCollection(Field f) {
		ForeignCollectionField foreignCollectionField = f == null ? null : f.getAnnotation(ForeignCollectionField.class);
		return foreignCollectionField != null;
	}
  
	/**
	 * Determines whether or not a given field's value should be encrypted
	 * 
	 * @param f
	 *            the field to determine whether its value should be encrypted
	 * @return true if the field should be encrypted, false otherwise
	 */
	private boolean shouldEncryptField(Field f) {
		Encrypt encrypt = f == null ? null : f.getAnnotation(Encrypt.class);
		return encrypt != null;
	}

	/**
	 * Determines whether or not a given {@link Field} is a BLOB data
	 * 
	 * @param f
	 *            The field to examine
	 * @return {@code true} if the field is a BLOB, {@code false} otherwise
	 */
	private boolean isBlobField(Field f) {
		BlobDatabaseField blob = f == null ? null : f.getAnnotation(BlobDatabaseField.class);
		return blob != null;
	}
  
	/**
	 * Returns whether or not the given {@link Field} should be omitted
	 * 
	 * @param f
	 *            The field to determine whether should be omitted
	 * @return {@code true} if the field should be omitted, {@code false}
	 *         otherwise
	 */
	private boolean shouldOmit(Field f) {
		Omit omit = f == null ? null : f.getAnnotation(Omit.class);
		return omit == null ? false : omit.value();
	}
	
	/**
	 * Determines whether or not a Field represents an ID column
	 * @param f the field being examined
	 * @return true if the Field represents an ID column, false otherwise
	 */
	private boolean isIdField(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		return databaseField == null ? false : databaseField.id() || databaseField.generatedId() || (databaseField.generatedIdSequence() != null && !databaseField.generatedIdSequence().isEmpty());
	}
	
	/**
	 * Determines whether or not a Field represents an auto generated ID column
	 * @param f the field being examined
	 * @return true if the Field represents an auto generated ID column, false otherwise
	 */
	private boolean isGeneratedId(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		return databaseField == null ? false : databaseField.generatedId() || !databaseField.generatedIdSequence().isEmpty();
	}
	
	/**
	 * Determines whether or not a Field represents a column in the database table
	 * @param f the field being examined
	 * @return true if the Field represents a database column, false otherwise
	 */
	private boolean isDatabaseField(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		return databaseField != null;
	}
	
	/**
	 * Determines whether or not a {@link Field} can be {@code null} 
	 * @param f the field being examined
	 * @return {@code true} if the {@link Field} can be {@code null} or if the {@link Field} is not a database field, {@code false} otherwise
	 */
	private boolean canBeNull(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		return databaseField == null ? true : databaseField.canBeNull();
	}
	
	/**
	 * Determines whether or not a {@link Field} is read-only
	 * 
	 * @param f the {@link Field} being examined
	 * @return {@code true} if the {@link DatabaseField} is read-only, {@code false} otherwise
	 */
	private boolean isReadOnlyField(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		return databaseField == null ? false : databaseField.readOnly();
	}
	
	/**
	 * Retrieves the database column name of the a given Field
	 * @param f the Field object
	 * @return the database column name of a given Field
	 */
	private String getColumnName(Field f) {
		DatabaseField databaseField = f == null ? null : f.getAnnotation(DatabaseField.class);
		if (databaseField == null) {			
			ForeignCollectionField foreignCollectionField = f == null ? null : f.getAnnotation(ForeignCollectionField.class);
			return foreignCollectionField == null ? f == null ? "" : f.getName() : foreignCollectionField.columnName();
		} else {			
			return databaseField == null ? f == null ? "" : f.getName() : databaseField.columnName();
		}
	}
	
	/**
   * Returns the name of the given column
   * @param field the field to return the name
   * @return the name of the field
   */
	private String getFieldSerializedName(Field field) {
		return getFieldSerializedName(field, fieldNamingPolicy);
	}
	
	/**
   * Returns the serialized name of the column using the given naming policy
   * @param f the field to return the name
   * @param namingPolicy the naming policy used to serialize name of the column
   * @return the name of the field
   */
	private String getFieldSerializedName(Field f, FieldNamingStrategy namingPolicy) {
		SerializedName serializedName = f == null ? null : f.getAnnotation(SerializedName.class);
		return serializedName == null ? namingPolicy.translateName(f) : serializedName.value();
	}
	
}
