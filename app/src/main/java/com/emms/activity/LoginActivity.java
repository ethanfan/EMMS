package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emms.ConfigurationManager;
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.push.PushService;
import com.emms.ui.KProgressHUD;
import com.emms.util.Constants;
import com.emms.util.SharedPreferenceManager;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.toolbox.Loger;

import org.json.JSONException;
import org.json.JSONObject;

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

    Handler pushHandler = PushService.mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
    }

    private void initView() {
        login = (TextView) findViewById(R.id.login);
        machine = (TextView) findViewById(R.id.machine);
        inputPassWord = (EditText) findViewById(R.id.inputPassWord);
        inputname = (EditText) findViewById(R.id.inputUserName);
       inputname.setText("GET0259106");
        inputPassWord.setText("888888");
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
                final String userid = inputname.getText().toString();
                final String password = inputPassWord.getText().toString();
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


                                Set<String> tagSet = new LinkedHashSet<String>();
                                tagSet.add(userid);
                                //调用JPush API设置Tag
                                pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_TAGS, tagSet));

                                pushHandler.sendMessage(pushHandler.obtainMessage(PushService.MSG_SET_ALIAS, userid));

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
            String[]cookies=cookie.split(";");
       // String[] cookievalues = cookies[0].split("=");
        SharedPreferenceManager.setCookie(LoginActivity.this,cookies[0]);

    }
}
