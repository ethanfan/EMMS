package com.emms.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.flyco.tablayout.widget.MsgView;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public abstract class BackgroundAdapter extends BaseAdapter{

    public ArrayList<Integer> getDatas() {
        return datas;
    }

    private ArrayList<Integer> datas;

    public BackgroundAdapter(ArrayList<Integer> datas) {
        this.datas = datas;
    }
    public void setDatas(ArrayList<Integer> mDatas){
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
        public  TextView moduleName;
        public ImageView image;
        public MsgView msgView;
    }
}
