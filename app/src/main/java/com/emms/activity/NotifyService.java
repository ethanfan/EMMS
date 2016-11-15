package com.emms.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Message;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/5.
 *
 */
public class NotifyService extends Service {
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private ArrayList<String> msgQueen = new ArrayList<>();
    private boolean isSaveInService = true;
    private boolean iscon = true;//用于在broadcast中判断是否需要重新连接
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notifyBuilder;
    private Vibrator vibrator;
    private Binder binder = new Binder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //在此进行推送消息的获取
        getMessageFromServer();
        return super.onStartCommand(intent, flags, startId);
    }

    private void getMessageFromServer() {
        HttpParams params=new HttpParams();
        HttpUtils.get(this, "", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                openVibrator();
                msgQueen.add(t);
                sendNotification();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

    private void openVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};
        vibrator.vibrate(pattern, -1);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable() && iscon == false) {
                    //断网的时候
                    Intent serviceIntent = new Intent(context, NotifyService.class);
                    context.startService(serviceIntent);
                }
            }
        }
    };
   public class MyBinder extends Binder{
        public void sendToUI(){
            //Message message=new Message();
            //message.
            Bundle b=new Bundle();
            ArrayList l=new ArrayList();
            l.addAll(msgQueen);
            b.putParcelableArrayList("list",l);

        }
       public void setIsSaveInSer(){
           isSaveInService=true;
       }
    }
    //发送Notification
    private void sendNotification(){
        Intent resultIntent=new Intent(this,CusActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.star_selected);
        notifyBuilder=new NotificationCompat.Builder(this)
                //设置large icon
                .setLargeIcon(bitmap)
                //设置samll icon
                .setSmallIcon(R.mipmap.star_selected)
                /*设置title*/
                .setContentTitle("您收到了"+String.valueOf(msgQueen.size())+"条消息")
                 /*设置详细文本*/
                .setContentText(msgQueen.get(msgQueen.size()-1))
                /*设置发出通知的时间为发出通知时的系统时间*/
                .setWhen(System.currentTimeMillis())
                /*设置发出通知时在status bar进行提醒*/
                .setTicker("收到新消息")
                /*setOngoing(boolean)设为true,notification将无法通过左右滑动的方式清除
                * 可用于添加常驻通知，必须调用cancle方法来清除 */
                .setOngoing(false)
                 /*设置点击后通知消失*/
                .setAutoCancel(true)
                /*设置通知数量的显示类似于QQ那种，用于同志的合并*/
                //                .setNumber(3)
                /*点击跳转到MainActivity*/
                .setContentIntent(pendingIntent);
        notificationManager.notify(121,notifyBuilder.build());
    }

}