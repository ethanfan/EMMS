package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by jaffer.deng on 2016/7/10.
 *
 */

@DatabaseTable(tableName = "DataRelation")
public class DataRelation extends Model<Operator, Long> implements Identity<Long> {

    public static final String DATARELATION_ID = "DataRelation_ID";
    public static final String FACTORY = "Factory";
    public static final String RELATION_CODE= "RelationCode";
    public static final String RELATION_NAME = "RelationName";
    public static final String DATATYPE1 = "DataType1";
    public static final String DATACODE1 = "DataCode1";
    public static final String DATATYPE2 = "DataType2";
    public static final String DATACODE2 = "DataCode2";
    public static final String LASTUPDATEOPERATOR = "lastUpdateOperator";
    public static final String REMARK = "Remark";
    public static final String CREATE_OPERATOR = "CreateOperator";
    public static final String CREATETIME = "CreateTime";
    public static final String SYNCDATATIME="SyncDataTime";
    public static final String LASTUPDATETIME = "LastUpdateTime";

    public Long getDataRelation_id() {
        return dataRelation_id;
    }

    public void setDataRelation_id(Long dataRelation_id) {
        this.dataRelation_id = dataRelation_id;
    }

    @DatabaseField(id = true,
            columnName = DATARELATION_ID, canBeNull = false)
    @SerializedName(DATARELATION_ID)
    @Expose
    private Long dataRelation_id;


    @DatabaseField(columnName = FACTORY, canBeNull = false,defaultValue = "")
    @SerializedName(FACTORY)
    @Expose
    private String factory;

    @DatabaseField(columnName = RELATION_CODE, canBeNull = false,defaultValue = "")
    @SerializedName(RELATION_CODE)
    @Expose
    private String RelationCode;

    @DatabaseField(columnName = RELATION_NAME, canBeNull = true,defaultValue = "")
    @SerializedName(RELATION_NAME)
    @Expose
    private String RelationName;

    @DatabaseField(columnName = DATATYPE1, canBeNull = false,defaultValue = "")
    @SerializedName(DATATYPE1)
    @Expose
    private String DataType1;

    @DatabaseField(columnName = DATACODE1, canBeNull = false,defaultValue = "")
    @SerializedName(DATACODE1)
    @Expose
    private String DataCode1;

    @DatabaseField(columnName = DATATYPE2, canBeNull = false,defaultValue = "")
    @SerializedName(DATATYPE2)
    @Expose
    private String DataType2;

    @DatabaseField(columnName = DATACODE2, canBeNull = false,defaultValue = "")
    @SerializedName(DATACODE2)
    @Expose
    private String DataCode2;

    @DatabaseField(columnName = LASTUPDATEOPERATOR, canBeNull = true,defaultValue = "")
    @SerializedName(LASTUPDATEOPERATOR)
    @Expose
    private String lastUpdateOperator;

    @DatabaseField(columnName = REMARK, canBeNull = true,defaultValue = "")
    @SerializedName(REMARK)
    @Expose
    private String remark;

    @DatabaseField(columnName = CREATE_OPERATOR, canBeNull = false,defaultValue = "")
    @SerializedName(CREATE_OPERATOR)
    @Expose
    private String create_operator;

    @DatabaseField(columnName = CREATETIME, canBeNull = false,defaultValue = "")
    @SerializedName(CREATETIME)
    @Expose
    private String createTime;

    @DatabaseField(columnName = SYNCDATATIME, canBeNull = true,defaultValue = "")
    @SerializedName(SYNCDATATIME)
    @Expose
    private String SyncDataTime;

    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false,defaultValue = "")
    @SerializedName(LASTUPDATETIME)
    @Expose
    private String lastupdatetime;

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getRelationCode() {
        return RelationCode;
    }

    public void setRelationCode(String relationCode) {
        RelationCode = relationCode;
    }

    public String getRelationName() {
        return RelationName;
    }

    public void setRelationName(String relationName) {
        RelationName = relationName;
    }

    public String getDataType1() {
        return DataType1;
    }

    public void setDataType1(String dataType1) {
        DataType1 = dataType1;
    }

    public String getDataCode1() {
        return DataCode1;
    }

    public void setDataCode1(String dataCode1) {
        DataCode1 = dataCode1;
    }

    public String getDataType2() {
        return DataType2;
    }

    public void setDataType2(String dataType2) {
        DataType2 = dataType2;
    }

    public String getDataCode2() {
        return DataCode2;
    }

    public void setDataCode2(String dataCode2) {
        DataCode2 = dataCode2;
    }

    public String getLastUpdateOperator() {
        return lastUpdateOperator;
    }

    public void setLastUpdateOperator(String lastUpdateOperator) {
        this.lastUpdateOperator = lastUpdateOperator;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreate_operator() {
        return create_operator;
    }

    public void setCreate_operator(String create_operator) {
        this.create_operator = create_operator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSyncDataTime() {
        return SyncDataTime;
    }

    public void setSyncDataTime(String syncDataTime) {
        SyncDataTime = syncDataTime;
    }

    public String getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(String lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }
    @Override
    public int hashCode() {
        return dataRelation_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return dataRelation_id;
    }

    @Override
    public String getIdentityAttribute() {
        return DATARELATION_ID;
    }
}
