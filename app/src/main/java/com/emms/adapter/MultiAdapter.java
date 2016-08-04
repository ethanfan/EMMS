package com.emms.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.emms.R;
import com.emms.bean.AwaitRepair;
import com.emms.util.DataUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jaffer.deng on 2016/7/16.
 */
public class MultiAdapter extends BaseAdapter {
    public List<Integer> getListItemID() {
        return listItemID;
    }

    public void setListItemID(List<Integer> listItemID) {
        this.listItemID = listItemID;
    }

    public List<ObjectElement> getListItems() {
        return listItems;
    }

    public void setListItems(List<ObjectElement> listItems) {
        this.listItems = listItems;
        if(mChecked!=null){
            for (; mChecked.size()<listItems.size();) {// 遍历且设置CheckBox默认状态为未选中
                mChecked.add(false);
            }}
        ((Activity)ctx).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }

    private List<ObjectElement> listItems;

    private Context ctx;
    /** 标记CheckBox是否被选中 **/
    List<Boolean> mChecked;

    List<Integer> listItemID;
    //HashSet<Integer> OperatorSet=new HashSet<Integer>();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, View> viewMap = new HashMap<Integer, View>();
    public MultiAdapter(Context context, List<ObjectElement> listItems) {
        // TODO Auto-generated constructor stub
        this.ctx = context;
        this.listItems = listItems;

        mChecked = new ArrayList<Boolean>();
        for (int i = 0; i < listItems.size(); i++) {// 遍历且设置CheckBox默认状态为未选中
            mChecked.add(false);
        }

        listItemID = new ArrayList<Integer>();

    }



    public void ClickResult(Context ctx)
    {
        listItemID.clear();// 清空listItemID

        for (int i = 0; i < mChecked.size(); i++) {
            if (mChecked.get(i)) {

                listItemID.add(i);
            }
        }

//		if (listItemID.size() == 0) {
//			AlertDialog.Builder builder1 = new AlertDialog.Builder(ctx);
//			builder1.setMessage("没有选中任何记录");
//			builder1.show();
//		} else {// 如果列表不为空,在对话框上显示选中项的ID
//			StringBuilder sb = new StringBuilder();
//
//			for (int i = 0; i < listItemID.size(); i++) {// 遍历listItemID列表取得存放的每一项
//				sb.append("ItemID=" + listItemID.get(i) + " . "+listItems.get(i).toString()+".");
//
//			}
//			AlertDialog.Builder builder2 = new AlertDialog.Builder(
//					ctx);
//			builder2.setMessage(sb.toString());
//
//			builder2.show();// 显示对话框
//		}
    }

    public List<Integer> getlistItemID (){
        return listItemID;
    }
    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        //  View rowView = this.viewMap.get(position);
        //  AwaitRepair awaitRepair =  listItems.get(position);
        ObjectElement Operator = listItems.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            holder=new ViewHolder();
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_invitor, null);
            holder.workname = (TextView) convertView.findViewById(R.id.id_worknum);
            holder.tech = (TextView) convertView.findViewById(R.id.id_tech);
            holder.status = (ImageView) convertView.findViewById(R.id.workstatus);
            holder.select = (ImageView) convertView.findViewById(R.id.select);
            holder.multi_item = (LinearLayout) convertView.findViewById(R.id.multi_item);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.workname.setText(DataUtil.isDataElementNull(Operator.get("Name")));
        holder.tech.setText(DataUtil.isDataElementNull(Operator.get("Skill")));
            if (DataUtil.isDataElementNull(Operator.get("Status")).equals("1")) {
                holder.status.setImageResource(R.mipmap.busy);
            } else {
                holder.status.setImageResource(R.mipmap.idle);
            }
           if(mChecked.get(position)){
               holder.select.setVisibility(View.VISIBLE);
           }
            else{
               holder.select.setVisibility(View.INVISIBLE);
           }
        holder.multi_item.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if(mChecked.get(position))//当前已选中，点击后取消选中
                    {
                        holder.select.setVisibility(View.INVISIBLE);
                        mChecked.set(position, false);
                    }
                    else
                    {
                        holder.select.setVisibility(View.VISIBLE);
                        holder.select.setImageResource(R.mipmap.select_pressed);
                        mChecked.set(position, true);
                    }
                   ClickResult(ctx);
                }
            });

            viewMap.put(position, convertView);



            return convertView;



    }
    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return true;
    }
    public static   class ViewHolder {
        TextView workname ;
        TextView tech;
        ImageView status;
        ImageView select;
        LinearLayout multi_item;
    }
}
