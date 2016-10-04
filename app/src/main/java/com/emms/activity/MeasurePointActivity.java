package com.emms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.adapter.ResultListAdapter;
import com.emms.adapter.StatusAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Equipment;
import com.emms.schema.MeasurePoint;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.apache.http.impl.client.TunnelRefusedException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/9/11.
 *
 */
public class MeasurePointActivity extends NfcActivity implements View.OnClickListener{
    private PullToRefreshListView Measure_Point_ListView;
    private ArrayList<ObjectElement> measure_point_list=new ArrayList<>();
    private TaskAdapter adapter;
    private Context context=this;
//    private int PAGE_SIZE=10;
//    private int pageIndex=1;
//    private int RecCount=0;
    private Handler handler=new Handler();

    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private String TaskId;
    private TextView menuSearchTitle;
    private EditText searchBox,iccard_id;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private boolean isSearchview ;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> MeasureValueList=new ArrayList<>();
    private HashMap<Integer,DropEditText> map=new HashMap<>();
    private HashMap<Integer,DropEditText> map2=new HashMap<>();
    private HashMap<String,String> MeasurePointType=new HashMap<>();
    private HashMap<String,String> MeasureValueMap=new HashMap<>();
    private HashMap<String,String> MeasureValueMap2=new HashMap<>();
    private ArrayList<ObjectElement> submitData=new ArrayList<>();
    private ArrayList<String> ObPointValueList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point);
        initData();
//        initView();
//        //TestData();
//        initSearchView();
//        GetMeasurePointList();
    }
    private void initView(){
        if (getIntent().getStringExtra(Task.TASK_SUBCLASS).equals(Task.ROUTING_INSPECTION)) {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.measure_point_list);
        }else {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.upkeep_point_list);
        }
        Measure_Point_ListView=(PullToRefreshListView)findViewById(R.id.measure_point_list);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        findViewById(R.id.btn_sure_bg).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_sure_bg).setOnClickListener(this);
        adapter=new TaskAdapter(measure_point_list) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
 //               if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_measure_point_list, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_group = (TextView) convertView.findViewById(R.id.measure_point_name);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.measure_point_content);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.measure_point_standard);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.measure_point_status);
                    holder.tv_task_state=(TextView)convertView.findViewById(R.id.sequence_number);
                    holder.editText=(EditText)convertView.findViewById(R.id.StateValueInput);
                    holder.dropEditText=(DropEditText)convertView.findViewById(R.id.MeasureValueSelect);
                    holder.image=(ImageView)convertView.findViewById(R.id.image);
                  //  holder.tv_repair_time=(TextView)convertView.findViewById(R.id.result_text);
                  //  holder.tv_end_time=(TextView)convertView.findViewById(R.id.state_text);

                   holder.dropEditText2=(DropEditText) convertView.findViewById(R.id.MeasureValueStandard);
                   holder.gridView=(GridView)convertView.findViewById(R.id.ObPointValueList);

                  // holder.tv_start_time=(TextView)convertView.findViewById(R.id.MeasureValueStandard_text);
