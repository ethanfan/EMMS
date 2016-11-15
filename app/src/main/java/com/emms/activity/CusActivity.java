package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.MainActivityAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.BaseData;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.flyco.tablayout.widget.MsgView;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/8/23.
 *
 */
public class CusActivity extends NfcActivity implements View.OnClickListener{
    private Context context=this;
    private ArrayList<ObjectElement> moduleList=new ArrayList<>();
    private static HashMap<String,Integer> TaskClass_moduleID_map=new HashMap<>();
    private HashMap<Integer,ObjectElement> ID_module_map=new HashMap<>();
    private MainActivityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus);
        {
            TaskClass_moduleID_map.put(Task.REPAIR_TASK,3);//维修任务
            TaskClass_moduleID_map.put(Task.MAINTAIN_TASK,2);//维护任务
            TaskClass_moduleID_map.put(Task.MOVE_CAR_TASK,4);//搬车任务
            TaskClass_moduleID_map.put(Task.OTHER_TASK,5);//其它任务
            TaskClass_moduleID_map.put(Task.ROUTING_INSPECTION,11);//点巡检
            TaskClass_moduleID_map.put(Task.UPKEEP,12);//保养
            TaskClass_moduleID_map.put(Task.MOVE_CAR_TASK,13);//搬车任务
            TaskClass_moduleID_map.put(Task.TRANSFER_MODEL_TASK,14);//调车任务
            TaskClass_moduleID_map.put("C2",8); //工时审核
            TaskClass_moduleID_map.put("C1",10);//任务审核
            TaskClass_moduleID_map.put("C3",7); //任务历史
        }
        initView();
        initData();
        getTaskCountFromServer();
        if(getIntent().getStringExtra(Constants.FLAG_CREATE_SHUNTING_TASK)!=null){
            Intent intent=new Intent(this,CreateTaskActivity.class);
            intent.putExtra(Constants.FLAG_CREATE_SHUNTING_TASK,Constants.FLAG_CREATE_SHUNTING_TASK);
            if(getIntent().getStringExtra("OperatorInfo")!=null){
                intent.putExtra("OperatorInfo",getIntent().getStringExtra("OperatorInfo"));
            }
            startActivity(intent);
        }
        if(getIntent().getStringExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK)!=null){
            Intent intent=new Intent(this,CreateTaskActivity.class);
            intent.putExtra(Constants.FLAG_CREATE_CAR_MOVING_TASK,Constants.FLAG_CREATE_CAR_MOVING_TASK);
            if(getIntent().getStringExtra("OperatorInfo")!=null){
                intent.putExtra("OperatorInfo",getIntent().getStringExtra("OperatorInfo"));
            }
            startActivity(intent);
        }
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void initView(){
        Button btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);
        if(getLoginInfo()!=null) {
            ((TextView) findViewById(R.id.UserName)).setText(getLoginInfo().getName());
            ((TextView) findViewById(R.id.WorkNum_tag)).setText(getLoginInfo().getOperator_no());
        }
        GridView module_list = (GridView) findViewById(R.id.module_list);
        adapter=new MainActivityAdapter(moduleList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                MainActivityAdapter.TaskViewHolder holder;
//                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_cur, parent, false);
                    holder = new MainActivityAdapter.TaskViewHolder();
                    holder.image=(ImageView)convertView.findViewById(R.id.module_image);
                    holder.moduleName=(TextView)convertView.findViewById(R.id.module_name);
                    holder.msgView=(MsgView)convertView.findViewById(R.id.task_num);
                    convertView.setTag(holder);
//                } else {
//                    holder = (MainActivityAdapter.TaskViewHolder) convertView.getTag();
//                }
                if(moduleList.get(position).get("module_image")!=null){
                holder.image.setImageResource(moduleList.get(position).get("module_image").valueAsInt());}
                if(moduleList.get(position).get("module_name")!=null){
                holder.moduleName.setText(moduleList.get(position).get("module_name").valueAsInt());}
                if(moduleList.get(position).get("TaskNum")!=null){
                    holder.msgView.setVisibility(View.VISIBLE);
                    switch (moduleList.get(position).get("TaskNumType").valueAsInt()){
                        case 0: {
                        break;
                        }
                        case 1:{
                            String s[]=DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")).split("/");
                            if(Integer.valueOf(s[1])==0){
                                holder.msgView.setBgSelector2();
                            }
                            break;
                        }case 2:{
                            if(moduleList.get(position).get("TaskNum").valueAsInt()==0){
                                holder.msgView.setBgSelector2();
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                    holder.msgView.setText(DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")));
                }
                return convertView;
            }
        };
        module_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(moduleList.get(position).get("Class")!=null){
                    try {
                        Class c=Class.forName(DataUtil.isDataElementNull(moduleList.get(position).get("Class")));
                        Intent intent=new Intent(context,c);
                        if(moduleList.get(position).get(Task.TASK_CLASS)!=null){
                            intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(moduleList.get(position).get(Task.TASK_CLASS)));
                        }
                        if(moduleList.get(position).get("TaskNum")!=null){
                            intent.putExtra("TaskNum",DataUtil.isDataElementNull(moduleList.get(position).get("TaskNum")));
                        }
                        if(moduleList.get(position).get(Task.TASK_SUBCLASS)!=null){
                            intent.putExtra(Task.TASK_SUBCLASS,DataUtil.isDataElementNull(moduleList.get(position).get(Task.TASK_SUBCLASS)));
                        }
                        startActivity(intent);
                    }catch (Throwable e){
                        CrashReport.postCatchedException(e);
                    }
                }
            }
        });
        module_list.setAdapter(adapter);
