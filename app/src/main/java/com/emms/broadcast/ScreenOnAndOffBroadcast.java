package com.emms.broadcast;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.emms.schema.Task;

/**
 * Created by Administrator on 2017/2/9.
 *
 */
public class ScreenOnAndOffBroadcast extends BroadcastReceiver {
    private static final String TAG="ScreenOnAndOff";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
            Log.e(TAG,"ScreenOFF");
        }else if (Intent.ACTION_USER_PRESENT.equals(intent.getAction())){
            Log.e(TAG,"ScreenOn");
        }
    }
}
