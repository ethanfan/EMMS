package com.emms.activity;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.datastore_android_sdk.datastore.Datastore;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.push.PushReceiver;
import com.emms.push.PushService;
import com.emms.util.BuildConfig;
import com.emms.util.LocaleUtils;
import com.google.common.util.concurrent.ServiceManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.StringUtils;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/3.
 *
 */
public class AppApplication extends Application {

    private EPassSqliteStoreOpenHelper sqliteStoreOpenHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        if (quickStart()) {
            return;
        }
//        CrashHandler catchHandler = CrashHandler.getInstance();
//        // 注册crashHandler
//        catchHandler.init(getApplicationContext());

        JPushInterface.init(this);
        PushService.registerMessageReceiver(getApplicationContext());
        System.setProperty("ssl.TrustManagerFactory.algorithm",
                javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(this);
        //LocaleUtils.setLanguage(this, language != null ? language : LocaleUtils.SupportedLanguage.getSupportedLanguage(Locale.CHINESE.toString()));
        LocaleUtils.setLanguage(this, language != null ? language :  LocaleUtils.SupportedLanguage.getSupportedLanguage(getResources().getConfiguration().locale.getLanguage()));
        CrashReport.initCrashReport(getApplicationContext(), "900057191", true);
        BuildConfig.NetWorkSetting(this);
        JPushInterface.stopPush(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        if(!JPushInterface.isPushStopped(getApplicationContext())) {
            JPushInterface.stopPush(getApplicationContext());
        }
        super.onTerminate();
    }

    public synchronized SqliteStore getSqliteStore() {
        if (sqliteStoreOpenHelper == null) {
            sqliteStoreOpenHelper = new EPassSqliteStoreOpenHelper(this);
        }
        return Datastore.getInstance().getSqliteStore(sqliteStoreOpenHelper);
    }

        public static final String KEY_DEX2_SHA1 = "dex2-SHA1-Digest";
        @Override
        protected void attachBaseContext(Context base) {
            super .attachBaseContext(base);
            Log.d( "loadDex", "App attachBaseContext ");
            if (!quickStart() && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {//>=5.0的系统默认对dex进行oat优化
                if (needWait(base)){
                    waitForDexopt(base);
                }
                MultiDex.install (this );
            }
        }


        public boolean quickStart() {
            if (StringUtils.contains( getCurProcessName(this), ":mini")) {
                Log.d( "loadDex", ":mini start!");
                return true;
            }
            return false ;
        }
        //neead wait for dexopt ?
        private boolean needWait(Context context){
            String flag = get2thDexSHA1(context);
            Log.d( "loadDex", "dex2-sha1 "+flag);
            SharedPreferences sp = context.getSharedPreferences(
                    getPackageInfo(context). versionName, MODE_MULTI_PROCESS);
            String saveValue = sp.getString(KEY_DEX2_SHA1, "");
            return !StringUtils.equals(flag,saveValue);
        }
        /**
         * Get classes.dex file signature
         * @param context c
         * @return s
         */
        private String get2thDexSHA1(Context context) {
            ApplicationInfo ai = context.getApplicationInfo();
            String source = ai.sourceDir;
            try {
                JarFile jar = new JarFile(source);
                Manifest mf = jar.getManifest();
                Map<String, Attributes> map = mf.getEntries();
                Attributes a = map.get("classes2.dex");
                return a.getValue("SHA1-Digest");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null ;
        }
        // optDex finish
        public void installFinish(Context context){
            SharedPreferences sp = context.getSharedPreferences(
                    getPackageInfo(context).versionName, MODE_MULTI_PROCESS);
            sp.edit().putString(KEY_DEX2_SHA1,get2thDexSHA1(context)).apply();
        }


        public static String getCurProcessName(Context context) {
            try {
                int pid = android.os.Process.myPid();
                ActivityManager mActivityManager = (ActivityManager) context
                        .getSystemService(Context. ACTIVITY_SERVICE);
                for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                        .getRunningAppProcesses()) {
                    if (appProcess.pid == pid) {
                        return appProcess. processName;
                    }
                }
            } catch (Exception e) {
                // ignore
            }
            return null ;
        }
        public void waitForDexopt(Context base) {
            Intent intent = new Intent();
            ComponentName componentName = new
                    ComponentName("com.emms", LoadResActivity.class.getName());
            intent.setComponent(componentName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            base.startActivity(intent);
            long startWait = System.currentTimeMillis ();
            long waitTime = 10 * 1000 ;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1 ) {
                waitTime = 20 * 1000 ;//实测发现某些场景下有些2.3版本有可能10s都不能完成optdex
            }
            while (needWait(base)) {
                try {
                    long nowWait = System.currentTimeMillis() - startWait;
                    Log.d("loadDex" , "wait ms :" + nowWait);
                    if (nowWait >= waitTime) {
                        return;
                    }
                    Thread.sleep(200 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    public static PackageInfo getPackageInfo(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("",e.getLocalizedMessage());
        }
        return  new PackageInfo();
    }

}
