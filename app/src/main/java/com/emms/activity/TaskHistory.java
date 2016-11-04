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
import android.widget.Toast;

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
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.ui.CancelTaskDialog;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DropEditText;
import com.emms.ui.TaskCancelListener;
import com.emms.util.DataUtil;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/8/4.
 *
 */
public class TaskHistory extends NfcActivity implements View.OnClickListener{
    private PullToRefreshListView listView;
    private TaskAdapter adapter;
    private ArrayList<ObjectElement> data=new ArrayList<>();
    private Context context=this;
    private int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();


    private TextView menuSearchTitle;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> taskClassList=new ArrayList<>();
    private ArrayList<ObjectElement> taskStatusList=new ArrayList<>();
    private ArrayList<ObjectElement> timeList=new ArrayList<>();
    private DropEditText task_class,task_status,time;
    private String taskClassCode=Task.REPAIR_TASK,taskStatusCode="",timeCode="30";
    private HashMap<String,String> map=new HashMap<>();
    private HashMap<String,Integer> taskStatusMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);
       // TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        initMap();
        initView();
        initData();
        initSearchView();
        //getTaskHistory();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.taskHistory);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.search_filter).setOnClickListener(this);
        task_class=(DropEditText)findViewById(R.id.task_class) ;
        task_status=(DropEditText)findViewById(R.id.task_status) ;
        time=(DropEditText)findViewById(R.id.time) ;
        listView=(PullToRefreshListView)findViewById(R.id.taskList);
        adapter=new TaskAdapter(data) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time= (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_device_name=(TextView)convertView.findViewById(R.id.tv_task_class);
                    holder.tv_creater=(TextView)convertView.findViewById(R.id.command);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
              //  holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get("Organise_ID")));
                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get("Status")));
                holder.tv_repair_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME)));
                holder.tv_start_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME)));
                holder.tv_end_time.setText(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                if(DataUtil.isDataElementNull(data.get(position).get("IsEvaluated")).equals("1")){
                holder.tv_creater.setText(getResources().getString(R.string.isCommand));
                    holder.tv_creater.setTextColor(getResources().getColor(R.color.order_color));
                }
                if(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS)))!=null) {
                    holder.tv_device_name.setText(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS))));
                }
                return convertView;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(context))!=7){
                    Intent intent=new Intent(context,TaskDetailsActivity.class);
                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
                    intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(data.get(position-1).get(Task.TASK_ID)));
                    intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(data.get(position-1).get(Task.TASK_CLASS)));
                    intent.putExtra("TaskStatus",taskStatusMap.get(DataUtil.isDataElementNull(data.get(position-1).get("Status"))));
                    intent.putExtra("isTaskHistory",true);
                startActivity(intent);
            //}
