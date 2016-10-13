package com.emms.activity;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.toolbox.Loger;
import com.emms.ConfigurationManager;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.push.ExampleUtil;
import com.emms.push.PushService;
import com.emms.schema.BaseOrganise;
import com.emms.schema.DataDictionary;
import com.emms.schema.DataRelation;
import com.emms.schema.DataType;
import com.emms.schema.Equipment;
import com.emms.schema.Operator;
import com.emms.schema.TaskOrganiseRelation;
import com.emms.ui.KProgressHUD;
import com.emms.ui.PopMenuLoginActivity;
import com.emms.ui.UserRoleDialog;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.DownloadCallback;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends NfcActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    //private TextView machine;
    private EditText inputPassWord;
    private EditText inputname;
    private KProgressHUD hud;
    private String def_Factory="GEW";
    //private ImageButton setting;
    private Handler pushHandler = PushService.mHandler;
    private Handler handle=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        if(SharedPreferenceManager.getFactory(this)==null){
            SharedPreferenceManager.setFactory(this,def_Factory);
        }

        //init();
        initView();
        //  registerMessageReceiver();
       if(!getIntent().getBooleanExtra("FromCusActivity",false)) {
           getNewDataFromServer();
       }
        if (!mAdapter.isEnabled()) {
            showWirelessSettingsDialog();
        }
    }

    private void initView() {
        TextView login = (TextView) findViewById(R.id.login);
        inputPassWord = (EditText) findViewById(R.id.inputPassWord);
        inputname = (EditText) findViewById(R.id.inputUserName);
      inputname.setText(SharedPreferenceManager.getUserName(this));
      inputPassWord.setText(SharedPreferenceManager.getPassWord(this));
        login.setOnClickListener(this);
//        machine.setOnClickListener(this);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(getResources().getString(R.string.logining))
                .setCancellable(true);
        findViewById(R.id.systemSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,SystemSettingActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
//        ConfigurationManager.getInstance().startToGetNewConfig(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //点击登录按钮，根据账号密码进行登录
                final String userid = inputname.getText().toString().toUpperCase();
                final String password = inputPassWord.getText().toString().toUpperCase();
                if (!hasNetworkConnection()) {
//                    showDialog(getString(R.string.warning_title),
//                            getString(R.string.network_error));
                    ToastUtil.showToastLong(R.string.network_error,mContext);
                    return;
                }

                if (userid.length() == 0) {
//                    showDialog(getString(R.string.warning_title),
//                            getString(R.string.warning_message_no_user));
                    ToastUtil.showToastLong(R.string.warning_message_no_user,mContext);
                    return;
                }
                if (password.length() == 0) {
//                    showDialog(getString(R.string.warning_title),
//                            getString(R.string.warning_message_no_password));
                    ToastUtil.showToastLong(R.string.warning_message_no_password,mContext);
                    return;
                }
                hud.show();
                HttpUtils.login(LoginActivity.this,userid, password, new HttpCallback() {

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Loger.debug(errorNo + ":strMsg");
                        hud.dismiss();
                        Toast.makeText(mContext,
                                getString(R.string.network_error),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);
                        SaveCookies(headers);
                    }

                    @Override
                    public void onSuccess(String t) {
                        super.onSuccess(t);
                        hud.dismiss();
                        SharedPreferenceManager.setUserName(LoginActivity.this, userid);
                        SharedPreferenceManager.setPassWord(LoginActivity.this, password);
                        LoginSuccessEvent(t,true);
                    }
                });
                break;
//            case R.id.machine:
//                Intent intentMachine = new Intent(LoginActivity.this, MachineActivity.class);
//                startActivity(intentMachine);
//                break;
        }
    }


    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton(R.string.warning_message_confirm, null);
        builder.show();
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        NetworkInfo.State network = info.getState();
        return network == NetworkInfo.State.CONNECTED;
    }

    /**
     * 保存Cookie
     */
    public void SaveCookies( Map<String, String> headers)
    {
        //保存登录信息Cookies
        if (headers == null)
            return;
            String cookie=headers.get("Set-Cookie");
        if(cookie!=null) {
            String[] cookies = cookie.split(";");
            // String[] cookievalues = cookies[0].split("=");
            SharedPreferenceManager.setCookie(LoginActivity.this, cookies[0]);
        }

    }
    //下载DB文件
    private void getNewDataFromServer() {
       // final File db = new File(getExternalFilesDir(null), "/EMMS"+SharedPreferenceManager.getFactory(this)+".db");
        //检测数据库文件是否已经存在，若已存在，则调用增量接口
        final File db = new File(getExternalFilesDir(null), "/EMMS.db");
        if(db.exists()){
            //getDataBaseUpdateFromServer();
           // getDBFromServer();
            getDBDataLastUpdateTime();
            return;
        }
       final File dbFile = new File(getExternalFilesDir(null), "/EMMS.zip");
           if(dbFile.exists()){
               try{
                   //解压db文件
                   HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);}catch (Exception e){
               }
             return;
          }
        getDBFromServer(dbFile);

       // String savepath = dbFile.getParentFile().getAbsolutePath();
      /*  new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                HttpUtils.downloadData(dbFile, "",LoginActivity.this);
                Looper.loop();
            }
        }).start();*/
      //  HttpUtils.downloadData(dbFile, "");
  /*      final RelativeLayout progress=(RelativeLayout)findViewById(R.id.downloadProgress);
        progress.setVisibility(View.VISIBLE);
        HttpUtils.download(LoginActivity.this, savepath, BuildConfig.getConfigurationDownload(), new ProgressListener() {
            @Override
            public void onProgress(long transferredBytes, long totalSize) {
                int a=(int)totalSize;
                ((ProgressBar)findViewById(R.id.progress)).setProgress((int)transferredBytes/(int)totalSize);
                if(transferredBytes>=totalSize){
                    progress.setVisibility(View.GONE);
                }
            }
        }, new HttpCallback() {
            @Override
            public void onSuccessInAsync(byte[] t) {
                super.onSuccessInAsync(t);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
            }

            @Override
            public void onFailure(VolleyError error) {
                super.onFailure(error);
            }
        });
    }*/
    }
    private void getDataBaseUpdateFromServer(DataElement dataElement){

        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set("LastUpdateTime_BaseOrganise", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_BaseOrganise")));
        data.set("LastUpdateTime_DataDictionary",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataDictionary")));
        data.set("LastUpdateTime_DataType",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataType")));
        data.set("LastUpdateTime_Equipment",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Equipment")));
        data.set("LastUpdateTime_TaskOrganiseRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskOrganiseRelation")));
        data.set("LastUpdateTime_DataRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataRelation")));
        if(SharedPreferenceManager.getFactory(this)==null){
        data.set("Factory_ID",def_Factory);}
        else {
            data.set("Factory_ID",SharedPreferenceManager.getFactory(this));
        }
       // data.set("IsNewApp",1);
       // params.put("sqlLiteDBModel",data.toJson());
        params.putJsonParams(data.toJson());
        HttpUtils.postWithoutCookie(this, "SqlToSqlite", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                /*    JsonParser jsonParser=new JsonParser();
                    JsonObject j=jsonParser.parse(t).getAsJsonObject();
                    JsonArray jsonArray=j.getAsJsonArray("DataType");
                    if(jsonArray!=null){
                        Log.e("dd","bb");
                    }*/
                JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get("DataType")!=null&&json.get("DataType").isArray()&&json.get("DataType").asArrayElement().size()>0){
                    updateData(json.get("DataType"), EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE);}
                    if(json.get("BaseOrganise")!=null&&json.get("BaseOrganise").isArray()&&json.get("BaseOrganise").asArrayElement().size()>0){
                        updateData(json.get("BaseOrganise"),EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE);}
                    if(json.get("DataDictionary")!=null&&json.get("DataDictionary").isArray()&&json.get("DataDictionary").asArrayElement().size()>0){
                         updateData(json.get("DataDictionary"),EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY);}
                    if(json.get("Equipment")!=null&&json.get("Equipment").isArray()&&json.get("Equipment").asArrayElement().size()>0){
                        updateData(json.get("Equipment"),EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT);}
                    if(json.get("TaskOrganiseRelation")!=null&&json.get("TaskOrganiseRelation").isArray()&&json.get("TaskOrganiseRelation").asArrayElement().size()>0){
                        updateData(json.get("TaskOrganiseRelation"),EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION);}
                    if(json.get("DataRelation")!=null&&json.get("DataRelation").isArray()&&json.get("DataRelation").asArrayElement().size()>0){
                        updateData(json.get("DataRelation"),EPassSqliteStoreOpenHelper.SCHEMA_DATA_RELATION);}
                }
               // ConfigurationManager.getInstance().startToGetNewConfig(mContext);
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }
    private void updateData(DataElement data,String resource){
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
                    s=Equipment.EQUIPMENT_ID;
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
    private void ShowPopWindow(View v){
        PopMenuLoginActivity popMenuLoginActivity = new PopMenuLoginActivity(this) {

            @Override
            public void onEventDismiss() {

            }
        };
        String[] mTitles = getResources().getStringArray(R.array.menu_login);

        popMenuLoginActivity.addItems(mTitles);
        popMenuLoginActivity.showAsDropDown(v);
    }
    private void getDBDataLastUpdateTime(){
        String sql="    select * from ( select max(LastUpdateTime) LastUpdateTime_BaseOrganise from BaseOrganise)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataDictionary from DataDictionary)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataType from DataType)," +
                "    (select max(LastUpdateTime) LastUpdateTime_Equipment from Equipment)," +
                "    (select max(LastUpdateTime) LastUpdateTime_TaskOrganiseRelation from TaskOrganiseRelation),"+
                "    (select max(LastUpdateTime) LastUpdateTime_DataRelation from DataRelation) ";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                ConfigurationManager.getInstance().startToGetNewConfig(mContext);
                getDataBaseUpdateFromServer(element.asArrayElement().get(0));
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                ConfigurationManager.getInstance().startToGetNewConfig(mContext);
            }
        });
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";


    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
            if (iccardID == null) {
                return;
            } else if (iccardID.equals("")) {
                return;
            }
            //刷卡登录
            getOperatorInfoFromServer(iccardID);
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!ExampleUtil.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                setCostomMsg(showMsg.toString());
            }
        }
    }

    private EditText msgText;
    private void setCostomMsg(String msg){
        if (null != msgText) {
            msgText.setText(msg);
            msgText.setVisibility(View.VISIBLE);
        }
    }
    // 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
    private void init(){
        Log.e("aaaaaaaa",mContext.toString());
        JPushInterface.init(mContext);
    }
    private void setStyleBasic(){
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.drawable.ic_emms;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        builder.notificationDefaults = Notification.DEFAULT_ALL;
        //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
        JPushInterface.setDefaultPushNotificationBuilder(builder);
        JPushInterface.setPushNotificationBuilder(4, builder);
    }

    private void setStyleCustom(){
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(this,R.layout.customer_notitfication_layout,R.id.icon, R.id.title, R.id.text);
        builder.layoutIconDrawable = R.drawable.ic_emms;
        builder.layoutIconId=R.drawable.ic_emms;
        builder.statusBarDrawable=R.drawable.ic_emms;
//        builder.layoutIconDrawable = R.mipmap.emmsa;
//        builder.layoutIconId=R.mipmap.emmsa;
//        builder.statusBarDrawable=R.mipmap.emmsa;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
        //builder.notificationDefaults = Notification.DEFAULT_VIBRATE;
        builder.notificationDefaults = Notification.DEFAULT_ALL;
        builder.developerArg0 = "developerArg2";
        JPushInterface.setDefaultPushNotificationBuilder(builder);
        JPushInterface.setPushNotificationBuilder(2, builder);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(PushService.mMessageReceiver!=null&&PushService.isBroadcastRegister()){
     // PushService.unregisterMessageReceiver(mContext);
            }
    }
    private void getDBFromServer(final File dbFile){
        // 下载Db文件
     HttpParams params=new HttpParams();
        params.put("factory",SharedPreferenceManager.getFactory(this));
        HttpUtils.getWithoutCookies(this, "SqlToSqlite", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
              if(t!=null){
                  JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                  downloadDB(dbFile,DataUtil.isDataElementNull(jsonObjectElement.get("DownloadUrl")));

              }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);

            }
        });
    }
    private void downloadDB(final File dbFile,String url){
        showCustomDialog(R.string.DownloadDataBase);
        HttpUtils.download(this, dbFile.getAbsolutePath(), url, null, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try{
                    com.emms.util.FileUtils fileUtil=new com.emms.util.FileUtils();
                    fileUtil.upZipFile(dbFile, dbFile.getParentFile().getAbsolutePath(), mContext, new DownloadCallback() {
                        @Override
                        public void success(boolean hasUpdate) {
                            getDBDataLastUpdateTime();
                        }
                        @Override
                        public void fail(Exception e) {
                        }
                    });
                }catch (Exception e){
                    Log.e("","");
                }
                dismissCustomDialog();
                ToastUtil.showToastLong(R.string.SuccessDownloadDB,mContext);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                showErrorDownloadDatabaseDialog();
                //ToastUtil.showToastLong(R.string.loadingFail,mContext);
                dismissCustomDialog();
            }
        });
    }
    public void getOperatorInfoFromServer(String iccardID){
        showCustomDialog(R.string.logining);
        HttpParams httpParams=new HttpParams();
        httpParams.put("ICCardID",iccardID);
        HttpUtils.getWithoutCookies(this, "Token", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                LoginSuccessEvent(t,false);
            }

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                SaveCookies(headers);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast toast=Toast.makeText(mContext,R.string.scanICCardFail,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                dismissCustomDialog();
            }
        });
    }
    private void LoginSuccessEvent(String t,boolean isAccountPasswordLogin){
        if(t!=null){
            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
            if(jsonObjectElement.get("Success")!=null&&jsonObjectElement.get("Success").valueAsBoolean()){
                try {
                    JSONObject jsonObject = new JSONObject(t);
                    int code = Integer.parseInt(jsonObject.get("Result").toString());
                    //boolean isSuccess = jsonObject.get("Success").equals(true);
                    if ((code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS ||
                            code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS_AUTO) ) {
                        String Msg=jsonObject.getString("Msg");
                        SharedPreferenceManager.setMsg(LoginActivity.this,Msg);
                        String userData =jsonObject.getString("UserData");
                        SharedPreferenceManager.setUserData(LoginActivity.this, userData);
                        final String data=jsonObject.getString("Data");
                        SharedPreferenceManager.setLoginData(LoginActivity.this,data);
                        JsonObjectElement json=new JsonObjectElement(Msg);
                        final ArrayElement arrayElement=json.get("UserRoles").asArrayElement();
                        if(arrayElement.size()==0){
                            ToastUtil.showToastLong(R.string.NoRoleInfo,mContext);
                        }else if(arrayElement.size()==1){
                            SetRole(arrayElement.get(0).asObjectElement(),data);
                        }else {
                            final ArrayList<ObjectElement> list=new ArrayList<>();
                            for(int i=0;i<arrayElement.size();i++){
                                list.add(arrayElement.get(i).asObjectElement());
                            }
                            UserRoleDialog userRoleDialog=new UserRoleDialog(mContext,list);
                            userRoleDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    SetRole(list.get(position), data);
                                }
                            });
                            userRoleDialog.show();
                        }
                    } else if (code == Constants.REQUEST_CODE_FROZEN_ACCOUNT) {
                        Toast.makeText(mContext, getResources().getString(R.string.warning_message_frozen), Toast.LENGTH_SHORT).show();
                    } else if (code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_FAIL) {
                        if(isAccountPasswordLogin){
                        Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(isAccountPasswordLogin){
                    hud.dismiss();
                    Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();}
                }catch (Exception e){
                    throw e;
                }
            }else{
                if(isAccountPasswordLogin){
                ToastUtil.showToastLong(R.string.AccountOrPasswordFail,mContext);
                }else {
                    ToastUtil.showToastLong(R.string.NoCardDetail,mContext);
                }
            }
        }
        dismissCustomDialog();
    }
    private void initPush(String data){
      try {
    //调用JPush API设置Tag\
    String Organise_ID = new JSONObject(data).get("Organise_ID").toString();
    Set<String> tagSet = new LinkedHashSet<>();
    //tagSet.add(userid);
    tagSet.add("1002");
    String[] or = Organise_ID.split(",");
    for (int k = 0; k < or.length; k++) {
        tagSet.add(or[k]);
    }
    //tagSet.add(new JSONObject(data).get(Operator.OPERATOR_ID).toString());
    JPushInterface.init(mContext);
    JPushInterface.resumePush(mContext);
    //setStyleBasic();
    setStyleCustom();
    pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, tagSet));
    pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, new JSONObject(data).get(Operator.OPERATOR_ID).toString()));
          }catch (Exception e){
          }
    }
    private void SetRole(ObjectElement objectElement,String data){
        SharedPreferenceManager.setUserRoleID(mContext,DataUtil.isDataElementNull(objectElement.get("UserRole_ID")));
        SharedPreferenceManager.setUserModuleList(mContext,DataUtil.isDataElementNull(objectElement.get("AppInterfaceList")));
        Intent intent=new Intent(LoginActivity.this,CusActivity.class);
        intent.putExtra("Module_ID_List",DataUtil.isDataElementNull(objectElement.get("AppInterfaceList")));
        startActivity(intent);
        initPush(data);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    private void showErrorDownloadDatabaseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.FailDownloadDB);
        builder.setPositiveButton(R.string.retry,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getNewDataFromServer();
                    }
                });
        builder.setCancelable(false);
        builder.show();
    }
}
