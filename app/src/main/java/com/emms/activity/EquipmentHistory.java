package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/22.
 *
 */
public class EquipmentHistory extends NfcActivity implements View.OnClickListener{
    private PullToRefreshListView listView;
    private TaskAdapter adapter;
    private String Task_Description="";
    private String EquipmentName="";
    private String FaultType="";
    private int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    private Context context=this;

    private TextView menuSearchTitle;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> EquipmentList=new ArrayList<>();
    private ArrayList<ObjectElement> task_description_list=new ArrayList<>();
    private ArrayList<ObjectElement> fault_type_list=new ArrayList<>();
    private ArrayList<ObjectElement> fault_summary_list=new ArrayList<>();
    private DropEditText equipment_name,task_description,fault_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_history);
        String equipment_ID = getIntent().getStringExtra(Equipment.EQUIPMENT_ID);
        Task_Description=getIntent().getStringExtra(Task.TASK_DESCRIPTION);
        EquipmentName=getIntent().getStringExtra(Equipment.EQUIPMENT_NAME);
        //createTextData();
        initView();
        if(equipment_ID !=null || EquipmentName!=null || Task_Description!=null){
            findViewById(R.id.search_filter).setVisibility(View.INVISIBLE);
            pageIndex=1;
            getEquipmentHistoryFromServer();
        }
        initData();
        initSearchView();
    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.DeveceHistory);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        equipment_name=(DropEditText)findViewById(R.id.equipment_name);
        task_description=(DropEditText)findViewById(R.id.task_description) ;
        fault_type=(DropEditText)findViewById(R.id.fault_type) ;
        if(EquipmentName!=null) {
            equipment_name.getmEditText().setText(EquipmentName);
        }
        listView=(PullToRefreshListView)findViewById(R.id.equipment_history_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex = 1;
                        getEquipmentHistoryFromServer();
                        listView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getEquipmentHistoryFromServer();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                }, 0);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context,EquipmentFaultSummaryActivity.class);
                intent.putExtra("FaultDetail",fault_summary_list.get(position-1).asObjectElement().toString());
                intent.putExtra(Equipment.EQUIPMENT_NAME,EquipmentName);
                startActivity(intent);
            }
        });
        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.search_button).setOnClickListener(this);
        findViewById(R.id.search_filter).setOnClickListener(this);
        adapter=new TaskAdapter(fault_summary_list) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_equipment_history, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater=(TextView)convertView.findViewById(R.id.task_description);//任务描述
                    holder.tv_task_describe=(TextView)convertView.findViewById(R.id.fault_type);//故障类型
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.summary_description);//故障描述
                    holder.tv_task_state=(TextView)convertView.findViewById(R.id.sequence_number);//序号
                    holder.tv_group=(TextView)convertView.findViewById(R.id.equipment_name);//设备名称
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.repair_status);//设备名称
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //待修改
                holder.tv_creater.setText(DataUtil.isDataElementNull(fault_summary_list.get(position).get(Task.TASK_DESCRIPTION)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(fault_summary_list.get(position).get("TroubleType")));
                holder.warranty_person.setText(DataUtil.isDataElementNull(fault_summary_list.get(position).get("TroubleDescribe")));
                holder.tv_group.setText(DataUtil.isDataElementNull(fault_summary_list.get(position).get("TaskEquipmentList")));
                holder.tv_create_time.setText(DataUtil.isDataElementNull(fault_summary_list.get(position).get("MaintainDescribe")));
                holder.tv_task_state.setText(String.valueOf(position+1));
                return convertView;
            }
        };
        listView.setAdapter(adapter);
    }
    private void getEquipmentHistoryFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastLong(R.string.noMoreData,context);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        JsonObjectElement json=new JsonObjectElement();
        json.set("pageSize",PAGE_SIZE);
        json.set("pageIndex",pageIndex);
