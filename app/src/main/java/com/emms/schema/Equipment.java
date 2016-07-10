package com.emms.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.*;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.table.DatabaseTable;
import com.jaffer_datastore_android_sdk.schema.BlobDatabaseField;
import com.jaffer_datastore_android_sdk.schema.Identity;
import com.jaffer_datastore_android_sdk.schema.Model;

import java.util.Date;

/**
 * Created by jaffer.deng on 2016/7/10.
 */
@DatabaseTable(tableName = "Equipment")
public class Equipment extends Model<Operator, Long> implements Identity<Long> {

    public static final String EQUIPMENT_ID = "Equipment_ID";
    public static final String ORACLE_ID = "OracleID";
    public static final String EQUIPMENT_NAME = "EquipmentName";
    public static final String EQUIPMENT_CLASS = "EquipmentClass";
    public static final String EQUIPMENT_TYPE = "EquipmentType";
    public static final String ASSETSCLASS = "AssetsClass";
    public static final String BAND = "Band";
    public static final String DESCR = "Descr";
    public static final String SERIALNUMBER = "SerialNumber";
    public static final String DEPARTMENT_ID = "Department_ID";
    public static final String TEAM_ID = "Team_ID";
    public static final String USEDEPARTMENT_ID = "UseDepartment_ID";
    public static final String USETEAM_ID = "UseTeam_ID";
    public static final String SKILLLEVEL = "SkillLevel";
    public static final String STATUS = "Status";
    public static final String REMARK = "Remark";
    public static final String EQUIPMENT_MODEL = "EquipmentModel";
    public static final String FUNCTION_CLASS = "FunctionClass";
    public static final String ASSETSSTATUS = "AssetsStatus";
    public static final String ASSETSID = "AssetsID";
    public static final String FROMFACTORY = "FromFactory";
    public static final String TIMESTAMP = "TimeStamp";
    public static final String CREATETIME = "CreateTime";
    public static final String EDITTIME = "EditTime";
    public static final String EQUIPMENT_KY_ID = "Equipment_KyID";
    public static final String POWER = "Power";
    public static final String MANTRY = "Mantry";
    public static final String MANUFACTUREDATE = "ManufactureDate";
    public static final String ORIVALUE = "Orivalue";
    public static final String MANER = "Maner";
    public static final String FIXDATE = "Fixdate";
    public static final String STARTDATE = "Startdate";
    public static final String TODE = "Tode";
    public static final String VIF = "Vif";
    public static final String USEBRANCH = "Usebranch";
    public static final String MB = "MB";
    public static final String USEPRI = "Usepri";
    public static final String FLNUMBER = "Flnumber";
    public static final String FIXLOCUS = "Fixlocus";
    public static final String RUNSTATE = "Runstate";
    public static final String TECSTATE = "Tecstate";
    public static final String EDITER = "Editer";
    public static final String MONEYTYPE = "Moneytype";
    public static final String TXFL = "Txfl";
    public static final String ABC = "Abc";
    public static final String ZGL = "Zgl";
    public static final String INSTALLCOMPANY = "Installcompany";
    public static final String COMPUTER = "Computer";

    public static final String PLC = "Plc";
    public static final String BPQ = "Bpq";
    public static final String ZMDGL = "Zmdgl";
    public static final String KY_UPDATE_LOG_DATE = "Ky_Update_Log_date";

    @DatabaseField(id = true,
            columnName = EQUIPMENT_ID, canBeNull = false)
    @SerializedName(EQUIPMENT_ID)
    @Expose
    private Long equipment_id;

    @DatabaseField(columnName = ORACLE_ID, canBeNull = false )
    @SerializedName(ORACLE_ID)
    @Expose
    private String oracleID;

    @DatabaseField(columnName = EQUIPMENT_NAME, canBeNull = false )
    @SerializedName(EQUIPMENT_NAME)
    @Expose
    private String equipmentName;

