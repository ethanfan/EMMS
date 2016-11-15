package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.MainActivityAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.BaseOrganise;
import com.emms.schema.DataDictionary;
import com.emms.schema.DataRelation;
import com.emms.schema.DataType;
import com.emms.schema.Equipment;
import com.emms.schema.TaskOrganiseRelation;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/31.
 *
 */
public class SystemSettingActivity extends NfcActivity implements View.OnClickListener{
    private Context context=this;
    private ArrayList<ObjectElement> moduleList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_setting);
        initView();
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if(SharedPreferenceManager.getLanguageChange(this)){
            initView();
//        }
    }

    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.systemSetting);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.filter).setOnClickListener(this);
        ((ImageView)findViewById(R.id.filter)).setImageResource(R.mipmap.sync);
        GridView module_list = (GridView) findViewById(R.id.module_list);
        MainActivityAdapter adapter = new MainActivityAdapter(moduleList) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_cur, parent, false);
                    holder = new TaskViewHolder();
                    holder.image = (ImageView) convertView.findViewById(R.id.module_image);
                    holder.moduleName = (TextView) convertView.findViewById(R.id.module_name);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                if (moduleList.get(position).get("module_image") != null) {
                    holder.image.setImageResource(moduleList.get(position).get("module_image").valueAsInt());
                }
                if (moduleList.get(position).get("module_name") != null) {
                    holder.moduleName.setText(moduleList.get(position).get("module_name").valueAsInt());
                }
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
            for(int i=0;i<3;i++ ){
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
            case 1: {//FactorySetting
                obj.set("module_image", R.mipmap.system_setting_activity_setting);
                obj.set("module_name", R.string.FactorySetting);
                obj.set("Class", packageName + "SettingActivity");
                break;
            }
            case 2: {//EquipmentBinding
                obj.set("module_image", R.mipmap.system_setting_activity_binding);
                obj.set("module_name", R.string.EquipmentBinding);
                obj.set("Class", packageName + "EnteringEquipmentICCardIDActivity");
                break;
            }
            case 3: {//version_info
                obj.set("module_image", R.mipmap.module_version_info);
                obj.set("module_name", R.string.version_info);
                obj.set("Class", packageName + "VersionInfoActivity");
                break;
            }
            default:{
                obj.set("module_image", R.mipmap.module_version_info);
                obj.set("module_name", R.string.version_info);
                obj.set("Class", packageName + "VersionInfoActivity");
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
            case R.id.filter:{
                getDBDataLastUpdateTime();
                break;
            }
        }
    }


}
