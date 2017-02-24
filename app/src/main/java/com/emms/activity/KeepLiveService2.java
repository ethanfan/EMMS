package com.emms.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.emms.R;
import com.emms.util.ServiceUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class KeepLiveService2 extends Service {
    private static final String TAG = "ForegroundService";
    private Timer timer;
    private TimerTask timerTask;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand");
        if(timer==null){
            timer=new Timer();
        }
        if(timerTask==null){
            timerTask=new TimerTask() {
                @Override
                public void run() {
                    Log.e(TAG,"KeepLiveService");
                    if(AppApplication.KeepLive) {
                        ServiceUtils.starKeepLiveService(ServiceUtils.Mode.Only_KeepLiveServiceNo_1, getApplicationContext());
                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask,new Date(),60000);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

    }


}
