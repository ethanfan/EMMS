package com.emms.util;

import com.emms.schema.Task;

/**
 * Created by Administrator on 2016/8/7.
 */
public class RootUtil {
    public static String ROOTREPAIRLEADER="2";
    public static String ROOTREPAIR="1";
    public static String ROOTWARRANTY="0";
    //权限机制
    public static boolean rootStatus(int ThisStatus,int CompareStatus){
        //1表示处理中任务
        if(ThisStatus==CompareStatus){
            return    true;}
        else return false;
    }
    public static  boolean rootTaskClass(String ThisTaskClass,String CompareTaskClass){
        if(ThisTaskClass.equals(CompareTaskClass)){
            return true;
        }else{
            return false;
        }
    }
    public static boolean rootMainPersonInTask(String OperatorID,String MainPersonID){
        if(OperatorID.equals(MainPersonID)){
            return true;
        }else{
            return false;
        }
    }
}
