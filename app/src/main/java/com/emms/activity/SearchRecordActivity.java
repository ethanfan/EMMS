package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.RxVolley;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.http.VolleyError;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.ui.NFCDialog;
import com.emms.util.BuildConfig;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.restlet.service.TaskService;
import org.w3c.dom.Text;

import java.util.Map;

/**
 * Created by jaffer.deng on 2016/5/24.
 */
public class SearchRecordActivity extends NfcActivity implements View.OnClickListener{
   private TextView repair_task,move_car_task,other_task;
    private int nfctag = 0;
    private Context mContext=this;
    private String TaskClass="T01";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);
        initView();
    }

    private void initView() {
       // TaskHistroyQuery=(TextView)findViewById(R.id.TaskHistroyQuery);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        repair_task=(TextView)findViewById(R.id.repair_task);
        move_car_task=(TextView)findViewById(R.id.move_car_task);
        other_task=(TextView)findViewById(R.id.other_task);
        repair_task.setOnClickListener(this);
        move_car_task.setOnClickListener(this);
        other_task.setOnClickListener(this);
        repair_task.setTextColor(Color.RED);
      //  TaskHistroyQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         int id=v.getId();
        switch (id){
            case R.id.repair_task:
            {   TaskClass="T01";
                {
                    repair_task.setTextColor(Color.RED);
                    move_car_task.setTextColor(Color.BLACK);
                    other_task.setTextColor(Color.BLACK);
                }
                break;
            }
            case R.id.move_car_task:
            {   TaskClass="T03";
                {
                    repair_task.setTextColor(Color.BLACK);
                    move_car_task.setTextColor(Color.RED);
                    other_task.setTextColor(Color.BLACK);
                }
                break;
            }
            case R.id.other_task:
            {   TaskClass="T04";
                {
                    repair_task.setTextColor(Color.BLACK);
                    move_car_task.setTextColor(Color.BLACK);
                    other_task.setTextColor(Color.RED);
                }
                break;
            }
        }
    }
    public void getTaskHistory(String TaskClass){
            Intent intent=new Intent(this,TaskHistory.class);
            intent.putExtra(Task.TASK_CLASS,TaskClass);
            startActivity(intent);
    }


    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
           getOperatorInfoFromServer(iccardID);
        }
    }
    public void getOperatorInfoFromServer(String iccardID){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        httpParams.put("ICCardID",Integer.valueOf(iccardID));
        HttpUtils.getWithoutCookies(this, "Token", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success")!=null&&jsonObjectElement.get("Success").valueAsBoolean()){
                Toast toast=Toast.makeText(mContext,R.string.scanICCardSuccess,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                getTaskHistory(TaskClass);}
                    else{
                        ToastUtil.showToastLong("刷卡登录失败",mContext);
                    }
                }
                dismissCustomDialog();
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
    public void SaveCookies( Map<String, String> headers)
    {

        if (headers == null)
            return;

        String cookie=headers.get("Set-Cookie");
        if(cookie!=null){
        String[] cookies = cookie.split(";");
        // String[] cookievalues = cookies[0].split("=");
        SharedPreferenceManager.setCookie(SearchRecordActivity.this, cookies[0]);
    }
    }
}
