package com.emms.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;



@DatabaseTable(tableName = "application_download_state")
public class ApplicationDownloadState extends Model<ApplicationDownloadState, Long> implements Identity<Long> {
	
	public static final String ID_FIELD_NAME = "id";
	public static final String STATE_FIELD_NAME = "state";
	
	@DatabaseField(id = true, 
			columnName = ID_FIELD_NAME, canBeNull = false)
	@SerializedName(ID_FIELD_NAME)
	@Expose
	private long id;
	
	@DatabaseField(columnName = STATE_FIELD_NAME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(STATE_FIELD_NAME)
	@Expose
	private int downloadState;
	
	public enum DonwloadState {
		Downloading,
		Downloaded
	}

	@Override
	public Long getIdentity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdentityAttribute() {
		// TODO Auto-generated method stub
		return null;
	}

}
