package com.emms.util;

import com.datastore_android_sdk.datastore.DataElement;

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
            date.replace("T","  ");
            return date;
        }
        return date;
    }
}
