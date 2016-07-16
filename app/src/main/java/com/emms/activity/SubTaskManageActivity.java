package com.emms.activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.SubTaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.ui.CustomDialog;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/6.
 */
public class SubTaskManageActivity extends BaseActivity implements View.OnClickListener {
   private ListView sub_task_listView;
   private LinearLayout add_sub_task;
    private SubTaskAdapter adapter;
    private ArrayList<ObjectElement> datas=new ArrayList<ObjectElement>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task);
        initView();
    }

    private void initView() {
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.sub_task);
        ((TextView)findViewById(R.id.group_type)).setText(R.string.sub_task);
        ((TextView)findViewById(R.id.task_number)).setText(R.string.sub_task);
        ((TextView)findViewById(R.id.task_state)).setText(R.string.sub_task);
        sub_task_listView=(ListView)findViewById(R.id.sub_task_list);
        add_sub_task=(LinearLayout)findViewById(R.id.add_sub_task);
        add_sub_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加子任务
                CustomDialog customDialog=new CustomDialog(SubTaskManageActivity.this,R.layout.add_sub_task_dialog,0);
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
                holder.work_num.setText(datas.get(position).get("TaskItem_ID").valueAsString());
           //     holder.approve_work_hours.setText(datas.get(position).get("approve_work_hours").valueAsString());
                holder.work_name.setText(datas.get(position).get("TaskItemName").valueAsString());
                holder.status.setText(datas.get(position).get("Status").valueAsString());
                holder.work_description.setText(datas.get(position).get("TaskItemDesc").valueAsString());
                holder.equipment_num.setText(datas.get(position).get("Equipment_ID").valueAsString());
                return convertView;
            }
        };
        sub_task_listView.setAdapter(adapter);
        getSubTaskDataFromServer();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

    }
    private void getSubTaskDataFromServer(){
        HttpParams params=new HttpParams();
        params.put("id",16);
        HttpUtils.get(this, "TaskItem", params, new HttpCallback() {
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
