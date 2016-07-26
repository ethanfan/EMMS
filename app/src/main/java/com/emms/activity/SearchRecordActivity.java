package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Equipment;
import com.emms.ui.NFCDialog;
import com.emms.util.BuildConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.restlet.service.TaskService;
import org.w3c.dom.Text;

/**
 * Created by jaffer.deng on 2016/5/24.
 */
public class SearchRecordActivity extends NfcActivity implements View.OnClickListener{
   private TextView repair_task,move_car_task,other_task;
    private int nfctag = 0;
    public final static int TASK_TYPE = 1;
    public final static int TASK_SUBTYPE = 2;
    public final static int DEVICE_NAME =5;
    public final static int GROUP = 4;
    public final static int CREATER = 3;
    public final static int DEVICE_NUM = 6;
    public final static int TASK_DESCRIPTION = 7;
    private NFCDialog nfcDialog;
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
                getTaskHistory(TaskClass);
                break;
            }
            case R.id.move_car_task:
            {   TaskClass="T03";
                getTaskHistory(TaskClass);
                break;
            }
            case R.id.other_task:
            {   TaskClass="T04";
                getTaskHistory(TaskClass);
                break;
            }
        }
    }
    public void getTaskHistory(String TaskClass){
        HttpParams params=new HttpParams();
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


    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);

        }
    }
}
