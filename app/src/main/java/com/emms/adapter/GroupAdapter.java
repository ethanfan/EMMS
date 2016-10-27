package com.emms.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.emms.R;
import com.emms.util.DataUtil;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/7/16.
 *
 */
public class GroupAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<ObjectElement> datas) {
        this.datas = datas;
    }

    private ArrayList<ObjectElement> datas;
    public GroupAdapter(Context context,ArrayList<ObjectElement> datas) {
        this.context =context;
        this.datas =datas;
    }
    private ObjectElement selection;
    public void setSelection(ObjectElement selection){
        this.selection=selection;
        notifyDataSetChanged();
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
//        if(position==0&&tag){
//            holder.groupItem.setTextColor(Color.RED);
//           tag=false;
//        }
        if(selection!=null){
        if(datas.get(position).equals(selection)){
            holder.groupItem.setTextColor(Color.parseColor("#AB2D42"));
        }
            else {
            holder.groupItem.setTextColor(Color.BLACK);
        }
        }
        holder.groupItem.setText(DataUtil.isDataElementNull(datas.get(position).get("OrganiseName")));

        return convertView;
    }

    private final class ViewHolder {
        TextView groupItem;
    }
}
