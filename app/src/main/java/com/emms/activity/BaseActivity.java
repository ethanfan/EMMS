package com.emms.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.DataDictionary;
import com.emms.schema.DataRelation;
import com.emms.schema.DataType;
import com.emms.schema.Equipment;
import com.emms.schema.Factory;
import com.emms.schema.Language_Translation;
import com.emms.schema.Languages;
import com.emms.schema.Operator;
import com.emms.schema.System_FunctionSetting;
import com.emms.schema.TaskOrganiseRelation;
import com.emms.ui.KProgressHUD;
import com.emms.ui.LoadingDialog;
import com.emms.util.BuildConfig;
import com.emms.util.DataUtil;
import com.emms.util.DownloadCallback;
import com.emms.util.NetworkUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;
import org.apache.commons.lang.StringUtils;
import java.io.File;
import java.util.Date;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/17.
 *
 */
public abstract class BaseActivity extends AppCompatActivity {
    private KProgressHUD hud;
    private LoadingDialog loadingDialog;
    private Handler mHandler;
    private Context mContext=this;
    public final int DBVersion=7;//需要进行DB更新的时候+1
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
          try {
              mHandler=new Handler(getMainLooper());
               if(Build.VERSION.SDK_INT>14) {
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
                operator.setFromFactory(DataUtil.isDataElementNull(json.get("FromFactory")));
                operator.setOrganiseID(DataUtil.isDataElementNull(json.get("Organise_ID")));
                operator.setOperator_no(DataUtil.isDataElementNull(json.get("OperatorNo")));
                operator.setMaintenMan(json.get("IsMaintenMan").valueAsBoolean());
               // operator.setUserRole_ID(json.get("UserRole_ID").valueAsInt());
//                operator = Operator.fromJson(userData, null, Operator.class);
//                JsonObjectElement objectElement=new JsonObjectElement(SharedPreferenceManager.getMsg(this));
//                operator.setUserRole_ID(objectElement.get("UserRole_ID").valueAsInt());
//                operator.setModuleList(DataUtil.isDataElementNull(objectElement.get("AppInterfaceList")));
            }catch (Exception e){
                //Do nothing
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
        if(Build.VERSION.SDK_INT>14) {
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
        if(Build.VERSION.SDK_INT>14) {
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
        //data.set("LastUpdateTime_Language_Translation","0x000000000003568F");//用于测试大量数据情况下数据更新效率
        data.set("LastUpdateTime_Languages",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Languages")));
        data.set("LastUpdateTime_System_FunctionSetting",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_System_FunctionSetting")));
        //data.set("LastUpdateTime_TaskMessage",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskMessage")));
        if(SharedPreferenceManager.getFactory(this)==null){
            data.set("Factory_ID", Factory.FACTORY_GEW);}
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
                    int Count=0;
                    try {
                        JsonObjectElement json = new JsonObjectElement(t);
                        if(DataUtil.isInt(DataUtil.isDataElementNull(json.get("Count")))) {
                            if(json.get("Count")!=null) {
                                Count = json.get("Count").valueAsInt();
                            }
                        }
                        if (json.get("DataType") != null && json.get("DataType").isArray() && json.get("DataType").asArrayElement().size() > 0) {
                            Log.e("DataType",String.valueOf(json.get("DataType").asArrayElement().size()));
                            updateData(json.get("DataType"), EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE,DataType.DATATYPE_ID);
                           // DoUpdate(0,DataType.DATATYPE_ID,json.get("DataType"),EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE);
                        }
                        if (json.get("BaseOrganise") != null && json.get("BaseOrganise").isArray() && json.get("BaseOrganise").asArrayElement().size() > 0) {
                            Log.e("BaseOrganise",String.valueOf(json.get("BaseOrganise").asArrayElement().size()));
                            updateData(json.get("BaseOrganise"), EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE,BaseOrganise.ORGANISE_ID);
                            //DoUpdate(0,BaseOrganise.ORGANISE_ID,json.get("BaseOrganise"),EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE);
                        }
                        if (json.get("DataDictionary") != null && json.get("DataDictionary").isArray() && json.get("DataDictionary").asArrayElement().size() > 0) {
                            Log.e("DataDictionary",String.valueOf(json.get("DataDictionary").asArrayElement().size()));
                            updateData(json.get("DataDictionary"), EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY,DataDictionary.DATA_ID);
                            //DoUpdate(0,DataDictionary.DATA_ID,json.get("DataDictionary"),EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY);
                        }
                        if (json.get("Equipment") != null && json.get("Equipment").isArray() && json.get("Equipment").asArrayElement().size() > 0) {
                            Log.e("Equipment",String.valueOf(json.get("Equipment").asArrayElement().size()));
                            updateData(json.get("Equipment"), EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT,Equipment.EQUIPMENT_ID);
                            //DoUpdate(0,Equipment.EQUIPMENT_ID,json.get("Equipment"),EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT);
                        }
                        if (json.get("TaskOrganiseRelation") != null && json.get("TaskOrganiseRelation").isArray() && json.get("TaskOrganiseRelation").asArrayElement().size() > 0) {
                            Log.e("TaskOrganiseRelation",String.valueOf(json.get("TaskOrganiseRelation").asArrayElement().size()));
                            updateData(json.get("TaskOrganiseRelation"), EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION,TaskOrganiseRelation.TEAM_SERVICE_ID);
                            //DoUpdate(0,TaskOrganiseRelation.TEAM_SERVICE_ID,json.get("TaskOrganiseRelation"),EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION);
                        }
                        if (json.get("DataRelation") != null && json.get("DataRelation").isArray() && json.get("DataRelation").asArrayElement().size() > 0) {
                            Log.e("DataRelation",String.valueOf(json.get("DataRelation").asArrayElement().size()));
                            updateData(json.get("DataRelation"), EPassSqliteStoreOpenHelper.SCHEMA_DATA_RELATION,DataRelation.DATARELATION_ID);
                            //DoUpdate(0,DataRelation.DATARELATION_ID,json.get("DataRelation"),EPassSqliteStoreOpenHelper.SCHEMA_DATA_RELATION);
                        }
                        if (json.get("Languages") != null && json.get("Languages").isArray() && json.get("Languages").asArrayElement().size() > 0) {
                            Log.e("Languages",String.valueOf(json.get("Languages").asArrayElement().size()));
                            updateData(json.get("Languages"), EPassSqliteStoreOpenHelper.SCHEMA_LANGUAGES,Languages.LANGUAGE_ID);
                        }
                        if (json.get("Language_Translation") != null && json.get("Language_Translation").isArray() && json.get("Language_Translation").asArrayElement().size() > 0) {
                            Log.e("Language_Translation",String.valueOf(json.get("Language_Translation").asArrayElement().size()));
                            updateData(json.get("Language_Translation"), EPassSqliteStoreOpenHelper.SCHEMA_LANGUAGE_TRANSLATION,Language_Translation.TRANSLATION_ID);
                        }
                        if (json.get("System_FunctionSetting") != null && json.get("System_FunctionSetting").isArray() && json.get("System_FunctionSetting").asArrayElement().size() > 0) {
                            Log.e("System_FunctionSetting",String.valueOf(json.get("System_FunctionSetting").asArrayElement().size()));
                            updateData(json.get("System_FunctionSetting"), EPassSqliteStoreOpenHelper.SCHEMA_SYSTEM_FUNCTION_SETTING, System_FunctionSetting.FUNCTION_ID);
                        }

                    } catch (Exception e) {
                        CrashReport.postCatchedException(e);
                    }
                    finally {
                        try {
                            RunDelay(Count/14);
                            if(Count>500) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToastLong(R.string.DataTooMorePleaseWait, mContext);
                                    }
                                });
                            }
                        }catch (Exception e){
                            CrashReport.postCatchedException(e);
                        }
                    }
                }else {
                    dismissCustomDialog();
                }
                // ConfigurationManager.getInstance().startToGetNewConfig(mContext);
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }
    public void updateData(final DataElement data, String resource, final String key){
        getSqliteStore().createElement(data, resource, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                Log.e("Success","successSave");
                DoUpdate(0,key,data,resource);
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                Log.e("Fail","FailSave");
                DoUpdate(0,key,data,resource);
            }
        });
