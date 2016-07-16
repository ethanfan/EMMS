package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Maintain;
import com.emms.util.SharedPreferenceManager;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;

import java.util.ArrayList;

import android.os.Handler;

/**
 * Created by jaffer.deng on 2016/6/20.
 */
public class ProcessingFragment extends Fragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;

    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

    public void setDatas(ListenableFuture<DataElement> data) {
        // this.datas = datas;
        Futures.addCallback(data, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement dataElement) {
                if(dataElement!=null&&dataElement.asArrayElement().size()>0){
                    for(int i=0;i<dataElement.asArrayElement().size();i++){
                        datas.add(dataElement.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }
    //private ArrayList<TaskBean> datas;
    private ArrayList<ObjectElement> datas=new ArrayList<ObjectElement>();
    private Context mContext;
    private Handler handler=new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
       // listView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(final PullToRefreshBase<ListView> refreshView) {
                //下拉刷新

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },2000);
           }
        });
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        Toast.makeText(mContext,"dadada",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
     /*   datas = new ArrayList<TaskBean>() {
            {
                add(new TaskBean("D", "0115", "平车", 1, 1403300, 0, "我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 2, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 1, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 1, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 2, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 2, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
            }
        };*/
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_process, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_group = (TextView) convertView.findViewById(R.id.tv_device_num);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num_process);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name_procee);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_repair_time_process);
                    holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_group.setText(datas.get(position).get(Maintain.GROUP_NAME).valueAsString());
                holder.tv_device_num.setText(datas.get(position).get(Maintain.MACHINE_CODE).valueAsString());
                holder.tv_device_name.setText(datas.get(position).get(Maintain.MACHINE_NAME).valueAsString());
           /*     int tag = datas.get(position).getTaskTag();
                String state = "";
                if (tag == 1) {
                    holder.tv_task_state.setTextColor(getResources().getColor(R.color.processing_color));
                    state = getResources().getString(R.string.working);
                } else if (tag == 2) {
                    holder.tv_task_state.setTextColor(getResources().getColor(R.color.pause_color));
                    state = getResources().getString(R.string.paused);
                }*/
                holder.tv_task_state.setText(datas.get(position).get(Maintain.STATUS).valueAsString());
                // String start_date = LongToDate.longPointDate(datas.get(position).get(Maintain.MAINTAIN_START_TIME).valueAsLong());
                String start_date=datas.get(position).get(Maintain.MAINTAIN_START_TIME).valueAsString();
                holder.tv_start_time.setText(start_date);
                holder.tv_end_time.setText("");
                holder.tv_task_describe.setText(datas.get(position).get(Maintain.DESCRIPTION).valueAsString());
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
       taskAdapter.setDatas(datas);
        getProcessingDataFromServer();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(mContext, TaskDetailsActivity.class));
                //Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                //intent.putExtra(Task.TASK_ID,datas.get(position).get(Task.TASK_ID).valueAsString());
                //intent.putExtra("TaskDetail",datas.get(position).asObjectElement().valueAsString());
                //startActivity(intent);
                //startActivity(new Intent(mContext, TaskDetailsActivity.class));
            }
        });
    }
    private void getProcessingDataFromServer(){
        HttpParams params=new HttpParams();
       // params.put("id", SharedPreferenceManager.getUserName(mContext));
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
       // Log.e("returnString","dd");
        String s=SharedPreferenceManager.getLoginData(mContext);
        //params.put("Operator_id",);
        JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
        int operator_id=jsonObjectElement.get("Operator_ID").valueAsInt();
        params.put("operator_id",operator_id);
        params.put("status",0);
        params.put("taskClass",0);
        HttpUtils.get(mContext, "TaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e("returnString",t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                   // JsonArrayElement jsonArrayElement=jsonObjectElement.asArrayElement();
                 /*  if(jsonArrayElement!=null&&jsonArrayElement.size()>0){
                        for(int i=0;i<jsonArrayElement.size();i++){
                            datas.add(jsonArrayElement.get(i).asObjectElement());
                        }
                    }*/

                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
            }
        });
    }



}
