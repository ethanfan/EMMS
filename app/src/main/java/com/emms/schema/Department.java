package com.emms.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jaffer_datastore_android_sdk.schema.Identity;
import com.jaffer_datastore_android_sdk.schema.Model;

/**
 * Created by jaffer.deng on 2016/7/10.
 */
@DatabaseTable(tableName = "Department")
public class Department extends Model<Operator, Long> implements Identity<Long> {

    public static final String DEPARTMENT_ID = "Department_ID";
    public static final String FACTORY_ID = "Factory_ID";
    public static final String PDEPARTMENT_ID = "PDepartment_ID";
    public static final String DEPARTMENT_CODE = "DepartmentCode";
    public static final String PDEPARTMENT_NAME = "DepartmentName";
    public static final String PDEPARTMENT_CLASS = "DepartmentClass";
    public static final String PDEPARTMENT_DESC = "DepartmentDesc";
    public static final String STATUS = "Status";
    public static final String FROMFACTORY = "FromFactory";
    public static final String KY_BH = "Ky_Bh";
    public static final String KY_ORGANISE_ID = "Ky_organise_id";

    @DatabaseField(id = true,
            columnName = DEPARTMENT_ID, canBeNull = false)
    @SerializedName(DEPARTMENT_ID)
    @Expose
    private Long department_id;

    @DatabaseField(columnName = FACTORY_ID, canBeNull = false, defaultValue = "GLE")
    @SerializedName(FACTORY_ID)
    @Expose
    private String factory_id;

    @DatabaseField(columnName = PDEPARTMENT_ID, canBeNull = false, defaultValue = "0")
    @SerializedName(PDEPARTMENT_ID)
    @Expose
    private String pDepartment_id;

    @DatabaseField(columnName = DEPARTMENT_CODE, canBeNull = false)
    @SerializedName(DEPARTMENT_CODE)
    @Expose
    private String department_code;

    @DatabaseField(columnName = PDEPARTMENT_NAME, canBeNull = false)
    @SerializedName(PDEPARTMENT_NAME)
    @Expose
    private String department_name;

    @DatabaseField(columnName = PDEPARTMENT_CLASS, canBeNull = false)
    @SerializedName(PDEPARTMENT_CLASS)
    @Expose
    private String department_class;

    @DatabaseField(columnName = PDEPARTMENT_DESC, canBeNull = false)
    @SerializedName(PDEPARTMENT_DESC)
    @Expose
    private String department_desc;

    @DatabaseField(columnName = STATUS, canBeNull = false ,defaultValue = "0")
    @SerializedName(STATUS)
    @Expose
    private String status;

    @DatabaseField(columnName = FROMFACTORY, canBeNull = false )
    @SerializedName(FROMFACTORY)
    @Expose
    private String fromFactory;

    @DatabaseField(columnName = KY_BH, canBeNull = false )
    @SerializedName(KY_BH)
    @Expose
    private String ky_bh;

    @DatabaseField(columnName = KY_ORGANISE_ID, canBeNull = false )
    @SerializedName(KY_ORGANISE_ID)
    @Expose
    private String ky_organise_id;

    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }

    public String getFactory_id() {
        return factory_id;
    }

    public void setFactory_id(String factory_id) {
        this.factory_id = factory_id;
    }

    public String getpDepartment_id() {
        return pDepartment_id;
    }

    public void setpDepartment_id(String pDepartment_id) {
        this.pDepartment_id = pDepartment_id;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public String getDepartment_class() {
        return department_class;
    }

    public void setDepartment_class(String department_class) {
        this.department_class = department_class;
    }

    public String getDepartment_desc() {
        return department_desc;
    }

    public void setDepartment_desc(String department_desc) {
        this.department_desc = department_desc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFromFactory() {
        return fromFactory;
    }

    public void setFromFactory(String fromFactory) {
        this.fromFactory = fromFactory;
    }

    public String getKy_bh() {
        return ky_bh;
    }

    public void setKy_bh(String ky_bh) {
        this.ky_bh = ky_bh;
    }

    public String getKy_organise_id() {
        return ky_organise_id;
    }

    public void setKy_organise_id(String ky_organise_id) {
        this.ky_organise_id = ky_organise_id;
    }

    @Override
    public int hashCode() {
        return department_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return department_id;
    }

    @Override
    public String getIdentityAttribute() {
        return DEPARTMENT_ID;
    }
}
