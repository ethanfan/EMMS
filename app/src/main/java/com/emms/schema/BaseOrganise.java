package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;



@DatabaseTable(tableName = "BaseOrganise")
public class BaseOrganise extends Model<BaseOrganise, Long> implements Identity<Long> {

    public static final String ORGANISE_ID = "Organise_ID";
    public static final String KY_ORGANISE_ID = "KY_Organise_ID";
    public static final String ORGANISECODE = "OrganiseCode";
    public static final String ORGANISENAME = "OrganiseName";
    public static final String STATUS = "Status";
    public static final String ISSPAREORDEPOT = "IsSpareOrDepot";
    public static final String ORGANISETYPE = "OrganiseType";
    public static final String FROMFACTORY = "FromFactory";
    public static final String LASTUPDATETIME="LastUpdateTime";

    @DatabaseField(id = true,
            columnName = ORGANISE_ID, canBeNull = false)
    @SerializedName(ORGANISE_ID)
    @Expose
    private Long organiseID;

    @DatabaseField(columnName = KY_ORGANISE_ID, canBeNull = true)
    @SerializedName(KY_ORGANISE_ID)
    @Expose
    private String kyOrganiseID;

    @DatabaseField(columnName = ORGANISECODE, canBeNull = true)
    @SerializedName(ORGANISECODE)
    @Expose
    private String organiseCode;

    @DatabaseField(columnName = ORGANISENAME, canBeNull = true)
    @SerializedName(ORGANISENAME)
    @Expose
    private String organiseName;

    @DatabaseField(columnName = ISSPAREORDEPOT, canBeNull = true)
    @SerializedName(ISSPAREORDEPOT)
    @Expose
    private String isSpareOrDepot;

    @DatabaseField(columnName = ORGANISETYPE, canBeNull = true)
    @SerializedName(ORGANISETYPE)
    @Expose
    private String organiseType;

    @DatabaseField(columnName = FROMFACTORY, canBeNull = true)
    @SerializedName(FROMFACTORY)
    @Expose
    private String fromFactory;


    @DatabaseField(columnName = STATUS, canBeNull = true)
    @SerializedName(STATUS)
    @Expose
    private String status;

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

    public Long getOrganiseID() {
        return organiseID;
    }

    public void setOrganiseID(Long organiseID) {
        this.organiseID = organiseID;
    }

    public String getKyOrganiseID() {
        return kyOrganiseID;
    }

    public void setKyOrganiseID(String kyOrganiseID) {
        this.kyOrganiseID = kyOrganiseID;
    }

    public String getOrganiseCode() {
        return organiseCode;
    }

    public void setOrganiseCode(String organiseCode) {
        this.organiseCode = organiseCode;
    }

    public String getOrganiseName() {
        return organiseName;
    }

    public void setOrganiseName(String organiseName) {
        this.organiseName = organiseName;
    }

    public String getIsSpareOrDepot() {
        return isSpareOrDepot;
    }

    public void setIsSpareOrDepot(String isSpareOrDepot) {
        this.isSpareOrDepot = isSpareOrDepot;
    }

    public String getOrganiseType() {
        return organiseType;
    }

    public void setOrganiseType(String organiseType) {
        this.organiseType = organiseType;
    }

    public String getFromFactory() {
        return fromFactory;
    }

    public void setFromFactory(String fromFactory) {
        this.fromFactory = fromFactory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return organiseID.hashCode();
    }

    @Override
    public Long getIdentity() {
        return organiseID;
    }

    @Override
    public String getIdentityAttribute() {
        return ORGANISE_ID;
    }
}
