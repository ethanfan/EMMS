package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;



@DatabaseTable(tableName = "Languages")
public class Languages extends Model<Languages, Long> implements Identity<Long> {

    public static final String LANGUAGE_ID = "Language_ID";
    public static final String LANGUAGE_CODE = "Language_Code";
    public static final String LANGUAGE_NAME = "Language_Name";
    public static final String LASTUPDATETIME="LastUpdateTime";

    @DatabaseField(id = true,
            columnName = LANGUAGE_ID, canBeNull = false)
    @SerializedName(LANGUAGE_ID)
    @Expose
    private Long languageID;


    @DatabaseField(columnName = LANGUAGE_CODE, canBeNull = false)
    @SerializedName(LANGUAGE_CODE)
    @Expose
    private String languageCode;

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Long getLanguageID() {
        return languageID;
    }

    public void setLanguageID(Long languageID) {
        this.languageID = languageID;
    }

    @DatabaseField(columnName = LANGUAGE_NAME, canBeNull = true)
    @SerializedName(LANGUAGE_NAME)
    @Expose
    private String languageName;


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
        return languageID.hashCode();
    }

    @Override
    public Long getIdentity() {
        return languageID;
    }

    @Override
    public String getIdentityAttribute() {
        return LANGUAGE_ID;
    }
}
