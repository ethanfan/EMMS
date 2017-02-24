package com.emms.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.push.PushService;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Factory;
import com.emms.schema.Operator;
import com.emms.ui.EquipmentSummaryDialog;
import com.emms.ui.KProgressHUD;
import com.emms.ui.UserRoleDialog;
import com.emms.util.BuildConfig;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.NetworkConnectChangedReceiver;
import com.emms.util.ServiceUtils;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends NfcActivity implements View.OnClickListener {
    private Context mContext=this;
    private EditText inputPassWord;
    private EditText inputname;
    private KProgressHUD hud;
    private static final String FILE_NAME = "emms.apk";
    private Handler pushHandler = PushService.mHandler;
    private AlertDialog dialog ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        AppApplication.KeepLive=false;
//        ServiceUtils.stopKeepLiveService(this);
//        BroadcastUtils.stopKeepLiveBroadcast(this);
//        IntentFilter intentFilter=new IntentFilter();
//        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
//        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//        intentFilter.addAction(Intent.ACTION_USER_PRESENT);
//        registerReceiver(new ScreenOnAndOffBroadcast(),intentFilter);
        pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, new LinkedHashSet<>()));
        pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, ""));
        setStyleCustom();
        initView();
        if(SharedPreferenceManager.getFactory(mContext)==null){
            ArrayList<ObjectElement> s=new ArrayList<>();
            JsonObjectElement GEW=new JsonObjectElement();
            GEW.set(Equipment.EQUIPMENT_NAME, Factory.FACTORY_GEW);
            JsonObjectElement EGM=new JsonObjectElement();
            EGM.set(Equipment.EQUIPMENT_NAME,Factory.FACTORY_EGM);
            s.add(GEW);
            s.add(EGM);
            final EquipmentSummaryDialog equipmentSummaryDialog=new EquipmentSummaryDialog(this,s);
            equipmentSummaryDialog.dismissCancelButton();
            equipmentSummaryDialog.setTitle(R.string.SelectFactory);
            equipmentSummaryDialog.setCancelable(false);
            equipmentSummaryDialog.show();
            equipmentSummaryDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String factory=DataUtil.isDataElementNull(equipmentSummaryDialog.getList().get(position).get(Equipment.EQUIPMENT_NAME));
                    DataUtil.FactoryAndNetWorkAddressSetting(mContext,factory);
                    BuildConfig.NetWorkSetting(mContext);
                    if(Factory.FACTORY_EGM.equals(factory)){
                    ChangeServerConnectBaseOnNetwork();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            equipmentSummaryDialog.dismiss();
                            getVersion();
                        }
                    });
                }
            });
        }else {
            initNetWorklist(new StoreCallback() {
                @Override
                public void success(DataElement element, String resource) {
                    if (element != null && element.isArray() && element.asArrayElement().size() > 0) {
                        for (DataElement dataElement : element.asArrayElement()) {
                            if (!NetworkConnectChangedReceiver.mNetworkList.contains(DataUtil.isDataElementNull(dataElement.asObjectElement().get(DataDictionary.DATA_NAME)))) {
                                NetworkConnectChangedReceiver.mNetworkList.add(DataUtil.isDataElementNull(dataElement.asObjectElement().get(DataDictionary.DATA_NAME)));
                            }
                        }
                    }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChangeServerConnectBaseOnNetwork();
                               // BuildConfig.NetWorkSetting(mContext);
                                getVersion();
                            }
                        });

                }

                @Override
                public void failure(DatastoreException ex, String resource) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ChangeServerConnectBaseOnNetwork();
                            //BuildConfig.NetWorkSetting(mContext);
                            getVersion();
                        }
                    });
                }
            });

        }
    }
   private void DoInit(){
       runOnUiThread(new Runnable() {
           @Override
           public void run() {
               if(!getIntent().getBooleanExtra("FromCusActivity", false)) {
                   if(SharedPreferenceManager.getDatabaseVersion(mContext)!=null&&Integer.valueOf(SharedPreferenceManager.getDatabaseVersion(mContext))<DBVersion){
                       getDBFromServer(getDBZipFile(BuildConfig.isDebug));
                   }else {
                       getNewDataFromServer();
                   }
               }
               if (mAdapter!=null&&!mAdapter.isEnabled()) {
                   showWirelessSettingsDialog();
               }
           }
       });
   }
    @Override
    protected void onRestart() {
        super.onRestart();
//        if(SharedPreferenceManager.getLanguageChange(this)){
            initView();
//            SharedPreferenceManager.setLanguageChange(this,false);
//        }
        if(SharedPreferenceManager.getLoginData(mContext)!=null) {
            getNewDataFromServer();
        }
    }

    private void initView() {
        inputPassWord = (EditText) findViewById(R.id.inputPassWord);
        inputname = (EditText) findViewById(R.id.inputUserName);
        inputPassWord.setHint(R.string.login_password_hint);
        inputname.setHint(R.string.login_id_hint);
        ((TextView) findViewById(R.id.login)).setText(R.string.login);
        ((TextView) findViewById(R.id.setting)).setText(R.string.systemSetting);
        ((TextView) findViewById(R.id.sweetTips)).setText(R.string.sweetTips);
        ((TextView) findViewById(R.id.tips)).setText(R.string.pleaseInputPasswordOrScanICcard);
        inputname.setText(SharedPreferenceManager.getUserName(this));
        inputPassWord.setText(SharedPreferenceManager.getPassWord(this));
        inputPassWord.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        inputPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        findViewById(R.id.login).setOnClickListener(this);
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
                    ToastUtil.showToastShort(R.string.network_error,mContext);
                    return;
                }

                if (userid.length() == 0) {
//                    showDialog(getString(R.string.warning_title),
//                            getString(R.string.warning_message_no_user));
                    ToastUtil.showToastShort(R.string.warning_message_no_user,mContext);
                    return;
                }
                if (password.length() == 0) {
//                    showDialog(getString(R.string.warning_title),
//                            getString(R.string.warning_message_no_password));
                    ToastUtil.showToastShort(R.string.warning_message_no_password,mContext);
                    return;
                }
                hud.show();
                HttpUtils.login(LoginActivity.this,userid, password, new HttpCallback() {

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Loger.debug(errorNo + ":strMsg");
                        hud.dismiss();
                        if(strMsg!=null){
                            Toast.makeText(mContext,
                                    getString(R.string.network_error)+"\n"+strMsg,
                                    Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(mContext,
                                    getString(R.string.network_error),
                                    Toast.LENGTH_LONG).show();
                        }
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



//    private void setStyleBasic(){
//        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
//        builder.statusBarDrawable = R.drawable.ic_emms;
//        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
//        builder.notificationDefaults = Notification.DEFAULT_ALL;
//        //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）
//        JPushInterface.setDefaultPushNotificationBuilder(builder);
//        JPushInterface.setPushNotificationBuilder(4, builder);
//    }

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
                            ToastUtil.showToastShort(R.string.NoRoleInfo,mContext);
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
                    CrashReport.postCatchedException(e);
                    if(isAccountPasswordLogin){
                    hud.dismiss();
                    Toast.makeText(mContext, getResources().getString(R.string.warning_message_error), Toast.LENGTH_SHORT).show();}
                }catch (Throwable e){
                    CrashReport.postCatchedException(e);
                }
            }else{
                if(isAccountPasswordLogin){
                ToastUtil.showToastShort(R.string.AccountOrPasswordFail,mContext);
                }else {
                    ToastUtil.showToastShort(R.string.NoCardDetail,mContext);
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
    Collections.addAll(tagSet, or);
    //tagSet.add(new JSONObject(data).get(Operator.OPERATOR_ID).toString());

    JPushInterface.resumePush(mContext);
    //setStyleBasic();
    setStyleCustom();
    pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, tagSet));
    pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, new JSONObject(data).get(Operator.OPERATOR_ID).toString()));
          }catch (Throwable e){
          CrashReport.postCatchedException(e);
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

    public void handleVersionUpdate(final Context context, String element) {
        JsonObjectElement json=new JsonObjectElement(element);
        final ObjectElement data=json.get(Data.PAGE_DATA).asArrayElement().get(0).asObjectElement();
        int version=data.get("Version").valueAsInt();
        try {
            PackageInfo packageInfo=context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            int CurrentVersion=packageInfo.versionCode;
            if(CurrentVersion<version){
                if (data.get("Content") != null && data.get("Content").isPrimitive()) {
                    if(LocaleUtils.getLanguage(context)!=null&&LocaleUtils.getLanguage(context)== LocaleUtils.SupportedLanguage.ENGLISH
                            || LocaleUtils.SupportedLanguage.getSupportedLanguage(context.getResources().getConfiguration().locale.getLanguage())==LocaleUtils.SupportedLanguage.ENGLISH) {
                        DataUtil.getDataFromLanguageTranslation(context.getApplicationContext(),DataUtil.isDataElementNull(data.get("Content")), new StoreCallback() {
                            @Override
                            public void success(DataElement e, String resource) {
                                if(e.isArray()&&e.asArrayElement().size()>0) {
                                    showDialog(context,data,DataUtil.isDataElementNull(e.asArrayElement().get(0).asObjectElement().get("Translation_Display"))
                                            + "1.0." + DataUtil.isDataElementNull(data.get("Version")));
                                }else {
                                    showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                            +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                                }
                            }

                            @Override
                            public void failure(DatastoreException ex, String resource) {
                                showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                        +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                            }
                        });
                    }else {
                        showDialog(context,data,DataUtil.isDataElementNull(data.get("Content"))
                                +"1.0."+DataUtil.isDataElementNull(data.get("Version")));
                    }
                }else {
                    DoInit();
                }

            }else {
                DoInit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            DoInit();
        }
    }
    public void showDialog(final Context context, final ObjectElement element, final String message) {
        Handler mainHandler = new Handler(context.getMainLooper());
        mainHandler.post(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try{
                    if (dialog == null || dialog.getContext() != context) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context);
                        dialog = builder.create();
                    }
                    DataElement e = element.get("Header");
                    if (e != null && e.isPrimitive()) {
                        dialog.setTitle(e.asPrimitiveElement()
                                .valueAsString());
                    }

                    //e = element.get("Content");
                    dialog.setMessage(message);
                    dialog.setCancelable(false);

                    e = element.get("ConfirmButtonText");
                    if (e != null&&e.isPrimitive()) {
//                    final DataElement clickEventUrl = element
//                            .asObjectElement().get("URL");
//                    final Reference url = new Reference(clickEventUrl.asPrimitiveElement().valueAsString());
                        String pathDir = FILE_NAME;
                        if (context.getExternalFilesDir(null) != null) {
                            //noinspection ConstantConditions
                            pathDir = context.getExternalFilesDir(null).toString() + "/" + FILE_NAME;
                        }
                        final File file = new File(pathDir);
                        //final Reference destination = new Reference(file.getAbsolutePath());
//                    if (downloadTask == null) {
//                        downloadTask = new DownloadTask(context, url, destination);
//                    }
//                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialog) {
//                                DoInit();
//                            }
//                        });
                        dialog.setButton(context.getResources().getString(R.string.Update), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ProgressBar progressView = new ProgressBar(context);
                                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                                alertDialog.setCancelable(false);
                                alertDialog.setView(progressView);
                                alertDialog.setTitle(R.string.downloading);
                                final Dialog d=alertDialog.create();
                                d.show();
                                HttpUtils.download(context, file.getAbsolutePath(), DataUtil.isDataElementNull(element.get("URL")), null, new HttpCallback() {
                                    @Override
                                    public void onSuccess(String t) {
                                        super.onSuccess(t);
                                        d.dismiss();
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void onFailure(int errorNo, String strMsg) {
                                        d.dismiss();
                                        DoInit();
                                        super.onFailure(errorNo, strMsg);
                                    }
                                });

                            }
                        });
                    }
                    if (!dialog.isShowing()) {
                        dialog.show();
                    }
                }catch (Exception e){
                    CrashReport.postCatchedException(e);
                    DoInit();
                }
            }
        });
    }
    private void getVersion(){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        HttpUtils.getWithoutCookies(mContext, "System_Version/GetAppDownloadInfo", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if(t!=null) {
                    handleVersionUpdate(mContext, t);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                DoInit();
            }
        });
    }

}
