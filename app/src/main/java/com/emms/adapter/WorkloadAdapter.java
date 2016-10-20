package com.emms.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/7/31.
 */
public abstract class WorkloadAdapter extends BaseAdapter{

    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

    private ArrayList<ObjectElement> datas;
    private boolean tag=true;
    public WorkloadAdapter(ArrayList<ObjectElement> datas) {
        this.datas = datas;
    }
    public WorkloadAdapter() {
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
    public static   class ViewHolder {
        public TextView name;
        public TextView skill;
        public  TextView startTime;
        public TextView endTime;
        public EditText workload;
        public ImageView imageView;
    }
}
