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
    public static final String RELATION_TYPE = "RelationType";
    public static final String CODETYPE = "CodeType";
    public static final String CODE = "Code";
    public static final String NAME = "Name";
    public static final String MATCHING_CODE_TYPE = "MatchingCodeType";
    public static final String MATCHING_CODE = "MatchingCode";
    public static final String MATCHING_NAME = "MatchingName";
    public static final String REMARK = "Remark";
    public static final String CREATE_OPERATOR = "CreateOperator";
    public static final String CREATETIME = "CreateTime";
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

    @DatabaseField(columnName = RELATION_TYPE, canBeNull = false,defaultValue = "")
    @SerializedName(RELATION_TYPE)
    @Expose
    private String relation_type;

    @DatabaseField(columnName = CODETYPE, canBeNull = true,defaultValue = "")
    @SerializedName(CODETYPE)
    @Expose
    private String code_type;

    @DatabaseField(columnName = CODE, canBeNull = false,defaultValue = "")
    @SerializedName(CODE)
    @Expose
    private String code;

    @DatabaseField(columnName = NAME, canBeNull = false,defaultValue = "")
    @SerializedName(NAME)
    @Expose
    private String name;

    @DatabaseField(columnName = MATCHING_CODE_TYPE, canBeNull = true,defaultValue = "")
    @SerializedName(MATCHING_CODE_TYPE)
    @Expose
    private String matching_code_type;

    @DatabaseField(columnName = MATCHING_CODE, canBeNull = false,defaultValue = "")
    @SerializedName(MATCHING_CODE)
    @Expose
    private String matching_code;

    @DatabaseField(columnName = MATCHING_NAME, canBeNull = false,defaultValue = "")
    @SerializedName(MATCHING_NAME)
    @Expose
    private String matching_name;

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

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getRelation_type() {
        return relation_type;
    }

    public void setRelation_type(String relation_type) {
        this.relation_type = relation_type;
    }

    public String getCode_type() {
        return code_type;
    }

    public void setCode_type(String code_type) {
        this.code_type = code_type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMatching_code_type() {
        return matching_code_type;
    }

    public void setMatching_code_type(String matching_code_type) {
        this.matching_code_type = matching_code_type;
    }

    public String getMatching_code() {
        return matching_code;
    }

    public void setMatching_code(String matching_code) {
        this.matching_code = matching_code;
    }

    public String getMatching_name() {
        return matching_name;
    }

    public void setMatching_name(String matching_name) {
        this.matching_name = matching_name;
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

    public String getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(String lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false,defaultValue = "")
    @SerializedName(LASTUPDATETIME)
    @Expose
    private String lastupdatetime;
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
