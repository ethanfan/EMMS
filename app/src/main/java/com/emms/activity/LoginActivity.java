package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emms.ConfigurationManager;
import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.ui.KProgressHUD;
import com.emms.util.Constants;
import com.emms.util.SharedPreferenceManager;
import com.jaffer_datastore_android_sdk.rxvolley.client.HttpCallback;
import com.jaffer_datastore_android_sdk.rxvolley.toolbox.Loger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private TextView login;
    private TextView machine;
    private EditText inputPassWord;
    private EditText inputname;
    private KProgressHUD hud;

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
                        SharedPreferenceManager.setCookie(LoginActivity.this, headers.get("Set-Cookie"));
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
                                SharedPreferenceManager.setUserData(LoginActivity.this,userData);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
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
}
