package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.emms.adapter.GroupAdapter;
import com.emms.adapter.MultiAdapter;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Data;
import com.emms.schema.Operator;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/7/15.
 *
 */
public class InvitorActivity extends NfcActivity implements View.OnClickListener{
    private PullToRefreshListView mListView;
    private MultiAdapter adapter=null;
    private GroupAdapter groupAdapter;
    private ArrayList<ObjectElement> listItems=new ArrayList<>();
    private ArrayList<ObjectElement> listGroup=new ArrayList<>();
    private boolean isExChangeOrder=false;
    private boolean isInviteHelp=false;
    private Context context=this;
    private String taskId;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    private ObjectElement groupData=null;
    private ArrayList<String> TaskOperator=new ArrayList<>();
    private String Tag;
    private String TaskParticipants;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitor);
        //标识，判断当前界面操作是转单还是邀请协助，若为转单，只能选一人，若为邀请协助，可多选
        isExChangeOrder=getIntent().getBooleanExtra("isExChangeOrder",false);
        isInviteHelp=getIntent().getBooleanExtra("isInviteHelp",false);
        Tag=getIntent().getStringExtra("Tag");
        TaskParticipants=getIntent().getStringExtra("TaskParticipantsList");
        if(isExChangeOrder){
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.exchangeOrder);
        }

        taskId=getIntent().getStringExtra(Task.TASK_ID) ;

        adapter=new MultiAdapter(InvitorActivity.this,listItems,true);
        if(Tag!=null){
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.AddTaskPeople);
            adapter.setFromTaskInfoEnteringActivity(true);
        }
        mListView = (PullToRefreshListView) findViewById(R.id.id_wait_list);
        mListView.setAdapter(adapter);
        ListView mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ObjectElement objectElement=listGroup.get(position);
