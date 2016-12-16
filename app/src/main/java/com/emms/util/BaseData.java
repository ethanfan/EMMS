package com.emms.util;

import android.app.Activity;
import android.content.Context;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.emms.R;
import com.emms.schema.DataDictionary;


import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/24.
 * This Util is used to keep base data.
 */
public class BaseData {
    private BaseData(){
        //no instance
    }
    public static HashMap<String,String> TaskClass=new HashMap<>();

    public static HashMap<String,String> TaskStatus=new HashMap<>();

    public static HashMap<String, String> getTaskStatus() {
        return TaskStatus;
    }

    public static void setTaskStatus(final Context context) {
        DataUtil.getDataFromDataBase(context, "TaskStatus", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for(int i=0;i<element.asArrayElement().size();i++) {
                    TaskStatus.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                }
                if(baseDataListener!=null){
                    baseDataListener.GetBaseDataSuccess();
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                if(baseDataListener!=null){
                    baseDataListener.GetBaseDataFail();
                }
//                ((Activity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,context);
//                    }
//                });
            }
        });
    }
    public static HashMap<String, String> getTaskClass() {
        return TaskClass;
    }

    public static void setTaskClass(final Context context) {
        DataUtil.getDataFromDataBase(context, "TaskClass", 0, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for(int i=0;i<element.asArrayElement().size();i++){
                    TaskClass.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
                            DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_NAME)));
                }
                if(baseDataListener!=null){
                    baseDataListener.GetBaseDataSuccess();
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                if(baseDataListener!=null){
                    baseDataListener.GetBaseDataFail();
                }
//                ((Activity)context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,context);
//                    }
//                });
            }
        });
    }
   public static boolean setBaseData(Context context){
       if(SharedPreferenceManager.getLanguageChange(context)){
           BaseData.setTaskClass(context);
           BaseData.setTaskStatus(context);
           SharedPreferenceManager.setLanguageChange(context,false);
           return false;
       }
       if(BaseData.getTaskClass().size()<=0||BaseData.getTaskStatus().size()<=0) {
           if (BaseData.getTaskClass().size() <= 0) {
               BaseData.setTaskClass(context);
           }
           if (BaseData.getTaskStatus().size() <= 0) {
               BaseData.setTaskStatus(context);
           }
           return false;
       }
       return true;
   }
    private interface BaseDataListener{
       void GetBaseDataSuccess();
        void GetBaseDataFail();
    }

    public BaseDataListener getBaseDataListener() {
        return baseDataListener;
    }

    public void setBaseDataListener(BaseDataListener baseDataListener) {
        this.baseDataListener = baseDataListener;
    }

    private static BaseDataListener baseDataListener;

}
