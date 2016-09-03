package com.emms.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/20.
 */
public class LinkedVerifyFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas=new ArrayList<ObjectElement>();
    private ArrayList<ObjectElement> submitData=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    private String TaskClass="";
    private int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        TaskClass=this.getArguments().getString(Task.TASK_CLASS);
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
                        getCommandListFromServer();
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
                        getCommandListFromServer();
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
                    holder.editText=(EditText)convertView.findViewById(R.id.verify_workTime) ;
                    holder.editText2=(EditText)convertView.findViewById(R.id.verify_workTime_remark) ;
                    holder.editText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                    holder.image=(ImageView)convertView.findViewById(R.id.image) ;
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //待修改
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get("WorkTime")));
                holder.editText.setText(DataUtil.isDataElementNull(datas.get(position).get("Workload")));
                holder.editText2.setText(DataUtil.isDataElementNull(datas.get(position).get("UpdateRemark")));
                holder.tv_repair_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(datas.get(position).get(Task.START_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                if(datas.get(position).get("tag")!=null) {
                    if (datas.get(position).get("tag").valueAsBoolean()) {
                        holder.image.setImageResource(R.mipmap.select_pressed);
                    } else {
                        holder.image.setImageResource(R.mipmap.select_normal);
                    }
                }
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                        intent.putExtra(Task.TASK_ID,datas.get(position).get(Task.TASK_ID).valueAsString());
                        intent.putExtra("TaskDetail",datas.get(position).toString());
                        intent.putExtra(Task.TASK_CLASS,"T04");
                        intent.putExtra("TaskStatus",2);
                        startActivity(intent);
                    }
                });
                holder.editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        datas.get(position).set("Workload",s.toString());
                    }
                });
                holder.editText2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        datas.get(position).set("UpdateRemark",s.toString());
                    }
                });
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(datas.get(position).get("tag").valueAsBoolean()){
                       // holder.image.setImageResource(R.mipmap.select_pressed);
                        datas.get(position).set("tag",false);
                    }else {
                        //holder.image.setImageResource(R.mipmap.select_normal);
                        datas.get(position).set("tag",true);
                    }
                        notifyDataSetChanged();
                        //submitWorkload(datas.get(position),holder.editText.getText().toString());
                        if(submitData.contains(datas.get(position))){
                            submitData.remove(datas.get(position));
                        }else {
                            submitData.add(datas.get(position));
                        }
                    }
                });
                return convertView;
            }
        };
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
        getCommandListFromServer();
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getCommandListFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastLong(R.string.noMoreData,mContext);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
       // params.put("task_class",Task.REPAIR_TASK);
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        params.put("Verity",1);//1为已核验
        HttpUtils.get(mContext, "TaskWorkloadVerity", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    // int RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    //  if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()==0){
                    //提示没有处理中的任务
                    //  }
                    if(jsonObjectElement!=null&&jsonObjectElement.get("PageData")!=null
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        if (pageIndex == 1) {
                            datas.clear();
                        }
                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().set("tag",false);
                            datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                taskAdapter.setDatas(datas);
                                taskAdapter.notifyDataSetChanged();
                            }
                        });
                        //      setData(datas);
                    }

                }
                dismissCustomDialog();
            }
            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);
               ToastUtil.showToastLong(R.string.FailGetList,mContext);
                dismissCustomDialog();
            }
        });
    }
    public static LinkedVerifyFragment newInstance(){
        LinkedVerifyFragment fragment = new LinkedVerifyFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(Task.TASK_CLASS, TaskClass);
        fragment.setArguments(bundle);
        return fragment;
    }
    public void doRefresh(){
        pageIndex=1;
        getCommandListFromServer();
    }

    public void setTaskNumInteface(TaskNumInteface taskNumInteface) {
        this.taskNumInteface = taskNumInteface;
    }

    private TaskNumInteface taskNumInteface;
    private void submitWorkload(ObjectElement data,String workload){
        if(!DataUtil.isNum(workload)||workload.equals("")){
            ToastUtil.showToastLong(R.string.pleaseInputNum,mContext);
            return;
        }
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(Task.TASK_ID,DataUtil.isDataElementNull(data.get(Task.TASK_ID)));
        jsonObjectElement.set("Workload",workload);
        ArrayList<ObjectElement> list=new ArrayList<>();
        list.add(jsonObjectElement);
        JsonArrayElement jsonArrayElement=new JsonArrayElement(list.toString());
        params.putJsonParams(jsonArrayElement.toJson());
        HttpUtils.post(mContext, "TaskWorkloadVerity", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastLong(R.string.SuccessVerify,mContext);
                    }else {
                        ToastUtil.showToastLong(R.string.FailVerify,mContext);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.FailVerifyCauseByTimeOut,mContext);
                dismissCustomDialog();
            }
        });
    }
    public void submitVerifyData(){
        for(int i=0;i<submitData.size();i++) {
            if (!DataUtil.isNum(DataUtil.isDataElementNull(submitData.get(i).get("Workload")).trim()) ||
                    DataUtil.isDataElementNull(submitData.get(i).get("Workload")).equals("")) {
                ToastUtil.showToastLong(R.string.pleaseInputNum, mContext);
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
                        ToastUtil.showToastLong(R.string.SuccessVerify,mContext);
                        datas.remove(submitData);
                        taskAdapter.notifyDataSetChanged();
                    }else {
                        ToastUtil.showToastLong(R.string.FailVerify,mContext);
                    }
                }
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.FailVerifyCauseByTimeOut,mContext);
                dismissCustomDialog();
            }
        });
    }
}
