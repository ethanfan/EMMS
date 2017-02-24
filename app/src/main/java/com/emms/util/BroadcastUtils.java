package com.emms.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.emms.broadcast.KeepLiveBroadcast;

/**
 * Created by Administrator on 2017/2/8.
 *
 */
public class BroadcastUtils {
    private static KeepLiveBroadcast broadcast=new KeepLiveBroadcast();
    private static boolean isBroadcastRegister=false;
    public static void startKeepLiveBroadcast(Context context){
        if(!isBroadcastRegister) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_TIME_TICK);
            context.registerReceiver(broadcast, intentFilter);
            isBroadcastRegister = true;
        }
    }
    public static void stopKeepLiveBroadcast(Context context){
        try {
            if(broadcast!=null&&isBroadcastRegister) {
                context.unregisterReceiver(broadcast);
                isBroadcastRegister=false;
            }
        }catch (Exception e){
            // do nothing
        }
    }
}
