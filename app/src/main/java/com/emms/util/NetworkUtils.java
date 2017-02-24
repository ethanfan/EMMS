package com.emms.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import com.emms.R;
import com.emms.schema.Factory;

/**
 * Created by Administrator on 2017/1/15.
 *
 */
public class NetworkUtils {
    public static void DoNetworkChange(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String SSID=wifiInfo.getSSID();
        if(Build.VERSION.SDK_INT>=17){//判断SDK版本大于17就去掉双引号
            if(SSID.startsWith("\"")&&SSID.endsWith("\"")){
                SSID=SSID.substring(1,SSID.length()-1);
            }
        }
        if(NetworkConnectChangedReceiver.mNetworkList.contains(SSID)
                && Factory.FACTORY_EGM.equals(SharedPreferenceManager.getFactory(context))){
            Log.d("SSID",wifiInfo.getSSID());
            ToastUtil.showToastLong(R.string.CheckForIntranet,context);
            SharedPreferenceManager.setNetwork(context.getApplicationContext(),initNetWork(true));
        }else {
            ToastUtil.showToastLong(R.string.CheckForExtranet,context);
            SharedPreferenceManager.setNetwork(context.getApplicationContext(),initNetWork(false));
        }
        //TODO 内外网切换
        BuildConfig.NetWorkSetting(context.getApplicationContext());
    }
    public static String initNetWork(boolean isInnerNetwork){
        if(isInnerNetwork){
            return "InnerNetwork";
        }else {
            return "OuterNetwork";
        }
    }
}
