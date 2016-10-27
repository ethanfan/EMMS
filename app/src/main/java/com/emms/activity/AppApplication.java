package com.emms.activity;

import android.app.Application;

import com.datastore_android_sdk.datastore.Datastore;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.push.PushService;
import com.emms.util.LocaleUtils;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by jaffer.deng on 2016/6/3.
 *
 */
public class AppApplication extends Application {

    private EPassSqliteStoreOpenHelper sqliteStoreOpenHelper;

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler catchHandler = CrashHandler.getInstance();
//        // 注册crashHandler
//        catchHandler.init(getApplicationContext());
        System.setProperty("ssl.TrustManagerFactory.algorithm",
                javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(this);
        //LocaleUtils.setLanguage(this, language != null ? language : LocaleUtils.SupportedLanguage.getSupportedLanguage(Locale.CHINESE.toString()));
        LocaleUtils.setLanguage(this, language != null ? language :  LocaleUtils.SupportedLanguage.getSupportedLanguage(getResources().getConfiguration().locale.getLanguage()));
        PushService.registerMessageReceiver(this);
        CrashReport.initCrashReport(getApplicationContext(), "900057191", true);

    }

    public synchronized SqliteStore getSqliteStore() {
        if (sqliteStoreOpenHelper == null) {
            sqliteStoreOpenHelper = new EPassSqliteStoreOpenHelper(this);
        }
        return Datastore.getInstance().getSqliteStore(sqliteStoreOpenHelper);
    }
}
