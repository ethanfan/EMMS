package com.emms.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.jaffer_datastore_android_sdk.sqlite.SqliteStore;

/**
 * Created by jaffer.deng on 2016/6/17.
 */
public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    protected SqliteStore getSqliteStore() {
        return ((AppAplication) getApplication()).getSqliteStore();
    }

}
