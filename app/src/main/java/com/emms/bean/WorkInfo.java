package com.emms.bean;

public class WorkInfo {
    private String workCode = "";
    private String workName = "";
    private String workDescr = "";
    private String approvedWorkingHours = "";

    public String getApprovedWorkingHours() {
        return approvedWorkingHours;
    }

    public void setApprovedWorkingHours(String approvedWorkingHours) {
        if (null == approvedWorkingHours) {
            this.approvedWorkingHours = "";
        }
        this.approvedWorkingHours = approvedWorkingHours;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        if (null == workCode) {
            this.workCode = "";
        }
        this.workCode = workCode;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        if (null == workName) {
            this.workName = "";
        }
        this.workName = workName;
    }

    public String getWorkDescr() {
        return workDescr;
    }

    public void setWorkDescr(String workDescr) {
        if (null == workDescr) {
            this.workDescr = "";
        }
        this.workDescr = workDescr;
    }
}
