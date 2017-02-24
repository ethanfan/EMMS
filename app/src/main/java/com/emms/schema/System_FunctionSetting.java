package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


@DatabaseTable(tableName = "System_FunctionSetting")
public class System_FunctionSetting extends Model<System_FunctionSetting, Long> implements Identity<Long> {

    public static final String FUNCTION_ID = "Function_ID";
    public static final String FUNCTION_CODE = "FunctionCode";
    public static final String FUNCTION_VALUE = "FunctionValue";
    public static final String FACTORY = "Factory";
    public static final String ORGANISE_ID = "Organise_ID";
    public static final String EDITOR = "Editor";
    public static final String EDITOR_TIME = "EditTime";
    public static final String LASTUPDATETIME="LastUpdateTime";

    @DatabaseField(id = true,
            columnName = FUNCTION_ID, canBeNull = false)
    @SerializedName(FUNCTION_ID)
    @Expose
    private Long Function_ID;


    @DatabaseField(columnName = FUNCTION_CODE, canBeNull = false)
    @SerializedName(FUNCTION_CODE)
    @Expose
    private String FunctionCode;

    @DatabaseField(columnName = FUNCTION_VALUE, canBeNull = true)
    @SerializedName(FUNCTION_VALUE)
    @Expose
    private String FunctionValue;

    public Long getFunction_ID() {
        return Function_ID;
    }

    public void setFunction_ID(Long function_ID) {
        Function_ID = function_ID;
    }

    public String getFunctionCode() {
        return FunctionCode;
    }

    public void setFunctionCode(String functionCode) {
        FunctionCode = functionCode;
    }

    public String getFunctionValue() {
        return FunctionValue;
    }

    public void setFunctionValue(String functionValue) {
        FunctionValue = functionValue;
    }

    public String getFactory() {
        return Factory;
    }

    public void setFactory(String factory) {
        Factory = factory;
    }

    public String getOrganise_ID() {
        return Organise_ID;
    }

    public void setOrganise_ID(String organise_ID) {
        Organise_ID = organise_ID;
    }

    public String getEditor() {
        return Editor;
    }

    public void setEditor(String editor) {
        Editor = editor;
    }

    public String getEditTime() {
        return EditTime;
    }

    public void setEditTime(String editTime) {
        EditTime = editTime;
    }

    @DatabaseField(columnName = FACTORY, canBeNull = true)
    @SerializedName(FACTORY)

    @Expose
    private String Factory;

    @DatabaseField(columnName = ORGANISE_ID, canBeNull = true)
    @SerializedName(ORGANISE_ID)
    @Expose
    private String Organise_ID;

    @DatabaseField(columnName = EDITOR, canBeNull = true)
    @SerializedName(EDITOR)
    @Expose
    private String Editor;

    @DatabaseField(columnName = EDITOR_TIME, canBeNull = true)
    @SerializedName(EDITOR_TIME)
    @Expose
    private String EditTime;


    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false,defaultValue = "")
    @SerializedName(LASTUPDATETIME)
    @Expose
    private String LastUpdateTime;



    @Override
    public int hashCode() {
        return Function_ID.hashCode();
    }

    @Override
    public Long getIdentity() {
        return Function_ID;
    }

    @Override
    public String getIdentityAttribute() {
        return FUNCTION_ID;
    }
}
