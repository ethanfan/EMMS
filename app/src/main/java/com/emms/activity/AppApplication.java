package com.emms.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.datastore_android_sdk.datastore.Datastore;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.push.PushService;
import com.emms.schema.Factory;
import com.emms.util.BuildConfig;
import com.emms.util.LocaleUtils;
import com.emms.util.NetworkConnectChangedReceiver;
import com.emms.util.SharedPreferenceManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/3.
 *
 */
public class AppApplication extends Application {
    public enum ServerEndPoint {
        GAOMING,
        PRODUCTION,
        DEVELOPMENT,
        UAT,
        AZURE_UAT,
        GARMENT,
        GARMENTTEST
    }
    public static boolean KeepLive=false;
    public static  ServerEndPoint endPoint = ServerEndPoint.UAT;
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
        //初始化Jpush推送服务
//        if(SharedPreferenceManager.getFactory(this)==null){
//            SharedPreferenceManager.setFactory(this, "GEW");
//        }
        //Garment测试包设置
        //SharedPreferenceManager.setFactory(this,"EGM");
        //SharedPreferenceManager.setNetwork(this,"InnerNetwork");
        JPushInterface.init(this);
        PushService.registerMessageReceiver(getApplicationContext());
//        if(BuildConfig.isDebug) {
//            NetworkConnectChangedReceiver.initNetWorkData();
//        }
        //控制内外网切换SSID初始化
        switch (BuildConfig.appEnvironment){
            case DEVELOPMENT:{
                NetworkConnectChangedReceiver.initNetWorkData();
                break;
            }
            default:{
                break;
            }
        }
        BuildConfig.NetWorkSetting(this);
        System.setProperty("ssl.TrustManagerFactory.algorithm",
                javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        //语言设置
        LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(this);
        //LocaleUtils.setLanguage(this, language != null ? language : LocaleUtils.SupportedLanguage.getSupportedLanguage(Locale.CHINESE.toString()));
        LocaleUtils.setLanguage(this, language != null ? language :  LocaleUtils.SupportedLanguage.getSupportedLanguage(getResources().getConfiguration().locale.getLanguage()));
        //bugly初始化
        CrashReport.initCrashReport(getApplicationContext(), "900057191", true);
        //地址设置
        JPushInterface.stopPush(getApplicationContext());
        if(Factory.FACTORY_EGM.equals(SharedPreferenceManager.getFactory(this))) {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+7"));
        }else {
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        }
        Intent intent=new Intent("AlarmKeepLive");
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),60*1000,pendingIntent);
    }

    @Override
    public void onTerminate() {
//        if(!JPushInterface.isPushStopped(getApplicationContext())) {
//            JPushInterface.stopPush(getApplicationContext());
//        }
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
                CrashReport.postCatchedException(e);
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
                    CrashReport.postCatchedException(e);
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
    public  static Context getContext(){
        return new AppApplication().getApplicationContext();
    }
    // 从asserts目录下拷贝文件到files
//    private void copyDB() {
//
//        // 获取输出流,文件存储目录:data/data/包名/files目录下，文件名相同
//        File file = new File(getFilesDir(), "EMMS.db");
//
//        // 当文件不存在的时候：才去拷贝，已经存在的不再去拷贝了。
//        if (!file.exists()) {
//            AssetManager assetManager = getAssets();
//
//            try {
//
//                // 获取输入流
//                InputStream is = assetManager.open("EMMS.db");
//
//                FileOutputStream fos = new FileOutputStream(file);
//                // 开始读和写
//                byte[] bys = new byte[1024];
//                int len;
//                while ((len = is.read(bys)) != -1) {
//                    fos.write(bys, 0, len);
//                }
//                is.close();
//                fos.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//    }
}
