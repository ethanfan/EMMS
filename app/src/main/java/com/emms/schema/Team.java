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
public class Team extends Model<Operator, Long> implements Identity<Long> {

    public static final String TEAM_ID = "Team_ID";
    public static final String PTEAM_ID = "PTeam_ID";
    public static final String TEAMNAME = "TeamName";
    public static final String TEAMTYPE_ID = "TeamType_ID";
    public static final String DEPARTMENT_ID = "Department_ID";
    public static final String TEAM_DESCR = "TeamDescr";
    public static final String TEAM_LEADER = "TeamLeader";
    public static final String SKILLLEVEL = "SkillLevel";
    public static final String KY_ORGANISE_SPAREORDEPOTNAME = "Ky_organise_SpareOrDepotName";
    public static final String FROMFACTORY = "FromFactory";
    public static final String KY_BH = "Ky_Bh";
    public static final String KY_ORGANISE_ID = "Ky_organise_id";
    public static final String ISINCREMENT = "IsIncrement";

    @DatabaseField(id = true,
            columnName = TEAM_ID, canBeNull = false)
    @SerializedName(TEAM_ID)
    @Expose
    private Long team_id;

    @DatabaseField(columnName = PTEAM_ID, canBeNull = false)
    @SerializedName(PTEAM_ID)
    @Expose
    private Long pTeam_id;

    @DatabaseField(columnName = TEAMNAME, canBeNull = false )
    @SerializedName(TEAMNAME)
    @Expose
    private String teamName;

    @DatabaseField(columnName = TEAMTYPE_ID, canBeNull = false)
    @SerializedName(TEAMTYPE_ID)
    @Expose
    private Long teamtype_id;

    @DatabaseField(columnName = DEPARTMENT_ID, canBeNull = false)
    @SerializedName(DEPARTMENT_ID)
    @Expose
    private Long department_id;

    @DatabaseField(columnName = TEAM_DESCR, canBeNull = false )
    @SerializedName(TEAM_DESCR)
    @Expose
    private String teamDescr;

    @DatabaseField(columnName = TEAM_LEADER, canBeNull = false )
    @SerializedName(TEAM_LEADER)
    @Expose
    private String teamLeader;

    @DatabaseField(columnName = SKILLLEVEL, canBeNull = false )
    @SerializedName(SKILLLEVEL)
    @Expose
    private String skillLevel;

    @DatabaseField(columnName = KY_ORGANISE_SPAREORDEPOTNAME, canBeNull = false )
    @SerializedName(KY_ORGANISE_SPAREORDEPOTNAME)
    @Expose
    private String ky_organise_SpareOrDepotName;

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

    @DatabaseField(columnName = ISINCREMENT, canBeNull = false ,defaultValue = "0")
    @SerializedName(ISINCREMENT)
    @Expose
    private String isIncrement;

    public Long getTeam_id() {
        return team_id;
    }

    public void setTeam_id(Long team_id) {
        this.team_id = team_id;
    }

    public Long getpTeam_id() {
        return pTeam_id;
    }

    public void setpTeam_id(Long pTeam_id) {
        this.pTeam_id = pTeam_id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Long getTeamtype_id() {
        return teamtype_id;
    }

    public void setTeamtype_id(Long teamtype_id) {
        this.teamtype_id = teamtype_id;
    }

    public Long getDepartment_id() {
        return department_id;
    }

    public void setDepartment_id(Long department_id) {
        this.department_id = department_id;
    }

    public String getTeamDescr() {
        return teamDescr;
    }

    public void setTeamDescr(String teamDescr) {
        this.teamDescr = teamDescr;
    }

    public String getTeamLeader() {
        return teamLeader;
    }

    public void setTeamLeader(String teamLeader) {
        this.teamLeader = teamLeader;
    }

    public String getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        this.skillLevel = skillLevel;
    }

    public String getKy_organise_SpareOrDepotName() {
        return ky_organise_SpareOrDepotName;
    }

    public void setKy_organise_SpareOrDepotName(String ky_organise_SpareOrDepotName) {
        this.ky_organise_SpareOrDepotName = ky_organise_SpareOrDepotName;
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

    public String getIsIncrement() {
        return isIncrement;
    }

    public void setIsIncrement(String isIncrement) {
        this.isIncrement = isIncrement;
    }

    @Override
    public int hashCode() {
        return team_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return team_id;
    }

    @Override
    public String getIdentityAttribute() {
        return TEAM_ID;
    }
}
