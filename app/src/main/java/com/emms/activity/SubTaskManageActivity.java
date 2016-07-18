package com.emms.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.SubTaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.ui.CustomDialog;
import com.emms.util.DataUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/6.
 */
public class SubTaskManageActivity extends BaseActivity implements View.OnClickListener {
   private ListView sub_task_listView;
   private LinearLayout add_sub_task;
    private SubTaskAdapter adapter;
    private ArrayList<ObjectElement> datas=new ArrayList<ObjectElement>();
    private String taskId;
    private ObjectElement TaskDetail;
    private HashMap<String,String> Status_Colors=new HashMap<String,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task);
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        taskId=TaskDetail.get(Task.TASK_ID).valueAsString();
        initView();
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.sub_task);
        ((TextView)findViewById(R.id.group_type)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.ORGANISE_NAME)));
        ((TextView)findViewById(R.id.task_number)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        ((TextView)findViewById(R.id.task_state)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_STATUS)));
        sub_task_listView=(ListView)findViewById(R.id.sub_task_list);
        add_sub_task=(LinearLayout)findViewById(R.id.add_sub_task);
        add_sub_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加子任务
                CustomDialog customDialog=new CustomDialog(SubTaskManageActivity.this,R.layout.add_sub_task_dialog,R.style.MyDialog);
                customDialog.show();
            }
        });
        adapter=new SubTaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(SubTaskManageActivity.this).inflate(R.layout.sub_task_item, parent, false);
                    holder = new SubTaskAdapter.TaskViewHolder();
                    holder.work_num = (TextView) convertView.findViewById(R.id.work_num);
                    holder.approve_work_hours = (TextView) convertView.findViewById(R.id.approve_work_hours);
                    holder.work_name = (TextView) convertView.findViewById(R.id.work_name);
                    holder.status = (TextView) convertView.findViewById(R.id.status);
                    holder.work_description = (TextView) convertView.findViewById(R.id.work_description);
                    holder.equipment_num = (TextView) convertView.findViewById(R.id.equipment_num);
                    convertView.setTag(holder);
                } else {
                    holder = (SubTaskAdapter.TaskViewHolder) convertView.getTag();
                }
                holder.work_num.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkCode")));
                holder.approve_work_hours.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkTime")));
                holder.work_name.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkName")));
                holder.status.setText(DataUtil.isDataElementNull(datas.get(position).get("DataName")));
                holder.work_description.setText(DataUtil.isDataElementNull(datas.get(position).get("DataDescr")));
                holder.equipment_num.setText(DataUtil.isDataElementNull(datas.get(position).get("Equipment_ID")));
                return convertView;
            }
        };
        sub_task_listView.setAdapter(adapter);
        sub_task_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CustomDialog customDialog=new CustomDialog(SubTaskManageActivity.this,R.layout.add_sub_task_dialog,R.style.MyDialog);
                customDialog.show();
            }
        });
        getSubTaskDataFromServer();
    }

    @Override
    public void onClick(View v) {{
            int id_click = v.getId();
            if (id_click == R.id.btn_right_action) {
                finish();
            }
        }

    }
    private void getSubTaskDataFromServer(){
        HttpParams params=new HttpParams();
        params.put("task_id",taskId);
        HttpUtils.get(this, "TaskItemList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement!=null&&jsonObjectElement.get("PageData")!=null
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                        for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                            datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                        adapter.setDatas(datas);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }
}
