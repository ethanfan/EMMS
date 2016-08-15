package com.emms.schema;

import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by jaffer.deng on 2016/7/10.
 */
@DatabaseTable(tableName = "TaskOrganiseRelation")
public class TaskOrganiseRelation extends Model<Operator, Long> implements Identity<Long> {

    public static final String TEAM_SERVICE_ID = "TeamService_ID";
    public static final String TEAM_ID = "Team_ID";
    public static final String SERVICE_TEAM_ID = "ServerTeam_ID";
    public static final String LASTUPDATETIME="LastUpdateTime";
    @DatabaseField(id = true,
            columnName = TEAM_SERVICE_ID, canBeNull = false)
    @SerializedName(TEAM_SERVICE_ID)
    @Expose
    private Long TeamService_ID;

    @DatabaseField(columnName = TEAM_ID, canBeNull = false)
    @SerializedName(TEAM_ID)
    @Expose
    private String Team_ID;

    @DatabaseField(columnName = SERVICE_TEAM_ID, canBeNull = false)
    @SerializedName(SERVICE_TEAM_ID)
    @Expose
    private String ServerTeam_ID;

    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false)
    @SerializedName(LASTUPDATETIME)
    @Expose
    private String LastUpdateTime;

    public Long getTeamService_ID() {
        return TeamService_ID;
    }

    public void setTeamService_ID(Long teamService_ID) {
        TeamService_ID = teamService_ID;
    }

    public String getTeam_ID() {
        return Team_ID;
    }

    public void setTeam_ID(String team_ID) {
        Team_ID = team_ID;
    }

    public String getServerTeam_ID() {
        return ServerTeam_ID;
    }

    public void setServerTeam_ID(String serverTeam_ID) {
        ServerTeam_ID = serverTeam_ID;
    }

    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }

    @Override
    public int hashCode() {
        return TeamService_ID.hashCode();
    }

    @Override
    public Long getIdentity() {
        return TeamService_ID;
    }

    @Override
    public String getIdentityAttribute() {
        return TEAM_SERVICE_ID;
    }
}