//                    convertView.setTag(holder);
//                }else {
//                    holder = (TaskViewHolder) convertView.getTag();
//                }
                if(measure_point_list.get(position).get("tag")!=null){
                    if(measure_point_list.get(position).get("tag").valueAsBoolean()){
                        holder.image.setImageResource(R.mipmap.select_pressed);
                    }else {
                        holder.image.setImageResource(R.mipmap.select_normal);
                    }
                }
                convertView.findViewById(R.id.imageLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(measure_point_list.get(position).get("tag").valueAsBoolean()){
                            measure_point_list.get(position).set("tag",false);
                        }else {
                            measure_point_list.get(position).set("tag",true);
                        }
                        notifyDataSetChanged();
                        if(submitData.contains(measure_point_list.get(position))){
                            submitData.remove(measure_point_list.get(position));
                        }else {
                            submitData.add(measure_point_list.get(position));
                        }
                    }
                });
                map.put(position,holder.dropEditText);
                map2.put(position,holder.dropEditText2);
                if(measure_point_list.get(position).get("IsResultSubmit").valueAsBoolean()){
                    holder.tv_device_name.setText(R.string.IsCheck);
                    holder.tv_device_name.setTextColor(getResources().getColor(R.color.order_color));
                   // holder.tv_repair_time.setVisibility(View.VISIBLE);
                    //holder.tv_end_time.setVisibility(View.VISIBLE);
                   // holder.tv_start_time.setVisibility(View.VISIBLE);
                  //  holder.editText.setVisibility(View.GONE);
                  //  holder.dropEditText2.setVisibility(View.GONE);
                  //  holder.dropEditText.setVisibility(View.GONE);
                    //holder.image.setVisibility(View.GONE);
                }else {
                    holder.tv_device_name.setText(R.string.IsNotCheck);
                    holder.tv_device_name.setTextColor(getResources().getColor(R.color.esquel_red));
                    // holder.image.setVisibility(View.VISIBLE);
                    // holder.dropEditText.setVisibility(View.VISIBLE);
                    // holder.editText.setVisibility(View.VISIBLE);
                    //  holder.dropEditText2.setVisibility(View.VISIBLE);
                    //  holder.tv_repair_time.setVisibility(View.GONE);
                    // holder.tv_start_time.setVisibility(View.GONE);
                    //holder.tv_end_time.setVisibility(View.GONE);
                }
                   holder.dropEditText.getmEditText().setInputType(EditorInfo.TYPE_CLASS_PHONE);
                   holder.dropEditText2.getmEditText().setInputType(EditorInfo.TYPE_CLASS_PHONE);
                   // holder.editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                initDropSearchView(null,holder.dropEditText.getmEditText(),getResources().getString(R.string.MeasureValueInput), DataDictionary.DATA_NAME,position,R.string.nothing_found,holder.dropEditText.getDropImage());
                initDropSearchView(null,holder.dropEditText2.getmEditText(),getResources().getString(R.string.MeasureValueInput), DataDictionary.DATA_NAME,position,R.string.nothing_found,holder.dropEditText2.getDropImage());
                SetTextChangeListener(holder.editText,measure_point_list.get(position),"Remarks");
                SetTextChangeListener(holder.dropEditText.getmEditText(),measure_point_list.get(position),"ResultValue");
                SetTextChangeListener(holder.dropEditText2.getmEditText(),measure_point_list.get(position),"ReferenceValue");
