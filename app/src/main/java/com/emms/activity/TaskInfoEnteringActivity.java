package com.emms.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.emms.adapter.WorkloadAdapter;
import com.emms.adapter.commandAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.DataDictionary;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.ui.CloseDrawerListener;
import com.emms.ui.CustomDrawerLayout;
import com.emms.ui.DateTimePickDialog;
import com.emms.ui.DropEditText;
import com.emms.ui.HorizontalListView;
import com.emms.ui.MyListView;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ListViewUtility;
import com.emms.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/10/4.
 *
 */
public class TaskInfoEnteringActivity extends NfcActivity implements View.OnClickListener{
    private EditText taskStartTime,taskEndTime;
    private Context context=this;
    //private String initStartDateTime = "2013年9月3日14:44";// 初始化开始时间  
    //private String initEndDateTime = "2016年10月1日17:44";// 初始化结束时间  
    private String initEndDateTime;// 初始化结束时间  
    private DropEditText type;
    private EditText description,repair_status;
    private ResultListAdapter mResultAdapter;
    private ListView mResultListView;
    private TextView menuSearchTitle;
    private EditText searchBox;
    private ImageView clearBtn;
    private ViewGroup emptyView;
    private int  searchtag =0;
    private CustomDrawerLayout mDrawer_layout;
    private ArrayList<ObjectElement> searchDataLists = new ArrayList<>();
    private ArrayList<ObjectElement> typeList=new ArrayList<>();
    private ObjectElement TaskDetail;

