package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.client.ProgressListener;
import com.datastore_android_sdk.rxvolley.http.VolleyError;
import com.datastore_android_sdk.schema.Query;
import com.emms.ConfigurationManager;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.push.PushService;
import com.emms.ui.KProgressHUD;
import com.emms.ui.PopMenuLoginActivity;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.util.BuildConfig;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.RootUtil;
import com.emms.util.SharedPreferenceManager;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.toolbox.Loger;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.util.JSONObjectUtils;

import net.minidev.json.JSONUtil;
import net.minidev.json.parser.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private TextView login;
    private TextView machine;
    private EditText inputPassWord;
    private EditText inputname;
    private KProgressHUD hud;
    private String def_Factory="GEW";
    //private ImageButton setting;
    private Handler pushHandler = PushService.mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        if(SharedPreferenceManager.getFactory(this)==null){
            SharedPreferenceManager.setFactory(this,def_Factory);
        }
//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
       //getDataBaseUpdateFromServer();
        initView();
        getNewDataFromServer();
    }

    private void initView() {
        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopWindow(v);
            }
        });
        login = (TextView) findViewById(R.id.login);
        machine = (TextView) findViewById(R.id.machine);
        inputPassWord = (EditText) findViewById(R.id.inputPassWord);
        inputname = (EditText) findViewById(R.id.inputUserName);
