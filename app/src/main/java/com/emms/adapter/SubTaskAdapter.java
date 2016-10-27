package com.emms.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.datastore_android_sdk.datastore.ObjectElement;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public abstract class SubTaskAdapter extends BaseAdapter{

    private ArrayList<ObjectElement> datas;

    public SubTaskAdapter(ArrayList<ObjectElement> datas) {
        this.datas = datas;
    }
    public void setDatas(ArrayList<ObjectElement> mDatas){
        if (mDatas.size()>0) {
            this.datas = mDatas;
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
        public  TextView work_num;
        public  TextView approve_work_hours;
        public  TextView work_name;
        public  TextView status;
        public  TextView work_description;
        public  TextView equipment_num;
        public ImageView imageView;
    }
}
