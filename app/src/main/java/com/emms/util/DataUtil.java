package com.emms.util;


import android.content.Context;

import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.emms.activity.AppApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/17.
 *
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class DataUtil {
    private DataUtil(){
     //no instance
    }

    public static boolean isNullOrEmpty(String checkStr){
        return checkStr == null || checkStr.isEmpty();
    }
    /**
     *  check whether the DataElement is null
     * @param s the DataElement to be checked
     * @return "" when DataElement is null Or StringValue when it is not null
     */
    public static String isDataElementNull(DataElement s){
        if(s==null){
            return "";
        }
        if(!s.isNull()){
            return s.valueAsString();
        }
        return "";
    }
    public static String getDate(String date){
        if(date.contains("T")){
           return date.replace("T","  ");
        }
        return date;
    }

    /**
     * Check the charsequence whether is Integer
     * @param checkStr the charsequence to be checked
     * @return true when the charsequence is Integer
     */
    public static boolean isInt(String checkStr) {
        try {
            Integer.parseInt(checkStr);
            return true; // Did not throw, must be a number
        } catch (NumberFormatException err) {
            return false; // Threw, So is not a number
        }
    }

    /**
     * Check the charsequence whether is Float
     * @param checkStr the charsequence to be checked
     * @return true when the charsequence is Float
     */
    public static boolean isFloat(String checkStr){
        try {
            Float.parseFloat(checkStr);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * Check the charsequence whether is Num with pattern[0-9.]*
     * @param checkStr the charsequence to be checked
     * @return true when the charsequence is Num
     */
    public static boolean isNum(String checkStr){
        try {
            //Float.parseFloat(checkStr);
            Pattern pattern = Pattern.compile("[0-9.]*");
            Matcher isNum = pattern.matcher(checkStr);
            return isNum.matches();
        } catch (NumberFormatException err) {
            return false; // Threw, So is not a number
        }
    }
    public static String utc2Local(String utcTime) {
        SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            return utcTime;
        }
        SimpleDateFormat localFormater = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        //localFormater.setTimeZone(TimeZone.getDefault());
        localFormater.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return localFormater.format(gpsUTCDate.getTime());
    }
    public static String Local2utc(String Local) {
        SimpleDateFormat LocalFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //LocalFormater.setTimeZone(TimeZone.getDefault());
        LocalFormater.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date gpsLocalDate = null;
        try {
            gpsLocalDate = LocalFormater.parse(Local);
        } catch (ParseException e) {
            return Local;
        }
        SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcFormater.format(gpsLocalDate.getTime());
    }
    public static void getDataFromDataBase(Context context,String DataType, StoreCallback storeCallback){
        String sql;
        if( (LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH)
                || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH){
//            sql="select distinct d.[DataCode],(case LT.[Translation_Display]"
//                    +" when '' then d.[DataName]"
//                    +" when null then d.[DataName]"
//                    +" else LT.[Translation_Display] end) DataName,LT.[Translation_Code]"
//                    +" from DataDictionary d,Language_Translation LT"
//                    +" where d.[DataName]=LT.[Translation_Code]"
//                    +" and d.DataType='"+DataType+"'";
            sql= "select distinct DataCode,DataName Translation_Code,"
                    +" (case when Translation_Display is null then DataName"
                    +" when Translation_Display ='' then DataName"
                    +" else Translation_Display end) DataName"
                    +" FROM (select  d.[DataCode],(select"
                    +" LT.[Translation_Display]"
                    +" from Language_Translation  LT"
                    +" where d.[DataName]=LT.[Translation_Code]"
                    +" and LT.[Translation_Display] is not null"
                    +" AND LT.[Translation_Display] <>''"
                    +" AND LT.[Language_Code] ='en-US'"
                    +" order by LT.Translation_ID asc limit 1"
                    +" ) Translation_Display,d.[DataName]"
                    +" from DataDictionary d"
                    +" where d.DataType='"+DataType+"') a";
        }else {
        sql= "select * from DataDictionary where DataType='"+DataType+"'";
        }
        ((AppApplication)context.getApplicationContext()).getSqliteStore().performRawQuery(sql, "DataDictionary",storeCallback);
    }
    public static void getDataFromDataBase(Context context,String DataType,int Pdata_ID, StoreCallback storeCallback){
        String sql;
        if( (LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH)
                || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH){
//            sql="select d.[DataCode],ifnull(LT.[Translation_Display],d.[DataName]) DataName "
//                    + " from DataDictionary d,Language_Translation LT"
//                    + " where d.[DataName]=LT.[Translation_Code]"
//                    + " and d.DataType='"+DataType+"'"
//                    +" and d.PData_ID ='" +Pdata_ID+"'";

           sql= "select distinct DataCode,DataName Translation_Code,"
                +" (case when Translation_Display is null then DataName"
                +" when Translation_Display ='' then DataName"
                +" else Translation_Display end) DataName"
                +" FROM (select  d.[DataCode],(select"
                +" LT.[Translation_Display]"
                +" from Language_Translation  LT"
                +" where d.[DataName]=LT.[Translation_Code]"
                +" and LT.[Translation_Display] is not null"
                +" AND LT.[Translation_Display] <>''"
                +" AND LT.[Language_Code] ='en-US'"
                +" order by LT.Translation_ID asc limit 1"
                +" ) Translation_Display,d.[DataName]"
                +" from DataDictionary d"
                +" where d.DataType in ('"+DataType+"','TaskSubClass')"
                +" and d.PData_ID in ('" +Pdata_ID+"','2') Order By Sort asc) a";
        }else {
            sql=  "select * from DataDictionary where DataType in ('"+DataType+"','TaskSubClass') and PData_ID in ('" +Pdata_ID+"','2') Order By Sort asc";
        }
        ((AppApplication)context.getApplicationContext()).getSqliteStore().performRawQuery(sql, "DataDictionary",storeCallback);
    }
   public static void getDataFromDataBase(Context context,String DataType, String DataValue1,String DataValue2, StoreCallback storeCallback){
       String sql;
       if( (LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH )
               || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH){
//           sql="select distinct d.[DataCode],(case LT.[Translation_Display]"
//                   +" when '' then d.[DataName]"
//                   +" when null then d.[DataName]"
//                   +" else LT.[Translation_Display] end) DataName"
//                   +" from DataDictionary d,Language_Translation LT"
//                   +" where d.[DataName]=LT.[Translation_Code]"
//                   +" and d.DataType='"+DataType+"'"
//                   +" and d.DataValue1 ='" +DataValue1+"'";


//           sql= "select distinct DataCode,"
//                   +" (case when Translation_Display is null then DataName"
//                   +" when Translation_Display ='' then DataName"
//                   +" else Translation_Display end) DataName"
//                   +" FROM (select  d.[DataCode],(select"
//                   +" LT.[Translation_Display]"
//                   +" from Language_Translation  LT"
//                   +" where d.[DataName]=LT.[Translation_Code]"
//                   +" and LT.[Translation_Display] is not null"
//                   +" AND LT.[Translation_Display] <>''"
//                   +" AND LT.[Language_Code] ='en-US'"
//                   +" order by LT.Translation_ID asc limit 1"
//                   +" ) Translation_Display,d.[DataName]"
//                   +" from DataDictionary d"
//                   +" where d.DataType='"+DataType+"'"
//                   +" and d.DataValue2 in ("+DataValue2+")"
//                   +" and d.DataValue1 ='" +DataValue1+"') a";
//                if(BuildConfig.isDebug){
                    sql = "select distinct DataCode DataCode,"
                            + " (case when Translation_Display is null then Name"
                            + " when Translation_Display ='' then Name"
                            + " else Translation_Display end) DataName"
                            + " FROM (select  DD.[DataCode],(select"
                            + " LT.[Translation_Display]"
                            + " from Language_Translation  LT"
                            + " where DD.[DataName]=LT.[Translation_Code]"
                            + " and LT.[Translation_Display] is not null"
                            + " AND LT.[Translation_Display] <>''"
                            + " AND LT.[Language_Code] ='en-US'"
                            + " order by LT.Translation_ID asc limit 1"
                            + " ) Translation_Display,DD.DataName Name"
                            + " from DataRelation d,DataDictionary DD"
                            + " where d.DataType1='" + DataType + "'"
                            + " and d.RelationCode in (" + DataValue2 + ")"
                            + " and d.DataCode2 ='" + DataValue1 + "' and d.DataCode1=DD.DataCode and DD.DataType='" + DataType + "') a";
//                }else {
//                    sql = "select distinct DataCode DataCode,"
//                            + " (case when Translation_Display is null then Name"
//                            + " when Translation_Display ='' then Name"
//                            + " else Translation_Display end) DataName"
//                            + " FROM (select  DD.[DataCode],(select"
//                            + " LT.[Translation_Display]"
//                            + " from Language_Translation  LT"
//                            + " where DD.[DataName]=LT.[Translation_Code]"
//                            + " and LT.[Translation_Display] is not null"
//                            + " AND LT.[Translation_Display] <>''"
//                            + " AND LT.[Language_Code] ='en-US'"
//                            + " order by LT.Translation_ID asc limit 1"
//                            + " ) Translation_Display,DD.DataName Name"
//                            + " from DataRelation d,DataDictionary DD"
//                            + " where d.CodeType='" + DataType + "'"
//                            + " and d.RelationType in (" + DataValue2 + ")"
//                            + " and d.MatchingCode ='" + DataValue1 + "' and d.Code=DD.DataCode and DD.DataType='" + DataType + "') a";
//                }
       }else {
//           sql=  "select * from DataDictionary where DataType='"+DataType+"' and 1=1 and DataValue1='" + DataValue1+"' and DataValue2 in ("+DataValue2+")";
//           if (BuildConfig.isDebug) {
               sql = "select distinct DR.DataCode1 DataCode,DD.DataName DataName from DataRelation DR,DataDictionary DD where DR.DataType1='" + DataType + "' and 1=1 "
                       + " and DR.DataCode2='" + DataValue1 + "' and DR.RelationCode in (" + DataValue2 + ") and DR.DataCode1=DD.DataCode and DD.DataType='" + DataType +"'";
//           } else {
//               sql = "select distinct DR.Code DataCode,DD.DataName DataName from DataRelation DR,DataDictionary DD where DR.CodeType='" + DataType + "' and 1=1 "
//                       + " and DR.MatchingCode='" + DataValue1 + "' and DR.RelationType in (" + DataValue2 + ") and DR.Code=DD.DataCode and DD.DataType='" + DataType + "'";
//           }
       }
       ((AppApplication)context.getApplicationContext()).getSqliteStore().performRawQuery(sql, "DataRelation",storeCallback);
   }
    public static void getDataFromLanguageTranslation(Context context,String Translation_Code,StoreCallback storeCallback){
        String sql="select distinct ifnull(LT.[Translation_Display],LT.[Translation_Code]) Translation_Display from Language_Translation LT where LT.[Translation_Code]='"+Translation_Code+"'"
                +" AND LT.[Language_Code] ='en-US'";
        ((AppApplication)context.getApplicationContext()).getSqliteStore().performRawQuery(sql, "Language_Translation",storeCallback);
    }
    public static void FactoryAndNetWorkAddressSetting(Context context,String factory){
        SharedPreferenceManager.setFactory(context,factory);
        if("GEW".equals(factory)){
            SharedPreferenceManager.setNetwork(context,"OuterNetwork");
        }else {
            SharedPreferenceManager.setNetwork(context,"InnerNetwork");
        }
    }

}
