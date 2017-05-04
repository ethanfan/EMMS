package com.emms.broadcast;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.emms.R;
import com.emms.activity.AppApplication;
import com.emms.activity.KeepLiveService;
import com.emms.activity.KeepLiveService2;
import com.emms.activity.LoginActivity;
import com.emms.util.ServiceUtils;

/**
 * Created by Administrator on 2017/2/8.
 *
 */
public class KeepLiveBroadcast extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if("AlarmKeepLive".equals(intent.getAction())){
            Log.e("KeepLiveBroadcast","KeepLive");
            if(AppApplication.KeepLive) {
                ServiceUtils.starKeepLiveService(ServiceUtils.Mode.Both_KeepLiveService, context);
            }
            if(Build.VERSION.SDK_INT>=23){
                Intent i = new Intent("AlarmKeepLive");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
                AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                int offset= 60 * 1000;//间隔时间10s
                long triggerAtTime = SystemClock.elapsedRealtime() + offset;
                manager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
            }
//            try {
//                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//                mBuilder.setContentIntent(PendingIntent.getActivity(context, 0,
//                        new Intent(context, LoginActivity.class), 0))
//                        .setWhen(System.currentTimeMillis())
//                        .setPriority(Notification.DEFAULT_VIBRATE)
//                        .setOngoing(false)
//                        .setSmallIcon(R.drawable.ic_emms);
//                mBuilder.setTicker("Foreground Service Start");
//                mBuilder.setContentTitle("Foreground Service");
//                mBuilder.setContentText("Make this service run in the foreground.");
//                Notification notify = mBuilder.build();
//                notify.flags |= Notification.FLAG_AUTO_CANCEL; // 点击通知后通知栏消失
//                notify.defaults |= Notification.DEFAULT_ALL;
//                notify.vibrate = new long[]{0, 100, 1100, 2100, 4100};
//                // 通知id需要唯一，要不然会覆盖前一条通知
//                int notifyId = (int) System.currentTimeMillis();
//                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.notify(notifyId, notify);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
        }
    }
}