//        params.put("pageSize",PAGE_SIZE);
//        params.put("pageIndex",pageIndex);
        if(EquipmentName!=null&&!EquipmentName.equals("")){
        //params.put("equipmentClass",EquipmentName);
            json.set("EquipmentClass",EquipmentName);
        }
        if(Task_Description!=null&&!Task_Description.equals("")){
       // params.put("taskDescr",Task_Description);
            json.set("TaskDescr",Task_Description);
        }
        if(FaultType!=null&&!FaultType.equals("")){
            //params.put("troubleType",FaultType);
            json.set("TroubleType",FaultType);
        }
        params.putJsonParams(json.toJson());
        HttpUtils.post(this, "TaskEquipmentHistoryList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                    super.onSuccess(t);
                if(t!=null){
                    if (pageIndex == 1) {
                        fault_summary_list.clear();
                    }
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    if(jsonObjectElement.get("PageData")!=null
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            fault_summary_list.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                        //      setData(datas);
                    }else {
                        ToastUtil.showToastLong(R.string.noData,context);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setDatas(fault_summary_list);
                            adapter.notifyDataSetChanged();
                        }
                    });

                }
                dismissCustomDialog();
                }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.loadingFail,context);
                dismissCustomDialog();
            }
        });
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
                if(findViewById(R.id.search_filter).getVisibility()==View.GONE||
                        findViewById(R.id.search_filter).getVisibility()==View.INVISIBLE){
                //findViewById(R.id.search_filter).setVisibility(View.VISIBLE);
                    buttonAnim(true);
                }
                else {
                    //findViewById(R.id.search_filter).setVisibility(View.GONE);
                    buttonAnim(false);
                }
                break;
            }
            case R.id.search_filter:{
                break;
            }
            case R.id.search_button:{
                pageIndex=1;
                getEquipmentHistoryFromServer();
                buttonAnim(false);
                break;
            }
        }
    }
    private void initSearchView() {
        searchBox = (EditText) findViewById(R.id.et_search);
        mDrawer_layout = (CustomDrawerLayout) findViewById(R.id.search_page);
        mDrawer_layout.setCloseDrawerListener(new CloseDrawerListener() {
            @Override
            public void close() {
                searchBox.setText("");
            }
        });
        mDrawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawer_layout.setBackgroundColor(Color.parseColor("#00000000"));
        menuSearchTitle = (TextView) findViewById(R.id.left_title);
        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultAdapter = new ResultListAdapter(context);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =mResultAdapter.getItem(position).get(itemNam).valueAsString();
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:{
                                    equipment_name.getmEditText().setText(searchResult);
                                    EquipmentName=searchResult;
                                    getTaskDecriptionFromDataBaseByEquipmentName(DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_CLASS)));
                                    break;}
                                case 2:{
                                    task_description.getmEditText().setText(searchResult);
                                    Task_Description=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;
                                }
                                case 3:{
                                    fault_type.getmEditText().setText(searchResult);
                                    FaultType=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;
                                }
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastLong(R.string.error_occur,context);
                }
            }
        });
        initDropSearchView(null, equipment_name.getmEditText(), context.getResources().
                        getString(R.string.title_search_equipment_name),Equipment.EQUIPMENT_NAME,
                1, R.string.getDataFail,equipment_name.getDropImage());
        initDropSearchView(null, task_description.getmEditText(), context.getResources().
                        getString(R.string.simpleDescription), DataDictionary.DATA_NAME,
                2, R.string.pleaseSelectEquipmentNameOrNoData,task_description.getDropImage());
        initDropSearchView(null, fault_type.getmEditText(), context.getResources().
                        getString(R.string.faultType),DataDictionary.DATA_NAME,
                3, R.string.pleaseSelectEquipmentName,fault_type.getDropImage());
        findViewById(R.id.left_btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer_layout.closeDrawer(Gravity.RIGHT);
            }
        });
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // initData(s.toString());
                String keyword = s.toString();
                clearBtn.setVisibility(View.VISIBLE);
                mResultListView.setVisibility(View.VISIBLE);
                String itemName = mResultAdapter.getItemName();
                ArrayList<ObjectElement> result = search(keyword, itemName);
                if (result == null || result.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    mResultAdapter.changeData(result, itemName);
                }
            }
        });


        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBox.setText("");
                clearBtn.setVisibility(View.GONE);
            }
        });
    }
    private ArrayList<ObjectElement> search(String keyword,String  tagString) {
        ArrayList<ObjectElement> reDatas = new ArrayList<>();
        for (int i = 0; i < searchDataLists.size(); i++) {
            if (searchDataLists.get(i).get(tagString).valueAsString().toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }
    private void initDropSearchView(
            final EditText condition, EditText subEditText,
            final String searchTitle, final String searchName, final int searTag , final int tips, ImageView imageView){
        subEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DropSearch(condition,
                                searchTitle,searchName,searTag ,tips);
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropSearch(condition,
                        searchTitle,searchName,searTag ,tips);
            }
        });
    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final int tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag) {
                    case 1:{
                        searchDataLists.addAll(EquipmentList);
                        break;
                    }
                    case 2:{
                        searchDataLists.addAll(task_description_list);
                        break;}
                    case 3:{
                        searchDataLists.addAll(fault_type_list);
                        break;
                    }
                }
                searchtag = searTag;
                if (condition != null) {
                    if (!condition.getText().toString().equals("") && searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastLong(tips,context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastLong(tips,context);
                    }
                }
            }
        });
    }
    private void initData(){
        getEquipmentListFromDataBase();
        getTaskDecriptionFromDataBaseByEquipmentName("");
        getFaultTypeFromDataBaseByEquipmentName("");
    }
    private void getEquipmentListFromDataBase(){
        String rawQuery ="select  distinct EquipmentName,EquipmentClass from Equipment where  EquipmentName is not null and EquipmentName is not ''";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(final DataElement element) {
                EquipmentList.clear();
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for(int i=0;i<element.asArrayElement().size();i++){
                        EquipmentList.add(element.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }
    private void getTaskDecriptionFromDataBaseByEquipmentName(String EquipmentClass){
        DataUtil.getDataFromDataBase(context, "EquipmentClassTrouble", EquipmentClass, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                task_description_list.clear();
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for (int i=0;i<element.asArrayElement().size();i++){
                        if(!DataUtil.isDataElementNull(element.asArrayElement().get(i).asObjectElement().get(DataDictionary.DATA_CODE)).equals("Default")) {
                            task_description_list.add(element.asArrayElement().get(i).asObjectElement());
                        }
                    }
                }
                JsonObjectElement jsonObjectElement=new JsonObjectElement();
                jsonObjectElement.set(DataDictionary.DATA_NAME,getResources().getString(R.string.other));
                jsonObjectElement.set(DataDictionary.DATA_CODE,"Default");
                task_description_list.add(0,jsonObjectElement);
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastLong(R.string.noSimpleDescriptionData,context);
                    }
                });
            }
        });

    }
    private void getFaultTypeFromDataBaseByEquipmentName(String EquipmentClass){

        DataUtil.getDataFromDataBase(context, "EquipmentTroubleSort", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                fault_type_list.clear();
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for (int i=0;i<element.asArrayElement().size();i++){
                        fault_type_list.add(element.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastLong(R.string.NoFaultType,context);
                    }
                });
            }
        });

    }
