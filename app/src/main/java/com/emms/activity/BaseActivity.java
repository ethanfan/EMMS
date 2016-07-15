package com.emms.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.datastore_android_sdk.sqlite.SqliteStore;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    protected SqliteStore getSqliteStore() {
        return ((AppAplication) getApplication()).getSqliteStore();
    }

}