//                holder.editText.addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        measure_point_list.get(position).set("Remarks",s.toString());
//                        measure_point_list.get(position).set("tag",true);
//                        //adapter.notifyDataSetChanged();
//                    }
//                });
//                holder.dropEditText.getmEditText().addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        measure_point_list.get(position).set("ResultValue",s.toString());
//                        measure_point_list.get(position).set("tag",true);
//                        //adapter.notifyDataSetChanged();
//                    }
//                });
//                holder.dropEditText2.getmEditText().addTextChangedListener(new TextWatcher() {
//                    @Override
//                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                    }
//
//                    @Override
//                    public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                    }
//
//                    @Override
//                    public void afterTextChanged(Editable s) {
//                        measure_point_list.get(position).set("ReferenceValue",s.toString());
//                        measure_point_list.get(position).set("tag",true);
//                        //adapter.notifyDataSetChanged();
//                    }
//                });
                String pointType=DataUtil.isDataElementNull(measure_point_list.get(position).get("PointType"));
                if(pointType.equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
                    convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.GONE);
                    holder.dropEditText.setVisibility(View.GONE);
                    ((TextView)convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(R.string.checkResult);
                    ((TextView)convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(R.string.StandardValue);
                    holder.gridView.setVisibility(View.VISIBLE);
                    holder.gridView.setAdapter(new StatusAdapter(ObPointValueList) {
                        @Override
                        public View getCustomView(View convertView1, int position1, ViewGroup parent1) {
                            StatusAdapter.ViewHolder viewHolder;
                            if(convertView1==null){
                                convertView1 = LayoutInflater.from(context).inflate(R.layout.item_ob_point, parent1, false);
                                viewHolder = new ViewHolder();
                                viewHolder.statu=(TextView)convertView1.findViewById(R.id.obValue);
                                convertView1.setTag(viewHolder);
                            }else {
                                viewHolder=(ViewHolder) convertView1.getTag();
                            }
                            viewHolder.statu.setText(ObPointValueList.get(position1));
                            if(viewHolder.statu.getText().toString().equals(
                                    DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")))){
                                viewHolder.statu.setTextColor(Color.WHITE);
                                viewHolder.statu.setBackgroundResource(R.drawable.bg_edit_select);
                            }else {
                                viewHolder.statu.setTextColor(Color.BLACK);
                                viewHolder.statu.setBackgroundResource(R.drawable.bg_edit_normal);
                            }
                            return convertView1;
                        }
                    });
                    holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent2, View view2, int position2, long id) {
                            measure_point_list.get(position).set("ResultValue",ObPointValueList.get(position2));
                            measure_point_list.get(position).set("tag", true);
                            if(!submitData.contains(measure_point_list.get(position))){
                                submitData.add(measure_point_list.get(position));
                            }
                            notifyDataSetChanged();
//                            for(int i=0;i<parent.getChildCount();i++){
//                                if(i==position){
//                                    View v=parent.getChildAt(position);
//                                    TextView tv=(TextView)v.findViewById(R.id.obValue);
//                                    tv.setTextColor(Color.WHITE);
//                                    //tv.setBackgroundColor(Color.RED);
//                                    tv.setBackgroundResource(R.drawable.bg_edit_select);
//                                }
//                                else{
//                                    View v=parent.getChildAt(i);
//                                    TextView tv=(TextView)v.findViewById(R.id.obValue);
//                                    tv.setTextColor(Color.BLACK);
//                                    tv.setBackgroundResource(R.drawable.bg_edit_normal);
//                                }
//                            }
                        }
                    });
                }else if(pointType.equals(MeasurePoint.PROCESS_MEASURE_POINT)){
                    convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.VISIBLE);
                    holder.gridView.setVisibility(View.GONE);
                    holder.dropEditText.setVisibility(View.VISIBLE);
                    ((TextView)convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(R.string.MeasureValue);
                    ((TextView)convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(R.string.SettingMeasureResult);
                }else {
                    convertView.findViewById(R.id.StandardMeasureValueLayout).setVisibility(View.VISIBLE);
                    holder.gridView.setVisibility(View.GONE);
                    holder.dropEditText.setVisibility(View.VISIBLE);
                    ((TextView)convertView.findViewById(R.id.MeasureValueSelect_tag)).setText(R.string.MeasureValue);
                    ((TextView)convertView.findViewById(R.id.MeasureValueStandard_tag)).setText(R.string.StandardValue);
                }

                holder.tv_task_state.setText(String.valueOf(position+1));
                holder.editText.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("Remarks")));
                holder.dropEditText.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")));
                //holder.tv_repair_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ResultValue")));
                //holder.tv_end_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("Remarks")));
                holder.tv_group.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("TaskItemName")));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("PointContent")));
                holder.tv_create_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("MaintainStandard")));
                holder.dropEditText2.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ReferenceValue")));
                //holder.tv_start_time.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("ReferenceValue")));
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context,MeasurePointContentActivity.class);
                        intent.putExtra("measure_point_detail",measure_point_list.get(position).toString());
                        startActivity(intent);
                    }
                });
                return convertView;
            }
        };
        Measure_Point_ListView.setAdapter(adapter);
        Measure_Point_ListView.setMode(PullToRefreshListView.Mode.PULL_FROM_START);
        Measure_Point_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                       // pageIndex=1;
                        GetMeasurePointList();
                        Measure_Point_ListView.onRefreshComplete();
                    }
                });
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GetMeasurePointList();
                        Measure_Point_ListView.onRefreshComplete();
                    }
                },0);
            }
        });
