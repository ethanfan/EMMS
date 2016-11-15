package com.emms.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.emms.ui.DropEditText;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public abstract class TaskAdapter extends BaseAdapter{

    public ArrayList<ObjectElement> getDatas() {
        return datas;
    }

    private ArrayList<ObjectElement> datas;

    public TaskAdapter(ArrayList<ObjectElement> datas) {
        this.datas = datas;
    }
    public TaskAdapter() {
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
    public static   class TaskViewHolder {
        public  TextView tv_creater;
        public  TextView tv_group;
        public  TextView tv_device_num;
        public  TextView tv_device_name;
        public TextView tv_task_state;
        public TextView tv_task_describe;
        public TextView tv_end_time;
        public TextView tv_start_time;
        public  TextView tv_create_time;
        public  TextView tv_repair_time;
        public TextView tv_target_group;
        public TextView warranty_person;
        public Button acceptTaskButton;
        public Button rejectTaskButton;
        public Button EndTaskButton;
        public ListView listView;
        public ImageView image;
        public EditText editText;
        public EditText editText2;
        public DropEditText dropEditText;
        public DropEditText dropEditText2;
        public GridView gridView;
        public EtTextChanged textChanged1;
        public EtTextChanged textChanged2;
        public ExOnClickListener onClickListener;
    }
    public class EtTextChanged implements TextWatcher {
        private int position;
        private String key;
        public EtTextChanged(int position,String key){
            this.position = position;
            this.key=key;
        }


        public void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            datas.get(position).set(key,s.toString());
        }
    }
    public class ExOnClickListener implements View.OnClickListener{
        private int position;
        private String key;
        public ExOnClickListener(int position,String Key){
            this.position=position;
            this.key=Key;
        }
        public void setPosition(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            if(datas.get(position).get(key).valueAsBoolean()){
                datas.get(position).set(key,false);
            }else {
                datas.get(position).set(key,true);
            }
            if(exArray!=null){
                if(exArray.contains(datas.get(position))){
                    exArray.remove(datas.get(position));
                }else {
                    exArray.add(datas.get(position));
                }
            }
            notifyDataSetChanged();
        }
    }

    public ArrayList<ObjectElement> getExArray() {
        return exArray;
    }

    public void setExArray(ArrayList<ObjectElement> exArray) {
        this.exArray = exArray;
    }

    private ArrayList<ObjectElement> exArray;
}
