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
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/23.
 */
public class CusActivity extends NfcActivity implements View.OnClickListener{
    private Button btn_exit;
    private GridView model_list;
    private Context context=this;
    private ArrayList<ObjectElement> ModelList=new ArrayList<>();
    private static HashMap<String,Integer> TaskClass_ModelID_map=new HashMap<String, Integer>();{
        TaskClass_ModelID_map.put(Task.REPAIR_TASK,3);
        TaskClass_ModelID_map.put(Task.MAINTAIN_TASK,2);
        TaskClass_ModelID_map.put(Task.MOVE_CAR_TASK,4);
        TaskClass_ModelID_map.put(Task.OTHER_TASK,9);
    }
    private HashMap<Integer,ObjectElement> ID_Model_map=new HashMap<Integer, ObjectElement>();
    private MainActivityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cus);
        initData();
        initView();
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
        model_list=(GridView)findViewById(R.id.model_list);
        adapter=new MainActivityAdapter(ModelList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                MainActivityAdapter.TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_main, parent, false);
                    holder = new MainActivityAdapter.TaskViewHolder();
                    holder.image=(ImageView)convertView.findViewById(R.id.model_image);
                    holder.modelName=(TextView)convertView.findViewById(R.id.model_name);
                    holder.taskNum=(TextView)convertView.findViewById(R.id.task_num);
                    convertView.setTag(holder);
                } else {
                    holder = (MainActivityAdapter.TaskViewHolder) convertView.getTag();
                }
                if(ModelList.get(position).get("model_image")!=null){
                holder.image.setImageResource(ModelList.get(position).get("model_image").valueAsInt());}
                if(ModelList.get(position).get("model_name")!=null){
                holder.modelName.setText(ModelList.get(position).get("model_name").valueAsInt());}
                holder.taskNum.setText(DataUtil.isDataElementNull(ModelList.get(position).get("TaskNum")));
                return convertView;
            }
        };
        model_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ModelList.get(position).get("Class")!=null){
                    try {
                        Class c=Class.forName(DataUtil.isDataElementNull(ModelList.get(position).get("Class")));
                        Intent intent=new Intent(context,c);
                        if(ModelList.get(position).get(Task.TASK_CLASS)!=null){
                            intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(ModelList.get(position).get(Task.TASK_CLASS)));
                        }
                        if(ModelList.get(position).get("TaskNum")!=null){
                            intent.putExtra("TaskNum",DataUtil.isDataElementNull(ModelList.get(position).get("TaskNum")));
                        }
                        startActivity(intent);
                    }catch (Exception e){
                        Log.e("e",e.toString());
                    }
                }
            }
        });
        model_list.setAdapter(adapter);
    }
    private void initData(){
        for(int i=0;i<9;i++ ){
            JsonObjectElement jsonObjectElement=new JsonObjectElement();
            jsonObjectElement.set("model_ID",i+1);
           // jsonObjectElement.set("model_name","维修");
            //jsonObjectElement.set("taskNum","9/8");
            jsonObjectElement=ModelMatchingRule(jsonObjectElement);
            ID_Model_map.put(i+1,jsonObjectElement);
            ModelList.add(jsonObjectElement);
        }
    }
    private JsonObjectElement ModelMatchingRule(JsonObjectElement obj){
         int model_id=obj.get("model_ID").valueAsInt();
        String packageName="com.emms.activity.";
        switch (model_id){
            case 1:{//createTask
                obj.set("model_image",R.mipmap.mainactivity_createtask);
                obj.set("model_name",R.string.create_task);
                obj.set("Class",packageName+"CreateTaskActivity");
                break;
            }
            case 2:{//maintainTask
                obj.set("model_image",R.mipmap.mainactivity_maintain);
                obj.set("model_name",R.string.maintenance);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.MAINTAIN_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 3:{//repairTask
                obj.set("model_image",R.mipmap.mainactivity_repair);
                obj.set("model_name",R.string.repair);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.REPAIR_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 4:{//moveCarTask
                obj.set("model_image",R.mipmap.mainactivity_move_car);
                obj.set("model_name",R.string.move_car);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.MOVE_CAR_TASK);
                obj.set("TaskNum","0/0");
                break;
            }
            case 5:{//teamStatus
                obj.set("model_image",R.mipmap.mainactivity_team_person_status);
                obj.set("model_name",R.string.team_status);
                //obj.set("Class",TaskListActivity.class.toString());
                break;
            }
            case 6:{//deviceFaultSummary
                obj.set("model_image",R.mipmap.mainactivity_device_fault_summary);
                obj.set("model_name",R.string.DeveceHistory);
                obj.set("Class",packageName+"EquipmentHistory");
                break;
            }
            case 7:{//TaskCommand
                obj.set("model_image",R.mipmap.mainactivity_taskcommand);
                obj.set("model_name",R.string.taskHistory);
                obj.set("Class",packageName+"TaskHistory");
                obj.set(Task.TASK_CLASS,Task.REPAIR_TASK);
                break;
            }
            case 8:{//workloadverify
                obj.set("model_image",R.mipmap.mainactivity_workload_verify);
                obj.set("model_name",R.string.workloadVerify);
               // obj.set("Class",TaskListActivity.class.toString());
                break;
            }
            case 9:{//otherTask
                obj.set("model_image",R.mipmap.mainactivity_other);
                obj.set("model_name",R.string.other);
                obj.set("Class",packageName+"TaskListActivity");
                obj.set(Task.TASK_CLASS,Task.OTHER_TASK);
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
                                String taskNumToShow = json.get("PageData").asArrayElement().get(i).asObjectElement().get("S1").valueAsString() + "/" +
                                        json.get("PageData").asArrayElement().get(i).asObjectElement().get("S0").valueAsString();
                                if(ID_Model_map.get(TaskClass_ModelID_map.get(DataUtil.isDataElementNull(json.get("PageData").asArrayElement()
                                        .get(i).asObjectElement().get("DataCode"))))!=null){
                               ID_Model_map.get(TaskClass_ModelID_map.get(DataUtil.isDataElementNull(json.get("PageData").asArrayElement()
                                        .get(i).asObjectElement().get("DataCode")))).set("TaskNum",taskNumToShow);
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
                    logout();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                    dismissCustomDialog();
                    logout();
                }
            });
        }
    }
    private void logout(){
        SharedPreferenceManager.setPassWord(CusActivity.this,null);
        SharedPreferenceManager.setCookie(CusActivity.this,null);
        SharedPreferenceManager.setLoginData(CusActivity.this,null);
        SharedPreferenceManager.setUserData(CusActivity.this,null);
        startActivity(new Intent(CusActivity.this, LoginActivity.class));
    }
}