//        if(data!=null&&data.isArray()){
//            String s="";
//            switch (resource){
//                case "DataDictionary":{
//                    s= DataDictionary.DATA_ID;
//                    break;
//                }
//                case "Equipment":{
//                    s= Equipment.EQUIPMENT_ID;
//                    break;
//                }
//                case "BaseOrganise":{
//                    s= BaseOrganise.ORGANISE_ID;
//                    break;
//                }
//                case "DataType":{
//                    s= DataType.DATATYPE_ID;
//                    break;
//                }
//                case "TaskOrganiseRelation":{
//                    s= TaskOrganiseRelation.TEAM_SERVICE_ID;
//                    break;
//                }
//                case "DataRelation":{
//                    s= DataRelation.DATARELATION_ID;
//                    break;
//                }
//                case "Languages":{
//                    s= Languages.LANGUAGE_ID;
//                    break;
//                }
//                case "Language_Translation":{
//                    s= Language_Translation.TRANSLATION_ID;
//                    break;
//                }
//                case "TaskMessage":{
//                    s= TaskMessage.MESSAGE_ID;
//                    break;
//                }
//            }

//            for(int i=0;i<data.asArrayElement().size();i++){
//                getSqliteStore().updateElement(DataUtil.isDataElementNull(data.asArrayElement().get(i).asObjectElement().get(s)),
//                        data.asArrayElement().get(i), resource, new StoreCallback() {
//                            @Override
//                            public void success(DataElement element, String resource) {
//                                Log.e("SuccessUpdate", "SuccessUpdate");
//                            }
//
//                            @Override
//                            public void failure(DatastoreException ex, String resource) {
//                                Log.e("FailUpdate", "FailUpdate");
//                            }
//                        });
//                //}
//            }
//        }

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
                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_Language_Translation from Language_Translation), "+
                "    (select ifnull(max(LastUpdateTime),'0x0000000000000000') LastUpdateTime_System_FunctionSetting from System_FunctionSetting)";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(final DataElement element, String resource) {
                //ConfigurationManager.getInstance().startToGetNewConfig(BaseActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDataBaseUpdateFromServer(element.asArrayElement().get(0));
                    }
                });
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                //ConfigurationManager.getInstance().startToGetNewConfig(BaseActivity.this);
            }
        });
    }
    private void DoUpdate(final  int i,final String key, final DataElement data,final String resource){
        try {
            if (i<data.asArrayElement().size()&&data.asArrayElement().get(i) != null) {
                getSqliteStore().updateElement(DataUtil.isDataElementNull(data.asArrayElement().get(i).asObjectElement().get(key)),
                        data.asArrayElement().get(i), resource, new StoreCallback() {
                            @Override
                            public void success(DataElement element, String resource) {
                                Log.e("SuccessUpdate", "SuccessUpdate");
                                DoUpdate(i + 1, key, data, resource);
                            }

                            @Override
                            public void failure(DatastoreException ex, String resource) {
                                Log.e("FailUpdate", "FailUpdate");
                                DoUpdate(i + 1, key, data, resource);
                            }
                        });
            }
        }catch (Exception e){
             CrashReport.postCatchedException(e);
        }
    }
    private void RunDelay(int DelayTime){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissCustomDialog();
            }
        },DelayTime*1000);
    }
    public void getDBFromServer(final File dbFile){
        // 下载Db文件
        showCustomDialog(R.string.DownloadDataBase);
        HttpParams params=new HttpParams();
        params.put("factory",SharedPreferenceManager.getFactory(this)==null?Factory.FACTORY_GEW:SharedPreferenceManager.getFactory(this));
        HttpUtils.getWithoutCookies(this, "SqlToSqliteAPI/GetDBDownloadUrl", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t.trim());
                    downloadDB(dbFile,DataUtil.isDataElementNull(jsonObjectElement.get("DownloadUrl")));
                }else {
                    dismissCustomDialog();
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                showErrorDownloadDatabaseDialog(strMsg);
                dismissCustomDialog();
            }
        });
    }
    public void downloadDB(final File dbFile,String url){
        HttpUtils.download(this, dbFile.getAbsolutePath(), url, null, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try{
                    com.emms.util.FileUtils fileUtil=new com.emms.util.FileUtils();
                    fileUtil.upZipFile(dbFile, dbFile.getParentFile().getAbsolutePath(), mContext, new DownloadCallback() {
                        @Override
                        public void success(boolean hasUpdate) {
                            SharedPreferenceManager.setDatabaseVersion(mContext,String.valueOf(DBVersion));
                            getDBDataLastUpdateTime();
                        }
                        @Override
                        public void fail(Exception e) {
                        }
                    });
                    ToastUtil.showToastShort(R.string.SuccessDownloadDB,mContext);
                }catch (Exception e){
                    showErrorDownloadDatabaseDialog(null);
                }finally {
                    dismissCustomDialog();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                if(dbFile.exists()) {
                    if (dbFile.delete()) {
                        showErrorDownloadDatabaseDialog(strMsg);
                    }
                }else {
                    showErrorDownloadDatabaseDialog(strMsg);
                }
                //ToastUtil.showToastShort(R.string.loadingFail,mContext);
                dismissCustomDialog();
            }
        });
    }
    private AlertDialog alertDialog;
    public void showErrorDownloadDatabaseDialog(String msg) {
        try {
            if (alertDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if(msg==null) {
                    builder.setMessage(R.string.FailDownloadDB);
                }else {
                    builder.setMessage(getString(R.string.FailDownloadDB)+"\n"+msg);
                }
                builder.setPositiveButton(R.string.retry,
                        new DialogInterface.OnClickListener() {
                            @SuppressWarnings("ResultOfMethodCallIgnored")
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File dbFile=getDBZipFile(BuildConfig.isDebug);
                                if(dbFile.exists()){
                                    dbFile.delete();
                                }
                                final File db = new File(getExternalFilesDir(null), "/EMMS.db");
                                if(db.exists()){
                                    db.delete();
                                }
                                getDBFromServer(dbFile);
                            }
                        });
                builder.setCancelable(false);
                alertDialog = builder.create();
            }
            if (alertDialog != null && !alertDialog.isShowing()) {
                alertDialog.show();
            }
        }catch (Exception e){
            CrashReport.postCatchedException(e);
        }
    }
    public void getNewDataFromServer() {
        //检测数据库文件是否已经存在，若已存在，则调用增量接口
        final File db = new File(getExternalFilesDir(null), "/EMMS.db");
//        final File dbZip;
//        if(BuildConfig.isDebug){
//            dbZip=new File(getExternalFilesDir(null), "/EMMS_TEST_"+SharedPreferenceManager.getFactory(this)+".zip");
//        }else {
//            dbZip = new File(getExternalFilesDir(null), "/EMMS_"+SharedPreferenceManager.getFactory(this)+".zip");
//        }
        if(db.exists()){
            getDBDataLastUpdateTime();
            return;
        }
        if(BuildConfig.isDebug){
            final File dbFile = getDBZipFile(BuildConfig.isDebug);
            if(dbFile.exists()){
                try{
                    //解压db文件
                    HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
                }catch (Throwable e){
                    CrashReport.postCatchedException(e);
                    if(dbFile.exists()&&dbFile.delete()){
                        showErrorDownloadDatabaseDialog(null);
                    }
                    ToastUtil.showToastLong(R.string.FailToUnZipDB,mContext);
                }
                return;
            }
            getDBFromServer(dbFile);
        }else {
            final File dbFile = getDBZipFile(BuildConfig.isDebug);
            if(dbFile.exists()){
                try{
                    //解压db文件
                    HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
                }catch (Throwable e){
                    CrashReport.postCatchedException(e);
                    if(dbFile.exists()&&dbFile.delete()){
                        showErrorDownloadDatabaseDialog(null);
                    }
                    ToastUtil.showToastLong(R.string.FailToUnZipDB,mContext);
                }
                return;
            }
            getDBFromServer(dbFile);
        }
    }
