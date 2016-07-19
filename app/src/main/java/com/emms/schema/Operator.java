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

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.datastore_android_sdk.schema.BlobDatabaseField;
import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;


/**
 * 
 * @author jaffer
 *
 */
@DatabaseTable(tableName = "Operator")
public class Operator extends Model<Operator, Long> implements Identity<Long> {

	// For QueryBuilder to be able to find the fields
	public static final String OPERATOR_ID = "Operator_ID";
	public static final String OPERATOR_KY_ID = "Operator_KyID";
    public static final String OPERATOR_NO = "OperatorNo";
    public static final String NAME = "Name";
    public static final String SEX = "Sex";
    public static final String IC_CARD_ID = "ICCardID";
    public static final String FACTORY_ID = "Factory_ID";
    public static final String DEPARTMENT_ID = "Department_ID";
    public static final String TEAM_ID = "Team_ID";
    public static final String TEAM_NAME = "TeamName";
	public static final String POST = "post";
	public static final String STATUS = "Status";
	public static final String BORNDATE = "BornDate";
	public static final String JOINDATE ="JoinDate";
	public static final String MAIL = "Mail";
	public static final String ACCOUNT = "Account";
	public static final String MOBILE = "Mobile";
	public static final String EDUCATION ="Education";
	public static final String CREDENTIAL = "Credential";
	public static final String FROMFACTORY = "FromFactory";
	public static final String TIMESTAMP = "TimeStamp";
	public static final String CREATETIME ="CreateTime";
	public static final String WX_USER_ID = "WX_User_Id";
	public static final String WX_USER_SYSUSERID = "WX_User_SysUserId";
	public static final String WX_USER_CODE ="WX_User_Code";
	public static final String WX_USER_WORKTYPE = "WX_User_WorkType";
	public static final String WX_USER_ISPRINCIPAL = "WX_User_IsPrincipal";
	public static final String WX_USER_POSTID = "WX_User_PostId";
	public static final String WX_USER_GROUPID ="WX_User_GroupId";
	public static final String WX_USER_SYSUSERLOGINID = "WX_User_SysUserLoginId";
    public static final String KY_UPDATE_LOG_DATE = "Ky_update_log_date";
	public static final String TRANCHES = "Tranches";


	@DatabaseField(id = true,
			columnName = OPERATOR_ID, canBeNull = false)
	@SerializedName(OPERATOR_ID)
	@Expose
	private Long id;

	@DatabaseField(columnName = OPERATOR_KY_ID, canBeNull = false)
	@SerializedName(OPERATOR_KY_ID)
	@Expose
	private String operator_KyID;

	@DatabaseField(columnName = OPERATOR_NO, canBeNull = false)
	@SerializedName(OPERATOR_NO)
	@Expose
	private String operator_no;


	@DatabaseField(columnName = NAME, canBeNull = false)
	@SerializedName(NAME)
	@Expose
	private String name;

	@DatabaseField(columnName = SEX, canBeNull = false)
	@SerializedName(SEX)
	@Expose
	private String sex;

	@DatabaseField(columnName = IC_CARD_ID, canBeNull = false)
	@SerializedName(IC_CARD_ID)
	@Expose
	private String ic_cardId;

	@DatabaseField(columnName = FACTORY_ID, canBeNull = false)
	@SerializedName(FACTORY_ID)
	@Expose
	private String factoryId;

	@DatabaseField(columnName = DEPARTMENT_ID, canBeNull = false)
	@SerializedName(DEPARTMENT_ID)
	@Expose
	private String departmentId;


	@DatabaseField(columnName = TEAM_ID, canBeNull = false)
	@SerializedName(TEAM_ID)
	@Expose
	private String teamId;

	@DatabaseField(columnName = TEAM_NAME, canBeNull = false)
	@SerializedName(TEAM_NAME)
	@Expose
	private String teamName;

	@DatabaseField(columnName = POST, canBeNull = false)
	@SerializedName(POST)
	@Expose
	private String post;

	@DatabaseField(columnName = STATUS, canBeNull = false)
	@SerializedName(STATUS)
	@Expose
	private String status;