//                for(int i=0;i<parent.getChildCount();i++){
//                    if(listGroup.get(i).equals(objectElement)){
//                        View v=parent.getChildAt(position);
//                        TextView tv=(TextView)v.findViewById(R.id.group_name);
//                        tv.setTextColor(Color.RED);
//                        //tv.setBackgroundColor(Color.RED);
//                    }
//                    else{
//                        View v=parent.getChildAt(i);
//                        TextView tv=(TextView)v.findViewById(R.id.group_name);
//                        tv.setTextColor(Color.BLACK);
//                    }
//                }
                groupAdapter.setSelection(listGroup.get(position));
                pageIndex=1;
                RecCount=0;
                groupData=listGroup.get(position);
                getListItems();
            }
        });
        ImageView bcakImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
     /*   bcakImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        Button sureButton = (Button) findViewById(R.id.btn_sure_bg);

        groupAdapter=new GroupAdapter(InvitorActivity.this,listGroup);
        mGroupListView.setAdapter(groupAdapter);
       // getListItems(); //获取假数据
        //设置组别
     //   adapter = new MultiAdapter(this, listItems);
   //     mListView.setAdapter(adapter);


        bcakImageView.setOnClickListener(this);
        sureButton.setOnClickListener(this);
        mListView.setMode(PullToRefreshListView.Mode.PULL_FROM_END);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                        //  Toast.makeText(mContext,"获取数据成功",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getListItems();
                        mListView.onRefreshComplete();
                        // Toast.makeText(mContext,"dada",Toast.LENGTH_SHORT).show();
                    }
                },0);
            }
        });
        if(Tag!=null){
            getGroupData();
        }else {
            getTaskOperatorListFromServer();
        }
    }

    /**
     * 初始化信息
     */
    private void getListItems() {
        int PAGE_SIZE = 10;
        if(RecCount!=0){
        if((pageIndex-1)* PAGE_SIZE >=RecCount){
            Toast toast=Toast.makeText(this,R.string.noMoreData,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        params.put("team_id", DataUtil.isDataElementNull(groupData.get("Organise_ID")));
        params.put("pageSize", PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(this, "TaskOperatorAPI/GetTaskOperatorStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get("Success").valueAsBoolean()){
                        RecCount=json.get("RecCount").valueAsInt();
                    if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                        if(pageIndex==1){
                            listItems.clear();
                        }
                        pageIndex++;
                        for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                            listItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                    }else{
                     listItems.clear();
                        ToastUtil.showToastShort(R.string.thisGroupHasNoPerson,context);
                        }
                        adapter.setListItems(listItems);
                }
                else{
                    ToastUtil.showToastShort(R.string.getDataFail,context);
                    }
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

    public void getGroupData() {
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        //params.put(Task.OPERATOR_ID,String.valueOf(getLoginInfo().getId()));
        if(taskId!=null&&!taskId.equals("")) {
            params.put("task_id", taskId);
        }else {
            params.put("task_id", 0);
        }
        HttpUtils.get(this, "BaseDataAPI/GetBaseOrganise", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(!DataUtil.isNullOrEmpty(t)){
                    JsonObjectElement json=new JsonObjectElement(t);
                   if(json.get("PageData")!=null&&json.get("PageData").isArray()&&json.get("PageData").asArrayElement().size()>0){
                       listGroup.clear();
                        for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                            listGroup.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                       groupData=listGroup.get(0);
                       getListItems();
                       groupAdapter.setDatas(listGroup);
                       groupAdapter.notifyDataSetChanged();
                       groupAdapter.setSelection(listGroup.get(0));
                    }

                   // if(json!=null)
                }else {
                    ToastUtil.showToastLong(R.string.GetGroupDataFail,context);
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastLong(R.string.GetGroupDataFailCauseByNetWork,context);
                    }
                });
                dismissCustomDialog();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int clikId =v.getId();
        switch (clikId){
            case R.id.btn_bar_left_action:
            {   finish();
                break;}

            case R.id.btn_sure_bg:{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(adapter.getListItems()!=null){
                            if(Tag!=null){
                                JsonArrayElement TaskParticipantsList=new JsonArrayElement(TaskParticipants);
                                for(int j=0;j<TaskParticipantsList.size();j++){
                                    for(int k=0;k<adapter.getlistItemID().size();k++){
                                  if(   DataUtil.isDataElementNull(adapter.getListItems().get(adapter.getListItemID().get(k)).get(Operator.OPERATOR_ID))
                                          .equals(DataUtil.isDataElementNull(TaskParticipantsList.get(j).asObjectElement().get(Operator.OPERATOR_ID)))){
                                      ToastUtil.showToastShort(DataUtil.isDataElementNull(TaskParticipantsList.get(j).asObjectElement().get("Name"))
                                              +" "+getResources().getString(R.string.JoinerIsInTask),context);
                                      return;
                                  }
                                    }
                                }
                                ArrayList<ObjectElement> data=new ArrayList<>();
                                for(int i=0;i<adapter.getlistItemID().size();i++){
                                    data.add(adapter.getListItems().get(adapter.getListItemID().get(i)));
                                }
                                if(data.size()<=0){
                                    ToastUtil.showToastShort(R.string.pleaseSelectJoiner,context);
                                    return;
                                }
                                Intent intent=new Intent();
                                intent.putExtra("Data",data.toString());
                                setResult(3,intent);
                                finish();
                                return;
                            }
                            ArrayList<Integer> invitorList=new ArrayList<>();
                        for(int i=0;i<adapter.getlistItemID().size();i++){
                            invitorList.add(Integer.valueOf(DataUtil.isDataElementNull(adapter.getListItems().get(adapter.getListItemID().get(i)).get("Operator_ID"))));
                        }
                            if(invitorList.size()==0){
                                if(isExChangeOrder){
                                    ToastUtil.showToastShort(R.string.pleaseSelectExchangeOrderTarget,context);
                                    return;
                                }else {
                                    ToastUtil.showToastShort(R.string.pleaseSelectInviteTarget,context);
                                    return;
                                }
                            }
                            for(int j=0;j<TaskOperator.size();j++) {
                                if (invitorList.contains(Integer.valueOf(TaskOperator.get(j)))) {
                                    if(isExChangeOrder){
                                        ToastUtil.showToastShort(R.string.exchangerIsInTask,context);
                                    }else{
                                        ToastUtil.showToastShort(R.string.invitorIsInTask,context);
                                    }
                                    return;
                                }
                            }
                            postInviteDataToServer(invitorList);
                        //    Toast.makeText(InvitorActivity.this, "你选择了："
                         //           + adapter.getlistItemID().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                break;}
        }
    }
    public void postInviteDataToServer(ArrayList<Integer> submitData){
        if(isExChangeOrder){
            if(submitData.size()>1){
                Toast toast=Toast.makeText(this,R.string.exchangeOrderToOnePerson,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                return;
            }
        }
        showCustomDialog(R.string.submitData);
        HttpParams params = new HttpParams();
        JsonObjectElement jsonObjectElement=new JsonObjectElement();
        jsonObjectElement.set(Task.TASK_ID,taskId);
        if(isInviteHelp){
            jsonObjectElement.set("OperatorType",0);}
        else {
            jsonObjectElement.set("OperatorType",1);
        }
    //    List<Integer> a=new ArrayList<Integer>();
   //     a.add(4667);
        final JsonArrayElement jsonArrayElement=new JsonArrayElement(submitData.toString());
        jsonObjectElement.set("Operator_IDS",jsonArrayElement);
       // Log.e("daf",a.toString());
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(this, "TaskOperatorAPI/ChangeTaskOperator", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get(Data.SUCCESS)!=null&&json.get(Data.SUCCESS).valueAsBoolean()){
                 if(Tag==null) {
                     if (isExChangeOrder) {
                         //setResult(1);
                         startActivity(new Intent(context, CusActivity.class));
                         Toast.makeText(InvitorActivity.this, R.string.exchangeOrderSuccess, Toast.LENGTH_LONG).show();
                     } else {
                         Toast.makeText(InvitorActivity.this, R.string.inviteSuccess, Toast.LENGTH_LONG).show();
                     }
                 }else {
                     Toast.makeText(InvitorActivity.this, R.string.SuccessAddTaskPeople, Toast.LENGTH_LONG).show();
                     //setResult(3);
                 }
                finish();
            }else {
                        if(Tag==null) {
                        if(isExChangeOrder){
                            ToastUtil.showToastShort(R.string.exchangeOrderFail,context);
                        }else {
                            ToastUtil.showToastShort(R.string.inviteFail,context);
                        }
                        }else {
                            ToastUtil.showToastShort(R.string.FailAddTaskPeople,context);
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                ToastUtil.showToastShort(R.string.submitFail,context);
                dismissCustomDialog();
            }
        });
    }
    private void getTaskOperatorListFromServer(){
        HttpParams params=new HttpParams();
        params.put("task_id",taskId);
        HttpUtils.get(this, "TaskOperatorAPI/GetTaskOperatorList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
               if(t!=null){
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           getGroupData();
                       }
                   });
                   JsonObjectElement json=new JsonObjectElement(t);
                   if(json.get(Data.SUCCESS)!=null&&json.get(Data.SUCCESS).valueAsBoolean()){
                   if(json.get(Data.PAGE_DATA)!=null&&json.get(Data.PAGE_DATA).asArrayElement().size()>0){
                 for(int i=0;i<json.get(Data.PAGE_DATA).asArrayElement().size();i++){
                       TaskOperator.add(DataUtil.isDataElementNull(json.get(Data.PAGE_DATA).asArrayElement()
                               .get(i).asObjectElement().get("Operator_ID")));
                   }
                   }
               }else{
                       ToastUtil.showToastShort(R.string.getTaskOperatorFail,context);
                   }
               }else{
                   ToastUtil.showToastShort(R.string.getTaskOperatorFail,context);
               }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToastShort(R.string.getTaskOperatorFail,context);
                    }
                });
            }
        });
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
