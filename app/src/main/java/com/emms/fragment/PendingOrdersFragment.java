package com.emms.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.TaskNumInteface;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Task;
import com.emms.ui.CancelTaskDialog;
import com.emms.ui.TaskCancelListener;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/21.
 *
 */
public class PendingOrdersFragment extends BaseFragment{

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    private String TaskClass;
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private int removeNum=0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext =getActivity();
        View v = inflater.inflate(R.layout.fr_processing, null);
        listView = (PullToRefreshListView) v.findViewById(R.id.processing_list);
        listView.setMode(PullToRefreshListView.Mode.BOTH);
        listView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;
                        removeNum=0;
                        getPendingOrderTaskDataFromServer();
                        listView.onRefreshComplete();
                     //   Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getPendingOrderTaskDataFromServer();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TaskClass=this.getArguments().getString(Task.TASK_CLASS);
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_pendingorder, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.tv_creater_order);
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group_type);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.Task_description);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.Task_status);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time_order);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.Task_Equipment);
                    holder.acceptTaskButton=(Button)convertView.findViewById(R.id.btn_order);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_creater.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
                holder.tv_task_state.setText(DataUtil.getDate(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_STATUS))));
                holder.tv_create_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_device_name.setText(DataUtil.getDate(DataUtil.isDataElementNull(datas.get(position).get("EquipmentName"))));
                holder.acceptTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // acceptTask(position);
                        taskReceive(position);
                    }
                });
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        getPendingOrderTaskDataFromServer();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                intent.putExtra(Task.TASK_ID,datas.get(position-1).get(Task.TASK_ID).valueAsString());
                intent.putExtra("TaskDetail",datas.get(position-1).asObjectElement().toString());
                intent.putExtra(Task.TASK_CLASS,TaskClass);
                startActivity(intent);
            }
        });
        listView.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {
                //if(is班组长)
//                CancelTaskDialog cancleTaskDialog=new CancelTaskDialog(mContext);
//                cancleTaskDialog.setTaskCancelListener(new TaskCancelListener() {
//                    @Override
//                    public void submitCancel(String CancelReason) {
//                        CancelTask(datas.get(position-1),CancelReason);
//                    }
//                });
//                cancleTaskDialog.show();
//
//

                return true;
            }
        });

    }
    private void getPendingOrderTaskDataFromServer(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastLong(R.string.noMoreData,mContext);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
     //   String s=SharedPreferenceManager.getLoginData(mContext);
      //  JsonObjectElement jsonObjectElement=new JsonObjectElement(s);
      //  String operator_id=jsonObjectElement.get("Operator_ID").valueAsString();
      //  params.put("operator_id",operator_id);
        params.put("status",0);
        params.put("taskClass",TaskClass);
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(mContext, "TaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    if(taskNumInteface!=null){
                        taskNumInteface.ChangeTaskNumListener(1,RecCount);}
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").asArrayElement().size()>0)
                    if(pageIndex==1){
                        datas.clear();
                    }
                    pageIndex++;
                    for(int i=0;i<jsonObjectElement.get("PageData").asArrayElement().size();i++){
                        datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            taskAdapter.setDatas(datas);
                            //taskAdapter.notifyDataSetChanged();
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
    public void acceptTask(final int position){
        //HttpParams params=new HttpParams();
        //params.put();
       // taskAdapter.getDatas()
       // datas.get(position).
        JsonObjectElement task=new JsonObjectElement();
        task.set(Task.TASK_ID,DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)));
     //   task.set("Status", 1);
    /*    JsonObjectElement operator=new JsonObjectElement();

        operator.set("Operator_ID", new JsonObjectElement(SharedPreferenceManager.getLoginData(mContext))
                .get("Operator_ID").valueAsString());

        JsonArray jsonArray=new JsonArray();
        JsonObject JsonObject=new JsonObject();
        JsonObject.addProperty("Operator_ID", new JsonObjectElement(SharedPreferenceManager.getLoginData(mContext))
                .get("Operator_ID").valueAsString());
        jsonArray.add(JsonObject);*/

        JsonObjectElement SubData=new JsonObjectElement();
        SubData.set("Task",task);
       // SubData.set("TaskOperator",jsonArray.toString());
     //   SubData.set("isChangeTaskItem","1");
        HttpParams params=new HttpParams();
        params.putJsonParams(SubData.toJson());
        HttpUtils.post(mContext, "TaskCollection", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                         if(jsonObjectElement.get("Success").valueAsBoolean()){
                             //成功，通知用户接单成功
                             Toast toast=Toast.makeText(mContext,"接单成功",Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.CENTER,0,0);
                             toast.show();
                         }else{
                             //失败，通知用户接单失败，单已经被接
                             Toast toast=Toast.makeText(mContext,"该单已被接",Toast.LENGTH_LONG);
                             toast.setGravity(Gravity.CENTER,0,0);
                             toast.show();
                         }
                         datas.remove(position);

                         taskAdapter.setDatas(datas);
                         taskAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });

    }
    public void taskReceive(final int position){
        showCustomDialog(R.string.submitData);
        HttpParams params=new HttpParams();
      //  params.put("task_id",Integer.valueOf(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID))));
        HttpUtils.post(mContext,"TaskRecieve?task_id="+DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_ID)), params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                        if(jsonObjectElement.get("Success").valueAsBoolean()){
                            //成功，通知用户接单成功
                            Toast toast=Toast.makeText(mContext,"接单成功",Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }else{
                            //失败，通知用户接单失败，单已经被接
                            Toast toast=Toast.makeText(mContext,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")),Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER,0,0);
                            toast.show();
                        }
                        datas.remove(position);
                        removeNum++;
                        if(taskNumInteface!=null){
                            taskNumInteface.ChangeTaskNumListener(1,RecCount-removeNum);
                            taskNumInteface.refreshProcessingFragment();
                        }
                        taskAdapter.setDatas(datas);
                        taskAdapter.notifyDataSetChanged();

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
    public static PendingOrdersFragment newInstance(String TaskClass){
        PendingOrdersFragment fragment = new PendingOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        fragment.setArguments(bundle);
        return fragment;
    }
    public void setTaskNumInteface(TaskNumInteface taskNumInteface) {
        this.taskNumInteface = taskNumInteface;
    }

    private TaskNumInteface taskNumInteface;
    public void doRefresh(){
        removeNum=0;
        pageIndex=1;
        getPendingOrderTaskDataFromServer();
    }
    private void CancelTask(ObjectElement task, final String reason){
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        JsonObjectElement submitData=new JsonObjectElement();
        submitData.set(Task.TASK_ID,DataUtil.isDataElementNull(task.get(Task.TASK_ID)));
        submitData.set("QuitReason",reason);
        params.putJsonParams(submitData.toJson());
        HttpUtils.post(mContext, "TaskRecieve/TaskQuit", params, new HttpCallback() {
            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastLong(R.string.FailCancelTask,mContext);
                dismissCustomDialog();
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement returnData=new JsonObjectElement(t);
                    if(returnData.get(Data.SUCCESS).valueAsBoolean()){
                        ToastUtil.showToastLong(R.string.SuccessCancelTask,mContext);
                        removeNum=0;
                        pageIndex=1;
                        getPendingOrderTaskDataFromServer();
                    }else {
                        ToastUtil.showToastLong(R.string.FailCancelTask,mContext);
                    }
                }
                dismissCustomDialog();
            }
        });
    }
}
