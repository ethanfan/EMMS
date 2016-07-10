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
@DatabaseTable(tableName = "AlertRule")
public class AlertRule extends Model<Operator, Long> implements Identity<Long> {

    public static final String ALERTRULE_ID = "AlertRule_ID";
    public static final String TASK_CLASS = "TaskClass";
    public static final String EQUIPMENT_NUM = "EquipmentNum";
    public static final String ALERT1 = "Alert1";
    public static final String ALERT2 = "Alert2";

    @DatabaseField(id = true,
            columnName = ALERTRULE_ID, canBeNull = false)
    @SerializedName(ALERTRULE_ID)
    @Expose
    private Long alertRule_id;

    @DatabaseField(columnName = TASK_CLASS, canBeNull = false)
    @SerializedName(TASK_CLASS)
    @Expose
    private String task_class;

    @DatabaseField(columnName = EQUIPMENT_NUM, canBeNull = false)
    @SerializedName(EQUIPMENT_NUM)
    @Expose
    private Long equipmentNum;

    @DatabaseField(columnName = ALERT1, canBeNull = false)
    @SerializedName(ALERT1)
    @Expose
    private String alert1;

    @DatabaseField(columnName = ALERT2, canBeNull = false)
    @SerializedName(ALERT2)
    @Expose
    private String alert2;

    public Long getAlertRule_id() {
        return alertRule_id;
    }

    public void setAlertRule_id(Long alertRule_id) {
        this.alertRule_id = alertRule_id;
    }

    public String getTask_class() {
        return task_class;
    }

    public void setTask_class(String task_class) {
        this.task_class = task_class;
    }

    public Long getEquipmentNum() {
        return equipmentNum;
    }

    public void setEquipmentNum(Long equipmentNum) {
        this.equipmentNum = equipmentNum;
    }

    public String getAlert1() {
        return alert1;
    }

    public void setAlert1(String alert1) {
        this.alert1 = alert1;
    }

    public String getAlert2() {
        return alert2;
    }

    public void setAlert2(String alert2) {
        this.alert2 = alert2;
    }

    @Override
    public int hashCode() {
        return alertRule_id.intValue();
    }

    @Override
    public Long getIdentity() {
        return alertRule_id;
    }

    @Override
    public String getIdentityAttribute() {
        return ALERTRULE_ID;
    }
}
