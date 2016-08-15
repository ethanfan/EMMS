package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ServiceCompat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ArrayElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.commandAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.ui.HorizontalListView;
import com.emms.ui.NFCDialog;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;

import org.apache.http.conn.routing.RouteInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/7/25.
 */
public class CommandActivity extends NfcActivity  {
    private TextView group,task_id,task_create_time,task_accept_time,task_complete_time;
    private HorizontalListView response_speed,service_attitude,repair_speed;
    private Button comfirm;
    private ObjectElement taskDetail;
    private Context context=this;
    private long TaskEvaluation_ID=-1;
    private ArrayList<Integer> response_speed_list=new ArrayList<Integer>();
    private ArrayList<Integer> service_attitude_list=new ArrayList<Integer>();
    private ArrayList<Integer> repair_speed_list=new ArrayList<Integer>();
    private commandAdapter response_speed_adapter;
    private commandAdapter service_attitude_adapter;
    private commandAdapter repair_speed_adapter;
    private HashMap<String,Integer> command=new HashMap<String, Integer>();
    private NFCDialog nfcDialog;
    private int nfctag=0;
    private static int TASK_COMPLETE=1;
    private final String STATUS_DONE = "4";
    private boolean TaskComplete=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        taskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        TaskComplete=getIntent().getBooleanExtra("TaskComplete",false);
        initCommandListData();
        initView();
    }
    public void initView(){
        group=(TextView)findViewById(R.id.group);
        task_id=(TextView)findViewById(R.id.task_id);
        task_create_time=(TextView)findViewById(R.id.task_create_time);
        task_accept_time=(TextView)findViewById(R.id.task_accept_time);
        task_complete_time=(TextView)findViewById(R.id.task_complete_time);
        group.setText(DataUtil.isDataElementNull(taskDetail.get(Task.ORGANISE_NAME)));
        task_id.setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_ID)));
        task_create_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT_TIME))));
        task_accept_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(taskDetail.get(Task.START_TIME))));
        task_complete_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(taskDetail.get(Task.FINISH_TIME))));
      //任务评价
       response_speed=(HorizontalListView)findViewById(R.id.response_speed);
        service_attitude=(HorizontalListView)findViewById(R.id.service_attitude);
        repair_speed=(HorizontalListView)findViewById(R.id.repair_speed);

        response_speed_adapter=new commandAdapter(this,response_speed_list);
        response_speed.setAdapter(response_speed_adapter);

        service_attitude_adapter=new commandAdapter(this,service_attitude_list);
        service_attitude.setAdapter(service_attitude_adapter);

        repair_speed_adapter=new commandAdapter(this,repair_speed_list);
        repair_speed.setAdapter(repair_speed_adapter);

        initListViewOnItemClickEvent();
        getTaskCommandFromServer();
        //确定提交，弹出扫描iccard
        comfirm=(Button)findViewById(R.id.comfirm);
        comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postTaskCommandToServer();
            }
        });
        //initTopToolbar
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.task_command);
        if(TaskComplete){
            findViewById(R.id.footer_toolbar).setVisibility(View.VISIBLE);
            comfirm.setVisibility(View.GONE);
        //initFooterToolbar
        findViewById(R.id.preStep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.nextStep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //待写
                postTaskCommandToServer();
            }
        });}
        nfcDialog = new NFCDialog(context, R.style.MyDialog) {
            @Override
            public void dismissAction() {
                nfctag = 0;
            }
        };
    }

    @Override
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
//
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);
            if(nfctag==1){
                TaskComplete(iccardID);
                if(nfcDialog!=null&&nfcDialog.isShowing()){
                    nfcDialog.dismiss();
                }
            }
        }
    }
    private void getTaskCommandFromServer(){
        HttpParams params=new HttpParams();
        params.put("task_id", DataUtil.isDataElementNull(taskDetail.get(Task.TASK_ID)));
        HttpUtils.get(this, "TaskEvaluationContent", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast toast=Toast.makeText(context,getResources().getString(R.string.getCommandFail),Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement CommandData=new JsonObjectElement(t);
                    if(CommandData.get("PageData")!=null){
                        if(CommandData.get("PageData").asArrayElement().size()==0){
                            TaskEvaluation_ID=0;
                        }else if(CommandData.get("PageData").asArrayElement().size()>0){
                            TaskEvaluation_ID=CommandData.get("PageData").asArrayElement().get(0).asObjectElement().get("TaskEvaluation_ID").valueAsLong();
                        ObjectElement objectElement=CommandData.get("PageData").asArrayElement().get(0).asObjectElement();
                        setCommandData(objectElement.get("RespondSpeed").valueAsInt(),"response_speed",response_speed_list,response_speed_adapter);
                        setCommandData(objectElement.get("ServiceAttitude").valueAsInt(),"service_attitude",service_attitude_list,service_attitude_adapter);
                        setCommandData(objectElement.get("MaintainSpeed").valueAsInt(),"repair_speed",repair_speed_list,repair_speed_adapter);
                        }
                    }
                }
            }
        });
    }
    private void postTaskCommandToServer(){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement submitCommandData=new JsonObjectElement();
        submitCommandData.set(Task.TASK_ID,DataUtil.isDataElementNull(taskDetail.get(Task.TASK_ID)));
        submitCommandData.set("RespondSpeed",command.get("response_speed"));
        submitCommandData.set("ServiceAttitude",command.get("service_attitude"));
        submitCommandData.set("MaintainSpeed",command.get("repair_speed"));
        //若已有，则对应，否则为0
        submitCommandData.set("TaskEvaluation_ID",TaskEvaluation_ID);
        params.putJsonParams(submitCommandData.toJson());
        HttpUtils.post(this, "TaskEvaluation", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                ToastUtil.showToastLong(R.string.submitSuccess,context);
                TaskCompleteScan();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.submitFail,context);
                dismissCustomDialog();
            }
        });
    }
    private void initCommandListData(){
        for(int i=0;i<5;i++){
        response_speed_list.add(0);
        service_attitude_list.add(0);
        repair_speed_list.add(0);
        }
        command.put("response_speed",0);
        command.put("service_attitude",0);
        command.put("repair_speed",0);
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
    public void setCommandData(int num,String key,final ArrayList<Integer> numList,final commandAdapter adapter){
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
                adapter.setDatas(numList);
                adapter.notifyDataSetChanged();
            }
        });
    }
    private void TaskCompleteScan(){
        //HttpParams httpParams=new HttpParams();
        getTaskEquipmentFromServerByTaskId();
    }
    private void TaskComplete(String iccardId){
        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set("Task_ID",task_id.getText().toString());
        params.putJsonParams(data.toJson());
        HttpUtils.post(this, "TaskFinish", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                if(t!=null){
                JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                if(jsonObjectElement!=null&&jsonObjectElement.get("Success")!=null&&
                        jsonObjectElement.get("Success").valueAsBoolean()){
                ToastUtil.showToastLong("任务完成",context);
                    if(nfcDialog!=null&&nfcDialog.isShowing()){
                        nfcDialog.dismiss();
                    }
                    startActivity(new Intent(context,MainActivity.class));
                }else {
                    ToastUtil.showToastLong("无法提交任务完成，请检查任务信息",context);
                }
            }}

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.submitFail,context);
            }
        });
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
                        ToastUtil.showToastLong(R.string.scanICCardSuccess,context);}
                    else{
                        ToastUtil.showToastLong("刷卡登录失败",context);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.scanICCardFail,context);
                dismissCustomDialog();
            }
        });
    }


    private void getTaskEquipmentFromServerByTaskId() {

        if (null == task_id.getText().toString()||task_id.getText().toString().equals("")) {
            return;
        }
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        params.put("task_id", task_id.getText().toString());
        params.put("pageSize",1000);
        params.put("pageIndex",1);
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        HttpUtils.get(this, "TaskDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if (t != null) {
                    ArrayElement jsonArrayElement = new JsonArrayElement(t);
                    if (jsonArrayElement != null && jsonArrayElement.size() > 0) {
                        for(int i=0;i<jsonArrayElement.size();i++ ){
                            if(!DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status")).equals(STATUS_DONE)){
                                ToastUtil.showToastLong(R.string.TaskEquipmentNotComplete,context);
                                return;
                            }
                        }
                    }
                    if(nfcDialog!=null&&!nfcDialog.isShowing()){
                        nfcDialog.show();}
                    nfctag=1;
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
                ToastUtil.showToastLong("获取设备状态失败，访问超时",context);
            }
        });
    }
}
