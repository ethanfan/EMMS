/** 
 * Operator.java
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
package com.emms.schema;

import com.datastore_android_sdk.schema.BlobDatabaseField;
import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;


/**
 *
 */
@DatabaseTable(tableName = "Message")
public class Message extends Model<Message, Long> implements Identity<Long> {

	// For QueryBuilder to be able to find the fields
	public static final String ID = "ID";
	public static final String MESSAGE_ID = "Message_ID";
	public static final String MESSAGE = "Message";
    public static final String REFER_ID = "Refer_ID";
	public static final String TYPE = "Type";

	@DatabaseField(id = true,
			columnName = ID, canBeNull = false)
	@SerializedName(ID)
	@Expose
	private Long id;

	@DatabaseField(columnName = MESSAGE_ID, canBeNull = false)
	@SerializedName(MESSAGE_ID)
	@Expose
	private String messsageID;

	@DatabaseField(columnName = MESSAGE, canBeNull = false)
	@SerializedName(MESSAGE)
	@Expose
	private String message;


	@DatabaseField(columnName = REFER_ID, canBeNull = false)
	@SerializedName(REFER_ID)
	@Expose
	private String referID;

	@DatabaseField(columnName = TYPE, canBeNull = false)
	@SerializedName(TYPE)
	@Expose
	private String type;


	@Override
	public int hashCode() {
		return id.hashCode();
	}




	@Override
	public Long getIdentity() {
		return id;
	}

	@Override
	public String getIdentityAttribute() {
		return MESSAGE_ID;
	}


}
