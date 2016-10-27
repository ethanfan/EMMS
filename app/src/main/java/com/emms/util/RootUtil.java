package com.emms.util;

/**
 * Created by Administrator on 2016/8/7.
 *
 */
public class RootUtil {
    public static int ROOT_SYSTEM=1;
    public static int ROOT_DEPARTMENT_MANAGER=2;
    public static int ROOT_DIRECTOR=3;
    public static int ROOT_CHARGER=4;
    public static int ROOT_REPAIR_ENGERINEER=5;
    public static int ROOT_REPAIR=6;
    public static int ROOT_WARRANTY=7;
    //权限机制
    public static boolean rootStatus(int ThisStatus,int CompareStatus){
        //1表示处理中任务
        return ThisStatus == CompareStatus;
    }
    public static  boolean rootTaskClass(String ThisTaskClass,String CompareTaskClass){
        return ThisTaskClass.equals(CompareTaskClass);
    }
    public static boolean rootMainPersonInTask(String OperatorID,String MainPersonID){
        return OperatorID.equals(MainPersonID);
    }
}
