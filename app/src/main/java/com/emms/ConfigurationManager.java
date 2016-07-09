package com.emms;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

//暂时未启用
public final class ConfigurationManager {


    private static ConfigurationManager manager;
    public static final String DB_NAME = "test4";
    private ConfigurationManager() {
    }

    public static ConfigurationManager getInstance() {
        if (manager == null) {
            manager = new ConfigurationManager();
        }

        return manager;
    }

    public void startToGetNewConfig(Context context) {


    }


}
