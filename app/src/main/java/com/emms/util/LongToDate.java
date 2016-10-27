package com.emms.util;

import java.text.SimpleDateFormat;
import java.sql.Date;
/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public class LongToDate {
    /**
     * @Description: String类型毫秒数转换成日期
     *
     * @param lo 毫秒数
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String stringToDate(String lo){
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    /**
     * @Description: long类型转换成日期
     *
     * @param lo 毫秒数
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String longToDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
        return sd.format(date);
    }

    /**
     * @Description: long类型生成没有符号的日期格式
     *
     * @param lo 日期毫秒数
     * @return String yyyyMMddHHmmss
     */
    public static String getLongToDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        return sd.format(date);
    }

    /**
     * @Description: String类型生成没有符号的日期格式
     *
     * @param lo 日期毫秒数（字符串形式）
     * @return String yyyyMMddHHmmss
     */
    public static String getStringToDate(String lo){
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
        return sd.format(date);
    }

    /**
     * @Description: long类型转换成点形式的日期格式
     *
     * @param lo 日期毫秒数
     * @return String yyyy.MM.dd
     */
    public static String getLongPointDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd");
        return sd.format(date);
    }

    /**
     * @Description: String类型转换成点形式的日期格式
     *
     * @param lo String类型日期毫秒数
     * @return String yyyy.MM.dd
     */
    public static String getStringPointDate(String lo){
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd");
        return sd.format(date);
    }

    /**
     * @Description: long类型转成日期格式
     *
     * @param lo long类型日期好藐视
     * @return String yyyyMMdd
     */
    public static String getloToDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        return sd.format(date);
    }

    /**
     * @Description: String类型转成日期格式
     *
     * @param lo String类型日期好藐视
     * @return String yyyyMMdd
     */
    public static String getStrToDate(String lo){
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
        return sd.format(date);
    }

    /**
     * @Description: long类型转换成点形式的日期格式
     *
     * @param lo 日期毫秒数
     * @return String yyyy.MM.dd  HH:mm:ss
     */
    public static String longPointDate(long lo){
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd  HH:mm");
        return sd.format(date);
    }

    /**
     * @Description: String类型转换成点形式的日期格式
     *
     * @param lo String类型日期毫秒数
     * @return String yyyy.MM.dd HH:mm:ss
     */
    public static String stringPointDate(String lo){
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy.MM.dd  HH:mm:ss");
        return sd.format(date);
    }
}
