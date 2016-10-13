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
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.systemSetting);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
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
        }
    }

    private void getDBDataLastUpdateTime(){
        String sql="    select * from ( select max(LastUpdateTime) LastUpdateTime_BaseOrganise from BaseOrganise)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataDictionary from DataDictionary)," +
                "    (select max(LastUpdateTime) LastUpdateTime_DataType from DataType)," +
                "    (select max(LastUpdateTime) LastUpdateTime_Equipment from Equipment)," +
                "    (select max(LastUpdateTime) LastUpdateTime_TaskOrganiseRelation from TaskOrganiseRelation),"+
                "    (select max(LastUpdateTime) LastUpdateTime_DataRelation from DataRelation) ";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                getDataBaseUpdateFromServer(element.asArrayElement().get(0));
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
            }
        });
    }
    private void getDataBaseUpdateFromServer(DataElement dataElement){

        HttpParams params=new HttpParams();
        JsonObjectElement data=new JsonObjectElement();
        data.set("LastUpdateTime_BaseOrganise", DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_BaseOrganise")));
        data.set("LastUpdateTime_DataDictionary",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataDictionary")));
        data.set("LastUpdateTime_DataType",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataType")));
        data.set("LastUpdateTime_Equipment",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_Equipment")));
        data.set("LastUpdateTime_TaskOrganiseRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_TaskOrganiseRelation")));
        data.set("LastUpdateTime_DataRelation",DataUtil.isDataElementNull(dataElement.asObjectElement().get("LastUpdateTime_DataRelation")));
        if(SharedPreferenceManager.getFactory(this)==null){
            data.set("Factory_ID","GEW");}
        else {
            data.set("Factory_ID",SharedPreferenceManager.getFactory(this));
        }
        // data.set("IsNewApp",1);
        // params.put("sqlLiteDBModel",data.toJson());
        params.putJsonParams(data.toJson());
        HttpUtils.postWithoutCookie(this, "SqlToSqlite", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get("DataType")!=null&&json.get("DataType").isArray()&&json.get("DataType").asArrayElement().size()>0){
                        updateData(json.get("DataType"), EPassSqliteStoreOpenHelper.SCHEMA_DATATYPE);}
                    if(json.get("BaseOrganise")!=null&&json.get("BaseOrganise").isArray()&&json.get("BaseOrganise").asArrayElement().size()>0){
                        updateData(json.get("BaseOrganise"),EPassSqliteStoreOpenHelper.SCHEMA_BASE_ORGANISE);}
                    if(json.get("DataDictionary")!=null&&json.get("DataDictionary").isArray()&&json.get("DataDictionary").asArrayElement().size()>0){
                        updateData(json.get("DataDictionary"),EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY);}
                    if(json.get("Equipment")!=null&&json.get("Equipment").isArray()&&json.get("Equipment").asArrayElement().size()>0){
                        updateData(json.get("Equipment"),EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT);}
                    if(json.get("TaskOrganiseRelation")!=null&&json.get("TaskOrganiseRelation").isArray()&&json.get("TaskOrganiseRelation").asArrayElement().size()>0){
                        updateData(json.get("TaskOrganiseRelation"),EPassSqliteStoreOpenHelper.SCHEMA_TASK_ORGANISE_RELATION);}
                    if(json.get("DataRelation")!=null&&json.get("DataRelation").isArray()&&json.get("DataRelation").asArrayElement().size()>0){
                        updateData(json.get("DataRelation"),EPassSqliteStoreOpenHelper.SCHEMA_DATA_RELATION);}
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }
    private void updateData(DataElement data,String resource){
        getSqliteStore().createElement(data, resource, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                Log.e("Success","successSave");
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                Log.e("Fail","FailSave");
            }
        });
        if(data!=null&&data.isArray()){
            String s="";
            switch (resource){
                case "DataDictionary":{
                    s= DataDictionary.DATA_ID;
                    break;
                }
                case "Equipment":{
                    s= Equipment.EQUIPMENT_ID;
                    break;
                }
                case "BaseOrganise":{
                    s= BaseOrganise.ORGANISE_ID;
                    break;
                }
                case "DataType":{
                    s= DataType.DATATYPE_ID;
                    break;
                }
                case "TaskOrganiseRelation":{
                    s= TaskOrganiseRelation.TEAM_SERVICE_ID;
                    break;
                }
                case "DataRelation":{
                    s= DataRelation.DATARELATION_ID;
                    break;
                }
            }
            for(int i=0;i<data.asArrayElement().size();i++){
                getSqliteStore().updateElement(DataUtil.isDataElementNull(data.asArrayElement().get(i).asObjectElement().get(s)),
                        data.asArrayElement().get(i), resource, new StoreCallback() {
                            @Override
                            public void success(DataElement element, String resource) {
                                Log.e("SuccessUpdate", "SuccessUpdate");
                            }

                            @Override
                            public void failure(DatastoreException ex, String resource) {
                                Log.e("FailUpdate", "FailUpdate");
                            }
                        });
            }
        }


    }
}
