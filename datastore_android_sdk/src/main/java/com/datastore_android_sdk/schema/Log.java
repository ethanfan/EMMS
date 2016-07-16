/**
 * Log.java
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

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The data model that represents a log entry in the datastore.
 */
@DatabaseTable(tableName = "logs")
public class Log extends Model<Log, Long> implements Identity<Long> {
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME = "log_id";
	public static final String TARGET_FIELD_NAME = "target";
	public static final String ACTION_FIELD_NAME = "action";
	public static final String IDENTIFIER_FIELD_NAME = "identifier";
	
	@DatabaseField(
			columnName = ID_FIELD_NAME, 
			canBeNull = false, 
			generatedId = true, 
			indexName = "log_idx")
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private long logId;
	
	@DatabaseField(
			columnName = TARGET_FIELD_NAME, 
			canBeNull = true)
	@SerializedName(TARGET_FIELD_NAME)
	@Expose
	private String target;
	
	@DatabaseField(
			columnName = ACTION_FIELD_NAME, 
			canBeNull = true)
	@SerializedName(ACTION_FIELD_NAME)
	@Expose
	private int action;
	
	@DatabaseField(
			columnName = IDENTIFIER_FIELD_NAME, 
			canBeNull = true)
	@SerializedName(IDENTIFIER_FIELD_NAME)
	@Expose
	private String identifier;
	
	@DatabaseField(
			columnName = CREATED_DATE_FIELD_NAME, 
			canBeNull = false,
  		dataType = DataType.DATE_STRING,
  		format = "yyyy-MM-dd HH:mm:ss",
  		indexName = "log_createdated_idx")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;
	
	@Override
	public int hashCode() {
		return (int) logId;
	}

	@Override
	public Long getIdentity() {
		return Long.valueOf(getLogId());
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}
	
	/**
	 * An enumeration of actions that can be performed in the datastore.
	 */
	public enum Action {
		CREATE(0),
		READ(1),
		UPDATE(2),
		DELETE(3);
		
		private final int value;
		
		/**
		 * Constructor.
		 * 
		 * @param value the enumeration value of the action
		 */
		private Action(int value) {
			this.value = value;
		}
		
		/**
		 * Returns the underlying value of this enum.
		 * 
		 * @return the underlying value of this enum
		 */
		public int getValue() {
			return value;
		}
	}
	
	/**
	 * Constructor.
	 */
	public Log() {}
	
	/**
	 * Creates a new instance of {@link Log}.
	 * 
	 * @param target
	 *            the target the action performed on
	 * @param action
	 *            the action performed
	 * @param identifier
	 *            the identifier of the data record the action performed on.
	 */
	public Log(String target, Action action, String identifier) {
		this.target = target;
		this.action = action.ordinal();
		this.identifier = identifier;
	}
	
	/**
	 * Returns the identifier of this log entry.
	 * 
	 * @return the identifier of this log entry
	 */
	public long getLogId() {
		return logId;
	}
	
	/**
	 * Specifies the identifier of this log entry.
	 * 
	 * @param logId the identifier of the log entry
	 */
	public void setLogId(long logId) {
		this.logId = logId;
	}
	
	/**
	 * Specifies the the target this log entry.
	 * 
	 * @param target the target
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * Returns the target of this log entry.
	 * 
	 * @return the target of this log entry
	 */
	public String getTarget() {
		return target;
	}
	
	/**
	 * Specifies the action performed in this log entry. 
	 * 
	 * @param action the action performed
	 */
	public void setAction(Action action) {
		this.action = action.ordinal();
	}
	
	/**
	 * Returns the action performed in this log entry.
	 * 
	 * @return the action performed
	 */
	public Action getAction() {
		return Action.values()[action];
	}
	
	/**
	 * Returns the identifier of the data record this log entry logged.
	 * 
	 * @return the identifier of the data record
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Specifies the identifier of the data record this log entry logged.
	 * 
	 * @param identifier the identifier of the data record
	 */
	public void setIdentifier(Object identifier) {
		this.identifier = identifier.toString();
	}
	
	/**
	 * Returns the {@link Date} this log entry was created.
	 * 
	 * @return the {@link Date} this log entry was created
	 */
	public Date getCreatedDate() {
		return createdDate == null ? null : new Date(createdDate.getTime());
	}

}
