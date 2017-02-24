package com.emms.broadcast;

import android.content.Context;

import com.emms.activity.KeepLiveService;

/**
 * Created by Administrator on 2017/2/9.
 *
 */
public class KeepLiveManager {
    private Context mContext;
    public KeepLiveManager(){

    }
    public KeepLiveManager(Context context){
       mContext=context;
    }
   private static KeepLiveManager keepLiveManager;
   public static KeepLiveManager getInstance(){
       if(keepLiveManager==null){
           keepLiveManager=new KeepLiveManager();
       }
       return keepLiveManager;
   }
   public void startKeepLiveActivity(){
     if(mContext!=null){

     }
   }
   public void finishKeepLiveActivity(){
     if(mContext!=null){

     }
   }
}
