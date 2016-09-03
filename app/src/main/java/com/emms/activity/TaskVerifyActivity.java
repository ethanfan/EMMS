package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/29.
 */
public class TaskVerifyActivity extends NfcActivity {
    private PullToRefreshListView VerifyTaskListView;
    private TaskAdapter adapter;
    private Context mContext=this;
    private ArrayList<ObjectElement> VerifyTaskList=new ArrayList<>();
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_verify);
        initView();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.TaskVerify);
        VerifyTaskListView=(PullToRefreshListView)findViewById(R.id.taskList);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter=new TaskAdapter(VerifyTaskList) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_task_verify, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.tv_creater_order);
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group_type);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.Task_description);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time_order);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.Task_Equipment);
                    holder.acceptTaskButton=(Button)convertView.findViewById(R.id.pass);
                    holder.rejectTaskButton=(Button)convertView.findViewById(R.id.Notpass) ;
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_creater.setText(DataUtil.isDataElementNull(VerifyTaskList.get(position).get(Task.APPLICANT)));
                holder.tv_group.setText(DataUtil.isDataElementNull(VerifyTaskList.get(position).get(Task.ORGANISE_NAME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(VerifyTaskList.get(position).get(Task.TASK_DESCRIPTION)));
                holder.tv_create_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(VerifyTaskList.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_device_name.setText(DataUtil.getDate(DataUtil.isDataElementNull(VerifyTaskList.get(position).get("EquipmentName"))));
                holder.acceptTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // acceptTask(position);
                        TaskPass(position,1);
                    }
                });
                holder.rejectTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TaskPass(position,0);
                    }
                });
                return convertView;
            }
        };
        VerifyTaskListView.setAdapter(adapter);
        VerifyTaskListView.setMode(PullToRefreshListView.Mode.BOTH);
        VerifyTaskListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;
                        getVerfyTaskListFromServer();
                        VerifyTaskListView.onRefreshComplete();
                    }
                });
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getVerfyTaskListFromServer();
                        VerifyTaskListView.onRefreshComplete();
                    }
                },0);
            }
        });
        VerifyTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                intent.putExtra(Task.TASK_ID,VerifyTaskList.get(position-1).get(Task.TASK_ID).valueAsString());
                intent.putExtra("TaskDetail",VerifyTaskList.get(position-1).asObjectElement().toString());
                intent.putExtra(Task.TASK_CLASS,"T01");
                startActivity(intent);
            }
        });
        getVerfyTaskListFromServer();
    }
    private void TaskPass(int position,int status){

    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void getVerfyTaskListFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastLong(R.string.noMoreData,mContext);
                return;
            }}
        showCustomDialog(R.string.loadingData);        HttpParams params=new HttpParams();
        //   String s=SharedPreferenceManager.getLoginData(mContext);
        //  JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
        //  String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
        //  params.put("operator_id",operator_id);
        params.put("status",0);
        params.put("taskClass","T01");
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(mContext, "TaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()>0)
                        RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    if(pageIndex==1){
                        VerifyTaskList.clear();
                    }
                    pageIndex++;
                    for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                        VerifyTaskList.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDatas(VerifyTaskList);
                            adapter.notifyDataSetChanged();
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
}
