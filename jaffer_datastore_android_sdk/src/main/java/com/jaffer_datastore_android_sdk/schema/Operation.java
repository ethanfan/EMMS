/**
 * Operation.java
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

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A data model that represents an operation performed on the datastore.
 */
@DatabaseTable(tableName = "operations")
public class Operation extends Model<Operation, Long> implements Identity<Long> {
	
	// Operation status
	public static final int OPERATION_BEGAN					= 1;
	public static final int OPERATION_IN_PROGRESS		= 2;
	public static final int OPERATION_ENDED					= 4;
	public static final int OPERATION_FAILED				= 8;
	
	// For QueryBuilder to be able to find the fields
	public static final String ID_FIELD_NAME 				= "operation_id";
	public static final String TARGET_FIELD_NAME		= "target";
	public static final String ACTION_FIELD_NAME		= "action";
	public static final String STATUS_FIELD_NAME		= "status";
	public static final String NOTE_FIELD_NAME			= "note";
	
	@DatabaseField(
			columnName = ID_FIELD_NAME, 
			canBeNull = false, 
			generatedId = true, 
			indexName = "operation_idx")
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private long operationId;
	
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
	private String action;
	
	@DatabaseField(
			columnName = STATUS_FIELD_NAME, 
			canBeNull = false)
	@SerializedName(STATUS_FIELD_NAME)
	@Expose
	private long status;
	
	@DatabaseField(
			columnName = NOTE_FIELD_NAME, 
			canBeNull = true)
	@SerializedName(NOTE_FIELD_NAME)
	@Expose
	private String note;
	
	@DatabaseField(
			columnName = CREATED_DATE_FIELD_NAME, 
			canBeNull = false,
  		dataType = DataType.DATE_STRING,
  		format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATED_DATE_FIELD_NAME)
	@Expose
	private Date createdDate;
	
	@DatabaseField(
			columnName = LAST_MODIFIED_DATE_FIELD_NAME, 
			canBeNull = false,
  		dataType = DataType.DATE_STRING,
  		format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(LAST_MODIFIED_DATE_FIELD_NAME)
	@Expose
	private Date lastModifiedDate;
	
	@Override
	public int hashCode() {
		return (int) operationId;
	}
	
	@Override
	public Long getIdentity() {
		return Long.valueOf(getOperationId());
	}

	@Override
	public String getIdentityAttribute() {
		return ID_FIELD_NAME;
	}
	
	/**
	 * Constructor.
	 */
	public Operation() {}
	
	/**
	 * Constructor.
	 * 
	 * @param target
	 *            the target of this operation
	 * @param action
	 *            the action performed in this operation
	 * @param status
	 *            the status of the operation
	 * @param note
	 *            a note on the operation
	 */
	public Operation(
			String target,
			String action,
			long status,
			String note) {
		this(-1, target, action, status, note, new Date(), new Date());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param operationId
	 *            the identity of this operation
	 * @param target
	 *            the target of this operation
	 * @param action
	 *            the action performed in this operation
	 * @param status
	 *            the status of the operation
	 * @param note
	 *            a note on the operation
	 * @param createdDate
	 *            the {@link Date} this operation was initiated
	 * @param lastModifiedDate
	 *            the {@link Date} this operation was last modified
	 */
	public Operation(
			long operationId,
			String target,
			String action,
			long status,
			String note,
			Date createdDate,
			Date lastModifiedDate) {
		
		this.operationId = operationId;
		this.target = target;
		this.action = action;
		this.status = status;
		this.note = note;
		this.createdDate = createdDate;
		this.lastModifiedDate = lastModifiedDate;
	}
	
	/**
	 * Returns the identity of this operation.
	 * 
	 * @return the identity of this operation
	 */
	public long getOperationId() {
		return operationId;
	}
	
	/**
	 * Specifies the identity of this operation.
	 * 
	 * @param operationId the identity of this operation
	 */
	public void setOperationId(long operationId) {
		this.operationId = operationId;
	}
	
	/**
	 * Returns the target this operation operated on.
	 * 
	 * @param target the target of this operation
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	
	/**
	 * Returns the target this operation operated on.
	 * 
	 * @return the target this operation operated on
	 */
	public String getTarget() {
		return target;
	}
	
	/**
	 * Specifies the action performed.
	 * 
	 * @param action the action performed
	 */
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * Returns the action this operation performed.
	 * 
	 * @return the action performed
	 */
	public String getAction() {
		return action;
	}
	
	/**
	 * Returns the status of this operation.
	 * 
	 * @return the status of this operation
	 * @see #OPERATION_BEGAN
	 * @see #OPERATION_ENDED
	 * @see #OPERATION_FAILED
	 * @see #OPERATION_IN_PROGRESS
	 */
	public long getStatus() {
		return status;
	}
	
	/**
	 * Specifies the status of this operation.
	 * 
	 * @param status
	 *            the status of this operation
	 * @see #OPERATION_BEGAN
	 * @see #OPERATION_ENDED
	 * @see #OPERATION_FAILED
	 * @see #OPERATION_IN_PROGRESS
	 */
	public void setStatus(long status) {
		this.status = status;
	}
	
	/**
	 * Returns the note attached to this operation.
	 * 
	 * @return the note attached
	 */
	public String getNote() {
		return note;
	}
	
	/**
	 * Attaches a note to this operation.
	 * 
	 * @param note the note to attach
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	/**
	 * Returns the {@link Date} this operation was last modified. An
	 * {@link Operation} will be modified if it succeeded or failed.
	 * 
	 * @return the {@link Date} this operation was last modified
	 */
	public Date getLastModifiedDate() {
		return lastModifiedDate == null ? null : new Date(lastModifiedDate.getTime());
	}
	
	/**
	 * Returns the {@link Date} that this operation was initiated.
	 * 
	 * @return the {@link Date} this operation was initiated
	 */
	public Date getCreatedDate() {
		return createdDate == null ? null : new Date(createdDate.getTime());
	}

}
