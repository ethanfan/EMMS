package com.emms.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.schema.Operator;
import com.emms.util.SharedPreferenceManager;

import org.apache.commons.lang.StringUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by jaffer.deng on 2016/6/17.
 */
public abstract class BaseActivity extends AppCompatActivity {

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

    }
    protected SqliteStore getSqliteStore() {
        return ((AppAplication) getApplication()).getSqliteStore();
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

//                operator = Operator.fromJson(userData, null, Operator.class);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return operator;
    }

}
