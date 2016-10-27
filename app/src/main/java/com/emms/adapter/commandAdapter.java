package com.emms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.emms.R;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/15.
 *
 */
public class commandAdapter extends BaseAdapter {

    private  Context mContext;
   private ArrayList<Integer> mShowDataList;
    public commandAdapter(Context context, ArrayList<Integer> dataList) {
        this.mContext =context;
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
                    R.layout.item_command_listview, null);
            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.star = (ImageView) convertView
                    .findViewById(R.id.star);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(mShowDataList.get(position)==1) {
            holder.star.setBackgroundResource(R.mipmap.star_selected);
        }else {
            holder.star.setBackgroundResource(R.mipmap.star_unselected);
        }
        return convertView;
    }
    private final class ViewHolder {
        ImageView star;
    }
    public ArrayList getDatas(){
        return mShowDataList;
    }

    public void setDatas(ArrayList<Integer> list){
        this.mShowDataList = list;
    }
}
