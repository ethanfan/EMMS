package com.emms.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
    @SuppressWarnings("deprecation")
    public void WifiNeverDormancy(Context mContext)
    {
        ContentResolver resolver = mContext.getContentResolver();

        int value = Settings.System.getInt(resolver, Settings.System.WIFI_SLEEP_POLICY,  Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(mContext);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("WIFI_SLEEP", value);

        editor.apply();
        if(Settings.System.WIFI_SLEEP_POLICY_NEVER != value)
        {
            Settings.System.putInt(resolver, Settings.System.WIFI_SLEEP_POLICY, Settings.System.WIFI_SLEEP_POLICY_NEVER);

        }
        Log.e("wifi value:","wifi value:" + value);
    }
}