//        Measure_Point_ListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(context,MeasurePointContentActivity.class);
//                intent.putExtra("measure_point_detail",measure_point_list.get(position).toString());
//                startActivity(intent);
//            }
//        });

    }
    private void initData(){
//        for(int i=0;i<3;i++){
//            JsonObjectElement jsonObjectElement=new JsonObjectElement();
//            if(i==0) {
//                jsonObjectElement.set(DataDictionary.DATA_NAME,"正常");
//            }
//            if(i==1) {
//                jsonObjectElement.set(DataDictionary.DATA_NAME, "报警");
//            }
//            if(i==2) {
//                jsonObjectElement.set(DataDictionary.DATA_NAME,"危险" );
//            }
//            MeasureValueList.add(jsonObjectElement);
//        }
        String sql="select * from DataDictionary where DataType = 'MaintainPointResult'";
        getSqliteStore().performRawQuery(sql, EPassSqliteStoreOpenHelper.SCHEMA_DATADICTIONARY, new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                for(int i=0;i<element.asArrayElement().size();i++){
                    ObjectElement jsonObjectElement=element.asArrayElement().get(i).asObjectElement();
                  //  MeasurePointType.put(DataUtil.isDataElementNull(jsonObjectElement.get("DataCode")),
                   //         DataUtil.isDataElementNull(jsonObjectElement.get("DataName")));
                    MeasureValueList.add(jsonObjectElement);
                    MeasureValueMap.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)),
                            DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)));
                    MeasureValueMap2.put(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_CODE)),
                            DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                    ObPointValueList.add(DataUtil.isDataElementNull(jsonObjectElement.get(DataDictionary.DATA_NAME)));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView();
                        //TestData();
                        initSearchView();
                        GetMeasurePointList();
                    }
                });
            }
            @Override
            public void failure(DatastoreException ex, String resource) {
                 ToastUtil.showToastLong("获取数据失败，请重启App",context);
            }
        });
        MeasurePointType.put(MeasurePoint.UPKEEP_POINT,getResources().getString(R.string.MPT01));
        MeasurePointType.put(MeasurePoint.PROCESS_MEASURE_POINT,getResources().getString(R.string.MPT02));
        MeasurePointType.put(MeasurePoint.OBVERSE_MEASURE_POINT,getResources().getString(R.string.MPT03));
        MeasurePointType.put(MeasurePoint.CHECK_POINT,getResources().getString(R.string.MPT04));
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                finish();
                break;
            }
            case R.id.btn_sure_bg:{
                SubmitDataToServer();
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
                isSearchview = true ;
                String itemNam = mResultAdapter.getItemName();
                //SelectItem=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(Equipment.EQUIPMENT_ID));
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            switch (searchtag) {
//                                case 1:
//                                    //equipment_id.getmEditText().setText(searchResult);
//                                    break;
//                            }
                            map.get(searchtag).getmEditText().setText(searchResult);
                            map2.get(searchtag).getmEditText().setText(searchResult);
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastLong(R.string.error_occur,context);
                }
            }
        });
//        initDropSearchView(null, equipment_id.getmEditText(), context.getResources().
//                        getString(R.string.work_num_dialog), Equipment.ASSETSID,
//                1, R.string.getDataFail,equipment_id.getDropImage());
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
            final EditText condition, final EditText subEditText,
            final String searchTitle, final String searchName, final int searTag , final int tips, ImageView imageView){
        subEditText.setFocusable(true);
        subEditText.setFocusableInTouchMode(true);
        subEditText.setHint(R.string.pleaseInput);
        subEditText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        DropSearch(condition,
//                                searchTitle,searchName,searTag ,tips);
                        subEditText.requestFocus();
                    }
                });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if(DataUtil.isDataElementNull(measure_point_list.get(searTag).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
                 DropSearch(condition,
                         searchTitle, searchName, searTag, tips);
             }else {
                 subEditText.setText(MeasureValueMap2.get("MPR03"));
             }
            }
        });
    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final int tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
