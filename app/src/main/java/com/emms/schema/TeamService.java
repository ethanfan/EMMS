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
@DatabaseTable(tableName = "TeamService")
public class TeamService extends Model<Operator, Long> implements Identity<Long> {

    public static final String TEAMSERVICE_ID = "TeamService_ID";
    public static final String TEAM_ID = "Team_ID";
    public static final String SERVERTEAM_ID = "ServerTeam_ID";

    @DatabaseField(id = true,
            columnName = TEAMSERVICE_ID, canBeNull = false)
    @SerializedName(TEAMSERVICE_ID)
    @Expose
    private Long teamService_id;

    @DatabaseField(columnName = TEAM_ID, canBeNull = false)
    @SerializedName(TEAM_ID)
    @Expose
    private Long team_id;

    @DatabaseField(columnName = SERVERTEAM_ID, canBeNull = false)
    @SerializedName(SERVERTEAM_ID)
    @Expose
    private Long serverTeam_ID;

    public Long getDataType_id() {
        return teamService_id;
    }

    public void setDataType_id(Long dataType_id) {
        this.teamService_id = dataType_id;
    }

    public Long getTeam_id() {
        return team_id;
    }

    public void setTeam_id(Long team_id) {
        this.team_id = team_id;
    }

    public Long getServerTeam_ID() {
        return serverTeam_ID;
    }

    public void setServerTeam_ID(Long serverTeam_ID) {
        this.serverTeam_ID = serverTeam_ID;
    }

    @Override
    public int hashCode() {
        return teamService_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return teamService_id;
    }

    @Override
    public String getIdentityAttribute() {
        return TEAMSERVICE_ID;
    }
}
