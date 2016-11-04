package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;



@DatabaseTable(tableName = "Language_Translation")
public class Language_Translation extends Model<Language_Translation, Long> implements Identity<Long> {

    public static final String TRANSLATION_ID = "Translation_ID";
    public static final String TRANSLATION_CODE = "Translation_Code";
    public static final String LANGUAGE_CODE = "Language_Code";
    public static final String TRANSLATION_DISPLAY = "Translation_Display";
    public static final String LASTUPDATETIME="LastUpdateTime";


    @DatabaseField(id = true,
            columnName = TRANSLATION_ID, canBeNull = false)
    @SerializedName(TRANSLATION_ID)
    @Expose
    private Long translationID;



    @DatabaseField(columnName = TRANSLATION_CODE, canBeNull = false,defaultValue = "")
    @SerializedName(TRANSLATION_CODE)
    @Expose
    private String translationCode;

    @DatabaseField(columnName = LANGUAGE_CODE, canBeNull = false,defaultValue = "")
    @SerializedName(LANGUAGE_CODE)
    @Expose
    private String languageCode;

    @DatabaseField(columnName = TRANSLATION_DISPLAY, canBeNull = false,defaultValue = "")
    @SerializedName(TRANSLATION_DISPLAY)
    @Expose
    private String translatioDisplay;



    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false,defaultValue = "")
    @SerializedName(LASTUPDATETIME)
    @Expose
    private String LastUpdateTime;


    public Long getTranslationID() {
        return translationID;
    }

    public void setTranslationID(Long translationID) {
        this.translationID = translationID;
    }

    public String getTranslationCode() {
        return translationCode;
    }

    public void setTranslationCode(String translationCode) {
        this.translationCode = translationCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getTranslatioDisplay() {
        return translatioDisplay;
    }

    public void setTranslatioDisplay(String translatioDisplay) {
        this.translatioDisplay = translatioDisplay;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    @Override
    public int hashCode() {
        return translationID.hashCode();
    }

    @Override
    public Long getIdentity() {
        return translationID;
    }

    @Override
    public String getIdentityAttribute() {
        return TRANSLATION_ID;
    }
}