//        if(getLoginInfo().isMaintenMan()){
//            ((ImageView)findViewById(R.id.rootImage)).setImageResource(R.mipmap.repairer);
//        }else {
//            ((ImageView)findViewById(R.id.rootImage)).setImageResource(R.mipmap.applicant);
//        }
        //TODO
        if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(this))==7){
            ((ImageView)findViewById(R.id.rootImage)).setImageResource(R.mipmap.applicant);
        }else if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(this))<5){
            ((ImageView)findViewById(R.id.rootImage)).setImageResource(R.mipmap.repairerleader);
        }else {
            ((ImageView)findViewById(R.id.rootImage)).setImageResource(R.mipmap.repairer);
        }
    }
    private void initData(){
        if(getIntent().getStringExtra("Module_ID_List")!=null&&!getIntent().getStringExtra("Module_ID_List").equals("")) {
            String module = getIntent().getStringExtra("Module_ID_List");
            String[] modules = module.split(",");
            for (String module1 : modules) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                jsonObjectElement.set("module_ID", Integer.valueOf(module1));
                jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                ID_module_map.put(Integer.valueOf(module1), jsonObjectElement);
                moduleList.add(jsonObjectElement);
            }
//            JsonObjectElement json=new JsonObjectElement();
//            json.set("module_ID", 11);
//            json = moduleMatchingRule(json);
//            ID_module_map.put(11, json);
//            moduleList.add(json);
//            JsonObjectElement json2=new JsonObjectElement();
//            json2.set("module_ID", 12);
//            json2 = moduleMatchingRule(json2);
//            ID_module_map.put(12, json2);
//            moduleList.add(json2);
        }else {
            if(!SharedPreferenceManager.getUserModuleList(this).equals("")){
                String module = SharedPreferenceManager.getUserModuleList(this);
                String[] modules = module.split(",");
                for (String module1 : modules) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement();
                    jsonObjectElement.set("module_ID", Integer.valueOf(module1));
                    jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                    ID_module_map.put(Integer.valueOf(module1), jsonObjectElement);
                    moduleList.add(jsonObjectElement);
                }
            }else {
                for (int i = 0; i < 10; i++) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement();
                    jsonObjectElement.set("module_ID", i + 1);
                    jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                    ID_module_map.put(i + 1, jsonObjectElement);
                    moduleList.add(jsonObjectElement);
                }
            }
        }

