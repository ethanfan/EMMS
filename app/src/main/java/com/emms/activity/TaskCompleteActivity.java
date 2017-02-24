package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.commandAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.ui.HorizontalListView;
import com.emms.util.DataUtil;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/1/9.
 *
 */
public class TaskCompleteActivity extends NfcActivity{
    private enum Mode{
        ACCOUNT_AND_PASSWORD,
        IC_CARD
    }
    private Mode mMode=Mode.IC_CARD;
//    private boolean TaskComplete;
//    private String TaskClass;
    private ObjectElement TaskDetail;
    private Context context=this;
    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(mMode==Mode.IC_CARD){
                //TODO
                String iccardID = NfcUtils.dumpTagData(tag);
                SubmitData(mMode,iccardID);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_complete);
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
//        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
//        if(getIntent().getStringExtra(Task.TASK_CLASS)!=null){
//            TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
//        }
        if (mAdapter!=null&&mAdapter.isEnabled()) {
            mMode=Mode.IC_CARD;
        }else {
            mMode=Mode.ACCOUNT_AND_PASSWORD;
        }
        initView();
        initTaskCommand();
    }
    private void initView(){
        if(mMode==Mode.ACCOUNT_AND_PASSWORD){
            findViewById(R.id.inputInfo_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.SwipeLayout).setVisibility(View.GONE);
        }else {
            findViewById(R.id.inputInfo_layout).setVisibility(View.GONE);
            findViewById(R.id.SwipeLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.comfirm).setVisibility(View.GONE);
            findViewById(R.id.Tips).setVisibility(View.GONE);
        }
        EditText inputPassWord=(EditText)findViewById(R.id.password);
        inputPassWord.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        inputPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.taskComplete);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              SubmitData(mMode,null);
            }
        });
    }
    private void SubmitData(Mode mMode,String IC_Card_ID){
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        submitData.set(Task.TASK_ID, DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        submitData.set("RespondSpeed",command.get("response_speed"));
        submitData.set("ServiceAttitude",command.get("service_attitude"));
        submitData.set("MaintainSpeed",command.get("repair_speed"));
        submitData.set("TaskEvaluation_ID",0);
        switch (mMode){
            case ACCOUNT_AND_PASSWORD:{
                TextView account=(TextView)findViewById(R.id.account);
                TextView password=(TextView)findViewById(R.id.password);
                if(account.getText().toString().equals("")){
                    ToastUtil.showToastShort(R.string.warning_message_no_user,context);
                    return;
                }
                if(password.getText().toString().equals("")){
                    ToastUtil.showToastShort(R.string.warning_message_no_password,context);
                    return;
                }
                submitData.set("OperatorNo",account.getText().toString().toUpperCase());
                submitData.set("Password",password.getText().toString());
                break;
            }
            default:{
                submitData.set("ICCardID",IC_Card_ID);
                break;
            }
        }
        params.putJsonParams(submitData.toJson());
        showCustomDialog(R.string.submitData);
        HttpUtils.post(context, "TaskAPI/TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(t!=null){
                            final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("Success")!=null&&
                                    jsonObjectElement.get("Success").valueAsBoolean()){
                                ToastUtil.showToastShort(R.string.taskComplete,context);
                                startActivity(new Intent(context,CusActivity.class));
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).isEmpty()) {
                                            ToastUtil.showToastShort(R.string.canNotSubmitTaskComplete, context);
                                        }else {
                                            TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                        }
                                    }
                                });
                            }
                        }
                        dismissCustomDialog();
                    }
                });
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.submitFail,context);
                    }
                });
            }
        });
    }
    private ArrayList<Integer> response_speed_list=new ArrayList<>();
    private ArrayList<Integer> service_attitude_list=new ArrayList<>();
    private ArrayList<Integer> repair_speed_list=new ArrayList<>();
    private commandAdapter response_speed_adapter,service_attitude_adapter,repair_speed_adapter;
    private HorizontalListView response_speed,service_attitude,repair_speed;
    private HashMap<String,Integer> command=new HashMap<>();
    private void initTaskCommand(){
        response_speed=(HorizontalListView)findViewById(R.id.response_speed);
        service_attitude=(HorizontalListView)findViewById(R.id.service_attitude);
        repair_speed=(HorizontalListView)findViewById(R.id.repair_speed);
        for(int i=0;i<5;i++){
            response_speed_list.add(0);
            service_attitude_list.add(0);
            repair_speed_list.add(0);
        }
        command.put("response_speed",0);
        command.put("service_attitude",0);
        command.put("repair_speed",0);
        response_speed_adapter=new commandAdapter(this,response_speed_list);
        response_speed.setAdapter(response_speed_adapter);

        service_attitude_adapter=new commandAdapter(this,service_attitude_list);
        service_attitude.setAdapter(service_attitude_adapter);

        repair_speed_adapter=new commandAdapter(this,repair_speed_list);
        repair_speed.setAdapter(repair_speed_adapter);
        initListViewOnItemClickEvent();
    }
    public void setCommandData(int num, String key, final ArrayList<Integer> numList, final commandAdapter cAdapter){
        command.put(key,num);
        for(int i=0;i<5;i++){
            if(i<num){
                numList.set(i,1);
            }else {
                numList.set(i,0);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cAdapter.setDatas(numList);
                cAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initListViewOnItemClickEvent(){
        response_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"response_speed",response_speed_list,response_speed_adapter);
            }
        });
        service_attitude.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"service_attitude",service_attitude_list,service_attitude_adapter);
            }
        });
        repair_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"repair_speed",repair_speed_list,repair_speed_adapter);
            }
        });
    }
}
