package com.emms.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.emms.util.MessageUtils;

import org.apache.commons.lang.StringUtils;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by lenovo on 2016/7/15.
 */
public class PushService {

    private static final String TAG = "PushService";
    //for receive customer msg from jpush server
    private static MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.emms.push.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private Button mInit;
    private Button mSetting;
    private Button mStopPush;
    private Button mResumePush;
    private Button mGetRid;
    private TextView mRegId;
    private EditText msgText;

    public static final int MSG_SET_ALIAS = 1001;
    public static final int MSG_SET_TAGS = 1002;

    private static Context applicationContext = null;


    public static void  registerMessageReceiver(Context context) {
        applicationContext = context;
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        context.registerReceiver(mMessageReceiver, filter);
    }

    public static class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
//                setCostomMsg(showMsg.toString());
            }
        }
    }

    public static final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    try {
                        JPushInterface.setAliasAndTags(applicationContext, (String) msg.obj, null, mAliasCallback);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;

                case MSG_SET_TAGS:
                    Log.d(TAG, "Set tags in handler.");
                    try {
                        JPushInterface.setAliasAndTags(applicationContext, null, (Set<String>) msg.obj, mTagsCallback);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    break;

                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
            }
        }
    };

    private static final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(applicationContext)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            if(StringUtils.isNotBlank(logs)){
                MessageUtils.showToast(logs, applicationContext);
            }
        }

    };


    public static final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs ;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    if (ExampleUtil.isConnected(applicationContext)) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }

            if(code != 0){
                MessageUtils.showToast(logs, applicationContext);
            }
        }

    };





}