//                switch (searTag) {
//                    case 1:{
//                if(DataUtil.isDataElementNull(measure_point_list.get(searTag).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
                searchDataLists.addAll(MeasureValueList);
//                }else {
//                searchDataLists.add(MeasureValueList.get(MeasureValueList.size()-1));
//                }
//                        break;
//                    }}
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
    private void GetMeasurePointList(){
//        if(RecCount!=0){
//            if((pageIndex-1)*PAGE_SIZE>=RecCount){
//                ToastUtil.showToastLong(R.string.noMoreData,context);
//                return;
//            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
//        params.put("task_id",getIntent().getStringExtra(Task.TASK_ID));
//        params.put("taskEquipment_id",DataUtil.isDataElementNull(new JsonObjectElement(getIntent().getStringExtra("TaskEquipment")).get("TaskEquipment_ID")));
//        params.put("pageSize",PAGE_SIZE);
//        params.put("pageIndex",pageIndex);
        HttpUtils.post(this, "TaskMaintain/MaintainPointList?task_id="+getIntent().getStringExtra(Task.TASK_ID)
                +"&taskEquipment_id="+DataUtil.isDataElementNull(new JsonObjectElement(getIntent().getStringExtra("TaskEquipment")).get("TaskEquipment_ID"))
                +"&pageSize=1000&pageIndex=1", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                    super.onSuccess(t);
                    if(t!=null) {
                        submitData.clear();
                        measure_point_list.clear();
                        JsonArrayElement jsonArrayElement = new JsonArrayElement(t);
                        //RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        if(jsonArrayElement.size()>0) {
                            //if (pageIndex == 1) {
//                            }
//                            pageIndex++;
                            for (int i = 0; i < jsonArrayElement.size(); i++) {
                               ObjectElement json=jsonArrayElement.get(i).asObjectElement();
                                json.set("tag",false);
//                                if(!json.get("IsResultSubmit").valueAsBoolean()
//                                        &&DataUtil.isDataElementNull(json.get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
//                                    json.set("ResultValue",DataUtil.isDataElementNull(MeasureValueList.get(0).get(DataDictionary.DATA_NAME)));
//                                }
                                json.set("num",i+1);
                                measure_point_list.add(json);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setDatas(measure_point_list);
                                adapter.notifyDataSetChanged();
                            }
                        });
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.FailGetMeasurePointListCauseByTimeOut,context);
                dismissCustomDialog();
            }
        });
    }
    private void SubmitDataToServer(){
        if(submitData.size()<=0){
            ToastUtil.showToastLong(R.string.pleaseSelectSubmitData,context);
            return;
        }
        for(int i=0;i<submitData.size();i++){
            if(submitData.get(i).get("ResultValue")==null||DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")).equals("")){
                String s=getResources().getString(R.string.pleaseInputMeasureData)+","
                        +getResources().getString(R.string.id)
                        +DataUtil.isDataElementNull(submitData.get(i).get("num"));
                ToastUtil.showToastLong(s,context);
                return;
            }
            if(!DataUtil.isDataElementNull(submitData.get(i).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)){
             if(submitData.get(i).get("ReferenceValue")==null||DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")).equals("")){
                String s=getResources().getString(R.string.pleaseInputMeasureDataStandard)+","
                        +getResources().getString(R.string.id)
                        +DataUtil.isDataElementNull(submitData.get(i).get("num"));
                ToastUtil.showToastLong(s,context);
                return;
             }
            if(MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")))==null) {
                    if (!DataUtil.isNum(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")).trim())
                            || !DataUtil.isFloat(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")).trim())) {
                        String s = getResources().getString(R.string.pleaseInputNum) + ","
                                + getResources().getString(R.string.id)
                                + DataUtil.isDataElementNull(submitData.get(i).get("num"));
                        ToastUtil.showToastLong(s, context);
                        return;
                    }
                }
            }else {
                if(MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")))==null) {
                        String s = getResources().getString(R.string.pleaseSelectValue) + ","
                                + getResources().getString(R.string.id)
                                + DataUtil.isDataElementNull(submitData.get(i).get("num"));
                        ToastUtil.showToastLong(s, context);
                        return;
                }
            }
            if(MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")))==null) {
                if (!DataUtil.isNum(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")).trim())
                        || !DataUtil.isFloat(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")).trim())) {
                    String s = getResources().getString(R.string.pleaseInputNum) + ","
                            + getResources().getString(R.string.id)
                            + DataUtil.isDataElementNull(submitData.get(i).get("num"));
                    ToastUtil.showToastLong(s, context);
                    return;
                }
            }

        }
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        ArrayList<ObjectElement> list=new ArrayList<>();
        for(int i=0;i<submitData.size();i++){
            JsonObjectElement jsonObjectElement=new JsonObjectElement();
            jsonObjectElement.set("TaskItem_ID",DataUtil.isDataElementNull(submitData.get(i).get("TaskItem_ID")));
            if(MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")))!=null){
                jsonObjectElement.set("ResultCode",MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ResultValue"))));
            }else {
                jsonObjectElement.set("ResultValue", DataUtil.isDataElementNull(submitData.get(i).get("ResultValue")));
            }
           //TODO
            if(!DataUtil.isDataElementNull(submitData.get(i).get("PointType")).equals(MeasurePoint.OBVERSE_MEASURE_POINT)) {
                if (MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue"))) != null) {
                    //jsonObjectElement.set("ReferenceValue", MeasureValueMap.get(DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue"))));
                } else {
                    jsonObjectElement.set("ReferenceValue", DataUtil.isDataElementNull(submitData.get(i).get("ReferenceValue")));
                }
            }
            jsonObjectElement.set("Remarks", DataUtil.isDataElementNull(submitData.get(i).get("Remarks")));
            list.add(jsonObjectElement);
        }
        final JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        HttpUtils.post(context, "TaskMaintain/JudeResultValue", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                dismissCustomDialog();
                if(t!=null) {
//                    JsonObjectElement json=new JsonObjectElement(t);
//                    if(json.get(Data.SUCCESS).valueAsBoolean()){
//                           //pageIndex=1;
//                          GetMeasurePointList();
//                    }else {
//                        ToastUtil.showToastLong(R.string.submit_Fail,context);
//                    }
//                }

                GetMeasurePointList();
                ToastUtil.showToastLong(R.string.submitSuccess,context);
                JsonArrayElement data=new JsonArrayElement(t);
                for(int i=0;i<data.size();i++) {
                    if (DataUtil.isDataElementNull(data.get(i).asObjectElement().get("EventType")).equals("ET01")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                context);
                        builder.setMessage("\""+DataUtil.isDataElementNull(data.get(i).asObjectElement().get("MaintainWorkItemName"))
                                +"\""+getResources().getString(R.string.CreateNewTaskTips));
                        builder.setPositiveButton(R.string.CreateNewTask, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CreateNewTask();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                        break;
                    }
                }


                }else {
                    ToastUtil.showToastLong(R.string.submitFail,context);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.submitFail,context);
                dismissCustomDialog();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case Constants.REQUEST_CODE_MEASURE_POINT:{
//                if(resultCode==1){
//                    GetMeasurePointList();
//                }
//                break;
//            }
//        }
//    }
    private void CreateNewTask(){
            Intent intent=new Intent(MeasurePointActivity.this,CreateTaskActivity.class);
            intent.putExtra("TaskEquipment",getIntent().getStringExtra("TaskEquipment"));
            intent.putExtra(Task.TASK_ID,getIntent().getStringExtra(Task.TASK_ID));
            intent.putExtra("FromMeasurePointActivity","FromMeasurePointActivity");
            startActivity(intent);
    }
    private void SetTextChangeListener(EditText editText,final ObjectElement objectElement, final String key){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                objectElement.set(key,s.toString());
                //objectElement.set("tag",true);
                //adapter.notifyDataSetChanged();
            }
        });
    }
}
