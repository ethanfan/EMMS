package com.emms.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.ConfigurationManager;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.DataDictionary;
import com.emms.schema.DataRelation;
import com.emms.schema.DataType;
import com.emms.schema.Equipment;
import com.emms.schema.Language_Translation;
import com.emms.schema.Languages;
import com.emms.schema.Operator;
import com.emms.schema.TaskMessage;
import com.emms.schema.TaskOrganiseRelation;
import com.emms.ui.KProgressHUD;
import com.emms.ui.LoadingDialog;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/17.
 *
 */
public abstract class BaseActivity extends AppCompatActivity {
    private KProgressHUD hud;
    private LoadingDialog loadingDialog;
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
//        long time=new Date().getTime();
//        if(time-SharedPreferenceManager.getDataUpdateTime(getApplicationContext())>1000*60*60*5){
//            getDBDataLastUpdateTime();
//        }
    }

    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
          try {
               if(Build.VERSION.SDK_INT>19) {
                 hud = KProgressHUD.create(this);
               }else {
               loadingDialog = new LoadingDialog(this);
               }
          }catch (Throwable throwable){
              CrashReport.postCatchedException(throwable);
          }
    }
    protected SqliteStore getSqliteStore() {
        return ((AppApplication) getApplication()).getSqliteStore();
    }

    protected Operator getLoginInfo(){
        Operator operator = null;
        String userData= SharedPreferenceManager.getLoginData(this);
        if(StringUtils.isNotBlank(userData)){
            try {
                operator = new Operator();
                JsonObjectElement json = new JsonObjectElement(userData);
                operator.setId(Long.valueOf(DataUtil.isDataElementNull(json.get("Operator_ID"))));
                operator.setTeamId(DataUtil.isDataElementNull(json.get("Team_ID")));
                operator.setTeamName(DataUtil.isDataElementNull(json.get("TeamName")));
                operator.setName(DataUtil.isDataElementNull(json.get("Name")));
                operator.setMaintenMan(json.get("IsMaintenMan").valueAsBoolean());
               // operator.setUserRole_ID(json.get("UserRole_ID").valueAsInt());
                operator.setOrganiseID(DataUtil.isDataElementNull(json.get("Organise_ID")));
                operator.setOperator_no(DataUtil.isDataElementNull(json.get("OperatorNo")));
//                operator = Operator.fromJson(userData, null, Operator.class);
//                JsonObjectElement objectElement=new JsonObjectElement(SharedPreferenceManager.getMsg(this));
//                operator.setUserRole_ID(objectElement.get("UserRole_ID").valueAsInt());
//                operator.setModuleList(DataUtil.isDataElementNull(objectElement.get("AppInterfaceList")));
            }catch (Exception e){
                CrashReport.postCatchedException(e);
            }
        }
        return operator;
    }
    protected Operator getLoginMsg(){
        Operator operator = null;
        String userData= SharedPreferenceManager.getMsg(this);
        if(StringUtils.isNotBlank(userData)){
            try {
                operator = new Operator();
                JsonObjectElement json = new JsonObjectElement(userData);
                operator.setUserRole_ID(json.get("UserRole_ID").valueAsInt());
                //operator.setModuleList(json.get("AppInterfaceList").valueAsString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return operator;
    }

    protected Operator getLoginInfo(String data){
        Operator operator = null;
        if(StringUtils.isNotBlank(data)){
            try {
                operator = new Operator();
                JsonObjectElement json = new JsonObjectElement(data);
                operator.setId(Long.valueOf(json.get("Operator_ID").valueAsString()));
                operator.setTeamId(json.get("Team_ID").valueAsString());
                operator.setTeamName(json.get("TeamName").valueAsString());
                operator.setName(json.get("Name").valueAsString());
                //operator.setUserRole_ID(json.get("UserRole_ID").valueAsInt());
                operator.setMaintenMan(json.get("IsMaintenMan").valueAsBoolean());
                operator.setOrganiseID(json.get("Organise_ID").valueAsString());
                operator.setOperator_no(json.get("OperatorNo").valueAsString());
//              operator = Operator.fromJson(userData, null, Operator.class);
//                JsonObjectElement objectElement=new JsonObjectElement(SharedPreferenceManager.getMsg(this));
//                operator.setUserRole_ID(objectElement.get("UserRole_ID").valueAsInt());
//                operator.setModuleList(objectElement.get("AppInterfaceList").valueAsString());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return operator;
    }
    public KProgressHUD initCustomDialog(int resId) {
         hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getResources().getString(resId))
                .setCancellable(true);
        return  hud;
    }
    public void showCustomDialog(int resId){
        if(Build.VERSION.SDK_INT>19) {
            initCustomDialog(resId);
            if (hud != null && !hud.isShowing() && !isFinishing()) {
                hud.show();
            }
        }else {
            if (loadingDialog != null && !loadingDialog.isShowing()  && !isFinishing()) {
                loadingDialog.show();
            }
        }
    }
    public void dismissCustomDialog(){
        if(Build.VERSION.SDK_INT>19) {
            if (hud != null && hud.isShowing() && !isFinishing()) {
                hud.dismiss();
            }
        }else {
            if (loadingDialog != null && loadingDialog.isShowing()  && !isFinishing()) {
                loadingDialog.dismiss();
            }
        }
    }
    public void getDataBaseUpdateFromServer(DataElement dataElement){

        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set("LastUpdateTime_BaseOrganise", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_BaseOrganise")));
        data.set("LastUpdateTime_DataDictionary",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataDictionary")));
        data.set("LastUpdateTime_DataType",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataType")));
        data.set("LastUpdateTime_Equipment",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Equipment")));
        data.set("LastUpdateTime_TaskOrganiseRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskOrganiseRelation")));
        data.set("LastUpdateTime_DataRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataRelation")));
        data.set("LastUpdateTime_Language_Translation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Language_Translation")));
        data.set("LastUpdateTime_Languages",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Languages")));
        data.set("LastUpdateTime_TaskMessage",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskMessage")));
        if(SharedPreferenceManager.getFactory(this)==null){
            data.set("Factory_ID","GEW");}
        else {
            data.set("Factory_ID",SharedPreferenceManager.getFactory(this));
        }
        // data.set("IsNewApp",1);
        // params.put("sqlLiteDBModel",data.toJson());
        params.putJsonParams(data.toJson());
        showCustomDialog(R.string.loadingData);
        HttpUtils.postWithoutCookie(this, "SqlToSqliteAPI/DBIncrementGet", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                SharedPreferenceManager.setDataUpdateTime(getApplicationContext(),new Date().getTime());
                /*    JsonParser jsonParser=new JsonParser();
                    JsonObject j=jsonParser.parse(t).getAsJsonObject();
                    JsonArray jsonArray=j.getAsJsonArray("DataType");
                    if(jsonArray!=null){
                        Log.e("dd","bb");
                    }*/

                    try {
                        JsonObjectElement json = new JsonObjectElement(t);
                        if (json.get("DataType") != null && json.get("DataType").isArray() && json.get("DataType").asArrayElement().size() > 0) {
                            Log.e("DataType",String.valueOf(json.get("DataType").asArrayElement().size()));
                            updateData(json.get("DataType"), EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE);
                        }
                        if (json.get("BaseOrganise") != null && json.get("BaseOrganise").isArray() && json.get("BaseOrganise").asArrayElement().size() > 0) {
                            Log.e("BaseOrganise",String.valueOf(json.get("BaseOrganise").asArrayElement().size()));
                            updateData(json.get("BaseOrganise"), EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE);
                        }
                        if (json.get("DataDictionary") != null && json.get("DataDictionary").isArray() && json.get("DataDictionary").asArrayElement().size() > 0) {
                            Log.e("DataDictionary",String.valueOf(json.get("DataDictionary").asArrayElement().size()));
                            updateData(json.get("DataDictionary"), EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY);
                        }
                        if (json.get("Equipment") != null && json.get("Equipment").isArray() && json.get("Equipment").asArrayElement().size() > 0) {
                            Log.e("Equipment",String.valueOf(json.get("Equipment").asArrayElement().size()));
                            updateData(json.get("Equipment"), EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT);
                        }
                        if (json.get("TaskOrganiseRelation") != null && json.get("TaskOrganiseRelation").isArray() && json.get("TaskOrganiseRelation").asArrayElement().size() > 0) {
                            Log.e("TaskOrganiseRelation",String.valueOf(json.get("TaskOrganiseRelation").asArrayElement().size()));
                            updateData(json.get("TaskOrganiseRelation"), EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION);
                        }
                        if (json.get("DataRelation") != null && json.get("DataRelation").isArray() && json.get("DataRelation").asArrayElement().size() > 0) {
                            Log.e("DataRelation",String.valueOf(json.get("DataRelation").asArrayElement().size()));
                            updateData(json.get("DataRelation"), EPassSqliteStoreOpenHelper.SCHEMA_DATA_RELATION);
                        }
                        if (json.get("Languages") != null && json.get("Languages").isArray() && json.get("Languages").asArrayElement().size() > 0) {
                            Log.e("Languages",String.valueOf(json.get("Languages").asArrayElement().size()));
                            updateData(json.get("Languages"), EPassSqliteStoreOpenHelper.SCHEMA_LANGUAGES);
                        }
                        if (json.get("Language_Translation") != null && json.get("Language_Translation").isArray() && json.get("Language_Translation").asArrayElement().size() > 0) {
                            Log.e("Language_Translation",String.valueOf(json.get("Language_Translation").asArrayElement().size()));
                            updateData(json.get("Language_Translation"), EPassSqliteStoreOpenHelper.SCHEMA_LANGUAGE_TRANSLATION);
                        }
                        if (json.get("TaskMessage") != null && json.get("TaskMessage").isArray() && json.get("TaskMessage").asArrayElement().size() > 0) {
                            Log.e("TaskMessage",String.valueOf(json.get("TaskMessage").asArrayElement().size()));
                            updateData(json.get("TaskMessage"), EPassSqliteStoreOpenHelper.SCHEMA_TASK_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        dismissCustomDialog();
                    }
                }
                // ConfigurationManager.getInstance().startToGetNewConfig(mContext);
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }
    public void updateData(DataElement data,String resource){
        getSqliteStore().createElement(data, resource, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                Log.e("Success","successSave");
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                Log.e("Fail","FailSave");
            }
        });
        if(data!=null&&data.isArray()){
            String s="";
            switch (resource){
                case "DataDictionary":{
                    s= DataDictionary.DATA_ID;
                    break;
                }
                case "Equipment":{
                    s= Equipment.EQUIPMENT_ID;
                    break;
                }
                case "BaseOrganise":{
                    s= BaseOrganise.ORGANISE_ID;
                    break;
                }
                case "DataType":{
                    s= DataType.DATATYPE_ID;
                    break;
                }
                case "TaskOrganiseRelation":{
                    s= TaskOrganiseRelation.TEAM_SERVICE_ID;
                    break;
                }
                case "DataRelation":{
                    s= DataRelation.DATARELATION_ID;
                    break;
                }
                case "Languages":{
                    s= Languages.LANGUAGE_ID;
                    break;
                }
                case "Language_Translation":{
                    s= Language_Translation.TRANSLATION_ID;
                    break;
                }
                case "TaskMessage":{
                    s= TaskMessage.MESSAGE_ID;
                    break;
                }
            }
            for(int i=0;i<data.asArrayElement().size();i++){
//           if(s==DataDictionary.DATA_ID){
//               ObjectElement objectElement=data.asArrayElement().get(i).asObjectElement();
//               String sql="UPDATE DATADICTIONARY SET "
//                       + DataDictionary.FACTORY_ID+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.FACTORY_ID))
//                       +"',"+DataDictionary.PDATA_ID+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.PDATA_ID))
//                       +"',"+DataDictionary.DATA_TYPE+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_TYPE))
//                       +"',"+DataDictionary.DATA_NAME+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_NAME))
//                       +"',"+DataDictionary.DATA_DESCR+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_DESCR))
//                       +"',"+DataDictionary.DATA_VALUE1+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_VALUE1))
//                       +"',"+DataDictionary.DATA_VALUE2+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_VALUE2))
//                       +"',"+DataDictionary.DATA_VALUE3+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_VALUE3))
//                       +"',"+DataDictionary.LASTUPDATETIME+"='"+DataUtil.isDataElementNull(objectElement.get(DataDictionary.LASTUPDATETIME))
//                       +"' WHERE "+DataDictionary.DATA_ID+"="+DataUtil.isDataElementNull(objectElement.get(DataDictionary.DATA_ID));
//               getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
//                   @Override
//                   public void success(DataElement element, String resource) {
//                       Log.e("","");
//                   }
//
//                   @Override
//                   public void failure(DatastoreException ex, String resource) {
//                     Log.e("","");
//                   }
//               });
//           }else {
                getSqliteStore().updateElement(DataUtil.isDataElementNull(data.asArrayElement().get(i).asObjectElement().get(s)),
                        data.asArrayElement().get(i), resource, new StoreCallback() {
                            @Override
                            public void success(DataElement element, String resource) {
                                Log.e("SuccessUpdate", "SuccessUpdate");
                            }

                            @Override
                            public void failure(DatastoreException ex, String resource) {
                                Log.e("FailUpdate", "FailUpdate");
                            }
                        });
                //}
            }
        }

      /*  for(int i=0;i<data.asArrayElement().size();i++) {
            getSqliteStore().updateElements(new Query(), data.asArrayElement().get(i),resource, new StoreCallback() {
                @Override
                public void success(DataElement element, String resource) {
                    Log.e("Success","Success");
                }

                @Override
                public void failure(DatastoreException ex, String resource) {
                    Log.e(ex.toString(),resource.toString());
                }
            });
        }*/

    }

    public void getDBDataLastUpdateTime(){
        String sql="    select * from ( select max(LastUpdateTime) LastUpdateTime_BaseOrganise from BaseOrganise)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataDictionary from DataDictionary)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataType from DataType)," +
                "    (select max(LastUpdateTime) LastUpdateTime_Equipment from Equipment)," +
                "    (select max(LastUpdateTime) LastUpdateTime_TaskOrganiseRelation from TaskOrganiseRelation),"+
                "    (select max(LastUpdateTime) LastUpdateTime_DataRelation from DataRelation), "+
                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_Languages from Languages), "+
                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_Language_Translation from Language_Translation),"+
                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_TaskMessage from TaskMessage)";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(final DataElement element, String resource) {
                ConfigurationManager.getInstance().startToGetNewConfig(BaseActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDataBaseUpdateFromServer(element.asArrayElement().get(0));
                    }
                });
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                ConfigurationManager.getInstance().startToGetNewConfig(BaseActivity.this);
            }
        });
    }
}
