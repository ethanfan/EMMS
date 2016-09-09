package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.emms.R;
import com.emms.adapter.SubTaskAdapter;
import com.emms.schema.Equipment;
import com.emms.util.DataUtil;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/31.
 *
 */
public class UserRoleDialog extends Dialog {
    private Context context;
    private ListView listView;
    private SubTaskAdapter Adapter;
    private HashMap<String,Integer> Role_ID_Picture_map=new HashMap<>();
    private ArrayList<ObjectElement> list=new ArrayList<>();
    public UserRoleDialog(Context context, ArrayList<ObjectElement> list){
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_user_role);
        this.context=context;
        this.list=list;
        initMap();
        setCancelable(false);
        initView();
    }
    private void initView(){
        listView=(ListView)findViewById(R.id.list);
        Adapter=new SubTaskAdapter(list) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_role, parent, false);
                    holder = new TaskViewHolder();
                    holder.work_num=(TextView)convertView.findViewById(R.id.text) ;
                    holder.imageView=(ImageView)convertView.findViewById(R.id.image) ;
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //
                if(Role_ID_Picture_map.get(DataUtil.isDataElementNull(list.get(position).get("UserRole_ID")))!=null) {
                    holder.imageView.setImageResource(Role_ID_Picture_map.get(DataUtil.isDataElementNull(list.get(position).get("UserRole_ID"))));
                }
                holder.work_num.setText(DataUtil.isDataElementNull(list.get(position).get("UserRoleName")));
                return convertView;
            }
        };
        listView.setAdapter(Adapter);
    }
    public ArrayList<ObjectElement> getList() {
        return list;
    }

    public void setList(ArrayList<ObjectElement> list) {
        this.list = list;
    }
    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }
    private void initMap(){
        Role_ID_Picture_map.put("1",R.mipmap.role_system_manager);
        Role_ID_Picture_map.put("2",R.mipmap.role_department_manager);
        Role_ID_Picture_map.put("3",R.mipmap.role_department_manager);
        Role_ID_Picture_map.put("4",R.mipmap.role_department_manager);
        Role_ID_Picture_map.put("5",R.mipmap.role_repair_engineering);
        Role_ID_Picture_map.put("6",R.mipmap.role_repairer);
        Role_ID_Picture_map.put("7",R.mipmap.role_war);
    }
}
