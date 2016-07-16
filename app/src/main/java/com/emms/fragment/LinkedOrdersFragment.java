package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Maintain;
import com.emms.util.SharedPreferenceManager;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        tabLayout_1 = (SegmentTabLayout) v.findViewById(R.id.tl_1);
        tabLayout_1.setVisibility(View.VISIBLE);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_linked_order, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.Warranty_person);
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.tv_repair_time = (TextView) convertView.findViewById(R.id.tv_repair_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time_linked);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe_linked);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //  holder.tv_creater.setText(data.get(position).getCreater());
                holder.tv_group.setText(data.get(position).get(Maintain.GROUP_NAME).valueAsString());
                holder.tv_device_num.setText(data.get(position).get(Maintain.MACHINE_CODE).valueAsString());
                holder.tv_device_name.setText(data.get(position).get(Maintain.MACHINE_NAME).valueAsString());
                //  String repairTime = LongToDate.longPointDate(data.get(position).getRepairTime());
                //   holder.tv_repair_time.setText(repairTime);

                /// String startTime = LongToDate.longPointDate(data.get(position).get(Maintain.MAINTAIN_START_TIME).valueAsLong());
                String startTime=data.get(position).get(Maintain.MAINTAIN_START_TIME).valueAsString();
                holder.tv_start_time.setText(startTime);
                //String endTime = LongToDate.longPointDate(data.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsLong());
                String endTime=data.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsString();
                holder.tv_end_time.setText(endTime);
                holder.tv_task_describe.setText(data.get(position).get(Maintain.DESCRIPTION).valueAsString());
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        data=new ArrayList<ObjectElement>();
        data.addAll(datas1);
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
                startActivity(new Intent(mContext, TaskDetailsActivity.class));
            }
        });
    }
    private void getProcessingDataFromServer(){
        HttpParams params=new HttpParams();
        params.put("id", SharedPreferenceManager.getUserName(mContext));
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        Log.e("returnString","dd");
        HttpUtils.get(mContext, "Task", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                Log.e("returnString",t);
                if(t!=null) {
                    //JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    JsonArrayElement jsonArrayElement=new JsonArrayElement(t);
                    if(jsonArrayElement!=null&&jsonArrayElement.size()>0){
                        for(int i=0;i<jsonArrayElement.size();i++){
                            data.add(jsonArrayElement.get(i).asObjectElement());
                        }
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