    @DatabaseField(columnName = EQUIPMENT_CLASS, canBeNull = false )
    @SerializedName(EQUIPMENT_CLASS)
    @Expose
    private String equipmentClass;

    @DatabaseField(columnName = EQUIPMENT_TYPE, canBeNull = false )
    @SerializedName(EQUIPMENT_TYPE)
    @Expose
    private String equipmentType;

    @DatabaseField(columnName = ASSETSCLASS, canBeNull = false )
    @SerializedName(ASSETSCLASS)
    @Expose
    private String assetsClass;

    @DatabaseField(columnName = BAND, canBeNull = false )
    @SerializedName(BAND)
    @Expose
    private String band;

    @DatabaseField(columnName = DESCR, canBeNull = false )
    @SerializedName(DESCR)
    @Expose
    private String descr;

    @DatabaseField(columnName = SERIALNUMBER, canBeNull = false )
    @SerializedName(SERIALNUMBER)
    @Expose
    private String serialNumber;

    @DatabaseField(columnName = DEPARTMENT_ID, canBeNull = false )
    @SerializedName(DEPARTMENT_ID)
    @Expose
    private Long Department_id;

    @DatabaseField(columnName = TEAM_ID, canBeNull = false )
    @SerializedName(TEAM_ID)
    @Expose
    private String teamId;

    @DatabaseField(columnName = USEDEPARTMENT_ID, canBeNull = false )
    @SerializedName(USEDEPARTMENT_ID)
    @Expose
    private Long usedepartment;

    @DatabaseField(columnName = USETEAM_ID, canBeNull = false )
    @SerializedName(USETEAM_ID)
    @Expose
    private String useteamid;

    @DatabaseField(columnName = SKILLLEVEL, canBeNull = false )
    @SerializedName(SKILLLEVEL)
    @Expose
    private String skilllevel;

    @DatabaseField(columnName = STATUS, canBeNull = false )
    @SerializedName(STATUS)
    @Expose
    private Long status;

    @DatabaseField(columnName = REMARK, canBeNull = false )
    @SerializedName(REMARK)
    @Expose
    private String remark;

    @DatabaseField(columnName = EQUIPMENT_MODEL, canBeNull = false )
    @SerializedName(EQUIPMENT_MODEL)
    @Expose
    private String equipmentModel;

    @DatabaseField(columnName = FUNCTION_CLASS, canBeNull = false )
    @SerializedName(FUNCTION_CLASS)
    @Expose
    private String functionClass;

    @DatabaseField(columnName = ASSETSSTATUS, canBeNull = false )
    @SerializedName(ASSETSSTATUS)
    @Expose
    private String assetsStatus;

    @DatabaseField(columnName = ASSETSID, canBeNull = false )
    @SerializedName(ASSETSID)
    @Expose
    private String assetsID;

    @DatabaseField(columnName = FROMFACTORY, canBeNull = false )
    @SerializedName(FROMFACTORY)
    @Expose
    private String fromFactory;

    @DatabaseField(columnName = TIMESTAMP, canBeNull = false , dataType = DataType.BYTE_ARRAY)
    @BlobDatabaseField(baseURI = "/resources/" + TIMESTAMP)
    @SerializedName(TIMESTAMP)
    @Expose
    private String timetamp;