//        inputname.setText("GET0259106");
//        inputPassWord.setText("888888");
    //    SharedPreferenceManager.getUserName(this);
    //    SharedPreferenceManager.getPassWord(this);
     //   inputname.setText("GET0006236");
     //   inputPassWord.setText("888888");
      inputname.setText(SharedPreferenceManager.getUserName(this));
      inputPassWord.setText(SharedPreferenceManager.getPassWord(this));
        login.setOnClickListener(this);
        machine.setOnClickListener(this);
        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(getResources().getString(R.string.logining))
                .setCancellable(true);
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
                final String userid = inputname.getText().toString().toUpperCase();
                final String password = inputPassWord.getText().toString().toUpperCase();
                if (!hasNetworkConnection()) {
                    showDialog(getString(R.string.warning_title),
                            getString(R.string.network_error));
                    return;
                }

                if (userid == null || userid.length() == 0) {
                    showDialog(getString(R.string.warning_title),
                            getString(R.string.warning_message_no_user));
                    return;
                }
                if (password == null || password.length() == 0) {
                    showDialog(getString(R.string.warning_title),
                            getString(R.string.warning_message_no_password));
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
                        if(t!=null){
                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("Success")!=null&&jsonObjectElement.get("Success").valueAsBoolean()){
                                try {
                                    JSONObject jsonObject = new JSONObject(t);
                                    int code = Integer.parseInt(jsonObject.get("Result").toString());
                                    boolean isSuccess = jsonObject.get("Success").equals(true);
                                    if ((code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS ||
                                            code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_SUCCESS_AUTO) && isSuccess) {
                                        SharedPreferenceManager.setUserName(LoginActivity.this, userid);
                                        SharedPreferenceManager.setPassWord(LoginActivity.this, password);
                                        String userData =jsonObject.getString("UserData");
                                        SharedPreferenceManager.setUserData(LoginActivity.this, userData);
                                        String data=jsonObject.getString("Data");
                                        SharedPreferenceManager.setLoginData(LoginActivity.this,data);
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                        String Organise_ID=new JSONObject(data).get("Organise_ID").toString();
                                        Set<String> tagSet = new LinkedHashSet<String>();
                                        //tagSet.add(userid);
                                        tagSet.add(Organise_ID);
                                        //调用JPush API设置Tag
                                        pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, tagSet));

                                        pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, Organise_ID));

                                        finish();
                                    } else if (code == Constants.REQUEST_CODE_FROZEN_ACCOUNT) {
                                        Toast.makeText(mContext, getResources().getString(R.string.warning_message_frozen), Toast.LENGTH_SHORT).show();
                                    } else if (code == Constants.REQUEST_CODE_IDENTITY_AUTHENTICATION_FAIL) {
                                        Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    hud.dismiss();
                                    Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                ToastUtil.showToastLong(R.string.AccountOrPasswordFail,mContext);
                            }
                        }

                    }
                });
                break;
            case R.id.machine:
                Intent intentMachine = new Intent(LoginActivity.this, MachineActivity.class);
                startActivity(intentMachine);
                break;
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
        final File db = new File(getExternalFilesDir(null), "/EMMS.db");
        if(db.exists()){
            //getDataBaseUpdateFromServer();
            getDBDataLastUpdateTime();
            return;
        }
       final File dbFile = new File(getExternalFilesDir(null), "/EMMS.zip");
           if(dbFile.exists()){
               try{
                   HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);}catch (Exception e){
               }
             return;
          }
        showCustomDialog(R.string.DownloadDataBase);
        HttpUtils.download(this, dbFile.getAbsolutePath(), "", null, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try{
                HttpUtils.upZipFile(dbFile,dbFile.getParentFile().getAbsolutePath(),mContext);
                dismissCustomDialog();
                ToastUtil.showToastLong("数据文件下载完毕",mContext);}catch (Exception e){
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.loadingFail,mContext);
                dismissCustomDialog();
            }
        });
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
//        data.set("LastUpdateTime_BaseOrganise", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_BaseOrganise")));
//        data.set("LastUpdateTime_DataDictionary",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataDictionary")));
//        data.set("LastUpdateTime_DataType",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataType")));
//        data.set("LastUpdateTime_Equipment",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Equipment")));
//        data.set("LastUpdateTime_TaskOrganiseRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskOrganiseRelation")));

        data.set("LastUpdateTime_BaseOrganise","0x000000000001499B");
        data.set("LastUpdateTime_DataDictionary","0x000000000001499B");
        data.set("LastUpdateTime_DataType","0x000000000001499B");
        data.set("LastUpdateTime_Equipment","0x000000000001499B");
        data.set("LastUpdateTime_TaskOrganiseRelation","0x000000000001499B");

        if(SharedPreferenceManager.getFactory(this)==null){
        data.set("Factory_ID",def_Factory);}
        else {
            data.set("Factory_ID",SharedPreferenceManager.getFactory(this));
        }
        data.set("IsNewApp",1);
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
                    if(json.get("DataType")!=null&&json.get("DataType").asArrayElement().size()>0){
                    updateData(json.get("DataType"), EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE);}
                    if(json.get("BaseOrganise")!=null&&json.get("BaseOrganise").asArrayElement().size()>0){
                        updateData(json.get("BaseOrganise"),EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE);}
                    if(json.get("DataDictionary")!=null&&json.get("DataDictionary").asArrayElement().size()>0){
                         updateData(json.get("DataDictionary"),EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY);
                    }
                    if(json.get("Equipment")!=null&&json.get("Equipment").asArrayElement().size()>0){
                        updateData(json.get("Equipment"),EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT);}
                    if(json.get("TaskOrganiseRelation")!=null&&json.get("TaskOrganiseRelation").asArrayElement().size()>0){
                        updateData(json.get("TaskOrganiseRelation"),EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION);}
                }
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
        PopMenuLoginActivity popMenuLoginActivity = new PopMenuLoginActivity(this, 300) {

            @Override
            public void onEventDismiss() {

            }
        };
        String[] mTitles = getResources().getStringArray(R.array.menu_login);

        popMenuLoginActivity.addItems(mTitles);
        popMenuLoginActivity.showAsDropDown(v);
    }
    private void getDBDataLastUpdateTime(){
        String sql="    select * from (select max(LastUpdateTime) LastUpdateTime_BaseOrganise from BaseOrganise)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataDictionary from DataDictionary)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataType from DataType)," +
                "    (select max(LastUpdateTime) LastUpdateTime_Equipment from Equipment)," +
                "    (select max(LastUpdateTime) LastUpdateTime_TaskOrganiseRelation from TaskOrganiseRelation)";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                getDataBaseUpdateFromServer(element.asArrayElement().get(0));
            }

            @Override
            public void failure(DatastoreException ex, String resource) {

            }
        });
    }
}