    private MyListView TaskParticipantsListView;
    private WorkloadAdapter workloadAdapter;
    private ArrayList<ObjectElement> TaskParticipantsList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info_entering);

        SimpleDateFormat sf=new SimpleDateFormat(getResources().getString(R.string.DateFormat2));
        initEndDateTime=sf.format(new Date(System.currentTimeMillis()));
        TaskDetail=new JsonObjectElement(getIntent().getStringExtra("TaskDetail"));
        initData();
        initView();
        //initSearchView();
    }
    private void initView(){
        ((TextView)findViewById(R.id.task_description)).setText(DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_DESCRIPTION)));
        ((TextView)findViewById(R.id.Task_Equipment)).setText(DataUtil.isDataElementNull(TaskDetail.get("EquipmentAssetsIDList")));
        ((TextView)findViewById(R.id.task_create_time)).setText(DataUtil.utc2Local(DataUtil.isDataElementNull(TaskDetail.get(Task.APPLICANT_TIME))));
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.task_info_entering);

        findViewById(R.id.btn_right_action).setOnClickListener(this);
        taskStartTime=(EditText)findViewById(R.id.task_start_time);
        initDatePickerDialog(taskStartTime,(ImageView)findViewById(R.id.task_start_time_image));
        taskEndTime=(EditText)findViewById(R.id.task_end_time);
        initDatePickerDialog(taskEndTime,(ImageView)findViewById(R.id.task_end_time_image));

        findViewById(R.id.AddTaskPeople).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,InvitorActivity.class);
                intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
                intent.putExtra("Tag","FromTaskInfoEnteringActivity");
                intent.putExtra("TaskParticipantsList",TaskParticipantsList.toString());
                startActivityForResult(intent, Constants.REQUEST_CODE_END_TASK_TO_INVITOR_ACTIVITY);
            }
        });
        type=(DropEditText)findViewById(R.id.type);
        description=(EditText)findViewById(R.id.description);
        repair_status=(EditText)findViewById(R.id.repair_status);

        findViewById(R.id.comfirm).setOnClickListener(this);

        findViewById(R.id.layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        findViewById(R.id.layout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repair_status.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
            }
        });
        initTaskCommand();
        initTaskParticipants();
    }
    private void initDatePickerDialog(final EditText editText, ImageView imageView){
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialog dateTimePicKDialog=new DateTimePickDialog(TaskInfoEnteringActivity.this,initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(editText);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimePickDialog dateTimePicKDialog=new DateTimePickDialog(TaskInfoEnteringActivity.this,initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(editText);
            }
        });
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
            case R.id.comfirm:{
                submitDataToServer();
                break;
            }
        }
    }
    private void initSearchView() {
        //initData();
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int inPosition = position;
                String itemNam = mResultAdapter.getItemName();
                final String searchResult =DataUtil.isDataElementNull(mResultAdapter.getItem(position).get(itemNam));
                if (!searchResult.equals("")) {
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (searchtag) {
                                case 1:
                                    type.getmEditText().setText(searchResult);
                                    break;
                            }
                            mDrawer_layout.closeDrawer(Gravity.RIGHT);
                        }
                    });
                } else {
                    ToastUtil.showToastShort(R.string.error_occur,context);
                }
            }
        });

        initDropSearchView(null, type.getmEditText(), context.getResources().
                        getString(R.string.faultType), DataDictionary.DATA_NAME,
                1, R.string.getDataFail,type.getDropImage());
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
            if (DataUtil.isDataElementNull(searchDataLists.get(i).get(tagString)).toUpperCase().contains(keyword.toUpperCase())) {
                reDatas.add(searchDataLists.get(i));
            }
        }
        return reDatas;
    }

    private void initDropSearchView(
            final EditText condition,EditText subEditText,
            final String searchTitle,final String searchName,final int searTag ,final int tips,final ImageView imageView){
        subEditText.
                setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DropSearch(condition,
                                        searchTitle,searchName,searTag ,tips);
                            }
                        }
                );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DropSearch(condition,
                        searchTitle,searchName,searTag ,tips);
            }
        });
    }
    private void initData(){
        DataUtil.getDataFromDataBase(context, "EquipmentTroubleSort", new StoreCallback() {
            @Override
            public void success(DataElement element, String resource) {
                if(element!=null&&element.isArray()&&element.asArrayElement().size()>0){
                    for(int i=0;i<element.asArrayElement().size();i++){
                        typeList.add(element.asArrayElement().get(i).asObjectElement());
                    }
                }
            }

            @Override
            public void failure(DatastoreException ex, String resource) {
                  runOnUiThread(new Runnable() {
                      @Override
                   public void run() {
                ToastUtil.showToastShort(R.string.FailGetDataPleaseRestartApp,context);
                   }
                   });
            }
        });
    }
    private void DropSearch(final EditText condition,
                            final String searchTitle,final String searchName,final int searTag ,final int tips){
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                searchDataLists.clear();
                switch (searTag){
                    case 1:
                        searchDataLists.addAll(typeList);
                        break;
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
    private ArrayList<Integer> response_speed_list=new ArrayList<>();
    private ArrayList<Integer> service_attitude_list=new ArrayList<>();
    private ArrayList<Integer> repair_speed_list=new ArrayList<>();
    private commandAdapter response_speed_adapter,service_attitude_adapter,repair_speed_adapter;
    private HorizontalListView response_speed,service_attitude,repair_speed;
    private HashMap<String,Integer> command=new HashMap<>();
    private void initTaskCommand(){
        response_speed=(HorizontalListView)findViewById(R.id.response_speed);
        service_attitude=(HorizontalListView)findViewById(R.id.service_attitude);
        repair_speed=(HorizontalListView)findViewById(R.id.repair_speed);
        for(int i=0;i<5;i++){
            response_speed_list.add(0);
            service_attitude_list.add(0);
            repair_speed_list.add(0);
        }
        command.put("response_speed",0);
        command.put("service_attitude",0);
        command.put("repair_speed",0);
        response_speed_adapter=new commandAdapter(this,response_speed_list);
        response_speed.setAdapter(response_speed_adapter);

        service_attitude_adapter=new commandAdapter(this,service_attitude_list);
        service_attitude.setAdapter(service_attitude_adapter);

        repair_speed_adapter=new commandAdapter(this,repair_speed_list);
        repair_speed.setAdapter(repair_speed_adapter);
        initListViewOnItemClickEvent();
    }
    public void setCommandData(int num,String key,final ArrayList<Integer> numList,final commandAdapter cAdapter){
        command.put(key,num);
        for(int i=0;i<5;i++){
            if(i<num){
                numList.set(i,1);
            }else {
                numList.set(i,0);
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cAdapter.setDatas(numList);
                cAdapter.notifyDataSetChanged();
            }
        });
    }
    private void initListViewOnItemClickEvent(){
        response_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"response_speed",response_speed_list,response_speed_adapter);
            }
        });
        service_attitude.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"service_attitude",service_attitude_list,service_attitude_adapter);
            }
        });
        repair_speed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setCommandData(position+1,"repair_speed",repair_speed_list,repair_speed_adapter);
            }
        });
    }
    private void getWorkLoadFromServer(){
        showCustomDialog(R.string.loadingData);
        HttpParams httpParams=new HttpParams();
        httpParams.put("task_id", DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
        HttpUtils.get(this, "TaskWorkload", httpParams, new HttpCallback() {
            @Override
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if(t!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TaskParticipantsList.clear();
                            JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("TaskOperator")!=null&&jsonObjectElement.get("TaskOperator").asArrayElement().size()>0) {
                                for (int i = 0; i < jsonObjectElement.get("TaskOperator").asArrayElement().size(); i++) {
                                    TaskParticipantsList.add(jsonObjectElement.get("TaskOperator").asArrayElement().get(i).asObjectElement());
                                }
                                if(TaskParticipantsList.size()==1){
                                    TaskParticipantsList.get(0).set("TaskWorkLoad",100);
                                }
                                workloadAdapter.setDatas(TaskParticipantsList);
                                ListViewUtility.setListViewHeightBasedOnChildren(TaskParticipantsListView);
                            }
                        }
                    });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.REQUEST_CODE_END_TASK_TO_INVITOR_ACTIVITY:{
                if(resultCode==3){
                    //getWorkLoadFromServer();
                    JsonArrayElement jsonArrayElement=new JsonArrayElement(data.getStringExtra("Data"));
                    for(int i=0;i<jsonArrayElement.size();i++){
                        jsonArrayElement.get(i).asObjectElement().set("isMain",false);
                        TaskParticipantsList.add(jsonArrayElement.get(i).asObjectElement());
                    }
                    for(int i=0;i<TaskParticipantsList.size();i++){
                        TaskParticipantsList.get(i).set("isMain",false);
                    }
                    if(TaskParticipantsList.size()==1){
                        TaskParticipantsList.get(0).set("TaskWorkLoad",100);
                        TaskParticipantsList.get(0).set("isMain",true);
                    }
                    workloadAdapter.setDatas(TaskParticipantsList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListViewUtility.setListViewHeightBasedOnChildren(TaskParticipantsListView);
                        }
                    });
                   // workloadAdapter.notifyDataSetChanged();
                    Log.e("","");
                }
                break;
            }
        }
    }
    private void initTaskParticipants(){
        TaskParticipantsListView=(MyListView)findViewById(R.id.TaskParticipants);
        workloadAdapter=new WorkloadAdapter(TaskParticipantsList) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final WorkloadAdapter.ViewHolder holder=new WorkloadAdapter.ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.item_activity_task_info_entering, parent, false);
                holder.name=(TextView)convertView.findViewById(R.id.name);
                holder.workload=(EditText)convertView.findViewById(R.id.workload);
                holder.workload.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                holder.imageView=(ImageView)convertView.findViewById(R.id.isMain);
                holder.workload.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        TaskParticipantsList.get(position).set("TaskWorkLoad",s.toString());
                    }
                });
                if(TaskParticipantsList.get(position).get("isMain").valueAsBoolean()){
                    holder.imageView.setImageResource(R.mipmap.select_pressed);
                }else {
                    holder.imageView.setImageResource(R.mipmap.select_normal);
                }
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int i=0;i<TaskParticipantsList.size();i++){
                            if(i==position){
                                TaskParticipantsList.get(i).set("isMain",true);
                            }else {
                                TaskParticipantsList.get(i).set("isMain",false);
                            }
                        }
                        notifyDataSetChanged();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ListViewUtility.setListViewHeightBasedOnChildren(TaskParticipantsListView);
                            }
                        });
                    }
                });
                holder.name.setText(DataUtil.isDataElementNull(TaskParticipantsList.get(position).get("Name")));
                holder.workload.setText(DataUtil.isDataElementNull(TaskParticipantsList.get(position).get("TaskWorkLoad")));
                return convertView;
            }
        };
        TaskParticipantsListView.setAdapter(workloadAdapter);
        //getWorkLoadFromServer();
    }
    private void submitDataToServer(){
        HttpParams params=new HttpParams();
        JsonObjectElement SubmitData=new JsonObjectElement();
        SubmitData.set(Task.TASK_ID,DataUtil.isDataElementNull(TaskDetail.get(Task.TASK_ID)));
       //开始时间，完成时间
        if(taskStartTime.getText().toString().equals("")){
            ToastUtil.showToastShort(R.string.pleaseSelectStartTime,context);
            return;
        }
        if(taskEndTime.getText().toString().equals("")){
            ToastUtil.showToastShort(R.string.pleaseSelectEndTime,context);
            return;
        }
        SubmitData.set(Task.START_TIME,DataUtil.Local2utc(taskStartTime.getText().toString()));
        SubmitData.set(Task.FINISH_TIME,DataUtil.Local2utc(taskEndTime.getText().toString()));
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.DateFormat));
            Date date1 = sdf.parse(taskStartTime.getText().toString());
            Date date2 = sdf.parse(taskEndTime.getText().toString());

            Long a=date1.getTime();
            Long b=date2.getTime();
            if(!DataUtil.isDataElementNull(TaskDetail.get(Task.APPLICANT_TIME)).equals("")){
            SimpleDateFormat sdf2=new SimpleDateFormat(getResources().getString(R.string.UpdateTime));
                //a为开始时间，c为创建时间，b为结束时间
            Date date3=sdf2.parse(DataUtil.utc2Local(DataUtil.isDataElementNull(TaskDetail.get(Task.APPLICANT_TIME))));
                Long c=date3.getTime();
                if(a<c){
                    ToastUtil.showToastShort(getResources().getString(R.string.StartTimeCanNotlessThanApplicantTime)+DataUtil.utc2Local(DataUtil.isDataElementNull(TaskDetail.get(Task.APPLICANT_TIME))),context);
                    return;
                }
            }
            if(b<a){
                ToastUtil.showToastShort(R.string.EndTimeCanNotlessThanStartTime,context);
                return;
            }
            Long d=System.currentTimeMillis();
            if(a>d){
                ToastUtil.showToastShort(R.string.StartTimeCanNotlargeThanNowTime,context);
                return;
            }
            if(b>d){
                ToastUtil.showToastShort(R.string.EndTimeCanNotlargeThanNowTime,context);
                return;
            }
        }catch (Exception e){
            Log.e("excep",e.toString());
            return;
        }
        //工作量
        if(TaskParticipantsList.size()<=0){
            ToastUtil.showToastShort(R.string.PleaseAddTaskPeople,this);
            return;
        }
        int sum=0;
        boolean hasMain=false;
        for(int i=0;i<TaskParticipantsList.size();i++){
            if(DataUtil.isDataElementNull(TaskParticipantsList.get(i).get("TaskWorkLoad")).equals("")){
                ToastUtil.showToastShort(R.string.pleaseInputWorkload,this);
                return;
            }
            if(    !DataUtil.isNum(DataUtil.isDataElementNull(TaskParticipantsList.get(i).get("TaskWorkLoad")))
                    || !DataUtil.isInt(DataUtil.isDataElementNull(TaskParticipantsList.get(i).get("TaskWorkLoad")))
                    ||  (Integer.parseInt(DataUtil.isDataElementNull(TaskParticipantsList.get(i).get("TaskWorkLoad")))<0) ){
                ToastUtil.showToastShort(R.string.pleaseInputInteger,this);
                return;
            }
            if(TaskParticipantsList.get(i).get("isMain").valueAsBoolean()){
                hasMain=true;
            }
            sum+=Integer.valueOf(DataUtil.isDataElementNull(TaskParticipantsList.get(i).get("TaskWorkLoad")));
        }
        if(sum!=100){
            ToastUtil.showToastShort(R.string.judgeWorkloadSum,this);
            return;
        }
        if(!hasMain){
            ToastUtil.showToastShort(R.string.pleaseSelectMainPerson,this);
            return;
        }
        showCustomDialog(R.string.submitData);
        ArrayList<ObjectElement> submitWorkloadData=new ArrayList<ObjectElement>();
        for (int i=0;i<TaskParticipantsList.size();i++){
            ObjectElement obj=TaskParticipantsList.get(i);
            JsonObjectElement jsonObjectElement=new JsonObjectElement();
            jsonObjectElement.set(Operator.OPERATOR_ID, DataUtil.isDataElementNull(obj.get(Operator.OPERATOR_ID)));
            jsonObjectElement.set("Coefficient",Float.valueOf(DataUtil.isDataElementNull(obj.get("TaskWorkLoad")))/100.0f);
            jsonObjectElement.set("IsMain",obj.get("isMain").valueAsBoolean());
            submitWorkloadData.add(jsonObjectElement);
        }
        JsonArrayElement WorkloadArray=new JsonArrayElement(submitWorkloadData.toString());
        SubmitData.set("TaskOperatorList",WorkloadArray);
       //任务评价
        JsonObjectElement EvaluationData=new JsonObjectElement();
        EvaluationData.set("RespondSpeed",command.get("response_speed"));
        EvaluationData.set("ServiceAttitude",command.get("service_attitude"));
        EvaluationData.set("MaintainSpeed",command.get("repair_speed"));
        SubmitData.set("Evaluation",EvaluationData);
        params.putJsonParams(SubmitData.toJson());
        HttpUtils.post(this, "TaskAPI/TaskStatement", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement data=new JsonObjectElement(t);
                    if(data.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.submitSuccess,context);
                        setResult(1);
                        finish();
                    }else {
                        ToastUtil.showToastShort(R.string.submit_Fail,context);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.submitFail,context);
                dismissCustomDialog();
            }
        });
    }
}
