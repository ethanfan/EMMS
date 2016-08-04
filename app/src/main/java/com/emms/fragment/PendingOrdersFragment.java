package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.RxVolley;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.TaskBean;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Maintain;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LongToDate;
import com.emms.util.SharedPreferenceManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;

import org.restlet.engine.header.ContentType;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class PendingOrdersFragment extends BaseFragment{

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas;
    private Context mContext;
    private Handler handler=new Handler();
    private String TaskClass;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPendingOrderTaskDataFromServer();
                        listView.onRefreshComplete();
                     //   Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskClass=this.getArguments().getString(Task.TASK_CLASS);
        datas =new ArrayList<ObjectElement>();
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_pendingorder, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.tv_creater_order);
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group_type);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.Task_description);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.Task_status);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time_order);
                    holder.acceptTaskButton=(Button)convertView.findViewById(R.id.btn_order);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_creater.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_STATUS)));
                holder.tv_create_time.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME)));
                holder.acceptTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // acceptTask(position);
                        taskReceive(position);
                    }
                });
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        getPendingOrderTaskDataFromServer();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                intent.putExtra(Task.TASK_ID,datas.get(position-1).get(Task.TASK_ID).valueAsString());
                intent.putExtra("TaskDetail",datas.get(position-1).asObjectElement().toString());
                intent.putExtra(Task.TASK_CLASS,TaskClass);
                startActivity(intent);
            }
        });
    }
    private void getPendingOrderTaskDataFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
     //   String s=SharedPreferenceManager.getLoginData(mContext);
      //  JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
      //  String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
      //  params.put("operator_id",operator_id);
        params.put("status",0);
        params.put("taskClass",TaskClass);
        params.put("pageSize",10);
        params.put("pageIndex",1);
        HttpUtils.get(mContext, "TaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    int RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    if(jsonObjectElement.get("PageData").asArrayElement().size()==0){
                    }
                    datas.clear();
                    for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                        datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(datas);
                            taskAdapter.notifyDataSetChanged();
                        }
                    });
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }
    public void acceptTask(final int position){
        //HttpParams params=new HttpParams();
        //params.put();
       // taskAdapter.getDatas()
       // datas.get(position).
        JsonObjectElement task=new JsonObjectElement();
        task.set(Task.TASK_ID,DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)));
     //   task.set("Status", 1);
    /*    JsonObjectElement operator=new JsonObjectElement();

        operator.set("Operator_ID", new JsonObjectElement(SharedPreferenceManager.getLoginData(mContext))
                .get("Operator_ID").valueAsString());

        JsonArray jsonArray=new JsonArray();
        JsonObject JsonObject=new JsonObject();
        JsonObject.addProperty("Operator_ID", new JsonObjectElement(SharedPreferenceManager.getLoginData(mContext))
                .get("Operator_ID").valueAsString());
        jsonArray.add(JsonObject);*/

        JsonObjectElement SubData=new JsonObjectElement();
        SubData.set("Task",task);
       // SubData.set("TaskOperator",jsonArray.toString());
     //   SubData.set("isChangeTaskItem","1");
        HttpParams params=new HttpParams();
        params.putJsonParams(SubData.toJson());
        HttpUtils.post(mContext, "TaskCollection", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                     if(jsonObjectElement!=null){
                         if(jsonObjectElement.get("Success").valueAsBoolean()){
                             //成功，通知用户接单成功
                             Toast toast=Toast.makeText(mContext,"接单成功",Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.CENTER,0,0);
                             toast.show();
                         }else{
                             //失败，通知用户接单失败，单已经被接
                             Toast toast=Toast.makeText(mContext,"该单已被接",Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.CENTER,0,0);
                             toast.show();
                         }
                         datas.remove(position);
                         taskAdapter.setDatas(datas);
                         taskAdapter.notifyDataSetChanged();
                     }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });

    }
    public void taskReceive(final int position){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
      //  params.put("task_id",Integer.valueOf(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID))));
        HttpUtils.post(mContext,"TaskRecieve?task_id="+DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)), params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement!=null){
                        if(jsonObjectElement.get("Success").valueAsBoolean()){
                            //成功，通知用户接单成功
                            Toast toast=Toast.makeText(mContext,"接单成功",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }else{
                            //失败，通知用户接单失败，单已经被接
                            Toast toast=Toast.makeText(mContext,"该单已被接",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                        datas.remove(position);
                        taskAdapter.setDatas(datas);
                        taskAdapter.notifyDataSetChanged();
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
    }
    public static Fragment newInstance(String TaskClass){
        PendingOrdersFragment fragment = new PendingOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        fragment.setArguments(bundle);
        return fragment;
    }
}
