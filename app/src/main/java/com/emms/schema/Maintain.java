package com.emms.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.jaffer_datastore_android_sdk.schema.Identity;
import com.jaffer_datastore_android_sdk.schema.Model;

/**
 * Created by Administrator on 2016/7/12.
 */
@DatabaseTable(tableName = "Maintain")
public class Maintain extends Model<Operator, Long> implements Identity<Long> {
    public static final String FACTORY_ID = "Maintain_ID";
    public static final String FACTORY_CODE = "Factory";
    public static final String MACHINE_CODE="MachineCode";
    public static final String MACHINE_NAME="MachineName";
    public static final String STATUS="Status";
    public static final String MAINTAIN_START_TIME="MaintainStartTime";
    public static final String MAINTAIN_END_TIME="MaintainEndTime";
    public static final String DESCRIPTION="Description";
    public static final String GROUP_NAME="GroupName";

    @DatabaseField(id = true,
            columnName = FACTORY_ID, canBeNull = false)
    @SerializedName(FACTORY_ID)
    @Expose
    private Long id;

    @DatabaseField(columnName = FACTORY_CODE, canBeNull = false )
    @SerializedName(FACTORY_CODE)
    @Expose
    private String factory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    @Override
    public Long getIdentity() {
        return id;
    }

    @Override
    public String getIdentityAttribute() {
        return FACTORY_ID;
    }
}
