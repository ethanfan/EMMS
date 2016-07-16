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
@DatabaseTable(tableName = "Team")
public class Factory extends Model<Operator, Long> implements Identity<Long> {

    public static final String FACTORY_ID = "Factory_ID";
    public static final String FACTORY_CODE = "FactoryCode";
    public static final String FACTORY_SHORT_NAME = "FactoryShortName";
    public static final String FACTORY_NAME = "FactoryName";
    public static final String FACTORY_DESCR = "FactoryDescr";
    public static final String GROUPNAME = "GroupName";
    public static final String LEADER = "Leader";
    public static final String ZONE = "Zone";
    public static final String TIMEZONE = "TimeZone";
    public static final String STATUS = "Status";
    public static final String FROMFACTORY = "FromFactory";
    public static final String KY_BH = "Ky_Bh";

    @DatabaseField(id = true,
            columnName = FACTORY_ID, canBeNull = false)
    @SerializedName(FACTORY_ID)
    @Expose
    private Long factory_id;

    @DatabaseField(columnName = FACTORY_CODE, canBeNull = false )
    @SerializedName(FACTORY_CODE)
    @Expose
    private String factory_code;

    @DatabaseField(columnName = FACTORY_SHORT_NAME, canBeNull = false )
    @SerializedName(FACTORY_SHORT_NAME)
    @Expose
    private String factory_short_name;

    @DatabaseField(columnName = FACTORY_NAME, canBeNull = false )
    @SerializedName(FACTORY_NAME)
    @Expose
    private String factory_name;

    @DatabaseField(columnName = FACTORY_DESCR, canBeNull = false )
    @SerializedName(FACTORY_DESCR)
    @Expose
    private String factory_descr;


    @DatabaseField(columnName = GROUPNAME, canBeNull = false )
    @SerializedName(GROUPNAME)
    @Expose
    private String groupName;

    @DatabaseField(columnName = LEADER, canBeNull = false )
    @SerializedName(LEADER)
    @Expose
    private String leader;

    @DatabaseField(columnName = ZONE, canBeNull = false )
    @SerializedName(ZONE)
    @Expose
    private String zone;

    @DatabaseField(columnName = TIMEZONE, canBeNull = false ,defaultValue = "0")
    @SerializedName(TIMEZONE)
    @Expose
    private Long timeZone;

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

    public Long getFactory_id() {
        return factory_id;
    }

    public void setFactory_id(Long factory_id) {
        this.factory_id = factory_id;
    }

    public String getFactory_code() {
        return factory_code;
    }

    public void setFactory_code(String factory_code) {
        this.factory_code = factory_code;
    }

    public String getFactory_short_name() {
        return factory_short_name;
    }

    public void setFactory_short_name(String factory_short_name) {
        this.factory_short_name = factory_short_name;
    }

    public String getFactory_name() {
        return factory_name;
    }

    public void setFactory_name(String factory_name) {
        this.factory_name = factory_name;
    }

    public String getFactory_descr() {
        return factory_descr;
    }

    public void setFactory_descr(String factory_descr) {
        this.factory_descr = factory_descr;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Long getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Long timeZone) {
        this.timeZone = timeZone;
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

    @Override
    public int hashCode() {
        return factory_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return factory_id;
    }

    @Override
    public String getIdentityAttribute() {
        return FACTORY_ID;
    }
}
