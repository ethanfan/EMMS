package com.emms.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.DataElement;
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

import java.security.spec.RSAOtherPrimeInfo;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/4.
 */
public class TaskHistory extends BaseActivity {
    private PullToRefreshListView listView;
    private TaskAdapter adapter;
    private ArrayList<ObjectElement> data=new ArrayList<ObjectElement>();
    private Context context=this;
    private String TaskClass;
    private static int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        initView();
        getTaskHistory();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.taskHistory);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView=(PullToRefreshListView)findViewById(R.id.taskList);
        adapter=new TaskAdapter(data) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_fr_process, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time= (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get("Organise_ID")));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get("Status")));
                holder.tv_repair_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME)));
                holder.tv_start_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME)));
                holder.tv_end_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                return convertView;
            }
        };
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },500);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        getTaskHistory();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });
    }
    private void getTaskHistory(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastLong(R.string.noMoreData,context);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        params.put("task_class", TaskClass);
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(this, "TaskHistoryList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement!=null){
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        if (pageIndex == 1) {
                            data.clear();
                        }
                        pageIndex++;
                        for(DataElement dataElement:jsonObjectElement.get("PageData").asArrayElement()){
                         data.add(dataElement.asObjectElement());
                        }
                        adapter.notifyDataSetChanged();
                    }
                }}
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
