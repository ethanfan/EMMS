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
import com.emms.schema.Maintain;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class LinkedOrdersFragment extends Fragment{
    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas1;
    private ArrayList<ObjectElement> datas2;
    private ArrayList<ObjectElement> datas3;
    private Context mContext;
    private SegmentTabLayout tabLayout_1;
    private String[] mTitles ;
    private ArrayList<ObjectElement> data;
    private Handler handler=new Handler();
    private String TaskClass;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        tabLayout_1 = (SegmentTabLayout) v.findViewById(R.id.tl_1);
        tabLayout_1.setVisibility(View.VISIBLE);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getCompleteTaskDataFromServer(data);
                        listView.onRefreshComplete();
                      //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                       // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },2000);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       TaskClass=this.getArguments().getString(Task.TASK_CLASS);
        mTitles = getResources().getStringArray(R.array.select_tab_time);
        tabLayout_1.setTabData(mTitles);
        datas1 =new ArrayList<ObjectElement>();
        datas2 =new ArrayList<ObjectElement>();
        datas3 =new ArrayList<ObjectElement>();
        taskAdapter =new TaskAdapter(datas1) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_process, parent, false);
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
                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_STATUS)));
                holder.tv_repair_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME)));
                holder.tv_start_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME)));
                holder.tv_end_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        data=new ArrayList<ObjectElement>();
        getCompleteTaskDataFromServer(data);
        //data.addAll(datas1);
        tabLayout_1.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

                if (position == 0) {
                    data.clear();
                    data.addAll(datas1);
                    taskAdapter.setDatas(data);
                } else if (position == 1) {
                    data.clear();
                    data.addAll(datas1);
                    data.addAll(datas2);
                    taskAdapter.setDatas(data);
                } else if (position == 2) {
                    data.clear();
                    data.addAll(datas1);
                    data.addAll(datas2);
                    data.addAll(datas3);
                    taskAdapter.setDatas(data);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                intent.putExtra(Task.TASK_ID,data.get(position-1).get(Task.TASK_ID).valueAsString());
                intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
                startActivity(intent);
            }
        });
    }
    private void getCompleteTaskDataFromServer(ArrayList<ObjectElement> list ){
        HttpParams params=new HttpParams();
      //  String s=SharedPreferenceManager.getLoginData(mContext);
       // JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
       // String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
       // params.put("operator_id",operator_id);
        params.put("status",2);
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
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()==0){
                    }
                 //  datas1.clear();
                 //   datas2.clear();
                 //   datas3.clear();
                    data.clear();
                    for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                        data.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }
                   // datas2.addAll(datas1);
                   // datas3.addAll(datas2);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(data);
                            taskAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
            }
        });
    }
    public static Fragment newInstance(String TaskClass){
        LinkedOrdersFragment fragment = new LinkedOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        fragment.setArguments(bundle);
        return fragment;
    }
}
