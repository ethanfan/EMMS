package com.emms.fragment;

import android.app.Activity;
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

import com.datastore_android_sdk.DatastoreException.DatastoreException;
import com.datastore_android_sdk.callback.StoreCallback;
import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.activity.TaskNumInteface;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.LocaleUtils;
import com.emms.util.TipsUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.jar.JarEntry;

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
    private String TaskSubClass;
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
        TaskSubClass=this.getArguments().getString(Task.TASK_SUBCLASS);
        taskAdapter = new TaskAdapter(datas) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_fr_pendingorder, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.tv_creater_order);
                    holder.tv_repair_time = (TextView) convertView.findViewById(R.id.target_group_tag);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.target_group);
                    if(TaskClass.equals(Task.MOVE_CAR_TASK)){
                        holder.tv_repair_time.setVisibility(View.VISIBLE);
                        holder.tv_start_time.setVisibility(View.VISIBLE);
                    }else {
                        holder.tv_repair_time.setVisibility(View.GONE);
                        holder.tv_start_time.setVisibility(View.GONE);
                    }
                    if(TaskSubClass!=null){
                        convertView.findViewById(R.id.textView6).setVisibility(View.GONE);
                        holder.tv_creater.setVisibility(View.GONE);
                    }else {
                        convertView.findViewById(R.id.textView6).setVisibility(View.VISIBLE);
                        holder.tv_creater.setVisibility(View.VISIBLE);
                    }
                    holder.tv_group = (TextView) convertView.findViewById(R.id.group_type);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.Task_description);
                    //holder.tv_task_state = (TextView) convertView.findViewById(R.id.Task_status);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_create_time_order);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.Task_Equipment);
                    holder.tv_device_num= (TextView) convertView.findViewById(R.id.Task_Equipment_Num);
                    holder.acceptTaskButton=(Button)convertView.findViewById(R.id.btn_order);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_creater.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT)));
                holder.tv_group.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.ORGANISE_NAME)));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_DESCRIPTION)));
               // holder.tv_task_state.setText(DataUtil.isDataElementNull(datas.get(position).get(Task.TASK_STATUS)));
                holder.tv_create_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(datas.get(position).get(Task.APPLICANT_TIME))));
               if(datas.get(position).get("IsExsitTaskEquipment").valueAsBoolean()){
                   holder.tv_device_name.setText(DataUtil.isDataElementNull(datas.get(position).get("EquipmentName")));
               }else {
                   holder.tv_device_name.setText(R.string.NoEquipment);
               }

                holder.tv_device_num.setText(DataUtil.isDataElementNull(datas.get(position).get("EquipmentAssetsIDList")));
                holder.tv_start_time.setText(DataUtil.isDataElementNull(datas.get(position).get("TargetTeam")));
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
                ToastUtil.showToastShort(R.string.noMoreData,mContext);
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
        if(TaskSubClass!=null&&!TaskSubClass.equals("")){
            params.put("taskSubClass",TaskSubClass);
        }
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(mContext, "TaskAPI/GetTaskList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    RecCount=jsonObjectElement.get("RecCount").valueAsInt();
                    if(taskNumInteface!=null){
                        taskNumInteface.ChangeTaskNumListener(1,RecCount);}
                    if (pageIndex == 1) {
                        datas.clear();
                    }
                    if(jsonObjectElement.get("PageData")!=null&& jsonObjectElement.get("PageData").isArray()
                            &&jsonObjectElement.get("PageData").asArrayElement().size()>0) {

                        pageIndex++;
                        for (int i = 0; i < jsonObjectElement.get("PageData").asArrayElement().size(); i++) {
                            datas.add(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
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
                ToastUtil.showToastShort(R.string.FailGetTaskListCauseByTimeOut,mContext);
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
            public void onSuccess(final String t) {
                super.onSuccess(t);
                if(t!=null) {

                            final JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                            if(jsonObjectElement.get("Success").valueAsBoolean()){
                                //成功，通知用户接单成功
                                ToastUtil.showToastShort(R.string.SuccessReceiveTask,mContext);
                            }else{
                                //失败，通知用户接单失败，单已经被接
                                TipsUtil.ShowTips(mContext,DataUtil.isDataElementNull(jsonObjectElement.get("Msg")));
                                if(jsonObjectElement.get("Tag").valueAsInt()==2){
                                    dismissCustomDialog();
                                    return;
                                }
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
    public static PendingOrdersFragment newInstance(String TaskClass,String TaskSubClass){
        PendingOrdersFragment fragment = new PendingOrdersFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Task.TASK_CLASS, TaskClass);
        if(TaskSubClass!=null&&!TaskSubClass.equals("")){
            bundle.putString(Task.TASK_SUBCLASS,TaskSubClass);
        }
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

}
