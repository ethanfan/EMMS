package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.TaskBean;
import com.emms.util.LongToDate;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public class PendingOrdersFragment extends Fragment{

    private ListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<TaskBean> datas;
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
        datas =new ArrayList<TaskBean>(){
            {
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
                add(new TaskBean("何邵勃","D","0115","平车",144400000));
            }
        };
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
                holder.tv_creater.setText(datas.get(position).getCreater());
                holder.tv_group.setText(datas.get(position).getGroup());
                holder.tv_device_num.setText(datas.get(position).getDeviceNum());
                holder.tv_device_name.setText(datas.get(position).getDeviceName());
                String createTime = LongToDate.longPointDate(datas.get(position).getCreatTime());
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

}
