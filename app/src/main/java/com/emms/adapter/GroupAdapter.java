package com.emms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emms.R;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/7/16.
 */
public class GroupAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<String> datas;
    public GroupAdapter(Context context,ArrayList<String> datas) {
        this.context =context;
        this.datas =datas;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.item_group_name, null);
            holder = new ViewHolder();

            convertView.setTag(holder);

            holder.groupItem = (TextView) convertView
                    .findViewById(R.id.group_name);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.groupItem.setText(datas.get(position));

        return convertView;
    }

    private final class ViewHolder {
        TextView groupItem;
    }
}
