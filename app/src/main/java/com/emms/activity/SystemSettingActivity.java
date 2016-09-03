package com.emms.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.Build;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.MainActivityAdapter;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.flyco.tablayout.widget.MsgView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/31.
 *
 */
public class SystemSettingActivity extends NfcActivity implements View.OnClickListener{
    private GridView module_list;
    private Context context=this;
    private ArrayList<ObjectElement> moduleList=new ArrayList<>();
    private MainActivityAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        initView();
        initData();
    }
    private void initView(){
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        module_list=(GridView)findViewById(R.id.module_list);
        adapter=new MainActivityAdapter(moduleList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                MainActivityAdapter.TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_cur, parent, false);
                    holder = new MainActivityAdapter.TaskViewHolder();
                    holder.image=(ImageView)convertView.findViewById(R.id.module_image);
                    holder.moduleName=(TextView)convertView.findViewById(R.id.module_name);
                    convertView.setTag(holder);
                } else {
                    holder = (MainActivityAdapter.TaskViewHolder) convertView.getTag();
                }
                if(moduleList.get(position).get("module_image")!=null){
                    holder.image.setImageResource(moduleList.get(position).get("module_image").valueAsInt());}
                if(moduleList.get(position).get("module_name")!=null){
                    holder.moduleName.setText(moduleList.get(position).get("module_name").valueAsInt());}
                return convertView;
            }
        };
        module_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(moduleList.get(position).get("Class")!=null){
                    try {
                        Class c=Class.forName(DataUtil.isDataElementNull(moduleList.get(position).get("Class")));
                        Intent intent=new Intent(context,c);
                        startActivity(intent);
                    }catch (Exception e){
                        Log.e("e",e.toString());
                    }
                }
            }
        });
        module_list.setAdapter(adapter);
    }
    private void initData(){
            for(int i=0;i<2;i++ ){
                JsonObjectElement jsonObjectElement=new JsonObjectElement();
                jsonObjectElement.set("module_ID",i+1);
                jsonObjectElement=moduleMatchingRule(jsonObjectElement);
                moduleList.add(jsonObjectElement);
            }
    }
    private JsonObjectElement moduleMatchingRule(JsonObjectElement obj){
        int module_id=obj.get("module_ID").valueAsInt();
        String packageName="com.emms.activity.";
        switch (module_id) {
            case 1: {//createTask
                obj.set("module_image", R.mipmap.system_setting_activity_setting);
                obj.set("module_name", R.string.FactorySetting);
                obj.set("Class", packageName + "SettingActivity");
                break;
            }
            case 2: {//maintainTask
                obj.set("module_image", R.mipmap.system_setting_activity_binding);
                obj.set("module_name", R.string.EquipmentBinding);
                obj.set("Class", packageName + "EnteringEquipmentICCardIDActivity");
                break;
            }

        }
        return obj;
    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.btn_right_action:{
                finish();
                break;
            }
        }
    }
}
