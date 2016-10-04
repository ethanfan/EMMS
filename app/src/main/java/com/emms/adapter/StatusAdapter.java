package com.emms.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public abstract class StatusAdapter extends BaseAdapter{

    public ArrayList<String> getDatas() {
        return datas;
    }

    private ArrayList<String> datas;

    public StatusAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }
    public StatusAdapter() {
    }
    public void setDatas(ArrayList<String> mDatas){
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
        return datas.get(position);
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
        public  TextView statu;

    }
}
