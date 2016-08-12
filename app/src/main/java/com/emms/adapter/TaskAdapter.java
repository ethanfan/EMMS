package com.emms.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaffer.deng on 2016/6/21.
 */
public abstract class TaskAdapter extends BaseAdapter{

    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

    private ArrayList<ObjectElement> datas;

    public TaskAdapter(ArrayList<ObjectElement> datas) {
        this.datas = datas;
    }
    public TaskAdapter() {
    }
    public void setDatas(ArrayList<ObjectElement> mDatas){
        if (mDatas.size()>0) {
            this.datas = mDatas;
      //  notifyDataSetInvalidated();
           notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        return  getCustomView(convertView,position,parent);
    }
    public abstract View getCustomView(View convertView,int position ,ViewGroup parent );
    public static   class TaskViewHolder {
        public  TextView tv_creater;
        public  TextView tv_group;
        public  TextView tv_device_num;
        public  TextView tv_device_name;
        public TextView tv_task_state;
        public TextView tv_task_describe;
        public TextView tv_end_time;
        public TextView tv_start_time;
        public  TextView tv_create_time;
        public  TextView tv_repair_time;
        public TextView warranty_person;
        public Button acceptTaskButton;
        public ListView listView;
    }
}