//    private void createTextData(){
//        for(int i=0;i<20;i++){
//        JsonObjectElement jsonObjectElement=new JsonObjectElement();
//        jsonObjectElement.set(Task.TASK_ID,"1234");
//        jsonObjectElement.set("FaultType","asdfasdfasdfasdfasd");
//        jsonObjectElement.set("summary_person","aaaa");
//        fault_summary_list.add(jsonObjectElement);}
//    }
    private void buttonAnim(final boolean showChannelFilterView){
        if(showChannelFilterView){
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.expand);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.search_filter).setVisibility(View.VISIBLE);
                    //o.pause();
                    //findViewById(R.id.btn_menu).clearAnimation();

                }
            });
            //Animation operatingAnim = AnimationUtils.loadAnimation(ArticleListActivity.this, R.anim.channellistfilterbuttonanim);
            LinearInterpolator lin = new LinearInterpolator();
            //operatingAnim.setFillAfter(true);
            //findViewById(R.id.btn_menu).startAnimation(operatingAnim);
            operatingAnim2.setInterpolator(lin);
            //operatingAnim2.startNow();
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);
        }else{
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.collapse);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    findViewById(R.id.search_filter).setVisibility(View.GONE);
                    //o.pause();


                }
            });
            //Animation operatingAnim = AnimationUtils.loadAnimation(ArticleListActivity.this, R.anim.channellistfilterbuttonanim2);
            LinearInterpolator lin = new LinearInterpolator();
            //operatingAnim.setFillAfter(true);
            // findViewById(R.id.btn_menu).startAnimation(operatingAnim);
            operatingAnim2.setInterpolator(lin);
            //operatingAnim2.startNow();
            findViewById(R.id.search_filter).startAnimation(operatingAnim2);

        }

        //findViewById(R.id.btn_menu).clearAnimation();
    }
}
