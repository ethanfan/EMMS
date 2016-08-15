package com.emms.util;

import com.datastore_android_sdk.datastore.DataElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/7/17.
 */
public class DataUtil {
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
    public static boolean isNumeric(String checkStr) {
        try {
            Integer.parseInt(checkStr);
            return true; // Did not throw, must be a number
        } catch (NumberFormatException err) {
            return false; // Threw, So is not a number
        }
    }
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
}
