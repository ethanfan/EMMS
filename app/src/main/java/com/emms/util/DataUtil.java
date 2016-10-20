package com.emms.util;

import com.datastore_android_sdk.datastore.DataElement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/17.
 */
public class DataUtil {
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
            if( !isNum.matches() ){
                return false;
            }
            return true; // Did not throw, must be a number
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
            e.printStackTrace();
            return utcTime;
        }
        SimpleDateFormat localFormater = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        //localFormater.setTimeZone(TimeZone.getDefault());
        localFormater.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }
    public static String Local2utc(String Local) {
        SimpleDateFormat LocalFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //LocalFormater.setTimeZone(TimeZone.getDefault());
        LocalFormater.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date gpsLocalDate = null;
        try {
            gpsLocalDate = LocalFormater.parse(Local);
        } catch (ParseException e) {
            e.printStackTrace();
            return Local;
        }
        SimpleDateFormat utcFormater = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = utcFormater.format(gpsLocalDate.getTime());
        return utcTime;
    }

}
