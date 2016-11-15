package com.emms.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.TaskAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/20.
 *
 */
public class PendingCommandFragment extends BaseFragment {

    private PullToRefreshListView listView;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> data=new ArrayList<>();
    private Context mContext;
    private Handler handler=new Handler();
    //private String TaskClass="";
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private static HashMap<String,String> map=new HashMap<>();
    private static HashMap<String,String> taskStatusMap=new HashMap<>();
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        //initMap();
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
                        getTaskHistory();
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
                        getTaskHistory();
                        listView.onRefreshComplete();
                        //Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        taskAdapter = new TaskAdapter(data) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.item_history, parent, false);
                    holder = new TaskViewHolder();
    //                holder.tv_group = (TextView) convertView.findViewById(R.id.group);
                    holder.warranty_person=(TextView)convertView.findViewById(R.id.Warranty_person);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.tv_Warranty_time_process);
                    holder.tv_start_time = (TextView) convertView.findViewById(R.id.tv_start_time_process);
                    holder.tv_end_time= (TextView) convertView.findViewById(R.id.tv_end_time_process);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.tv_task_describe);
                    holder.tv_device_name=(TextView)convertView.findViewById(R.id.tv_task_class);
                    holder.tv_creater=(TextView)convertView.findViewById(R.id.command);
                    holder.tv_create_time=(TextView)convertView.findViewById(R.id.Task_Equipment);
                    holder.tv_device_num=(TextView)convertView.findViewById(R.id.Task_Equipment_Num);
                    holder.tv_target_group=(TextView)convertView.findViewById(R.id.target_group);
                    holder.tv_group=(TextView)convertView.findViewById(R.id.target_group_tag);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                //  holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get("Organise_ID")));
                if(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS)).equals(Task.MOVE_CAR_TASK)){
                    holder.tv_target_group.setVisibility(View.VISIBLE);
                    holder.tv_group.setVisibility(View.VISIBLE);
                }else {
                    holder.tv_target_group.setVisibility(View.GONE);
                    holder.tv_group.setVisibility(View.GONE);
                }
                holder.tv_target_group.setText(DataUtil.isDataElementNull(data.get(position).get("TargetTeam")));
                holder.tv_create_time.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentName")));
                holder.tv_device_num.setText(DataUtil.isDataElementNull(data.get(position).get("EquipmentAssetsIDList")));
//                holder.tv_group.setText(DataUtil.isDataElementNull(data.get(position).get(Task.ORGANISE_NAME)));
                holder.warranty_person.setText(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT)));
                if(taskStatusMap.get(DataUtil.isDataElementNull(data.get(position).get("Status")))!=null){
                    holder.tv_task_state.setText(taskStatusMap.get(DataUtil.isDataElementNull(data.get(position).get("Status"))));
                }else {
                    holder.tv_task_state.setText(DataUtil.isDataElementNull(data.get(position).get("Status")));
                }
                holder.tv_repair_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.APPLICANT_TIME))));
                holder.tv_start_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.START_TIME))));
                holder.tv_end_time.setText(DataUtil.utc2Local(DataUtil.isDataElementNull(data.get(position).get(Task.FINISH_TIME))));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_DESCRIPTION)));
                if(data.get(position).get("IsEvaluated").valueAsBoolean()){
                    holder.tv_creater.setText(getResources().getString(R.string.isCommand));
                    holder.tv_creater.setTextColor(getResources().getColor(R.color.order_color));
                }else{
                    holder.tv_creater.setText(getResources().getString(R.string.NoCommand));
                    holder.tv_creater.setTextColor(getResources().getColor(R.color.esquel_red));
                }
                if(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS)))!=null) {
                    holder.tv_device_name.setText(map.get(DataUtil.isDataElementNull(data.get(position).get(Task.TASK_CLASS))));
                }
                return convertView;
            }
        };
        listView.setAdapter(taskAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // if(getLoginInfo().isMaintenMan()){
                    Intent intent=new Intent(mContext,TaskDetailsActivity.class);
                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
                    intent.putExtra(Task.TASK_ID,DataUtil.isDataElementNull(data.get(position-1).get(Task.TASK_ID)));
                    intent.putExtra(Task.TASK_CLASS,DataUtil.isDataElementNull(data.get(position-1).get(Task.TASK_CLASS)));
                    intent.putExtra("TaskStatus",data.get(position-1).get("Status").valueAsInt());
                    intent.putExtra("IsEvaluated","0");
                    intent.putExtra("FromFragment","0");
                    intent.putExtra("isTaskHistory",true);
                ((Activity)mContext).startActivityForResult(intent, Constants.REQUEST_CODE_TASKHISTORY);
                    //startActivity(intent);
            }
//                else {
//                    Intent intent=new Intent(mContext,CommandActivity.class);
//                    intent.putExtra("TaskDetail",data.get(position-1).asObjectElement().toString());
//                    startActivity(intent);
//                }
//           }
        });
        getTaskHistory();
        return v;
    }
    public void doRefresh(){
        pageIndex=1;
        getTaskHistory();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    public static PendingCommandFragment newInstance(HashMap<String,String> TaskClass,HashMap<String,String> TaskStatus){
        PendingCommandFragment fragment = new PendingCommandFragment();
        Bundle bundle = new Bundle();
        //bundle.putString(Task.TASK_CLASS, TaskClass);
        map=TaskClass;
        taskStatusMap=TaskStatus;
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initMap(){
//        map.put(Task.REPAIR_TASK,getResources().getString(R.string.repair));
//        map.put(Task.MAINTAIN_TASK,getResources().getString(R.string.maintenance));
//        map.put(Task.MOVE_CAR_TASK,getResources().getString(R.string.move_car));
//        map.put(Task.OTHER_TASK,getResources().getString(R.string.other));
//
//        taskStatusMap.put(getResources().getString(R.string.waitingDeal),0);
//        taskStatusMap.put(getResources().getString(R.string.start),1);
//        taskStatusMap.put(getResources().getString(R.string.linked_order),2);
//        taskStatusMap.put(getResources().getString(R.string.cancel),3);
//        taskStatusMap.put(getResources().getString(R.string.verity),4);
//        taskStatusMap.put(getResources().getString(R.string.MonthlyStatement),5);
    }
    private void getTaskHistory(){
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
                ToastUtil.showToastShort(R.string.noMoreData,mContext);
                return;
            }}
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set("pageSize",PAGE_SIZE);
        jsonObjectElement.set("pageIndex",pageIndex);
        //jsonObjectElement.set("Status",2);
        jsonObjectElement.set("IsEvaluated",0);
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(mContext, "TaskAPI/TaskHistoryListGet", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    if (pageIndex == 1) {
                        data.clear();
                    }
                    JsonObjectElement jsonObjectElement=new JsonObjectElement(t);
                    if(jsonObjectElement.get("PageData")!=null&&jsonObjectElement.get("PageData").isArray()&&jsonObjectElement.get("PageData").isArray()&&jsonObjectElement.get("PageData").asArrayElement().size()>0){
                        RecCount = jsonObjectElement.get("RecCount").valueAsInt();
                        pageIndex++;
                        for(DataElement dataElement:jsonObjectElement.get("PageData").asArrayElement()){
                            data.add(dataElement.asObjectElement());
                        }
                    }else{
                        ToastUtil.showToastShort(R.string.noCommandData,mContext);
                    }
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
}