//    private void RunDelayToast(int DelayTime){
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ToastUtil.showToastLong(R.string.DataTooMorePleaseWait,mContext);
//            }
//        },DelayTime*1000);
//    }
    public void initNetWorklist(StoreCallback storeCallback){
            String Factory = SharedPreferenceManager.getFactory(mContext)==null?"EGM":SharedPreferenceManager.getFactory(mContext);
            String sql = "select * from DataDictionary where DataType='NetSetting' and DataDescr='intranet' and Factory_ID='" + Factory + "'";
            getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, storeCallback);
    }
    public void ChangeServerConnectBaseOnNetwork(){
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
//        Toast.makeText(context, "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()
//                +"\n"+"active:"+activeInfo.getTypeName(), 1).show();
        if(activeInfo==null){
            return;
        }
        if( activeInfo.getType()==ConnectivityManager.TYPE_MOBILE &&
             mobileInfo.isConnected() ){
            ToastUtil.showToastLong(R.string.CheckForMONET,mContext);
            SharedPreferenceManager.setNetwork(mContext.getApplicationContext(), NetworkUtils.initNetWork(false));
            BuildConfig.NetWorkSetting(mContext.getApplicationContext());
            return;
        }
        if(activeInfo.getType()==ConnectivityManager.TYPE_WIFI &&
                wifiInfo.isConnected()){
            NetworkUtils.DoNetworkChange(mContext);
            return;
        }
        BuildConfig.NetWorkSetting(mContext);
    }
    public File getDBZipFile(boolean isDebug){
        if(isDebug){
            return new File(getExternalFilesDir(null), "/EMMS_TEST_"+SharedPreferenceManager.getFactory(mContext)+".zip");
        }
        else {
            return new File(getExternalFilesDir(null), "/EMMS_"+SharedPreferenceManager.getFactory(mContext)+".zip");
        }
    }
}
