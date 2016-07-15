package com.emms.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;

/**
 * Created by jaffer.deng on 2016/7/10.
 */

@DatabaseTable(tableName = "DataDictionary")
public class DataDictionary extends Model<Operator, Long> implements Identity<Long> {

    public static final String DATA_ID = "Data_ID";
    public static final String FACTORY_ID = "Factory_ID";
    public static final String PDATA_ID = "PData_ID";
    public static final String DATA_TYPE = "DataType";
    public static final String DATA_CODE = "DataCode";
    public static final String DATA_NAME = "DataName";
    public static final String DATA_DESCR = "DataDescr";
    public static final String DATA_VALUE1 = "DataValue1";
    public static final String DATA_VALUE2 = "DataValue2";
    public static final String DATA_VALUE3 = "DataValue3";
    public static final String STATUS = "Status";
    public static final String REMARK = "Remark";

    @DatabaseField(id = true,
            columnName = DATA_ID, canBeNull = false)
    @SerializedName(DATA_ID)
    @Expose
    private Long data_id;

    @DatabaseField(columnName = FACTORY_ID, canBeNull = false)
    @SerializedName(FACTORY_ID)
    @Expose
    private String factory_id;

    @DatabaseField(columnName = PDATA_ID, canBeNull = false)
    @SerializedName(PDATA_ID)
    @Expose
    private String pdata_id;

    @DatabaseField(columnName = DATA_TYPE, canBeNull = false)
    @SerializedName(DATA_TYPE)
    @Expose
    private String data_type;

    @DatabaseField(columnName = DATA_CODE, canBeNull = false)
    @SerializedName(DATA_CODE)
    @Expose
    private String data_code;

    @DatabaseField(columnName = DATA_NAME, canBeNull = false)
    @SerializedName(DATA_NAME)
    @Expose
    private String data_name;

    @DatabaseField(columnName = DATA_DESCR, canBeNull = false)
    @SerializedName(DATA_DESCR)
    @Expose
    private String data_descr;

    @DatabaseField(columnName = DATA_VALUE1, canBeNull = false)
    @SerializedName(DATA_VALUE1)
    @Expose
    private String data_value1;

    @DatabaseField(columnName = DATA_VALUE2, canBeNull = false)
    @SerializedName(DATA_VALUE2)
    @Expose
    private String data_value2;

    @DatabaseField(columnName = DATA_VALUE3, canBeNull = false)
    @SerializedName(DATA_VALUE3)
    @Expose
    private String data_value3;

    @DatabaseField(columnName = STATUS, canBeNull = false ,defaultValue = "0")
    @SerializedName(STATUS)
    @Expose
    private String status;

    @DatabaseField(columnName = REMARK, canBeNull = false)
    @SerializedName(REMARK)
    @Expose
    private String remark;

    public Long getData_id() {
        return data_id;
    }

    public void setData_id(Long data_id) {
        this.data_id = data_id;
    }

    public String getFactory_id() {
        return factory_id;
    }

    public void setFactory_id(String factory_id) {
        this.factory_id = factory_id;
    }

    public String getPdata_id() {
        return pdata_id;
    }

    public void setPdata_id(String pdata_id) {
        this.pdata_id = pdata_id;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getData_code() {
        return data_code;
    }

    public void setData_code(String data_code) {
        this.data_code = data_code;
    }

    public String getData_name() {
        return data_name;
    }

    public void setData_name(String data_name) {
        this.data_name = data_name;
    }

    public String getData_descr() {
        return data_descr;
    }

    public void setData_descr(String data_descr) {
        this.data_descr = data_descr;
    }

    public String getData_value1() {
        return data_value1;
    }

    public void setData_value1(String data_value1) {
        this.data_value1 = data_value1;
    }

    public String getData_value2() {
        return data_value2;
    }

    public void setData_value2(String data_value2) {
        this.data_value2 = data_value2;
    }

    public String getData_value3() {
        return data_value3;
    }

    public void setData_value3(String data_value3) {
        this.data_value3 = data_value3;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int hashCode() {
        return data_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return data_id;
    }

    @Override
    public String getIdentityAttribute() {
        return DATA_ID;
    }
}
