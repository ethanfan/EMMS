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
import android.widget.ListView;
import android.widget.TextView;

import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Maintain;
import com.emms.util.SharedPreferenceManager;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class PendingOrdersFragment extends Fragment{

    private ListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas;
    private Context mContext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (ListView) v.findViewById(R.id.processing_list);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        datas =new ArrayList<ObjectElement>();
    /*    {
            {
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
            }
        };*/
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_pendingorder, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.tv_creater_order);
                    holder.tv_group = (TextView) convertView.findViewById(R.id.tv_device_num);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num_order);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name_order);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time_order);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //   holder.tv_creater.setText(datas.get(position).getCreater());
                holder.tv_group.setText(datas.get(position).get(Maintain.GROUP_NAME).valueAsString());
                holder.tv_device_num.setText(datas.get(position).get(Maintain.MACHINE_CODE).valueAsString());
                holder.tv_device_name.setText(datas.get(position).get(Maintain.MACHINE_NAME).valueAsString());
                // String createTime = LongToDate.longPointDate(datas.get(position).get(Maintain.MAINTAIN_START_TIME).valueAsInt());
                String createTime=datas.get(position).get(Maintain.MAINTAIN_START_TIME).valueAsString();
                holder.tv_create_time.setText(createTime);
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
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
                            datas.add(jsonArrayElement.get(i).asObjectElement());
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
