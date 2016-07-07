package com.emms.activity;

import android.app.Application;

import com.emms.util.CrashHandler;
import com.emms.util.LocaleUtils;
import com.jaffer_datastore_android_sdk.rxvolley.RxVolley;

import java.util.Locale;

/**
 * Created by jaffer.deng on 2016/6/3.
 */
public class AppAplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler catchHandler = CrashHandler.getInstance();
//        catchHandler.init(getApplicationContext());
        System.setProperty("ssl.TrustManagerFactory.algorithm",
                javax.net.ssl.KeyManagerFactory.getDefaultAlgorithm());
        LocaleUtils.SupportedLanguage language = LocaleUtils.getLanguage(this);
        LocaleUtils.setLanguage(this, language != null ? language : LocaleUtils.SupportedLanguage.getSupportedLanguage(Locale.CHINESE.toString()));

    }
}
