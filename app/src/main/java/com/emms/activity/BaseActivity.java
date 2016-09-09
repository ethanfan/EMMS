package com.emms.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.schema.Operator;
import com.emms.ui.KProgressHUD;
import com.emms.util.SharedPreferenceManager;

import org.apache.commons.lang.StringUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/17.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private KProgressHUD hud;
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
        hud=KProgressHUD.create(this);

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
                operator.setId(Long.valueOf(json.get("Operator_ID").valueAsString()));
                operator.setTeamId(json.get("Team_ID").valueAsString());
                operator.setTeamName(json.get("TeamName").valueAsString());
                operator.setName(json.get("Name").valueAsString());
                operator.setMaintenMan(json.get("IsMaintenMan").valueAsBoolean());
               // operator.setUserRole_ID(json.get("UserRole_ID").valueAsInt());
                operator.setOrganiseID(json.get("Organise_ID").valueAsString());
                operator.setOperator_no(json.get("OperatorNo").valueAsString());
//                operator = Operator.fromJson(userData, null, Operator.class);
                JsonObjectElement objectElement=new JsonObjectElement(SharedPreferenceManager.getMsg(this));
                operator.setUserRole_ID(objectElement.get("UserRole_ID").valueAsInt());
                operator.setModuleList(objectElement.get("AppInterfaceList").valueAsString());
            }catch (Exception e){
                e.printStackTrace();
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
                JsonObjectElement objectElement=new JsonObjectElement(SharedPreferenceManager.getMsg(this));
                operator.setUserRole_ID(objectElement.get("UserRole_ID").valueAsInt());
                operator.setModuleList(objectElement.get("AppInterfaceList").valueAsString());
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
        initCustomDialog(resId);
        if(hud!=null&&!hud.isShowing()){
            hud.show();
        }
    }
    public void dismissCustomDialog(){
        if(hud!=null&&hud.isShowing()){
            hud.dismiss();
        }
    }
}
