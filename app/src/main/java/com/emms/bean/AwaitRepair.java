package com.emms.bean;

/**
 * Created by jaffer.deng on 2016/7/15.
 */
public class AwaitRepair {
    private String wg;
    private String grd;

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public AwaitRepair() {
    }

    public AwaitRepair(String wg,String grd,int status) {
        this.wg = wg;
        this.grd = grd;
        this.status = status;
    }

    public String getGrd() {
        return grd;
    }

    public void setGrd(String grd) {
        this.grd = grd;
    }

    public String getWg() {
        return wg;
    }

    public void setWg(String wg) {
        this.wg = wg;
    }
}
