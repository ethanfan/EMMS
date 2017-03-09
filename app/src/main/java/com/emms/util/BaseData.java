package com.emms.util;

import android.app.Activity;
import android.content.Context;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.schema.DataDictionary;
import com.emms.schema.Factory;
import com.emms.schema.System_FunctionSetting;


import java.io.PipedReader;
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

    public static HashMap<String,String> CheckStatus=new HashMap<>();

    public static HashMap<String, String> getCheckStatus() {
        return CheckStatus;
    }

    public static void setCheckStatus(Context context) {
        DataUtil.getDataFromDataBase(context, "CheckStatus", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for(int i=0;i<element.asArrayElement().size();i++) {
                    CheckStatus.put(DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)),
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
            }
        });
    }



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
       if(SharedPreferenceManager.getFactory(context)!=null){
           setConfigData(context,SharedPreferenceManager.getFactory(context));
       }
       if(SharedPreferenceManager.getLanguageChange(context)){
           BaseData.setTaskClass(context);
           BaseData.setTaskStatus(context);
           BaseData.setCheckStatus(context);
           SharedPreferenceManager.setLanguageChange(context,false);
           return false;
       }
       if(BaseData.getTaskClass().size()<=0||BaseData.getTaskStatus().size()<=0||BaseData.getCheckStatus().size()<=0) {
           if (BaseData.getTaskClass().size() <= 0) {
               BaseData.setTaskClass(context);
           }
           if (BaseData.getTaskStatus().size() <= 0) {
               BaseData.setTaskStatus(context);
           }
           if(BaseData.getCheckStatus().size() <= 0){
               BaseData.setCheckStatus(context);
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

    public static JsonObjectElement getConfigData() {
        return ConfigData;
    }
    public static void setConfigData(Context context, final String FromFactory){
        DataUtil.getConfigurationData(context, FromFactory, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for(DataElement dataElement:element.asArrayElement()){
                        ConfigData.set(DataUtil.isDataElementNull(dataElement.asObjectElement().get(System_FunctionSetting.FUNCTION_CODE)),
                                DataUtil.isDataElementNull(dataElement.asObjectElement().get(System_FunctionSetting.FUNCTION_VALUE)));
                    }
                }else {
                    setConfigDataDefault(FromFactory);
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                setConfigDataDefault(FromFactory);
            }
        });
    }
    public static void setConfigDataDefault(String FromFactory){
        if(DataUtil.isDataElementNull(ConfigData.get(SIMPLE_DESCRIPTION_ACTION)).isEmpty()) {
            switch (FromFactory) {
                case Factory.FACTORY_EGM: {
                    ConfigData.set(SIMPLE_DESCRIPTION_ACTION, 1);//1为创建任务不强制需要任务简要描述
                    ConfigData.set(RECEIVE_TASK_ACTION, 1);//1为接单成功时跳转进任务详情
                    ConfigData.set(TASK_DETAIL_SHOW_WORKLOAD_ACTION, 1);//1为任务详情处不显示工作量块
                    ConfigData.set(TASK_COMPLETE_ACTION, 1);//1为任务完成试需要验证和评价
                    ConfigData.set(TASK_COMPLETE_SHOW_WORKLOAD_ACTION, 1);//1为任务完成流程不需要展现工作量录入界面
                    break;
                }
                case Factory.FACTORY_GEW: {
                    ConfigData.set(SIMPLE_DESCRIPTION_ACTION, 2);//1为创建任务强制需要任务简要描述
                    ConfigData.set(RECEIVE_TASK_ACTION, 2);//2为接单成功时不跳转进任务详情
                    ConfigData.set(TASK_DETAIL_SHOW_WORKLOAD_ACTION, 2);//2为任务详情处显示工作量块
                    ConfigData.set(TASK_COMPLETE_ACTION, 2);//2为任务完成试不需要验证和评价
                    ConfigData.set(TASK_COMPLETE_SHOW_WORKLOAD_ACTION, 2);//1为任务完成流程需要展现工作量录入界面
                    break;
                }
                default: {
                    ConfigData.set(SIMPLE_DESCRIPTION_ACTION, 2);
                    ConfigData.set(RECEIVE_TASK_ACTION, 2);
                    ConfigData.set(TASK_DETAIL_SHOW_WORKLOAD_ACTION, 2);
                    ConfigData.set(TASK_COMPLETE_ACTION, 2);
                    ConfigData.set(TASK_COMPLETE_SHOW_WORKLOAD_ACTION, 2);
                    break;
                }
            }
        }
    }
    private static final JsonObjectElement ConfigData=new JsonObjectElement();
    public static final String SIMPLE_DESCRIPTION_ACTION="SimpleDescriptionAction";
    public static final String RECEIVE_TASK_ACTION="ReceiveTaskAction";
    public static final String TASK_DETAIL_SHOW_WORKLOAD_ACTION="TaskDetailShowWorkloadAction";
    public static final String TASK_COMPLETE_ACTION="TaskCompleteAction";
    public static final String TASK_COMPLETE_SHOW_WORKLOAD_ACTION="TaskCompleteShowWorkloadAction";
}
