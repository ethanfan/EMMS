package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by jaffer.deng on 2016/6/20.
 */
public class ProcessingFragment extends Fragment {

    private ListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<TaskBean> datas;
    private Context mContext;

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
        datas = new ArrayList<TaskBean>() {
            {
                add(new TaskBean("D", "0115", "平车", 1, 1403300, 0, "我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 2, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 1, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 1, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 2, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
                add(new TaskBean("D", "0115", "平车", 2, 1403300, 0, "我是描述我是描述描述我是描述描述我是描述描述我是描述描述我是描述描述"));
            }
        };
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
                holder.tv_group.setText(datas.get(position).getGroup());
                holder.tv_device_num.setText(datas.get(position).getDeviceNum());
                holder.tv_device_name.setText(datas.get(position).getDeviceName());
                int tag = datas.get(position).getTaskTag();
                String state = "";
                if (tag == 1) {
                    holder.tv_task_state.setTextColor(getResources().getColor(R.color.processing_color));
                    state = getResources().getString(R.string.working);
                } else if (tag == 2) {
                    holder.tv_task_state.setTextColor(getResources().getColor(R.color.pause_color));
                    state = getResources().getString(R.string.paused);
                }
                holder.tv_task_state.setText(state);
                String start_date = LongToDate.longPointDate(datas.get(position).getStartTime());
                holder.tv_start_time.setText(start_date);
                holder.tv_end_time.setText("");
                holder.tv_task_describe.setText(datas.get(position).getTaskDescriptions());
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
