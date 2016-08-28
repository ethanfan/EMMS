package com.emms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emms.R;
import com.emms.activity.CreateTaskActivity;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/15.
 */
public class MenuAdapter extends BaseAdapter {

    private  Context mContext;
    private ArrayList<String> mDataList ;
    private ArrayList<String> mShowDataList ;
    public MenuAdapter(Context context,ArrayList<String> dataList) {
        this.mContext =context;
        this.mDataList =dataList;
        mShowDataList =dataList;
    }

    @Override
    public int getCount() {
        return mShowDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mShowDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.pomenu_item, null);
            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.groupItem = (TextView) convertView
                    .findViewById(R.id.textView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupItem.setText(mShowDataList.get(position));
        return convertView;
    }
    private final class ViewHolder {
        TextView groupItem;
    }
    public ArrayList getDatas(){
        return mDataList;
    }

    public void setDatas(ArrayList list){
        this.mShowDataList = list;
    }
}
