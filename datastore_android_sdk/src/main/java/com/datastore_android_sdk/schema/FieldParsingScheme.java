/**
 * FieldParsingScheme.java
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
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

/**
 * A scheme to parse field value to the corresponding data type for a data model.
 */
public class FieldParsingScheme {
	
	/** The Java class type of the data model to be parsed. */
	private final Class<?> raw;

	/**
	 * Constructor.
	 * 
	 * @param type the Java class type of a data model
	 */
	public FieldParsingScheme(Class<?> type) {
		this.raw = type;
	}

	/**
	 * Parses the given {@code fieldValue} into the appropriate type of the data
	 * field identified by the given {@code fieldName}.
	 * 
	 * @param fieldName
	 *            the name of the field to parse into
	 * @param fieldValue
	 *            the value to parse
	 * @return the parsed value of the given field
	 * @throws SQLException
	 *             thrown if there was error parsing the value
	 * @throws NoSuchFieldException
	 *             thrown the given field is not found
	 */
	public Object parseField(String fieldName, Object fieldValue) throws SQLException, NoSuchFieldException {
		Type type = getFieldType(fieldName);
		if (Date.class.equals(type)) {
			try {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
				try {
					if (((String) fieldValue).length() == simpleDateFormat.toPattern().length()) {
						return simpleDateFormat.parse((String) fieldValue);
					}
				} catch (Exception e) {
				}

				simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.US);
				try {
					if (((String) fieldValue).length() == simpleDateFormat.toPattern().length() - 2) {
						return simpleDateFormat.parse((String) fieldValue);
					}
				} catch (Exception e) {
				}

				final int length = 4;
				String format = "yyyy-MM-dd HH:mm:ss.SSSSSS";
				simpleDateFormat = new SimpleDateFormat(format, Locale.US);
				int lengthLimit = format.length() - length;
				try {
					while (format.length() > lengthLimit) {
						if (((String) fieldValue).length() == simpleDateFormat.toPattern().length()) {
							return simpleDateFormat.parse((String) fieldValue);
						}
						format = format.substring(0, format.length() - 2);
						simpleDateFormat = new SimpleDateFormat(format, Locale.US);
					}
				} catch (Exception e) {
				}

				format = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";
				simpleDateFormat = new SimpleDateFormat(format, Locale.US);
				lengthLimit = format.length() - length;
				try {
					while (format.length() > lengthLimit) {
						if (((String) fieldValue).length() == simpleDateFormat.toPattern().length() - 2) {
							return simpleDateFormat.parse((String) fieldValue);
						}
						format = format.substring(0, format.length() - 2);
						simpleDateFormat = new SimpleDateFormat(format, Locale.US);
					}
				} catch (Exception e) {
				}

				long longDate = Long.parseLong((String) fieldValue);
				return new Date(longDate);

			} catch (Exception ex) {
				throw new SQLException("[" + fieldValue
						+ "] does not match the data type of field [" + fieldName
						+ "] where its type is [" + type + "]", ex);
			}
		} else if (Long.class.equals(type)) {
			return Long.parseLong((String) fieldValue);
		}
		return fieldValue;
	}

	/**
	 * Returns the {@link Type} of the {@link Field} in the data model.
	 * 
	 * @param fieldName the name of the {@link Field}
	 * @return the type of {@link Field} or {@code null} if the field is not found
	 */
	private Type getFieldType(String fieldName) {
		if (raw != null && !raw.isInterface()) {
			Class<?> rawType = raw;
			TypeToken<?> type = TypeToken.get(rawType);
			while (rawType != Object.class) {
				Field[] fields = raw.getDeclaredFields();
				for (Field field : fields) {
					String name = getFieldName(field);
					if (name.equals(fieldName)) {
						return field.getGenericType();
					}
				}
				type = TypeToken.get($Gson$Types.resolve(type.getType(), raw,
						raw.getGenericSuperclass()));
				rawType = type.getRawType();
			}
		}
		return null;
	}

	/**
	 * Returns the name of the given {@link Field}.
	 * 
	 * @param f the {@link Field} to return the name
	 * @return the serialized name of the {@link Field}
	 */
	private String getFieldName(Field f) {
		SerializedName serializedName = f.getAnnotation(SerializedName.class);
		return serializedName == null ? f.getName() : serializedName.value();
	}

}
