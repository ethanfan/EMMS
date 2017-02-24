package com.emms.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.emms.activity.KeepLiveService;
import com.emms.activity.KeepLiveService2;

/**
 * Created by Administrator on 2017/2/6.
 *
 */
public class ServiceUtils {
    public enum Mode{
        Only_KeepLiveServiceNo_1,
        Only_KeepLiveServiceNo_2,
        Both_KeepLiveService
    }
    public synchronized static void starKeepLiveService(Mode mode,Context mContext){
        try {
            boolean isKeepLiveServiceNo_1_Running=false;
            boolean isKeepLiveServiceNo_2_Running=false;
            boolean isPushServiceRunning=false;
            ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
                switch (mode){
                    case Only_KeepLiveServiceNo_1:{
                        if("com.emms.activity.KeepLiveService".equals(service.service.getClassName()))
                        {
                            isKeepLiveServiceNo_1_Running = true;
                        }
                        break;
                    }
                    case Only_KeepLiveServiceNo_2:{
                        if("com.emms.activity.KeepLiveService2".equals(service.service.getClassName()))
                        {
                            isKeepLiveServiceNo_2_Running = true;
                        }
                        break;
                    }
                    default:{
                        if("com.emms.activity.KeepLiveService".equals(service.service.getClassName()))
                        {
                            isKeepLiveServiceNo_1_Running = true;
                        }
                        if("com.emms.activity.KeepLiveService2".equals(service.service.getClassName()))
                        {
                            isKeepLiveServiceNo_2_Running = true;
                        }
                        break;
                    }
                }
                if("cn.jpush.android.service.PushService".equals(service.service.getClassName())){
                    isPushServiceRunning=true;
                }
            }
            if (!isKeepLiveServiceNo_1_Running) {
                Intent i = new Intent(mContext.getApplicationContext(), com.emms.activity.KeepLiveService.class);
                mContext.startService(i);
            }
            if (!isKeepLiveServiceNo_2_Running) {
                Intent i = new Intent(mContext.getApplicationContext(), com.emms.activity.KeepLiveService2.class);
                mContext.startService(i);
            }
            if(!isPushServiceRunning){
                Intent iten=new Intent(mContext.getApplicationContext(),cn.jpush.android.service.PushService.class);
                mContext.startService(iten);
            }
        }catch (Exception e){
            //DO nothing
            Log.e("Exception","StarServiceException");
        }
    }
    public synchronized static void stopKeepLiveService(Context mContext){
        try {
            ActivityManager manager = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) {
                if("com.emms.activity.KeepLiveService2".equals(service.service.getClassName()))
                {
                    mContext.stopService(new Intent(mContext.getApplicationContext(), KeepLiveService2.class));
                }
                if("com.emms.activity.KeepLiveService".equals(service.service.getClassName())){
                    mContext.stopService(new Intent(mContext.getApplicationContext(), KeepLiveService.class));
                }
            }
        }catch (Exception e){
            //DO nothing
        }
    }
}