//                else {
//                    Intent intent=new Intent(context,CommandActivity.class);
//                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
//                    startActivity(intent);
//                }
            }
        });
        listView.setMode(PullToRefreshListView.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        pageIndex=1;
                        getTaskHistory();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listView.onRefreshComplete();
                        getTaskHistory();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                //TODO
                if(Integer.valueOf(SharedPreferenceManager.getUserRoleID(context))>4){
                    return true;
                }
                if(DataUtil.isDataElementNull(data.get(position-1).get("Status")).equals(getResources().getString(R.string.linked_order))){
                    ToastUtil.showToastShort(R.string.FailCancelTaskCauseByTaskCompleted,context);
                    return true;
                }
                CancelTaskDialog cancleTaskDialog=new CancelTaskDialog(context);
                cancleTaskDialog.setTaskCancelListener(new TaskCancelListener() {
                    @Override
                    public void submitCancel(String CancelReason) {
                        CancelTask(data.get(position-1),CancelReason);
                    }
                });
                cancleTaskDialog.show();
                return true;
            }
        });
        findViewById(R.id.filter).setOnClickListener(this);
        findViewById(R.id.filter).setVisibility(View.VISIBLE);
        findViewById(R.id.search_button).setOnClickListener(this);
    }
    private void getTaskHistory(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastShort(R.string.noMoreData,context);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        if(!taskClassCode.equals("")){
        params.put("task_class", taskClassCode);}
        if(!taskStatusCode.equals("")){
            params.put("status",taskStatusCode);
        }
        if(!timeCode.equals("")){
            params.put("dateLength",timeCode);
        }
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(this, "TaskHistoryList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    if (pageIndex == 1) {
                        data.clear();
                    }
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        pageIndex++;
                        for(DataElement dataElement:jsonObjectElement.get("PageData").asArrayElement()){
                         data.add(dataElement.asObjectElement());
                        }
                    }else{
                        ToastUtil.showToastShort(R.string.noData,context);
                    }
                    adapter.notifyDataSetChanged();
                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });
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
                                    task_class.getmEditText().setText(searchResult);
                                    taskClassCode=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(DataDictionary.DATA_CODE));
                                    break;}

                                case 2:{
                                    task_status.getmEditText().setText(searchResult);
                                    taskStatusCode=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("Status"));
                                    break;
                                }
                                case 3:{
                                    time.getmEditText().setText(searchResult);
                                    timeCode=DataUtil.isDataElementNull(mResultAdapter.getItem(position).get("Time"));
                                }
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                   ToastUtil.showToastShort(R.string.error_occur,context);
                }
            }
        });
        initDropSearchView(null, task_class.getmEditText(), context.getResources().
                        getString(R.string.title_search_task_type),DataDictionary.DATA_NAME,
                1, R.string.getDataFail,task_class.getDropImage());
        initDropSearchView(null, task_status.getmEditText(), context.getResources().
                        getString(R.string.task_s), DataDictionary.DATA_NAME,
                2, R.string.getDataFail,task_status.getDropImage());
        initDropSearchView(null, time.getmEditText(), context.getResources().
                        getString(R.string.title_time),DataDictionary.DATA_NAME,
                3, R.string.getDataFail,time.getDropImage());
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
                        searchDataLists.addAll(taskClassList);
                        break;
                    }
                    case 2:{
                        searchDataLists.addAll(taskStatusList);
                        break;}
                    case 3:{
                        searchDataLists.addAll(timeList);
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
                        ToastUtil.showToastShort(tips,context);
                    }
                } else {
                    if (searchDataLists.size() > 0) {
                        mDrawer_layout.openDrawer(Gravity.RIGHT);
                        mResultAdapter.changeData(searchDataLists, searchName);
                        menuSearchTitle.setText(searchTitle);
                        menuSearchTitle.postInvalidate();
                        mDrawer_layout.postInvalidate();

                    } else {
                        ToastUtil.showToastShort(tips,context);
                    }
                }
            }
        });
    }
    private void initData(){
        initTaskClassData();
        initTaskStatusData();
        intiTimeData();
    }
    private void initTaskClassData(){

        String rawQuery = "select * from DataDictionary " +
                "where DataType = 'TaskClass' and PData_ID = 0";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_DEPARTMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {
            @Override
            public void onSuccess(DataElement element) {
                if (element != null && element.isArray()
                        && element.asArrayElement().size() > 0) {
                    taskClassList.clear();
                    for (int i = 0; i < element.asArrayElement().size(); i++) {
                        taskClassList.add(element.asArrayElement().get(i).asObjectElement());
                    }
                } else {
                    Toast.makeText(context, "无数据", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                System.out.println(throwable.getMessage());
            }
        });
    }
    private void initTaskStatusData(){
        JsonObjectElement jsonObjectElement0=new JsonObjectElement();
        jsonObjectElement0.set(DataDictionary.DATA_NAME,getResources().getString(R.string.pending_orders));
        jsonObjectElement0.set("Status",0);
        JsonObjectElement jsonObjectElement1=new JsonObjectElement();
        jsonObjectElement1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.start));
        jsonObjectElement1.set("Status",1);
        JsonObjectElement jsonObjectElement2=new JsonObjectElement();
        jsonObjectElement2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.task_state_details_finish));
        jsonObjectElement2.set("Status",2);
        taskStatusList.add(jsonObjectElement0);
        taskStatusList.add(jsonObjectElement1);
        taskStatusList.add(jsonObjectElement2);
    }
    private void intiTimeData(){
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(DataDictionary.DATA_NAME,getResources().getString(R.string.OneDay));
        jsonObjectElement.set("Time",1);
        JsonObjectElement jsonObjectElement1=new JsonObjectElement();
        jsonObjectElement1.set(DataDictionary.DATA_NAME,getResources().getString(R.string.TwoDay));
        jsonObjectElement1.set("Time",2);
        JsonObjectElement jsonObjectElement2=new JsonObjectElement();
        jsonObjectElement2.set(DataDictionary.DATA_NAME,getResources().getString(R.string.OneWeek));
        jsonObjectElement2.set("Time",7);
        timeList.add(jsonObjectElement);
        timeList.add(jsonObjectElement1);
        timeList.add(jsonObjectElement2);
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
                getTaskHistory();
                buttonAnim(false);
                break;
            }
        }
    }
    private void buttonAnim(final boolean showChannelFilterView){
        if(showChannelFilterView){
            Animation operatingAnim2 = AnimationUtils.loadAnimation(this, R.anim.expand);
            operatingAnim2.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
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
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // TODO Auto-generated method stub
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
    private void initMap(){
        map.put(Task.REPAIR_TASK,getResources().getString(R.string.repair));
        map.put(Task.MAINTAIN_TASK,getResources().getString(R.string.maintenance));
        map.put(Task.MOVE_CAR_TASK,getResources().getString(R.string.move_car));
        map.put(Task.OTHER_TASK,getResources().getString(R.string.other));

        taskStatusMap.put(getResources().getString(R.string.pending_orders),0);
        taskStatusMap.put(getResources().getString(R.string.start),1);
        taskStatusMap.put(getResources().getString(R.string.linked_order),2);
        taskStatusMap.put(getResources().getString(R.string.cancel),3);
    }
    private void CancelTask(ObjectElement task, final String reason){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        submitData.set(Task.TASK_ID,DataUtil.isDataElementNull(task.get(Task.TASK_ID)));
        submitData.set("QuitReason",reason);
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(context, "TaskRecieve/TaskQuit", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.FailCancelTask,context);
                dismissCustomDialog();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement returnData=new JsonObjectElement(t);
                    if(returnData.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.SuccessCancelTask,context);
                        pageIndex=1;
                        getTaskHistory();
                    }else {
                        ToastUtil.showToastShort(R.string.FailCancelTask,context);
                    }
                }
                dismissCustomDialog();
            }
        });
    }
}
