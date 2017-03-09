package com.emms.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.VpnService;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Parcelable;
import android.util.Log;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.http.Network;
import com.emms.R;
import com.emms.activity.AppApplication;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.schema.DataDictionary;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/1/10.
 *
 */
public class NetworkConnectChangedReceiver extends  BroadcastReceiver{
    private static final String TAG="NetworkConnectChanged";
    @Override
    public void onReceive(final Context context,final Intent intent) {
        switch (BuildConfig.appEnvironment){
            case DEVELOPMENT:{
                if(!mNetworkList.contains("Linkgoo-Base")){
                    initNetWorkData();
                }
                break;
            }
            case PROD:
            case UAT:{
                if(mNetworkList.size()==0){
                    initNetWorkData();
                }
                break;
            }
            default:{
                if(mNetworkList.size()==0){
                    initNetWorkData();
                }
                break;
            }
        }
//        if(BuildConfig.isDebug){
//            if(!mNetworkList.contains("Linkgoo-Base")){
//                initNetWorkData();
//            }
//        }else {
//            if(mNetworkList.size()==0){
//                initNetWorkData();
//            }
//        }
       doReceive(context,intent);
    }
    private String getConnectionType(int type) {
        String connType = "";
        if (type == ConnectivityManager.TYPE_MOBILE) {
            connType = "3G网络数据";
        } else if (type == ConnectivityManager.TYPE_WIFI) {
            connType = "WIFI网络";
        }
        return connType;
    }

    public static ArrayList<String> mNetworkList=new ArrayList<>();
    public static void initNetWorkData(){
        mNetworkList.add("Linkgoo-Base");
        mNetworkList.add("esq-data");
        //mNetworkList.add("Linkgoo-Base");
    }

    private void doReceive(Context context,Intent intent){
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {// 监听wifi的打开与关闭，与wifi的连接无关
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.e("TAG", "wifiState:" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    break;
            }
        }
        // 监听wifi的连接状态即是否连上了一个有效无线路由
//        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
//            Parcelable parcelableExtra = intent
//                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//            if (null != parcelableExtra) {
//                // 获取联网状态的NetWorkInfo对象
//                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
//                //获取的State对象则代表着连接成功与否等状态
//                NetworkInfo.State state = networkInfo.getState();
//                //判断网络是否已经连接
//                boolean isConnected = state == NetworkInfo.State.CONNECTED;
//                Log.e(TAG, "isConnected:" + isConnected);
//                if (isConnected) {
//                } else {
//
//                }
//            }
//        }
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        Log.e(TAG, getConnectionType(info.getType()) + "连上");
                        if(info.getType()==ConnectivityManager.TYPE_WIFI){
                            NetworkUtils.DoNetworkChange(context);
                        }else {
                            ToastUtil.showToastLong(R.string.CheckForMONET,context);
                            SharedPreferenceManager.setNetwork(context.getApplicationContext(),NetworkUtils.initNetWork(false));
                            BuildConfig.NetWorkSetting(context.getApplicationContext());
                        }
                    }
                } else {
                    Log.e(TAG, getConnectionType(info.getType()) + "断开");
                }
            }
        }
    }
}
