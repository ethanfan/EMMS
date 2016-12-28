package com.emms.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.dialogOnSubmitInterface;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class ChangeEquipmentDialog extends Dialog implements View.OnClickListener{
    private Context context;
    private String TaskId;
    private String EquipmentId;
    private String TaskEquipmentId;
    private KProgressHUD hud;
    public void setEquipemntStatus(int equipemntStatus) {
        EquipemntStatus = equipemntStatus;
    }
    public final static String DELETE="delete";
    private int EquipemntStatus=-1;
    //private ArrayList<String> status=new ArrayList<String>();
    //private int tag=1;
    private Button change_equipment_operator_status,change_equipment_status;
 //   private Button equipment_resume,equipment_complete,equipment_wait_material,equipment_pause,equipment_start,quit,material_requisition;
    private ObjectElement TaskEquipmentData;
    private boolean is_Main_person_in_charge_operator_id=false;
    private boolean isNoEuqipment=false;

    private int Operator_Status=-1;

//    public void setEquipment_Status(int equipment_Status) {
//        Equipment_Status = equipment_Status;
//    }
//
//    private int Equipment_Status=-1;
//    private ArrayWheelAdapter<String> adapter;
    private ArrayList<ObjectElement> Equipment_Status_List=new ArrayList<>();
    private ArrayList<ObjectElement> Equipment_Operator_Status_List=new ArrayList<>();
    private ArrayList<ObjectElement> showList=new ArrayList<>();
    private HashMap<String,Integer> Equipment_Operator_Status_Name_ID_map=new HashMap<>();
    private HashMap<String,Integer> Equipment_Status_Name_ID_map=new HashMap<>();
    private int ViewTag=1;
    /*
    public HashMap<String, Integer> getEquipment_OperatorID_Status() {
        return Equipment_OperatorID_Status;
    }
    public void setEquipment_OperatorID_Status(HashMap<String, Integer> equipment_OperatorID_Status) {
        Equipment_OperatorID_Status = equipment_OperatorID_Status;
    }*/
   // private HashMap<String,Integer> Equipment_OperatorID_Status=new HashMap<String, Integer>();
    private boolean isMaintainTask=false;
    public ChangeEquipmentDialog(Context context, int layout, int style,boolean tag,boolean tag2,boolean tag3,boolean tag4,String Equipment_num) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        hud=KProgressHUD.create(context);
        is_Main_person_in_charge_operator_id=tag;
        isOneOperator=tag2;
        isNoEuqipment=tag3;
        isMaintainTask=tag4;
        //if(Equipment_OperatorID_Status.get())
      //  Collections.addAll(status,context.getResources().getStringArray(R.array.equip_status));
        if(Equipment_num!=null){
            findViewById(R.id.Equipment_info_layout).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.Equipment_info)).setText(context.getResources().getString(R.string.device_num)+Equipment_num);
        }
        initMap();
        initData();
        initview();
        setCanceledOnTouchOutside(false);

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
//        findViewById(R.id.comfirm).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(ViewTag==1){
//                   postTaskOperatorEquipment(Equipment_Operator_Status_Name_ID_map.get(Status.getSelectionItem().toString()));
//                    }
//                    else if(ViewTag==2){
//                    postTaskEquipment(Equipment_Status_Name_ID_map.get((Status.getSelectionItem().toString())));
//                    }
//            }
//        });
//        initTagButton();
//        showList.addAll(Equipment_Operator_Status_List);
//        Status=(WheelView)findViewById(R.id.WheelView);
//        Status.setWheelAdapter(new MyWheelAdapter(context));
//        Status.setSkin(com.wx.wheelview.widget.WheelView.Skin.Holo);
//        Status.setWheelData(showList);
//        Status.setWheelSize(5);
//        Status.setDividerHeight(2);
    //   findViewById(R.id.dialog).setTranslationY((-1)*getNavigationBarHeight(context));

      //  initEquipmentTagView();
        ListView listView = (ListView) findViewById(R.id.listView);
        TaskAdapter adapter = new TaskAdapter(showList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.dialog_item, parent, false);
                    holder = new TaskViewHolder();
                    holder.image = (ImageView) convertView.findViewById(R.id.image);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.status);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                // if(!showList.get(position).get("Type").valueAsString().equals("delete")){
                if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentStatus")) {
                    holder.image.setImageResource(R.mipmap.equipment_status);
                } else if (DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentOperatorStatus")) {
                    holder.image.setImageResource(R.mipmap.equipment_operator_status_mipmap);
                } else {
                    holder.image.setImageResource(R.mipmap.delete_equipment);
                }
                holder.tv_task_state.setText(DataUtil.isDataElementNull(showList.get(position).get("Status")));
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        adapter.setDatas(showList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //修改设备状态
                if(DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentStatus")){
                    postTaskEquipment(Equipment_Status_Name_ID_map.get(DataUtil.isDataElementNull(showList.get(position).get("Status"))));
                }else if(DataUtil.isDataElementNull(showList.get(position).get("Type")).equals("EquipmentOperatorStatus")){
                    //修改设备参与人状态
                    if(isNoEuqipment){
                        ChangeTaskOperatorStatus(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(showList.get(position).get("Status"))));
                    }else {
                        postTaskOperatorEquipment(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(showList.get(position).get("Status"))));
                    }
                }
                else if(DataUtil.isDataElementNull(showList.get(position).get("Type")).equals(DELETE)){
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setMessage(R.string.sureDeleteEquipment);
                    builder.setPositiveButton(R.string.sure, new OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteEquipment();
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
    }



    private void postTaskEquipment(int status) {
        showCustomDialog(R.string.submitData);
        HttpParams params = new HttpParams();

        JsonObjectElement taskEquepment=new JsonObjectElement();
//创建任务提交数据：任务创建人，任务类型“T01”那些，几台号（数组），
        taskEquepment.set(Task.TASK_ID,TaskId);
        //若任务未有设备，则输入为0，表示添加
        taskEquepment.set("TaskEquipment_ID",TaskEquipmentId);
        //若已有设备，申请状态变更
        taskEquepment.set("Equipment_ID", EquipmentId);
        //taskEquepment.set("Equipment_ID", equipmentID);
        taskEquepment.set("Status",status);

        params.putJsonParams(taskEquepment.toJson());

        HttpUtils.post(context, "TaskEquipmentAPI/ModifyTaskEquipmentStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                        if(jsonObjectElement.get("Success")!=null&&jsonObjectElement.get("Success").valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.SuccessChangeStatus,context);
                        dismiss();
                            onSubmitInterface.onsubmit();}else{
                            if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).equals("")){
                                ToastUtil.showToastShort(R.string.FailChangeEquipmentStatusCauseByOperator,context);
                            }else {
                                TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                            }
                        }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                   ToastUtil.showToastShort(R.string.FailChangeEquipmentStatusCauseByTimeOut,context);
                dismissCustomDialog();
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
       // int id = v.getId();
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
                   }
                else {
                   ToastUtil.showToastShort(R.string.onlyTaskChargerCanChangeEquipmentStatus,context);
               }
            }
        });
    }
    private void postTaskOperatorEquipment(int status){
        showCustomDialog(R.string.submitData);
      HttpParams params=new HttpParams();
       // JsonObjectElement TaskOperatorDataToSubmit=new JsonObjectElement();
     //   TaskOperatorDataToSubmit.set("task_id",Integer.valueOf(TaskId));
     //   TaskOperatorDataToSubmit.set("equipment_id",Integer.valueOf(EquipmentId));
     //   TaskOperatorDataToSubmit.set("TaskEquipment_ID",Integer.valueOf(TaskEquipmentId));
     //   TaskOperatorDataToSubmit.set("status",status);
     //   params.putJsonParams(TaskOperatorDataToSubmit.toJson());
        HttpUtils.post(context, "TaskOperatorAPI/MotifyTaskOperatorStatus?task_id="+TaskId+"&equipment_id="+EquipmentId+"&status="+status,
                params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("Success").valueAsBoolean()){
                        onSubmitInterface.onsubmit();
                        dismiss();
                    }else {
                        if(DataUtil.isDataElementNull(jsonObjectElement.get("Msg")).equals("")){
                            ToastUtil.showToastShort(R.string.CanNotChangeStatus,context);
                        }else {
                            TipsUtil.ShowTips(context,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                        }
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.failToChangeStatus,context);
                dismissCustomDialog();
            }
        });
    }
    private void StatusControl(int Status){

    }
    public void initData(){
        ArrayList<String> arrayList=new ArrayList<>();
        ArrayList<String> list=new ArrayList<>();
        Collections.addAll(arrayList,context.getResources().getStringArray(R.array.Equipment_Status));
        Collections.addAll(list,context.getResources().getStringArray(R.array.Equipment_Operator_Status));

        for(String ss:list){
            JsonObjectElement json=new JsonObjectElement();
            json.set("Status",ss);
            json.set("Type","EquipmentOperatorStatus");
            Equipment_Operator_Status_List.add(json);
        }


        if(is_Main_person_in_charge_operator_id&&!isOneOperator){
//            JsonObjectElement jsonObjectElement=new JsonObjectElement();
//            jsonObjectElement.set("Status",context.getResources().getString(R.string.deleteEquipment));
//            jsonObjectElement.set("Type","delete");
//            showList.add(jsonObjectElement);
            for(String s:arrayList){
                JsonObjectElement json=new JsonObjectElement();
                json.set("Status",s);
                json.set("Type","EquipmentStatus");
                Equipment_Status_List.add(json);
            }
        showList.addAll(Equipment_Status_List);
        }
        showList.addAll(Equipment_Operator_Status_List);
        if(isOneOperator&&!isNoEuqipment&&!isMaintainTask){//单人操作
        JsonObjectElement json=new JsonObjectElement();
        json.set("Status",context.getResources().getString(R.string.deleteEquipment));
        json.set("Type",DELETE);
        showList.add(0,json);
        }else if(is_Main_person_in_charge_operator_id&&!isOneOperator&&!isNoEuqipment&&!isMaintainTask){//多人操作，主负责人控制
            JsonObjectElement json=new JsonObjectElement();
            json.set("Status",context.getResources().getString(R.string.deleteEquipment));
            json.set("Type",DELETE);
            showList.add(0,json);
        }
       // adapter.notifyDataSetChanged();
    }
    private void initMap(){

            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.start), 0);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.pause), 2);
