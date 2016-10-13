package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.MainActivityAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.flyco.tablayout.widget.MsgView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/8/23.
 */
public class CusActivity extends NfcActivity implements View.OnClickListener{
    private Button btn_exit;
    private GridView module_list;
    private Context context=this;
    private ArrayList<ObjectElement> moduleList=new ArrayList<>();
    private static HashMap<String,Integer> TaskClass_moduleID_map=new HashMap<>();{
        TaskClass_moduleID_map.put(Task.REPAIR_TASK,3);
        TaskClass_moduleID_map.put(Task.MAINTAIN_TASK,2);
        TaskClass_moduleID_map.put(Task.MOVE_CAR_TASK,4);
        TaskClass_moduleID_map.put(Task.OTHER_TASK,5);
        TaskClass_moduleID_map.put(Task.ROUTING_INSPECTION,11);
        TaskClass_moduleID_map.put(Task.UPKEEP,12);
        TaskClass_moduleID_map.put("C2",8);
        TaskClass_moduleID_map.put("C1",10);
        TaskClass_moduleID_map.put("C3",7);
    }
    private HashMap<Integer,ObjectElement> ID_module_map=new HashMap<>();
    private MainActivityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus);
        initView();
        initData();
        getTaskCountFromServer();
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void initView(){
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(this);
        ((TextView)findViewById(R.id.UserName)).setText(getLoginInfo().getName());
        ((TextView)findViewById(R.id.WorkNum_tag)).setText(getLoginInfo().getOperator_no());
        module_list=(GridView)findViewById(R.id.module_list);
        adapter=new MainActivityAdapter(moduleList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                MainActivityAdapter.TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_cur, parent, false);
                    holder = new MainActivityAdapter.TaskViewHolder();
                    holder.image=(ImageView)convertView.findViewById(R.id.module_image);
                    holder.moduleName=(TextView)convertView.findViewById(R.id.module_name);
                    holder.msgView=(MsgView)convertView.findViewById(R.id.task_num);
                    convertView.setTag(holder);
                } else {
                    holder = (MainActivityAdapter.TaskViewHolder) convertView.getTag();
                }
                if(moduleList.get(position).get("module_image")!=null){
                holder.image.setImageResource(moduleList.get(position).get("module_image").valueAsInt());}
                if(moduleList.get(position).get("module_name")!=null){
                holder.moduleName.setText(moduleList.get(position).get("module_name").valueAsInt());}
                if(moduleList.get(position).get("TaskNum")!=null){
                    holder.msgView.setVisibility(View.VISIBLE);
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
                    }catch (Exception e){
                        Log.e("e",e.toString());
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
        JsonObjectElement data=new JsonObjectElement(SharedPreferenceManager.getLoginData(this));
        if(getIntent().getStringExtra("Module_ID_List")!=null&&!getIntent().getStringExtra("Module_ID_List").equals("")) {
            String module = getIntent().getStringExtra("Module_ID_List");
            String[] modules = module.split(",");
            for (int i = 0; i < modules.length; i++) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement();
                jsonObjectElement.set("module_ID", Integer.valueOf(modules[i]));
                jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                ID_module_map.put(Integer.valueOf(modules[i]), jsonObjectElement);
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
                for (int i = 0; i < modules.length; i++) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement();
                    jsonObjectElement.set("module_ID", Integer.valueOf(modules[i]));
                    jsonObjectElement = moduleMatchingRule(jsonObjectElement);
                    ID_module_map.put(Integer.valueOf(modules[i]), jsonObjectElement);
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
                break;
            }
            case 2:{//maintainTask
                obj.set("module_image",R.mipmap.cur_activity_maintain);
                obj.set("module_name",R.string.maintenance);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 3:{//repairTask
                obj.set("module_image",R.mipmap.cur_activity_repair);
                obj.set("module_name",R.string.repair);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.REPAIR_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 4:{//moveCarTask
                obj.set("module_image",R.mipmap.cur_activity_move_car);
                obj.set("module_name",R.string.move_car);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.MOVE_CAR_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 5:{//teamStatus
                obj.set("module_image",R.mipmap.cur_activity_other);
                obj.set("module_name",R.string.other);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.OTHER_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 6:{//deviceFaultSummary
                obj.set("module_image",R.mipmap.cur_activity_equipment_summary);
                obj.set("module_name",R.string.DeveceHistory);
                obj.set("Class",packageName+"EquipmentHistory");
                break;
            }
            case 7:{//TaskCommand
                obj.set("module_image",R.mipmap.cur_activity_task_history);
                obj.set("module_name",R.string.taskHistory);
                obj.set("Class",packageName+"TaskHistoryCheck");
                obj.set("TaskNum","0");
                //obj.set(Task.TASK_CLASS,Task.REPAIR_TASK);
                break;
            }
            case 8:{//workloadverify
                obj.set("module_image",R.mipmap.cur_activity_workload_verify);
                obj.set("module_name",R.string.workloadVerify);
                obj.set("Class",packageName+"WorkloadVerifyActivity");
                obj.set("TaskNum","0");
                break;
            }
            case 9:{//otherTask
                obj.set("module_image",R.mipmap.cur_activity_team);
                obj.set("module_name",R.string.team);
                obj.set("Class",packageName+"TeamStatusActivity");
                break;
            }
            case 10:{//taskverify
                obj.set("module_image",R.mipmap.cur_activity_verify);
                obj.set("module_name",R.string.TaskVerify);
                obj.set("Class",packageName+"TaskVerifyActivity");
                obj.set("TaskNum","0");
                break;
            }
            case 11:{//巡检
                obj.set("module_image",R.mipmap.module_measure_point);
                obj.set("module_name",R.string.routingInspection);
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set(Task.TASK_SUBCLASS,Task.ROUTING_INSPECTION);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set("TaskNum","0/0");
                break;
            }
            case 12:{//保养
                obj.set("module_image",R.mipmap.module_upkeep);
                obj.set("module_name",R.string.upkeep);
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set(Task.TASK_SUBCLASS,Task.UPKEEP);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set("TaskNum","0/0");
                break;
            }
        }
        return obj;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getTaskCountFromServer();
    }
    //获取任务数量
    private void getTaskCountFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        //params.put("id",String.valueOf(getLoginInfo().getId()));
        // String s=SharedPreferenceManager.getUserName(this);
        HttpUtils.get(this, "TaskNum", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if (t != null) {
                    JsonObjectElement json = new JsonObjectElement(t);
                    //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                    if (json.get("PageData") != null && json.get("PageData").asArrayElement() != null&&json.get("PageData").asArrayElement().size()>0) {
                        for (int i = 0; i < json.get("PageData").asArrayElement().size(); i++) {
                            //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                            //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                            if(json.get("PageData").asArrayElement().get(i).asObjectElement().get("S1")!=null&&
                                    json.get("PageData").asArrayElement().get(i).asObjectElement().get("S0")!=null) {
                                ObjectElement jsonObjectElement=json.get("PageData").asArrayElement().get(i).asObjectElement();
                                String taskNumToShow;
                                if(DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")).equals("C1")
                                        ||DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")).equals("C2")
                                        ||DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")).equals("C3")){
                                    taskNumToShow=DataUtil.isDataElementNull(jsonObjectElement.get("S0"));
                                }else {
                                    taskNumToShow = DataUtil.isDataElementNull(jsonObjectElement.get("S1")) + "/" +
                                            DataUtil.isDataElementNull(jsonObjectElement.get("S0"));
                                }
                                if(ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(jsonObjectElement.get("DataCode"))))!=null){
                               ID_module_map.get(TaskClass_moduleID_map.get(DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")))).set("TaskNum",taskNumToShow);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                if(errorNo==401){
                    ToastUtil.showToastLong(R.string.unauthorization,context);
                    dismissCustomDialog();
                    return;
                }
                ToastUtil.showToastLong(R.string.loadingFail,context);
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
}
