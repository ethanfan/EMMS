package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
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
    private String TaskClass=null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);
        initView();
    }

    private void initView() {
       // TaskHistroyQuery=(TextView)findViewById(R.id.TaskHistroyQuery);
        repair_task=(TextView)findViewById(R.id.repair_task);
        move_car_task=(TextView)findViewById(R.id.move_car_task);
        other_task=(TextView)findViewById(R.id.other_task);
        repair_task.setOnClickListener(this);
        move_car_task.setOnClickListener(this);
        other_task.setOnClickListener(this);
      //  TaskHistroyQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
         int id=v.getId();
        switch (id){
            case R.id.repair_task:
            {   TaskClass="T01";
                break;
            }
            case R.id.move_car_task:
            {   TaskClass="T03";
                break;
            }
            case R.id.other_task:
            {   TaskClass="T04";
                break;
            }
        }
    }
    public void getTaskHistory(String TaskClass){
        if(TaskClass==null){
            Toast toast=Toast.makeText(this,getResources().getString(R.string.pleaseSelectTaskClass),Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }else {
            HttpParams params = new HttpParams();
            params.put(Task.TASK_CLASS, TaskClass);
            HttpUtils.get(this, "TaskHistoryList", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                }
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            });
        }
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
        HttpParams httpParams=new HttpParams();
        httpParams.put("ICCardID",Integer.valueOf(iccardID));
        HttpUtils.getWithoutCookies(this, "Token", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                getTaskHistory(TaskClass);
            }

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                SaveCookies(headers);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }
    public void SaveCookies( Map<String, String> headers)
    {

        if (headers == null)
            return;

        String cookie=headers.get("Set-Cookie");
        String[]cookies=cookie.split(";");
        // String[] cookievalues = cookies[0].split("=");
        SharedPreferenceManager.setCookie(SearchRecordActivity.this,cookies[0]);

    }
}
