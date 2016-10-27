package com.emms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emms.R;
import com.datastore_android_sdk.datastore.ObjectElement;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/16.
 *
 */
public class ResultListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<ObjectElement> results;
    private String itemName;
    public ResultListAdapter(Context mContext) {
        this.results = new ArrayList<>();
        this.mContext = mContext;
    }

    public void changeData(ArrayList<ObjectElement> list,String itemName){
        this.itemName =itemName ;
        if (results == null){
            results=list;
        }else{
            results.clear();
            results.addAll(list);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return results == null ? 0 : results.size();
    }

    @Override
    public ObjectElement getItem(int position) {
        return results == null ? null : results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ResultViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_search_result_listview, parent, false);
            holder = new ResultViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tv_item_result_listview_name);
            view.setTag(holder);
        }else{
            holder = (ResultViewHolder) view.getTag();
        }
        holder.name.setText(results.get(position).get(itemName).valueAsString());
        return view;
    }

    public static class ResultViewHolder{
        TextView name;
    }
    public String getItemName(){
        return itemName;
    }

}