//            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.quit), 2);
//            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.material_requisition), 3);
//            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.wait_material), 4);
            Equipment_Operator_Status_Name_ID_map.put(context.getResources().getString(R.string.complete), 1);


            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.process), 1);
            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.pause), 3);
//            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.wait_material), 3);
            Equipment_Status_Name_ID_map.put(context.getResources().getString(R.string.complete), 2);

    }
    private static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            Log.w("", e);
        }

        return hasNavigationBar;

    }
    //获取NavigationBar的高度：
    private static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }
    public KProgressHUD initCustomDialog(int resId) {
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(context.getResources().getString(resId))
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
    public void deleteEquipment(){
        showCustomDialog(R.string.deletingEquipment);
        HttpParams params=new HttpParams();
        //IDList即TaskEquipmentId对应删除字段
        //params.put("id",TaskEquipmentId);
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set("IDList",TaskEquipmentId);
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(context, "TaskEquipmentAPI/TaskEquipmentDelete", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.submitFail,context);
                dismissCustomDialog();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.deleteEquipmentSuccess,context);
                        onSubmitInterface.onsubmit();
                        dismiss();
                    }else {
                        ToastUtil.showToastShort(R.string.deleteEquipmentFail,context);
                    }
                }
                dismissCustomDialog();
            }
        });
    }

    public void setOneOperator(boolean oneOperator) {
        isOneOperator = oneOperator;
    }

    private boolean isOneOperator=false;

    public void setTaskOperatorID(int taskOperatorID) {
        TaskOperatorID = taskOperatorID;
    }

    private int TaskOperatorID=0;
    private void ChangeTaskOperatorStatus(int Status){
       showCustomDialog(R.string.submitData);
       HttpParams params=new HttpParams();
       JsonObjectElement submitData=new JsonObjectElement();
       submitData.set("TaskOperator_ID",TaskOperatorID);
       submitData.set("Status",Status);
       params.putJsonParams(submitData.toJson());
       HttpUtils.post(context, "TaskOperatorAPI/MotifyTaskOperatorStatusForSimple", params, new HttpCallback() {
           @Override
           public void onSuccess(String t) {
               super.onSuccess(t);
               if(t!=null){
                   JsonObjectElement data=new JsonObjectElement(t);
                   if(data.get(Data.SUCCESS).valueAsBoolean()){
                       onSubmitInterface.onsubmit();
                       dismiss();
                   }else {
                       if(DataUtil.isDataElementNull(data.get("Msg")).equals("")){
                       ToastUtil.showToastShort(R.string.CanNotChangeStatus,context);
                       }else {
                           TipsUtil.ShowTips(context,DataUtil.isDataElementNull(data.get("Msg")));
                       }
                   }
               }
               dismissCustomDialog();
           }

           @Override
           public void onFailure(int errorNo, String strMsg) {
               super.onFailure(errorNo, strMsg);
               ToastUtil.showToastShort(R.string.failToChangeStatus,context);
               dismissCustomDialog();
           }
       });
   }
}