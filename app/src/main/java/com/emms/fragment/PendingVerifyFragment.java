package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.TaskNumInteface;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class PendingVerifyFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    //private String TaskClass="";
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private ArrayList<ObjectElement> submitData=new ArrayList<>();
    private static HashMap<String,String> taskStatusMap=new HashMap<>();
    //   private HashMap<String,String> taskClass_map=new HashMap<>();
//    private HashMap<Integer,String> mapVerifyWorkTime=new HashMap<>();
//    private HashMap<Integer,String> mapVerifyStates=new HashMap<>();
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//        taskClass_map.put(Task.REPAIR_TASK,getResources().getString(R.string.repair_task));
//        taskClass_map.put(Task.MAINTAIN_TASK,getResources().getString(R.string.maintain_task));
//        taskClass_map.put(Task.MOVE_CAR_TASK,getResources().getString(R.string.move_car_task));
//        taskClass_map.put(Task.OTHER_TASK,getResources().getString(R.string.other_task));
//        taskStatusMap.put("0",getResources().getString(R.string.waitingDeal));
//        taskStatusMap.put("1",getResources().getString(R.string.start));
//        taskStatusMap.put("2",getResources().getString(R.string.NotVerify));
//        taskStatusMap.put("3",getResources().getString(R.string.cancel));
//        taskStatusMap.put("4",getResources().getString(R.string.isVerity));
//        taskStatusMap.put("5",getResources().getString(R.string.MonthlyStatement));
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView)v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshBase.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;
                        getVerifyListFromServer();
                        listView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getVerifyListFromServer();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_activity_workload_verify, parent, false);
                    holder = new TaskViewHolder();
                    //显示6个内容，组别，报修人，状态，保修时间,开始时间，任务描述
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.standard_workload);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_end_time=(TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.editText=(EditText)convertView.findViewById(R.id.verify_workTime) ;
                    holder.editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                    holder.editText2=(EditText)convertView.findViewById(R.id.verify_workTime_remark) ;
                    holder.editText.setText(DataUtil.isDataElementNull(datas.get(position).get("Workload")));
                    holder.editText2.setText(DataUtil.isDataElementNull(datas.get(position).get("UpdateRemark")));
                    holder.textChanged1=new TaskAdapter.EtTextChanged(position,"Workload");
                    holder.textChanged2=new TaskAdapter.EtTextChanged(position,"UpdateRemark");
                    holder.editText.addTextChangedListener(holder.textChanged1);
                    holder.editText2.addTextChangedListener(holder.textChanged2);
                    holder.image=(ImageView)convertView.findViewById(R.id.image) ;
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.status);
                    holder.onClickListener=new ExOnClickListener(position,"tag");
                    holder.image.setOnClickListener(holder.onClickListener);
                    holder.tv_target_group=(TextView)convertView.findViewById(R.id.target_group);
                    holder.tv_device_num=(TextView)convertView.findViewById(R.id.target_group_tag);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                if(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_CLASS)).equals(Task.MOVE_CAR_TASK)){
                    holder.tv_target_group.setVisibility(View.VISIBLE);
                    holder.tv_device_num.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_target_group.setVisibility(View.GONE);
                    holder.tv_device_num.setVisibility(View.GONE);
                }
                holder.tv_target_group.setText(DataUtil.isDataElementNull(datas.get(position).get("TargetTeam")));
                //待修改
                holder.textChanged1.setPosition(position);
                holder.textChanged2.setPosition(position);
                holder.onClickListener.setPosition(position);
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkTime")));
                holder.editText.setText(DataUtil.isDataElementNull(datas.get(position).get("Workload")));
                holder.editText2.setText(DataUtil.isDataElementNull(datas.get(position).get("UpdateRemark")));
                if(taskStatusMap.get(DataUtil.isDataElementNull(datas.get(position).get("Status")))!=null) {
                    holder.tv_create_time.setText(taskStatusMap.get(DataUtil.isDataElementNull(datas.get(position).get("Status"))));
                }else {
                    holder.tv_create_time.setText(DataUtil.isDataElementNull(datas.get(position).get("Status")));
                }
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get("FinishTime"))));
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.START_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                if(datas.get(position).get("tag").valueAsBoolean()){
                    holder.image.setImageResource(R.mipmap.select_pressed);
                }else {
                    holder.image.setImageResource(R.mipmap.select_normal);
                }
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
//                        datas.get(position).set("Workload",s.toString());
////                       mapVerifyWorkTime.put(datas.get(position).get(Task.TASK_ID).valueAsInt(),s.toString());
//                    }
//                });
//                holder.editText2.addTextChangedListener(new TextWatcher() {
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
//                        datas.get(position).set("UpdateRemark",s.toString());
////                        mapVerifyStates.put(datas.get(position).get(Task.TASK_ID).valueAsInt(),s.toString());
//                    }
//                });
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                        intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)));
                        intent.putExtra("TaskDetail",datas.get(position).toString());
                        intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_CLASS)));
                        intent.putExtra("TaskStatus",2);
                        startActivity(intent);
                    }
                });

                return convertView;
            }
        };
        taskAdapter.setExArray(submitData);
        listView.setAdapter(taskAdapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
//                intent.putExtra(Task.TASK_ID,datas.get(position-1).get(Task.TASK_ID).valueAsString());
//                intent.putExtra("TaskDetail",datas.get(position-1).toString());
//                intent.putExtra(Task.TASK_CLASS,TaskClass);
//                intent.putExtra("TaskStatus",1);
//                startActivity(intent);
//            }
//        });
        getVerifyListFromServer();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getVerifyListFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastShort(R.string.noMoreData,mContext);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        // params.put("task_class",Task.REPAIR_TASK);
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        params.put("Verity",0);//0为未核验
        HttpUtils.get(mContext, "TaskWorkloadVerity", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                try {
                    if(t!=null) {
                        JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                        // int RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                        //  if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()==0){
                        //提示没有处理中的任务
                        //  }
                        if (pageIndex == 1) {
                            datas.clear();
                            submitData.clear();
                        }
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        if(jsonObjectElement.get("PageData")!=null
                                &&jsonObjectElement.get("PageData").isArray()
                                &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
                            pageIndex++;
                            for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                                ObjectElement json=jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement();
                                json.set("Workload",
                                        DataUtil.isDataElementNull(json.get("WorkTime")));
                                json.set("tag",false);
//                            mapVerifyWorkTime.put(json.get(Task.TASK_ID).valueAsInt()
//                                    ,DataUtil.isDataElementNull(json.get("Workload")));
//                            mapVerifyStates.put(json.get(Task.TASK_ID).valueAsInt()
//                                    ,DataUtil.isDataElementNull(json.get("UpdateRemark")));
                                datas.add(json);
                            }
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                taskAdapter.setDatas(datas);
                                taskAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    dismissCustomDialog();
                }catch (Throwable throwable){
                    CrashReport.postCatchedException(throwable);
                }
                finally {
                    dismissCustomDialog();
                }
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.FailGetList,mContext);
                dismissCustomDialog();
            }
        });
    }
    public static PendingVerifyFragment newInstance(HashMap<String,String> TaskClass,HashMap<String,String> TaskStatus){
        PendingVerifyFragment fragment = new PendingVerifyFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(Task.TASK_CLASS, TaskClass);
        taskStatusMap=TaskStatus;
        fragment.setArguments(bundle);
        return fragment;
    }
    public void doRefresh(){
        pageIndex=1;
        getVerifyListFromServer();
    }

    public void setTaskNumInteface(TaskNumInteface taskNumInteface) {
        this.taskNumInteface = taskNumInteface;
    }

    private TaskNumInteface taskNumInteface;
    private void submitWorkload(final ObjectElement data, String workload,String UpdateRemark){
        if(!DataUtil.isNum(workload)
                ||workload.equals("")){
            ToastUtil.showToastShort(R.string.pleaseInputNum,mContext);
            return;
        }
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(Task.TASK_ID,DataUtil.isDataElementNull(data.get(Task.TASK_ID)));
        jsonObjectElement.set("Workload",workload);
        jsonObjectElement.set("UpdateRemark",UpdateRemark);
        // jsonObjectElement.set("UpdateRemark");
        ArrayList<ObjectElement> list=new ArrayList<>();
        list.add(jsonObjectElement);
        final JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        HttpUtils.post(mContext, "TaskWorkloadVerity", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.SuccessVerify,mContext);
                        datas.remove(data);
                        taskAdapter.notifyDataSetChanged();
                    }else {
                        ToastUtil.showToastShort(R.string.FailVerify,mContext);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.FailVerifyCauseByTimeOut,mContext);
                dismissCustomDialog();
            }
        });
    }
    public void submitVerifyData(){
        if(submitData.size()<=0){
            ToastUtil.showToastShort(R.string.pleaseSelectSubmitData,mContext);
            return;
        }
        for(int i=0;i<submitData.size();i++) {
            if ( !DataUtil.isNum(DataUtil.isDataElementNull(submitData.get(i).get("Workload")).trim())
                    || DataUtil.isDataElementNull(submitData.get(i).get("Workload")).equals("")
                    || !DataUtil.isFloat(DataUtil.isDataElementNull(submitData.get(i).get("Workload")).trim())) {
                ToastUtil.showToastShort(R.string.pleaseInputNum, mContext);
                return;
            }
        }
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        ArrayList<ObjectElement> list=new ArrayList<>();
        for(int i=0;i<submitData.size();i++){
            JsonObjectElement jsonObjectElement=new JsonObjectElement();
            jsonObjectElement.set(Task.TASK_ID,DataUtil.isDataElementNull(submitData.get(i).get(Task.TASK_ID)));
            jsonObjectElement.set("Workload",DataUtil.isDataElementNull(submitData.get(i).get("Workload")));
            jsonObjectElement.set("UpdateRemark",DataUtil.isDataElementNull(submitData.get(i).get("UpdateRemark")));
            list.add(jsonObjectElement);
        }
        final JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        HttpUtils.post(mContext, "TaskWorkloadVerity", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastShort(R.string.SuccessVerify,mContext);
                        //datas.removeAll(submitData);
                        pageIndex=1;
                        getVerifyListFromServer();
                        submitData.clear();
                        taskAdapter.notifyDataSetChanged();
                        taskNumInteface.refreshProcessingFragment();
                    }else {
                        ToastUtil.showToastShort(R.string.FailVerify,mContext);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.FailVerifyCauseByTimeOut,mContext);
                dismissCustomDialog();
            }
        });
    }
}
