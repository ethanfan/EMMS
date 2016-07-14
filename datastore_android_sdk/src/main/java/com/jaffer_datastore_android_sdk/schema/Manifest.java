/**
 *  Manifest.java
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
package com.jaffer_datastore_android_sdk.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The data model that represents a manifest record in the datastore.
 */
@DatabaseTable(tableName = "manifests")
public class Manifest extends Model<Manifest, String> implements Identity<String>  {
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME 			= "name";
	public static final String VALUE_FIELD_NAME		= "value";
	
	@DatabaseField(
			columnName = ID_FIELD_NAME, 
			canBeNull = false, 
			id = true, 
			unique = true, 
			indexName = "manifest_idx")
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private String name;
	
	@DatabaseField(
			columnName = VALUE_FIELD_NAME, 
			canBeNull = true)
	@SerializedName(VALUE_FIELD_NAME)
	@Expose
	private String value;
	
	@Override
	public int hashCode() {
		return (int) name.hashCode();
	}
	
	@Override
	public String getIdentity() {
		return getName();
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}
	
	/** Constructor. */
	public Manifest() {}
	
	/**
	 * Creates a new instance of {@link Manifest} with the given {@code name}
	 * and {@code value}.
	 * 
	 * @param name the name of the manifest record
	 * @param value the value of the manifest record
	 */
	public Manifest(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the name of the manifest record.
	 * 
	 * @return the name of the manifest record
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Specifies the name of the manifest record.
	 * 
	 * @param name the name of the manifest record
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the value of the manifest record.
	 * 
	 * @return the value of the manifest record
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Specifies the value of the manifest record.
	 * 
	 * @param value the value of the manifest record
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
