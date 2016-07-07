package com.emms;


import android.content.Context;

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


//    public SQLiteDatabase getSqliteStore(final Context context) {
//        databaseContext = new DatabaseContext(context);
//        try {
//            return  databaseContext.openOrCreateDatabase(DB_NAME+".db",Context.MODE_PRIVATE,null,new DatabaseErrorHandler() {
//                @Override
//                public void onCorruption(SQLiteDatabase dbObj) {
//                    Toast.makeText(context,"数据库出错，请重启App",Toast.LENGTH_SHORT).show();
//                }
//            });
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//      return null;
//    }

}