	@DatabaseField(columnName = BORNDATE, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(BORNDATE)
	@Expose
	private Date bornDate;

	@DatabaseField(columnName = JOINDATE, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(JOINDATE)
	@Expose
	private Date joinDate;

	@DatabaseField(columnName = MAIL, canBeNull = false)
	@SerializedName(MAIL)
	@Expose
	private String mail;

	@DatabaseField(columnName = ACCOUNT, canBeNull = false)
	@SerializedName(ACCOUNT)
	@Expose
	private String account;

	@DatabaseField(columnName = MOBILE, canBeNull = false)
	@SerializedName(MOBILE)
	@Expose
	private String moblie;

	@DatabaseField(columnName = EDUCATION, canBeNull = false)
	@SerializedName(EDUCATION)
	@Expose
	private String education;


	@DatabaseField(columnName = CREDENTIAL, canBeNull = false)
	@SerializedName(CREDENTIAL)
	@Expose
	private String credential;

	@DatabaseField(columnName = FROMFACTORY, canBeNull = false)
	@SerializedName(FROMFACTORY)
	@Expose
	private String fromFactory;

	@DatabaseField(columnName = TIMESTAMP, canBeNull = false , dataType = DataType.BYTE_ARRAY)
	@BlobDatabaseField(baseURI = "/resources/" + TIMESTAMP)
	@SerializedName(TIMESTAMP)
	@Expose
	private String timetamp;

	@DatabaseField(columnName = CREATETIME, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(CREATETIME)
	@Expose
	private Date createdTime;

	@DatabaseField(columnName = WX_USER_ID, canBeNull = false)
	@SerializedName(WX_USER_ID)
	@Expose
	private String wx_user_id;

	@DatabaseField(columnName = WX_USER_SYSUSERID, canBeNull = false)
	@SerializedName(WX_USER_SYSUSERID)
	@Expose
	private String wx_user_sysuserid;

	@DatabaseField(columnName = WX_USER_CODE, canBeNull = false)
	@SerializedName(WX_USER_CODE)
	@Expose
	private String wx_user_code;

	@DatabaseField(columnName = WX_USER_WORKTYPE, canBeNull = false)
	@SerializedName(WX_USER_WORKTYPE)
	@Expose
	private String wx_user_worktype;

	@DatabaseField(columnName = WX_USER_ISPRINCIPAL, canBeNull = false)
	@SerializedName(WX_USER_ISPRINCIPAL)
	@Expose
	private String wx_user_isprincipal;

	@DatabaseField(columnName = WX_USER_POSTID, canBeNull = false)
	@SerializedName(WX_USER_POSTID)
	@Expose
	private String wx_user_postid;

	@DatabaseField(columnName = WX_USER_GROUPID, canBeNull = false)
	@SerializedName(WX_USER_GROUPID)
	@Expose
	private String wx_user_groupid;

	@DatabaseField(columnName = WX_USER_SYSUSERLOGINID, canBeNull = false)
	@SerializedName(WX_USER_SYSUSERLOGINID)
	@Expose
	private String wx_user_sysuserloginid;


	@DatabaseField(columnName = KY_UPDATE_LOG_DATE, canBeNull = false, dataType = DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
	@SerializedName(KY_UPDATE_LOG_DATE)
	@Expose
	private Date ky_update_log_date;

	@DatabaseField(columnName = TRANCHES, canBeNull = false)
	@SerializedName(TRANCHES)
	@Expose
	private String tranches;




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
		return OPERATOR_ID;
	}

	public Operator() {
		// all persisted classes must define a no-arg constructor with at least
		// package visibility
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOperator_KyID() {
		return operator_KyID;
	}

	public void setOperator_KyID(String operator_KyID) {
		this.operator_KyID = operator_KyID;
	}

	public String getOperator_no() {
		return operator_no;
	}

	public void setOperator_no(String operator_no) {
		this.operator_no = operator_no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getIc_cardId() {
		return ic_cardId;
	}

	public void setIc_cardId(String ic_cardId) {
		this.ic_cardId = ic_cardId;
	}

	public String getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(String factoryId) {
		this.factoryId = factoryId;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getBornDate() {
		return bornDate;
	}

	public void setBornDate(Date bornDate) {
		this.bornDate = bornDate;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getMoblie() {
		return moblie;
	}

	public void setMoblie(String moblie) {
		this.moblie = moblie;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getFromFactory() {
		return fromFactory;
	}

	public void setFromFactory(String fromFactory) {
		this.fromFactory = fromFactory;
	}

	public String getTimetamp() {
		return timetamp;
	}

	public void setTimetamp(String timetamp) {
		this.timetamp = timetamp;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getWx_user_id() {
		return wx_user_id;
	}

	public void setWx_user_id(String wx_user_id) {
		this.wx_user_id = wx_user_id;
	}

	public String getWx_user_sysuserid() {
		return wx_user_sysuserid;
	}

	public void setWx_user_sysuserid(String wx_user_sysuserid) {
		this.wx_user_sysuserid = wx_user_sysuserid;
	}

	public String getWx_user_code() {
		return wx_user_code;
	}

	public void setWx_user_code(String wx_user_code) {
		this.wx_user_code = wx_user_code;
	}

	public String getWx_user_worktype() {
		return wx_user_worktype;
	}

	public void setWx_user_worktype(String wx_user_worktype) {
		this.wx_user_worktype = wx_user_worktype;
	}

	public String getWx_user_isprincipal() {
		return wx_user_isprincipal;
	}

	public void setWx_user_isprincipal(String wx_user_isprincipal) {
		this.wx_user_isprincipal = wx_user_isprincipal;
	}

	public String getWx_user_postid() {
		return wx_user_postid;
	}

	public void setWx_user_postid(String wx_user_postid) {
		this.wx_user_postid = wx_user_postid;
	}

	public String getWx_user_groupid() {
		return wx_user_groupid;
	}

	public void setWx_user_groupid(String wx_user_groupid) {
		this.wx_user_groupid = wx_user_groupid;
	}

	public String getWx_user_sysuserloginid() {
		return wx_user_sysuserloginid;
	}

	public void setWx_user_sysuserloginid(String wx_user_sysuserloginid) {
		this.wx_user_sysuserloginid = wx_user_sysuserloginid;
	}

	public Date getKy_update_log_date() {
		return ky_update_log_date;
	}

	public void setKy_update_log_date(Date ky_update_log_date) {
		this.ky_update_log_date = ky_update_log_date;
	}

	public String getTranches() {
		return tranches;
	}

	public void setTranches(String tranches) {
		this.tranches = tranches;
	}
}
