/**
 * FieldNamingScheme.java
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

/**
 * A scheme to translate serialized name to the column names for a data model.
 * 
 * @author Stanley Lam
 */
public final class FieldNamingScheme {
	
	/** The Java class type of the data model whose columns are to be translated. */
	private final Class<?> raw;
	
	/**
	 * Creates a new instance of the {@link FieldNamingScheme}.
	 * 
	 * @param type the Java class type of the data model
	 */
	public FieldNamingScheme(Class<?> type) {
		this.raw = type;
	}
	
	/**
	 * Translate a serialized field name into a database column name.
	 * 
	 * @param serializedName
	 *            the serialized field name
	 * @return the database column name identified by the serialized field name
	 */
	public String translateName(String serializedName) {
		Field field = Schema.getAttributes(raw).getColumns().get(serializedName);
		return field == null ? serializedName : field.getName();
	}

}