//        if(moduleList.size()<=6){
//            module_list.setNumColumns(2);
//        }
    }
    //个性化开发，根据服务器返回的角色模块ID进行个性化配置
    private JsonObjectElement moduleMatchingRule(JsonObjectElement obj){
         int module_id=obj.get("module_ID").valueAsInt();
        String packageName="com.emms.activity.";
        switch (module_id){
            case 1:{//createTask
                obj.set("module_image",R.mipmap.cur_activity_create_task);
                obj.set("module_name",R.string.create_task);
                obj.set("Class",packageName+"CreateTaskActivity");
                obj.set("TaskNumType",0);
                break;
            }
            case 2:{//maintainTask
                obj.set("module_image",R.mipmap.cur_activity_maintain);
                obj.set("module_name",R.string.maintenance);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 3:{//repairTask
                obj.set("module_image",R.mipmap.cur_activity_repair);
                obj.set("module_name",R.string.repair);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.REPAIR_TASK);
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 4:{//moveCarTask
                obj.set("module_image",R.mipmap.cur_activity_move_car);
                obj.set("module_name",R.string.move_car);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.MOVE_CAR_TASK);
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 5:{//teamStatus
                obj.set("module_image",R.mipmap.cur_activity_other);
                obj.set("module_name",R.string.other);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.OTHER_TASK);
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 6:{//deviceFaultSummary
                obj.set("module_image",R.mipmap.cur_activity_equipment_summary);
                obj.set("module_name",R.string.DeveceHistory);
                obj.set("Class",packageName+"EquipmentHistory");
                obj.set("TaskNumType",0);
                break;
            }
            case 7:{//TaskCommand
                obj.set("module_image",R.mipmap.cur_activity_task_history);
                obj.set("module_name",R.string.taskHistory);
                obj.set("Class",packageName+"TaskHistoryCheck");
                obj.set("TaskNum","0");
                obj.set("TaskNumType",2);
                //obj.set(Task.TASK_CLASS,Task.REPAIR_TASK);
                break;
            }
            case 8:{//workloadverify
                obj.set("module_image",R.mipmap.cur_activity_workload_verify);
                obj.set("module_name",R.string.workloadVerify);
                obj.set("Class",packageName+"WorkloadVerifyActivity");
                obj.set("TaskNum","0");
                obj.set("TaskNumType",2);
                break;
            }
            case 9:{//otherTask
                obj.set("module_image",R.mipmap.cur_activity_team);
                obj.set("module_name",R.string.team);
                obj.set("Class",packageName+"TeamStatusActivity");
                obj.set("TaskNumType",0);
                break;
            }
            case 10:{//taskverify
                obj.set("module_image",R.mipmap.cur_activity_verify);
                obj.set("module_name",R.string.TaskVerify);
                obj.set("Class",packageName+"TaskVerifyActivity");
                obj.set("TaskNum","0");
                obj.set("TaskNumType",2);
                break;
            }
            case 11:{//巡检
                obj.set("module_image",R.mipmap.module_measure_point);
                obj.set("module_name",R.string.routingInspection);
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set(Task.TASK_SUBCLASS,Task.ROUTING_INSPECTION);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 12:{//保养
                obj.set("module_image",R.mipmap.module_upkeep);
                obj.set("module_name",R.string.upkeep);
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set(Task.TASK_SUBCLASS,Task.UPKEEP);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 13:{//搬车
                obj.set("module_image",R.mipmap.cur_activity_move_car);
                obj.set("module_name",R.string.move_car);
                obj.set(Task.TASK_CLASS,Task.MOVE_CAR_TASK);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
            case 14:{//转款
                obj.set("module_image",R.mipmap.model_transfer_model);
                obj.set("module_name",R.string.transfer_model);
                obj.set(Task.TASK_CLASS,Task.TRANSFER_MODEL_TASK);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set("TaskNum","0/0");
                obj.set("TaskNumType",1);
                break;
            }
        }
        return obj;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getTaskCountFromServer();
        getDBDataLastUpdateTime();
    }
    //获取任务数量
    private void getTaskCountFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        //params.put("id",String.valueOf(getLoginInfo().getId()));
        // String s=SharedPreferenceManager.getUserName(this);
        HttpUtils.get(this, "TaskAPI/TaskNum", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonArrayElement json = new JsonArrayElement(t);
                    //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                        for (int i = 0; i < json.size(); i++) {
                            //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                            //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                            if(json.get(i).asObjectElement().get("DoingNo")!=null&&
                                    json.get(i).asObjectElement().get("ToDoNo")!=null) {
                                ObjectElement jsonObjectElement=json.get(i).asObjectElement();
                                optimizationData(jsonObjectElement,"ToDoNo","DoingNo");
                                String taskNumToShow;
                                if(DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")).equals("C1")
                                        ||DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")).equals("C2")
                                        ||DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")).equals("C3")){
                                    taskNumToShow=DataUtil.isDataElementNull(jsonObjectElement.get("ToDoNo"));
                                }else {
                                    taskNumToShow = DataUtil.isDataElementNull(jsonObjectElement.get("DoingNo")) + "/" +
                                            DataUtil.isDataElementNull(jsonObjectElement.get("ToDoNo"));
                                }
                                if(ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass"))))!=null){
                               ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(jsonObjectElement.get("TaskClass")))).set("TaskNum",taskNumToShow);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                }
                BaseData.setBaseData(context);
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                BaseData.setBaseData(context);
                if(errorNo==401){
                    ToastUtil.showToastShort(R.string.unauthorization,context);
                    dismissCustomDialog();
                    return;
                }
                ToastUtil.showToastShort(R.string.loadingFail,context);
                dismissCustomDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_exit) {
            showCustomDialog(R.string.logout);
            HttpParams params = new HttpParams();
            HttpUtils.delete(this, "Token", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    dismissCustomDialog();
                    if(!JPushInterface.isPushStopped(context)){
                    JPushInterface.stopPush(context);}
                    logout();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    dismissCustomDialog();
                    if(!JPushInterface.isPushStopped(context)) {
                        JPushInterface.stopPush(context);
                    }
                    logout();
                }
            });
        }
    }

    /**
     * When the User Logout ,clear all the User Info from SharePreference except Account
     */
    private void logout(){
        SharedPreferenceManager.setPassWord(CusActivity.this,null);
        SharedPreferenceManager.setCookie(CusActivity.this,null);
        SharedPreferenceManager.setLoginData(CusActivity.this,null);
        SharedPreferenceManager.setUserData(CusActivity.this,null);
        SharedPreferenceManager.setMsg(CusActivity.this,null);
        SharedPreferenceManager.setUserRoleID(CusActivity.this,null);
        Intent intent=new Intent(CusActivity.this, LoginActivity.class);
        intent.putExtra("FromCusActivity",true);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    private void optimizationData(ObjectElement data,String key1,String key2){
        if(data.get(key1)!=null
                &&DataUtil.isNum(DataUtil.isDataElementNull(data.get(key1)))
                &&data.get(key1).valueAsInt()>=100){
            data.set(key1,"99+");
        }
        if(data.get(key2)!=null
                &&DataUtil.isNum(DataUtil.isDataElementNull(data.get(key2)))
                &&data.get(key2).valueAsInt()>=100){
            data.set(key2,"99+");
        }
    }
}
