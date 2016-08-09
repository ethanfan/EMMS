package com.emms.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.R;
import com.emms.activity.AppAplication;
import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.dialogOnSubmitInterface;
import com.emms.adapter.StatusAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.WorkInfo;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.util.ArrayWheelAdapter;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.j256.ormlite.field.types.IntegerObjectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class ChangeEquipmentDialog extends Dialog implements View.OnClickListener{
    private ChangeEquipmentDialog dialog = this;
    private Context context;
    private String TaskId;
    private String EquipmentId;
    private String TaskEquipmentId;
    //private ArrayList<String> status=new ArrayList<String>();
    //private int tag=1;
    private Button change_equipment_operator_status,change_equipment_status;
 //   private Button equipment_resume,equipment_complete,equipment_wait_material,equipment_pause,equipment_start,quit,material_requisition;
    private ObjectElement TaskEquipmentData;
    private boolean is_Main_person_in_charge_operator_id=false;

    public void setOperator_Status(int operator_Status) {
        Operator_Status = operator_Status;
    }
    private int Operator_Status=-1;
    private WheelView Status;
    private ArrayWheelAdapter<String> adapter;
    private ArrayList<String> Equipment_Status_List=new ArrayList<String>();
    private ArrayList<String> Equipment_Operator_Status_List=new ArrayList<String>();
    private ArrayList<String> showList=new ArrayList<String>();
    private HashMap<String,Integer> Equipment_Operator_Status_Name_ID_map=new HashMap<String, Integer>();
    private HashMap<String,Integer> Equipment_Status_Name_ID_map=new HashMap<String, Integer>();
    private int ViewTag=1;
    /*
    public HashMap<String, Integer> getEquipment_OperatorID_Status() {
        return Equipment_OperatorID_Status;
    }
    public void setEquipment_OperatorID_Status(HashMap<String, Integer> equipment_OperatorID_Status) {
        Equipment_OperatorID_Status = equipment_OperatorID_Status;
    }*/
   // private HashMap<String,Integer> Equipment_OperatorID_Status=new HashMap<String, Integer>();
    public ChangeEquipmentDialog(Context context, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        //if(Equipment_OperatorID_Status.get())
      //  Collections.addAll(status,context.getResources().getStringArray(R.array.equip_status));
        initMap();
        initData();
        initview();
    }
    public void setOnSubmitInterface(dialogOnSubmitInterface onSubmitInterface) {
        this.onSubmitInterface = onSubmitInterface;
    }

    private dialogOnSubmitInterface onSubmitInterface=null;
    public void initview() {
        ((TextView)findViewById(R.id.cancle)).setText(R.string.cancel);
        findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ViewTag==1){
                   postTaskOperatorEquipment(Equipment_Operator_Status_Name_ID_map.get(showList.get(Status.getCurrentItem())));
                    }
                    else if(ViewTag==2){
                    postTaskEquipment(Equipment_Status_Name_ID_map.get(showList.get(Status.getCurrentItem())));
                    }
            }
        });
        Status=(WheelView)findViewById(R.id.list);
        Status.setVisibleItems(5);
        //Status.getCurrentItem();
        Status.setDrawingCacheEnabled(false);
        showList=Equipment_Operator_Status_List;
        adapter=new ArrayWheelAdapter<>(showList);
        Status.setAdapter(adapter);
        initTagButton();
   //     initEquipmentTagView();
    }



    private void postTaskEquipment(int status) {

        HttpParams params = new HttpParams();

        JsonObjectElement taskEquepment=new JsonObjectElement();
//创建任务提交数据：任务创建人，任务类型“T01”那些，几台号（数组），
        taskEquepment.set(Task.TASK_ID,TaskId);
        //若任务未有设备，则输入为0，表示添加
        taskEquepment.set("TaskEquipment_ID",TaskEquipmentId);
        //若已有设备，申请状态变更
        taskEquepment.set("OracleID", EquipmentId);
        //taskEquepment.set("Equipment_ID", equipmentID);
        taskEquepment.set("Status",status);

        params.putJsonParams(taskEquepment.toJson());

        HttpUtils.post(context, "TaskEquipmentStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Toast.makeText(context, "修改设备状态成功", Toast.LENGTH_SHORT).show();
                dismiss();
                onSubmitInterface.onsubmit();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                        Toast.makeText(context, "修改设备状态失败", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public ObjectElement getTaskEquipmentData() {
        return TaskEquipmentData;
    }

    public void setTaskEquipmentData(ObjectElement taskEquipmentData) {
        TaskEquipmentData = taskEquipmentData;
    }
   public void setDatas(String taskId,String equipmentId,String taskEquipmentId){
       this.TaskId=taskId;
       this.EquipmentId=equipmentId;
       this.TaskEquipmentId=taskEquipmentId;
   }
    public void setMainPersonInChargeOperatorId(boolean is_Main_person_in_charge_operator_id){
        this.is_Main_person_in_charge_operator_id=is_Main_person_in_charge_operator_id;
    }
    /*
    private void initEquipmentTagView(){
        equipment_resume=(Button)findViewById(R.id.equipment_resume);
        equipment_complete=(Button)findViewById(R.id.equipment_complete);
        equipment_wait_material=(Button)findViewById(R.id.equipment_wait_material);
        equipment_pause=(Button)findViewById(R.id.equipment_pause);
        equipment_start=(Button)findViewById(R.id.equipment_start);
        quit=(Button)findViewById(R.id.quit);
        material_requisition=(Button)findViewById(R.id.material_requisition);
        equipment_resume.setOnClickListener(this);
        equipment_complete.setOnClickListener(this);
        equipment_wait_material.setOnClickListener(this);
        equipment_pause.setOnClickListener(this);
        equipment_start.setOnClickListener(this);
        quit.setOnClickListener(this);
        material_requisition.setOnClickListener(this);
    }
    private void TagView(int tag){
        if(tag==1){
            equipment_resume.setVisibility(View.VISIBLE);
            equipment_complete.setVisibility(View.VISIBLE);
            equipment_wait_material.setVisibility(View.VISIBLE);
            equipment_pause.setVisibility(View.VISIBLE);
            equipment_start.setVisibility(View.VISIBLE);
            quit.setVisibility(View.VISIBLE);
            material_requisition.setVisibility(View.VISIBLE);
        }
        else{
            equipment_resume.setVisibility(View.VISIBLE);
            equipment_complete.setVisibility(View.VISIBLE);
            equipment_wait_material.setVisibility(View.VISIBLE);
            equipment_pause.setVisibility(View.VISIBLE);
            equipment_start.setVisibility(View.VISIBLE);
            quit.setVisibility(View.GONE);
            material_requisition.setVisibility(View.GONE);
        }
    }
*/
    @Override
    public void onClick(View v) {
        int id = v.getId();
   /*     switch (id){
            case R.id.equipment_resume:{
                if(tag==1){
                    postTaskOperatorEquipment(0);
                }else {
                    postTaskEquipment(1);
                }
                break;
            }
            case R.id.equipment_complete:{
                if(tag==1){
                    postTaskOperatorEquipment(5);
                }else{
                    postTaskEquipment(4);
                }
                break;
            }
            case R.id.equipment_wait_material:{
                if(tag==1){
                    postTaskOperatorEquipment(4);
                }else{
                    postTaskEquipment(3);
                }
                break;
            }
            case R.id.equipment_pause:{
                if(tag==1){
                    postTaskOperatorEquipment(1);
                }else{
                    postTaskEquipment(2);
                }
                break;
            }
            case R.id.equipment_start:{
                if(tag==1){
                    postTaskOperatorEquipment(0);
                }
                else{
                    postTaskEquipment(1);
                }
                break;
            }
            case R.id.quit:{
                postTaskOperatorEquipment(2);
                break;
            }
            case R.id.material_requisition:{
                postTaskOperatorEquipment(3);
                break;
            }
        }*/

    }

    private void initTagButton(){
        change_equipment_operator_status=(Button)findViewById(R.id.change_equipment_operator_status);
       // change_equipment_operator_status.setBackgroundColor(Color.RED);
        change_equipment_operator_status.setTextColor(Color.parseColor("#C4647C"));
        change_equipment_operator_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewTag=1;

               // change_equipment_status.setBackgroundColor(Color.WHITE);
              //  change_equipment_operator_status.setBackgroundColor(Color.RED);
               change_equipment_status.setTextColor(Color.parseColor("#D2D2D2"));
                change_equipment_operator_status.setTextColor(Color.parseColor("#C4647C"));
                TagView(ViewTag);
            }
        });
        change_equipment_status=(Button)findViewById(R.id.change_equipment_status);
        change_equipment_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(is_Main_person_in_charge_operator_id){
                   ViewTag=2;
                //change_equipment_operator_status.setBackgroundColor(Color.WHITE);
              //  change_equipment_status.setBackgroundColor(Color.RED);
                   change_equipment_operator_status.setTextColor(Color.parseColor("#D2D2D2"));
                   change_equipment_status.setTextColor(Color.parseColor("#C4647C"));
                TagView(ViewTag);
                   }
                else {
                   ToastUtil.showToastLong(R.string.onlyTaskChargerCanChangeEquipmentStatus,context);
               }
            }
        });
    }
    private void TagView(final int tag){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(tag==1){
                    showList=Equipment_Operator_Status_List;
                    adapter.setList(showList);
                   Status.invalidate();
                   // Status.
                }else if(tag==2){
                    showList=Equipment_Status_List;
                    adapter.setList(showList);
                   Status.invalidate();
                }
            }
        });
    }
    private void postTaskOperatorEquipment(int status){
      HttpParams params=new HttpParams();
       // JsonObjectElement TaskOperatorDataToSubmit=new JsonObjectElement();
     //   TaskOperatorDataToSubmit.set("task_id",Integer.valueOf(TaskId));
     //   TaskOperatorDataToSubmit.set("equipment_id",Integer.valueOf(EquipmentId));
     //   TaskOperatorDataToSubmit.set("TaskEquipment_ID",Integer.valueOf(TaskEquipmentId));
     //   TaskOperatorDataToSubmit.set("status",status);
     //   params.putJsonParams(TaskOperatorDataToSubmit.toJson());
        HttpUtils.post(context, "TaskOperatorStatus?task_id="+TaskId+"&equipment_id="+EquipmentId+"&status="+status, params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success").valueAsBoolean()){
                        onSubmitInterface.onsubmit();
                        dismiss();
                    }else {
                        ToastUtil.showToastLong("不允许修改状态",context);
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.failToChangeStatus,context);
            }
        });
    }
    private void StatusControl(int Status){

    }
    public void initData(){
        Collections.addAll(Equipment_Status_List,context.getResources().getStringArray(R.array.Equipment_Status));
        Collections.addAll(Equipment_Operator_Status_List,context.getResources().getStringArray(R.array.Equipment_Operator_Status));
    }
    private void initMap(){
        {
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.start), 0);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.pause), 1);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.quit), 2);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.material_requisition), 3);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.wait_material), 4);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.complete), 5);
        }
        {
            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.start), 1);
            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.pause), 2);
            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.wait_material), 3);
            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.complete), 4);
        }
    }
}