package com.emms.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.R;
import com.emms.activity.BaseActivity;
import com.emms.adapter.GroupAdapter;
import com.emms.adapter.MultiAdapter;
import com.emms.bean.AwaitRepair;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by jaffer.deng on 2016/7/15.
 */
public class InvitorActivity extends BaseActivity implements View.OnClickListener{
    private PullToRefreshListView mListView;
    private ListView mGroupListView;
    private MultiAdapter adapter=null;
    private GroupAdapter groupAdapter;
    private ArrayList<ObjectElement> listItems=new ArrayList<ObjectElement>();
    private ArrayList<ObjectElement> listGroup=new ArrayList<ObjectElement>();
    // private HashMap<ObjectElement,ArrayList<ObjectElement>> List_Group_Items=new HashMap<ObjectElement,ArrayList<ObjectElement>>();
    private ImageView bcakImageView;
    private ImageView sureImageView;
    private boolean isExChangeOrder=false;
    private boolean isInviteHelp=false;
    private Context context=this;
    private String taskId;
    private static int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    private ObjectElement groupData=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitor);
        //标识，判断当前界面操作是转单还是邀请协助，若为转单，只能选一人，若为邀请协助，可多选
        isExChangeOrder=getIntent().getBooleanExtra("isExChangeOrder",false);
        isInviteHelp=getIntent().getBooleanExtra("isInviteHelp",false);
        if(isExChangeOrder){
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.exchangeOrder);
        }
       taskId=getIntent().getStringExtra(Task.TASK_ID) ;
        adapter=new MultiAdapter(InvitorActivity.this,listItems);
        mListView = (PullToRefreshListView) findViewById(R.id.id_wait_list);
        mListView.setAdapter(adapter);
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pageIndex=1;
                RecCount=0;
                groupData=listGroup.get(position);
                getListItems();
            }
        });
        bcakImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        sureImageView = (ImageView) findViewById(R.id.btn_right_action);

        groupAdapter=new GroupAdapter(InvitorActivity.this,listGroup);
        mGroupListView.setAdapter(groupAdapter);
       // getListItems(); //获取假数据
        getGroupData(); //设置组别
     //   adapter = new MultiAdapter(this, listItems);
   //     mListView.setAdapter(adapter);


        bcakImageView.setOnClickListener(this);
        sureImageView.setOnClickListener(this);
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
                },500);
            }
        });
    }

    /**
     * 初始化信息
     */
    private void getListItems() {
        if(RecCount!=0){
        if(pageIndex*PAGE_SIZE>=RecCount){
            Toast toast=Toast.makeText(this,R.string.noMoreData,Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return;
        }}
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        params.put("team_id", DataUtil.isDataElementNull(groupData.get("Organise_ID")));
        params.put("pageSize",PAGE_SIZE);
        params.put("pageIndex",pageIndex);
        HttpUtils.get(this, "OperatorStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get("Success").valueAsBoolean()){
                        RecCount=json.get("RecCount").valueAsInt();
                    if(json.get("PageData")!=null&&json.get("PageData").asArrayElement().size()>0){
                        if(pageIndex==1){
                            listItems.clear();
                        }
                        pageIndex++;
                        for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                            listItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                         adapter.setListItems(listItems);
                    }
                }
                else{
                    Toast toast=Toast.makeText(context,"获取数据失败",Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();}
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
        params.put(Task.OPERATOR_ID,String.valueOf(getLoginInfo().getId()));
        HttpUtils.get(this, "BaseOrganiseTeam", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                   if(json.get("PageData")!=null&&json.get("PageData").asArrayElement().size()>0){
                       listGroup.clear();
                        for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                            listGroup.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                       groupData=listGroup.get(0);
                       getListItems();
                       groupAdapter.setDatas(listGroup);
                       groupAdapter.notifyDataSetChanged();
                    }

                   // if(json!=null)
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });

    }

    @Override
    public void onClick(View v) {
        int clikId =v.getId();
        switch (clikId){
            case R.id.btn_bar_left_action:
                finish();
                break;

            case R.id.btn_right_action:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(adapter.getListItems()!=null){
                            ArrayList<Integer> invitorList=new ArrayList<Integer>();
                        for(int i=0;i<adapter.getlistItemID().size();i++){
                            invitorList.add(Integer.valueOf(DataUtil.isDataElementNull(adapter.getListItems().get(adapter.getListItemID().get(i)).get("Operator_ID"))));
                        }
                            postInviteDataToServer(invitorList);
                        //    Toast.makeText(InvitorActivity.this, "你选择了："
                         //           + adapter.getlistItemID().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                break;
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
            jsonObjectElement.set("OperatorType",1);}
        else {
            jsonObjectElement.set("OperatorType",0);
        }
    //    List<Integer> a=new ArrayList<Integer>();
   //     a.add(4667);
        JsonArrayElement jsonArrayElement=new JsonArrayElement(submitData.toString());
        jsonObjectElement.set("Operator_IDS",jsonArrayElement);
       // Log.e("daf",a.toString());
        params.putJsonParams(jsonObjectElement.toJson());
        HttpUtils.post(this, "TaskOperatorChange", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                dismissCustomDialog();
                if(isExChangeOrder){
                    Toast.makeText(InvitorActivity.this,R.string.exchangeOrderSuccess,Toast.LENGTH_LONG).show();
                }else{
                Toast.makeText(InvitorActivity.this,R.string.inviteSuccess,Toast.LENGTH_LONG).show();}
                finish();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                dismissCustomDialog();
            }
        });

    }
}
