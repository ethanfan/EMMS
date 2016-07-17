package com.emms.fragment;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.emms.schema.Message;
import com.emms.schema.Task;
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
    private ProcessingFragment processingFragment=this;
    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

  //  public void setData(ArrayList<ObjectElement> objectElements){
   //     listView.setAdapter(taskAdapter);
  //      taskAdapter.setDatas(objectElements);
  //  }
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
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_process, parent, false);
                    holder = new TaskViewHolder();
                    //显示6个内容，组别，报修人，状态，保修时间,开始时间，任务描述
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //待修改
                holder.tv_group.setText(datas.get(position).get(Task.ORGANISE_NAME).valueAsString());
                holder.warranty_person.setText(datas.get(position).get(Task.APPLICANT).valueAsString());
                holder.tv_task_state.setText(datas.get(position).get(Task.TASK_STATUS).valueAsString());
                holder.tv_repair_time.setText(datas.get(position).get(Task.APPLICANT_TIME).valueAsString());
                holder.tv_start_time.setText(datas.get(position).get(Task.START_TIME).valueAsString());
                holder.tv_task_describe.setText(datas.get(position).get(Task.TASK_DESCRIPTION).valueAsString());
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
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        params.put("operator_id",4673);
        params.put("status",0);
        params.put("taskClass","T01");
        HttpUtils.get(mContext, "TaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e("returnString",t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    int RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    if(jsonObjectElement.get("PageData").asArrayElement().size()==0){
                      //提示没有处理中的任务
                    }
                    for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                        datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }
             //      setData(datas);


                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
            }
        });
    }



}
