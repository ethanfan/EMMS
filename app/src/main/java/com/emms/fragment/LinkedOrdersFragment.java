package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Maintain;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public class LinkedOrdersFragment extends BaseFragment{
    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private Context mContext;
    private String[] mTitles ;
    private ArrayList<ObjectElement> data=new ArrayList<>();
    private Handler handler=new Handler();
    private String TaskClass;
    private String TaskSubClass;
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshListView.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;
                        getCompleteTaskDataFromServer();
                        listView.onRefreshComplete();
                      //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCompleteTaskDataFromServer();
                        listView.onRefreshComplete();
                       // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       TaskClass=this.getArguments().getString(Task.TASK_CLASS);
        TaskSubClass=this.getArguments().getString(Task.TASK_SUBCLASS);
        mTitles = getResources().getStringArray(R.array.select_tab_time);
        taskAdapter =new TaskAdapter(data) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_process, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                   // holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time= (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.Task_Equipment);
                    holder.tv_device_num=(TextView)convertView.findViewById(R.id.Task_Equipment_Num);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_create_time.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentName")));
                holder.tv_device_num.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentAssetsIDList")));
                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
               // holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_STATUS)));
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME))));
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        getCompleteTaskDataFromServer();
        //data.addAll(datas1);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                intent.putExtra(Task.TASK_ID,data.get(position-1).get(Task.TASK_ID).valueAsString());
                intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
                intent.putExtra("TaskStatus",2);
                intent.putExtra(Task.TASK_CLASS,TaskClass);
                startActivity(intent);
            }
        });
    }
    private void getCompleteTaskDataFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastLong(R.string.noMoreData,mContext);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
      //  String s=SharedPreferenceManager.getLoginData(mContext);
       // JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
       // String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
       // params.put("operator_id",operator_id);
        params.put("status",2);
        params.put("taskClass",TaskClass);
        if(TaskSubClass!=null&&!TaskSubClass.equals("")){
            params.put("taskSubClass",TaskSubClass);
        }
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(mContext, "TaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    if(pageIndex==1){
                        data.clear();
                    }
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                    pageIndex++;
                    for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                        data.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }
                }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(data);
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
    public static Fragment newInstance(String TaskClass,String TaskSubClass){
        LinkedOrdersFragment fragment = new LinkedOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        if(TaskSubClass!=null&&!TaskSubClass.equals("")){
            bundle.putString(Task.TASK_SUBCLASS,TaskSubClass);
        }
        fragment.setArguments(bundle);
        return fragment;
    }
}
