package com.emms.schema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.*;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.table.DatabaseTable;
import com.datastore_android_sdk.schema.BlobDatabaseField;
import com.datastore_android_sdk.schema.Identity;
import com.datastore_android_sdk.schema.Model;

import java.util.Date;

/**
 * Created by jaffer.deng on 2016/7/10.
 *
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
//    public static final String DEPARTMENT_ID = "Department_ID";
    public static final String ORGANISE_ID_BELONG = "Organise_ID_Belong";
    public static final String ORGANISE_ID_USE = "Organise_ID_Use";
//    public static final String TEAM_ID = "Team_ID";
//    public static final String USEDEPARTMENT_ID = "UseDepartment_ID";
//    public static final String USETEAM_ID = "UseTeam_ID";
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
//    public static final String POWER = "Power";
//    public static final String MANTRY = "Mantry";
//    public static final String MANUFACTUREDATE = "ManufactureDate";
//    public static final String ORIVALUE = "Orivalue";
//    public static final String MANER = "Maner";
//    public static final String FIXDATE = "Fixdate";
//    public static final String STARTDATE = "Startdate";
//    public static final String TODE = "Tode";
//    public static final String VIF = "Vif";
//    public static final String USEBRANCH = "Usebranch";
//    public static final String MBB = "MB";
//    public static final String USEPRI = "Usepri";
//    public static final String FLNUMBER = "Flnumber";
//    public static final String FIXLOCUS = "Fixlocus";
//    public static final String RUNSTATE = "Runstate";
//    public static final String TECSTATE = "Tecstate";
//    public static final String EDITER = "Editer";
//    public static final String MONEYTYPE = "Moneytype";
//    public static final String TXFL = "Txfl";
//    public static final String ABC = "Abc";
//    public static final String ZGL = "Zgl";
//    public static final String INSTALLCOMPANY = "Installcompany";
//    public static final String COMPUTER = "Computer";
//    public static final String PLC = "Plc";
//    public static final String BPQ = "Bpq";
//    public static final String ZMDGL = "Zmdgl";
    public static final String KY_UPDATE_LOG_DATE = "Ky_Update_Log_date";
    public static final String IC_CARD_ID = "ICCardID";
    public static final String LASTUPDATETIME="LastUpdateTime";
    @DatabaseField(id = true,
            columnName = EQUIPMENT_ID, canBeNull = false)
    @SerializedName(EQUIPMENT_ID)
    @Expose
    private Long equipment_id;

    @DatabaseField(columnName = ORACLE_ID, canBeNull = true,defaultValue = "")
    @SerializedName(ORACLE_ID)
    @Expose
    private String oracleID;

//    @DatabaseField(columnName = TEAM_ID, canBeNull = true,defaultValue = "")
//    @SerializedName(TEAM_ID)
//    @Expose
//    private String Team_ID;
//    @DatabaseField(columnName = USEDEPARTMENT_ID, canBeNull = true,defaultValue = "")
//    @SerializedName(USEDEPARTMENT_ID)
//    @Expose
//    private String UseDepartment_ID;
//    @DatabaseField(columnName = USETEAM_ID, canBeNull = true,defaultValue = "")
//    @SerializedName(USETEAM_ID)
//    @Expose
//    private String UseTeam_ID;
    @DatabaseField(columnName = SKILLLEVEL, canBeNull = true,defaultValue = "")
    @SerializedName(SKILLLEVEL)
    @Expose
    private String SkillLevel;
    @DatabaseField(columnName = STATUS, canBeNull = true,defaultValue = "")
    @SerializedName(STATUS)
    @Expose
    private String Status;
    @DatabaseField(columnName = REMARK, canBeNull = true,defaultValue = "")
    @SerializedName(REMARK)
    @Expose
    private String Remark;
    @DatabaseField(columnName = EQUIPMENT_MODEL, canBeNull = true,defaultValue = "")
    @SerializedName(EQUIPMENT_MODEL)
    @Expose
    private String EquipmentModel;
    @DatabaseField(columnName = FUNCTION_CLASS, canBeNull = true,defaultValue = "")
    @SerializedName(FUNCTION_CLASS)
    @Expose
    private String FunctionClass;
    @DatabaseField(columnName = FROMFACTORY, canBeNull = true,defaultValue = "")
    @SerializedName(FROMFACTORY)
    @Expose
    private String FromFactory;

    @DatabaseField(columnName = TIMESTAMP, canBeNull = true,defaultValue = "")
    @SerializedName(TIMESTAMP)
    @Expose
    private String TimeStamp;

    @DatabaseField(columnName = CREATETIME, canBeNull = true,defaultValue = "")
    @SerializedName(CREATETIME)
    @Expose
    private String CreateTime;

    @DatabaseField(columnName = EDITTIME, canBeNull = true,defaultValue = "")
    @SerializedName(EDITTIME)
    @Expose
    private String EditTime;

    @DatabaseField(columnName = EQUIPMENT_KY_ID, canBeNull = true,defaultValue = "")
    @SerializedName(EQUIPMENT_KY_ID)
    @Expose
    private String Equipment_KyID;

//    @DatabaseField(columnName = POWER, canBeNull = true,defaultValue = "")
//    @SerializedName(POWER)
//    @Expose
//    private String Power;

    @DatabaseField(columnName = EQUIPMENT_NAME, canBeNull = true ,defaultValue = "")
    @SerializedName(EQUIPMENT_NAME)
    @Expose
    private String equipmentName;

    @DatabaseField(columnName = EQUIPMENT_CLASS, canBeNull = true ,defaultValue = "")
    @SerializedName(EQUIPMENT_CLASS)
    @Expose
    private String equipmentClass;

    @DatabaseField(columnName = EQUIPMENT_TYPE, canBeNull = true ,defaultValue = "")
    @SerializedName(EQUIPMENT_TYPE)
    @Expose
    private String equipmentType;

    @DatabaseField(columnName = ASSETSCLASS, canBeNull = true ,defaultValue = "")
    @SerializedName(ASSETSCLASS)
    @Expose
    private String assetsClass;

    @DatabaseField(columnName = BAND, canBeNull = true ,defaultValue = "")
    @SerializedName(BAND)
    @Expose
    private String band;

    @DatabaseField(columnName = DESCR, canBeNull = true ,defaultValue = "")
    @SerializedName(DESCR)
    @Expose
    private String descr;

    @DatabaseField(columnName = ASSETSID, canBeNull = true ,defaultValue = "")
    @SerializedName(ASSETSID)
    @Expose
    private String assetsID;

    @DatabaseField(columnName = SERIALNUMBER, canBeNull = true ,defaultValue = "")

    @SerializedName(SERIALNUMBER)
    @Expose
    private String serialNumber;

//    @DatabaseField(columnName = DEPARTMENT_ID, canBeNull = true ,defaultValue = "")
//    @SerializedName(DEPARTMENT_ID)
//    @Expose
//    private String Department_id;

    @DatabaseField(columnName = ORGANISE_ID_BELONG, canBeNull = true ,defaultValue = "")
    @SerializedName(ORGANISE_ID_BELONG)
    @Expose
    private String organiseIDbelong;

    @DatabaseField(columnName = ORGANISE_ID_USE, canBeNull = true ,defaultValue = "")
    @SerializedName(ORGANISE_ID_USE)
    @Expose
    private String organiseIDuse;

//    @DatabaseField(columnName = MANTRY, canBeNull = true ,defaultValue = "")
//    @SerializedName(MANTRY)
//    @Expose
//    private String Mantry;
//
//    @DatabaseField(columnName = MANUFACTUREDATE, canBeNull = true ,defaultValue = "")
//    @SerializedName(MANUFACTUREDATE)
//    @Expose
//    private String ManufactureDate;
//
//    @DatabaseField(columnName = ORIVALUE, canBeNull = true ,defaultValue = "")
//    @SerializedName(ORIVALUE)
//    @Expose
//    private String Orivalue;
//
//    @DatabaseField(columnName = MANER, canBeNull = true ,defaultValue = "")
//    @SerializedName(MANER)
//    @Expose
//    private String Maner;
//
//    @DatabaseField(columnName = FIXDATE, canBeNull = true ,defaultValue = "")
//    @SerializedName(FIXDATE)
//    @Expose
//    private String Fixdate;
//
//    @DatabaseField(columnName = STARTDATE, canBeNull = true ,defaultValue = "")
//    @SerializedName(STARTDATE)
//    @Expose
//    private String Startdate;
//
//    @DatabaseField(columnName = TODE, canBeNull = true ,defaultValue = "")
//    @SerializedName(TODE)
//    @Expose
//    private String Tode;
//
//    @DatabaseField(columnName = VIF, canBeNull = true ,defaultValue = "")
//    @SerializedName(VIF)
//    @Expose
//    private String Vif;
//
//    @DatabaseField(columnName = USEBRANCH, canBeNull = true ,defaultValue = "")
//    @SerializedName(USEBRANCH)
//    @Expose
//    private String Usebranch;
//
//    @DatabaseField(columnName = MBB, canBeNull = true ,defaultValue = "")
//    @SerializedName(MBB)
//    @Expose
//    private String MB;
//
//    @DatabaseField(columnName = USEPRI, canBeNull = true ,defaultValue = "")
//    @SerializedName(USEPRI)
//    @Expose
//    private String Usepri;
//
//    @DatabaseField(columnName = FLNUMBER, canBeNull = true ,defaultValue = "")
//    @SerializedName(FLNUMBER)
//    @Expose
//    private String Flnumber;
//
//    @DatabaseField(columnName = FIXLOCUS, canBeNull = true ,defaultValue = "")
//    @SerializedName(FIXLOCUS)
//    @Expose
//    private String Fixlocus;
//
//    @DatabaseField(columnName = RUNSTATE, canBeNull = true ,defaultValue = "")
//    @SerializedName(RUNSTATE)
//    @Expose
//    private String Runstate;
//
//    @DatabaseField(columnName = TECSTATE, canBeNull = true ,defaultValue = "")
//    @SerializedName(TECSTATE)
//    @Expose
//    private String Tecstate;
//
//    @DatabaseField(columnName = EDITER, canBeNull = true ,defaultValue = "")
//    @SerializedName(EDITER)
//    @Expose
//    private String Editer;
//
//    @DatabaseField(columnName = MONEYTYPE, canBeNull = true ,defaultValue = "")
//    @SerializedName(MONEYTYPE)
//    @Expose
//    private String Moneytype;
//
//
//    @DatabaseField(columnName = TXFL, canBeNull = true ,defaultValue = "")
//    @SerializedName(TXFL)
//    @Expose
//    private String Txfl;
//
//    @DatabaseField(columnName = ABC, canBeNull = true ,defaultValue = "")
//    @SerializedName(ABC)
//    @Expose
//    private String Abc;
//
//    @DatabaseField(columnName = ZGL, canBeNull = true ,defaultValue = "")
//    @SerializedName(ZGL)
//    @Expose
//    private String Zgl;
//
//    @DatabaseField(columnName = INSTALLCOMPANY, canBeNull = true ,defaultValue = "")
//    @SerializedName(INSTALLCOMPANY)
//    @Expose
//    private String Installcompany;
//
//    @DatabaseField(columnName = COMPUTER, canBeNull = true ,defaultValue = "")
//    @SerializedName(COMPUTER)
//    @Expose
//    private String Computer;
//
//    @DatabaseField(columnName = PLC, canBeNull = true ,defaultValue = "")
//    @SerializedName(PLC)
//    @Expose
//    private String Plc;
//
//    @DatabaseField(columnName = BPQ, canBeNull = true ,defaultValue = "")
//    @SerializedName(BPQ)
//    @Expose
//    private String Bpq;
//
//    @DatabaseField(columnName = ZMDGL, canBeNull = true ,defaultValue = "")
//    @SerializedName(ZMDGL)
//    @Expose
//    private String Zmdgl;

    @DatabaseField(columnName = KY_UPDATE_LOG_DATE, canBeNull = true ,defaultValue = "")
    @SerializedName(KY_UPDATE_LOG_DATE)
    @Expose
    private String Ky_Update_Log_date;

    @DatabaseField(columnName = IC_CARD_ID, canBeNull = true ,defaultValue = "")
    @SerializedName(IC_CARD_ID)
    @Expose
    private String ICCardID;

    public static String getEquipmentId() {
        return EQUIPMENT_ID;
    }

//    public String getDepartment_id() {
//        return Department_id;
//    }
//
//    public void setDepartment_id(String department_id) {
//        Department_id = department_id;
//    }

    public String getOrganiseIDuse() {
        return organiseIDuse;
    }

    public void setOrganiseIDuse(String organiseIDuse) {
        this.organiseIDuse = organiseIDuse;
    }

//    public String getMantry() {
//        return Mantry;
//    }
//
//    public void setMantry(String mantry) {
//        Mantry = mantry;
//    }
//
//    public String getManufactureDate() {
//        return ManufactureDate;
//    }
//
//    public void setManufactureDate(String manufactureDate) {
//        ManufactureDate = manufactureDate;
//    }
//
//    public String getOrivalue() {
//        return Orivalue;
//    }
//
//    public void setOrivalue(String orivalue) {
//        Orivalue = orivalue;
//    }
//
//    public String getManer() {
//        return Maner;
//    }
//
//    public void setManer(String maner) {
//        Maner = maner;
//    }
//
//    public String getFixdate() {
//        return Fixdate;
//    }
//
//    public void setFixdate(String fixdate) {
//        Fixdate = fixdate;
//    }
//
//    public String getStartdate() {
//        return Startdate;
//    }
//
//    public void setStartdate(String startdate) {
//        Startdate = startdate;
//    }
//
//    public String getTode() {
//        return Tode;
//    }
//
//    public void setTode(String tode) {
//        Tode = tode;
//    }
//
//    public String getVif() {
//        return Vif;
//    }
//
//    public void setVif(String vif) {
//        Vif = vif;
//    }
//
//    public String getUsebranch() {
//        return Usebranch;
//    }
//
//    public void setUsebranch(String usebranch) {
//        Usebranch = usebranch;
//    }
//
//    public String getMB() {
//        return MB;
//    }
//
//    public void setMB(String MB) {
//        this.MB = MB;
//    }
//
//    public String getUsepri() {
//        return Usepri;
//    }
//
//    public void setUsepri(String usepri) {
//        Usepri = usepri;
//    }
//
//    public String getFlnumber() {
//        return Flnumber;
//    }
//
//    public void setFlnumber(String flnumber) {
//        Flnumber = flnumber;
//    }
//
//    public String getFixlocus() {
//        return Fixlocus;
//    }
//
//    public void setFixlocus(String fixlocus) {
//        Fixlocus = fixlocus;
//    }
//
//    public String getRunstate() {
//        return Runstate;
//    }
//
//    public void setRunstate(String runstate) {
//        Runstate = runstate;
//    }
//
//    public String getMoneytype() {
//        return Moneytype;
//    }
//
//    public void setMoneytype(String moneytype) {
//        Moneytype = moneytype;
//    }
//
//    public String getTecstate() {
//        return Tecstate;
//    }
//
//    public void setTecstate(String tecstate) {
//        Tecstate = tecstate;
//    }
//
//    public String getEditer() {
//        return Editer;
//    }
//
//    public void setEditer(String editer) {
//        Editer = editer;
//    }
//
//    public String getTxfl() {
//        return Txfl;
//    }
//
//    public void setTxfl(String txfl) {
//        Txfl = txfl;
//    }
//
//    public String getAbc() {
//        return Abc;
//    }
//
//    public void setAbc(String abc) {
//        Abc = abc;
//    }
//
//    public String getZgl() {
//        return Zgl;
//    }
//
//    public void setZgl(String zgl) {
//        Zgl = zgl;
//    }
//
//    public String getInstallcompany() {
//        return Installcompany;
//    }
//
//    public void setInstallcompany(String installcompany) {
//        Installcompany = installcompany;
//    }
//
//    public String getComputer() {
//        return Computer;
//    }
//
//    public void setComputer(String computer) {
//        Computer = computer;
//    }
//
//    public String getPlc() {
//        return Plc;
//    }
//
//    public void setPlc(String plc) {
//        Plc = plc;
//    }
//
//    public String getBpq() {
//        return Bpq;
//    }
//
//    public void setBpq(String bpq) {
//        Bpq = bpq;
//    }
//
//    public String getZmdgl() {
//        return Zmdgl;
//    }
//
//    public void setZmdgl(String zmdgl) {
//        Zmdgl = zmdgl;
//    }

    public String getKy_Update_Log_date() {
        return Ky_Update_Log_date;
    }

    public void setKy_Update_Log_date(String ky_Update_Log_date) {
        Ky_Update_Log_date = ky_Update_Log_date;
    }

    public String getICCardID() {
        return ICCardID;
    }

    public void setICCardID(String ICCardID) {
        this.ICCardID = ICCardID;
    }

    @DatabaseField(columnName = LASTUPDATETIME, canBeNull = false,defaultValue = "")

    @SerializedName(LASTUPDATETIME)
    @Expose
    private String LastUpdateTime;


//    public String getUseDepartment_ID() {
//        return UseDepartment_ID;
//    }
//
//    public void setUseDepartment_ID(String useDepartment_ID) {
//        UseDepartment_ID = useDepartment_ID;
//    }
//
//    public String getTeam_ID() {
//        return Team_ID;
//    }
//
//    public void setTeam_ID(String team_ID) {
//        Team_ID = team_ID;
//    }
//
//    public String getUseTeam_ID() {
//        return UseTeam_ID;
//    }
//
//    public void setUseTeam_ID(String useTeam_ID) {
//        UseTeam_ID = useTeam_ID;
//    }

    public String getSkillLevel() {
        return SkillLevel;
    }

    public void setSkillLevel(String skillLevel) {
        SkillLevel = skillLevel;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getEquipmentModel() {
        return EquipmentModel;
    }

    public void setEquipmentModel(String equipmentModel) {
        EquipmentModel = equipmentModel;
    }

    public String getFunctionClass() {
        return FunctionClass;
    }

    public void setFunctionClass(String functionClass) {
        FunctionClass = functionClass;
    }

    public String getFromFactory() {
        return FromFactory;
    }

    public void setFromFactory(String fromFactory) {
        FromFactory = fromFactory;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getEditTime() {
        return EditTime;
    }

    public void setEditTime(String editTime) {
        EditTime = editTime;
    }

    public String getEquipment_KyID() {
        return Equipment_KyID;
    }

    public void setEquipment_KyID(String equipment_KyID) {
        Equipment_KyID = equipment_KyID;
    }

//    public String getPower() {
//        return Power;
//    }
//
//    public void setPower(String power) {
//        Power = power;
//    }


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getAssetsClass() {
        return assetsClass;
    }

    public void setAssetsClass(String assetsClass) {
        this.assetsClass = assetsClass;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getEquipmentClass() {
        return equipmentClass;
    }

    public void setEquipmentClass(String equipmentClass) {
        this.equipmentClass = equipmentClass;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getOracleID() {
        return oracleID;
    }

    public void setOracleID(String oracleID) {
        this.oracleID = oracleID;
    }

    public Long getEquipment_id() {
        return equipment_id;
    }

    public void setEquipment_id(Long equipment_id) {
        this.equipment_id = equipment_id;
    }


    public String getLastUpdateTime() {
        return LastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        LastUpdateTime = lastUpdateTime;
    }



    public String getOrganiseIDbelong() {
        return organiseIDbelong;
    }

    public void setOrganiseIDbelong(String organiseIDbelong) {
        this.organiseIDbelong = organiseIDbelong;
    }




//    @DatabaseField(columnName = TEAM_ID, canBeNull = false )
//    @SerializedName(TEAM_ID)
//    @Expose
//    private String teamId;
//
//    @DatabaseField(columnName = USEDEPARTMENT_ID, canBeNull = false )
//    @SerializedName(USEDEPARTMENT_ID)
//    @Expose
//    private Long usedepartment;
//
//    @DatabaseField(columnName = USETEAM_ID, canBeNull = false )
//    @SerializedName(USETEAM_ID)
//    @Expose
//    private String useteamid;
//
//    @DatabaseField(columnName = SKILLLEVEL, canBeNull = false )
//    @SerializedName(SKILLLEVEL)
//    @Expose
//    private String skilllevel;
//
//    @DatabaseField(columnName = STATUS, canBeNull = false )
//    @SerializedName(STATUS)
//    @Expose
//    private Long status;
//
//    @DatabaseField(columnName = REMARK, canBeNull = false )
//    @SerializedName(REMARK)
//    @Expose
//    private String remark;
//
//    @DatabaseField(columnName = EQUIPMENT_MODEL, canBeNull = false )
//    @SerializedName(EQUIPMENT_MODEL)
//    @Expose
//    private String equipmentModel;
//
//    @DatabaseField(columnName = FUNCTION_CLASS, canBeNull = false )
//    @SerializedName(FUNCTION_CLASS)
//    @Expose
//    private String functionClass;

    public String getAssetsStatus() {
        return assetsStatus;
    }

    public void setAssetsStatus(String assetsStatus) {
        this.assetsStatus = assetsStatus;
    }

    @DatabaseField(columnName = ASSETSSTATUS, canBeNull = true )
    @SerializedName(ASSETSSTATUS)
    @Expose
    private String assetsStatus;

    public String getAssetsID() {
        return assetsID;
    }

    public void setAssetsID(String assetsID) {
        this.assetsID = assetsID;
    }


//    @DatabaseField(columnName = FROMFACTORY, canBeNull = false )
//    @SerializedName(FROMFACTORY)
//    @Expose
//    private String fromFactory;
//
//    @DatabaseField(columnName = TIMESTAMP, canBeNull = false , dataType = DataType.BYTE_ARRAY)
//    @BlobDatabaseField(baseURI = "/resources/" + TIMESTAMP)
//    @SerializedName(TIMESTAMP)
//    @Expose
//    private String timetamp;
//
//    @DatabaseField(columnName = CREATETIME, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
//    @SerializedName(CREATETIME)
//    @Expose
//    private Date createTime;
//
//    @DatabaseField(columnName = EDITTIME, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
//    @SerializedName(EDITTIME)
//    @Expose
//    private Date editTime;
//
//    @DatabaseField(columnName = EQUIPMENT_KY_ID, canBeNull = false )
//    @SerializedName(EQUIPMENT_KY_ID)
//    @Expose
//    private String equipment_KyID;
//
//    @DatabaseField(columnName = POWER, canBeNull = false )
//    @SerializedName(POWER)
//    @Expose
//    private String power;
//
//    @DatabaseField(columnName = MANTRY, canBeNull = false )
//    @SerializedName(MANTRY)
//    @Expose
//    private String mantry;
//
//    @DatabaseField(columnName = MANUFACTUREDATE, canBeNull = false )
//    @SerializedName(MANUFACTUREDATE)
//    @Expose
//    private String manufactureDate;
//
//    @DatabaseField(columnName = ORIVALUE, canBeNull = false )
//    @SerializedName(ORIVALUE)
//    @Expose
//    private String orivalue;
//
//    @DatabaseField(columnName = MANER, canBeNull = false )
//    @SerializedName(MANER)
//    @Expose
//    private String maner;
//
//    @DatabaseField(columnName = FIXDATE, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
//    @SerializedName(FIXDATE)
//    @Expose
//    private Date fixdate;
//
//    @DatabaseField(columnName = STARTDATE, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss")
//    @SerializedName(STARTDATE)
//    @Expose
//    private Date startdate;
//
//    @DatabaseField(columnName = TODE, canBeNull = false )
//    @SerializedName(TODE)
//    @Expose
//    private String tode;
//
//    @DatabaseField(columnName = VIF, canBeNull = false )
//    @SerializedName(VIF)
//    @Expose
//    private String vif;
//
//    @DatabaseField(columnName = USEBRANCH, canBeNull = false )
//    @SerializedName(USEBRANCH)
//    @Expose
//    private String usebranch;
//
//    @DatabaseField(columnName = MB, canBeNull = false )
//    @SerializedName(MB)
//    @Expose
//    private String mb;
//
//    @DatabaseField(columnName = USEPRI, canBeNull = false )
//    @SerializedName(USEPRI)
//    @Expose
//    private String usepri;
//
//    @DatabaseField(columnName = FLNUMBER, canBeNull = false )
//    @SerializedName(FLNUMBER)
//    @Expose
//    private String flnumber;
//
//    @DatabaseField(columnName = FIXLOCUS, canBeNull = false )
//    @SerializedName(FIXLOCUS)
//    @Expose
//    private String fixlocus;
//
//    @DatabaseField(columnName = RUNSTATE, canBeNull = false )
//    @SerializedName(RUNSTATE)
//    @Expose
//    private String runstate;
//
//    @DatabaseField(columnName = TECSTATE, canBeNull = false )
//    @SerializedName(TECSTATE)
//    @Expose
//    private String tecstate;
//
//    @DatabaseField(columnName = EDITER, canBeNull = false )
//    @SerializedName(EDITER)
//    @Expose
//    private String editer;
//
//    @DatabaseField(columnName = MONEYTYPE, canBeNull = false )
//    @SerializedName(MONEYTYPE)
//    @Expose
//    private String moneytype;
//
//    @DatabaseField(columnName = TXFL, canBeNull = false )
//    @SerializedName(TXFL)
//    @Expose
//    private String txfl;
//
//    @DatabaseField(columnName = ABC, canBeNull = false )
//    @SerializedName(ABC)
//    @Expose
//    private String abc;
//
//    @DatabaseField(columnName = ZGL, canBeNull = false )
//    @SerializedName(ZGL)
//    @Expose
//    private String zgl;
//
//    @DatabaseField(columnName = INSTALLCOMPANY, canBeNull = false )
//    @SerializedName(INSTALLCOMPANY)
//    @Expose
//    private String installcompany;
//
//    @DatabaseField(columnName = COMPUTER, canBeNull = false )
//    @SerializedName(COMPUTER)
//    @Expose
//    private String computer;
//
//    @DatabaseField(columnName = PLC, canBeNull = false )
//    @SerializedName(PLC)
//    @Expose
//    private String plc;
//
//    @DatabaseField(columnName = BPQ, canBeNull = false )
//    @SerializedName(BPQ)
//    @Expose
//    private String bpq;
//
//    @DatabaseField(columnName = ZMDGL, canBeNull = false )
//    @SerializedName(ZMDGL)
//    @Expose
//    private String zmdgl;
//
//    @DatabaseField(columnName = KY_UPDATE_LOG_DATE, canBeNull = false, dataType = com.j256.ormlite.field.DataType.DATE_STRING, format = "yyyy-MM-dd HH:mm:ss",defaultValue = "1901-01-01 00:00:00.000")
//    @SerializedName(KY_UPDATE_LOG_DATE)
//    @Expose
//    private Date ky_update_log_date;

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