    @DatabaseField(columnName = CREATETIME, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(CREATETIME)
    @Expose
    private Date createTime;

    @DatabaseField(columnName = EDITTIME, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(EDITTIME)
    @Expose
    private Date editTime;

    @DatabaseField(columnName = EQUIPMENT_KY_ID, canBeNull = false )
    @SerializedName(EQUIPMENT_KY_ID)
    @Expose
    private String equipment_KyID;

    @DatabaseField(columnName = POWER, canBeNull = false )
    @SerializedName(POWER)
    @Expose
    private String power;

    @DatabaseField(columnName = MANTRY, canBeNull = false )
    @SerializedName(MANTRY)
    @Expose
    private String mantry;

    @DatabaseField(columnName = MANUFACTUREDATE, canBeNull = false )
    @SerializedName(MANUFACTUREDATE)
    @Expose
    private String manufactureDate;

    @DatabaseField(columnName = ORIVALUE, canBeNull = false )
    @SerializedName(ORIVALUE)
    @Expose
    private String orivalue;

    @DatabaseField(columnName = MANER, canBeNull = false )
    @SerializedName(MANER)
    @Expose
    private String maner;

    @DatabaseField(columnName = FIXDATE, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(FIXDATE)
    @Expose
    private Date fixdate;

    @DatabaseField(columnName = STARTDATE, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
    @SerializedName(STARTDATE)
    @Expose
    private Date startdate;

    @DatabaseField(columnName = TODE, canBeNull = false )
    @SerializedName(TODE)
    @Expose
    private String tode;

    @DatabaseField(columnName = VIF, canBeNull = false )
    @SerializedName(VIF)
    @Expose
    private String vif;

    @DatabaseField(columnName = USEBRANCH, canBeNull = false )
    @SerializedName(USEBRANCH)
    @Expose
    private String usebranch;

    @DatabaseField(columnName = MB, canBeNull = false )
    @SerializedName(MB)
    @Expose
    private String mb;

    @DatabaseField(columnName = USEPRI, canBeNull = false )
    @SerializedName(USEPRI)
    @Expose
    private String usepri;

    @DatabaseField(columnName = FLNUMBER, canBeNull = false )
    @SerializedName(FLNUMBER)
    @Expose
    private String flnumber;

    @DatabaseField(columnName = FIXLOCUS, canBeNull = false )
    @SerializedName(FIXLOCUS)
    @Expose
    private String fixlocus;

    @DatabaseField(columnName = RUNSTATE, canBeNull = false )
    @SerializedName(RUNSTATE)
    @Expose
    private String runstate;

    @DatabaseField(columnName = TECSTATE, canBeNull = false )
    @SerializedName(TECSTATE)
    @Expose
    private String tecstate;

    @DatabaseField(columnName = EDITER, canBeNull = false )
    @SerializedName(EDITER)
    @Expose
    private String editer;

    @DatabaseField(columnName = MONEYTYPE, canBeNull = false )
    @SerializedName(MONEYTYPE)
    @Expose
    private String moneytype;

    @DatabaseField(columnName = TXFL, canBeNull = false )
    @SerializedName(TXFL)
    @Expose
    private String txfl;

    @DatabaseField(columnName = ABC, canBeNull = false )
    @SerializedName(ABC)
    @Expose
    private String abc;

    @DatabaseField(columnName = ZGL, canBeNull = false )
    @SerializedName(ZGL)
    @Expose
    private String zgl;

    @DatabaseField(columnName = INSTALLCOMPANY, canBeNull = false )
    @SerializedName(INSTALLCOMPANY)
    @Expose
    private String installcompany;

    @DatabaseField(columnName = COMPUTER, canBeNull = false )
    @SerializedName(COMPUTER)
    @Expose
    private String computer;

    @DatabaseField(columnName = PLC, canBeNull = false )
    @SerializedName(PLC)
    @Expose
    private String plc;

    @DatabaseField(columnName = BPQ, canBeNull = false )
    @SerializedName(BPQ)
    @Expose
    private String bpq;

    @DatabaseField(columnName = ZMDGL, canBeNull = false )
    @SerializedName(ZMDGL)
    @Expose
    private String zmdgl;

    @DatabaseField(columnName = KY_UPDATE_LOG_DATE, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss",defaultValue = "1901-01-01 00:00:00.000")
    @SerializedName(KY_UPDATE_LOG_DATE)
    @Expose
    private Date ky_update_log_date;

    @Override
    public int hashCode() {
        return equipment_id.hashCode();
    }

    @Override
    public Long getIdentity() {
        return equipment_id;
    }

    @Override
    public String getIdentityAttribute() {
        return EQUIPMENT_ID;
    }
}
