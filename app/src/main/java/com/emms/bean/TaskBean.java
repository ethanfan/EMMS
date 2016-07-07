package com.emms.bean;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class TaskBean {
    private String creater;
    private String group;
    private String deviceNum;
    private String deviceName;
    private int taskTag;
    private long creatTime;
    private long repairTime;
    private long startTime;
    private long endTime;
    private String taskDescriptions;

    public TaskBean(String group, String deviceNum, String deviceName,
                    int taskTag, long startTime,
                    long endTime, String taskDescriptions) {
        this.group = group;
        this.deviceNum = deviceNum;
        this.deviceName = deviceName;
        this.taskTag = taskTag;
        this.startTime = startTime;
        this.endTime = endTime;
        this.taskDescriptions = taskDescriptions;
    }

    public TaskBean( String creater,String group, String deviceNum, String deviceName,long creatTime) {
        this.creater = creater;
        this.group = group;
        this.deviceNum = deviceNum;
        this.deviceName = deviceName;
        this.creatTime = creatTime;
    }
    public TaskBean( String creater, String deviceNum, String deviceName,int tag,long creatTime,long endTime) {
        this.creater = creater;
        this.endTime = endTime;
        this.deviceNum = deviceNum;
        this.taskTag = tag;
        this.deviceName = deviceName;
        this.creatTime = creatTime;
    }
    public TaskBean(String creater, String group, String deviceNum,
                    String deviceName, long repairTime, long startTime,
                    long endTime, String taskDescriptions) {
        this.creater = creater;
        this.group = group;
        this.deviceNum = deviceNum;
        this.deviceName = deviceName;
        this.repairTime = repairTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.taskDescriptions = taskDescriptions;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
    public long getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(long repairTime) {
        this.repairTime = repairTime;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getTaskTag() {
        return taskTag;
    }

    public void setTaskTag(int taskTag) {
        this.taskTag = taskTag;
    }

    public long getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(long creatTime) {
        this.creatTime = creatTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTaskDescriptions() {
        return taskDescriptions;
    }

    public void setTaskDescriptions(String taskDescriptions) {
        this.taskDescriptions = taskDescriptions;
    }
}
