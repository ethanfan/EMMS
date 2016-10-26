package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.ToastUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;

public class TeamStatusActivity extends NfcActivity implements View.OnClickListener{

    private PullToRefreshListView mListView;
    private ListView mGroupListView;
    private MultiAdapter adapter=null;
    private GroupAdapter groupAdapter;
    private ArrayList<ObjectElement> listItems=new ArrayList<ObjectElement>();
    private ArrayList<ObjectElement> listGroup=new ArrayList<ObjectElement>();
    // private HashMap<ObjectElement,ArrayList<ObjectElement>> List_Group_Items=new HashMap<ObjectElement,ArrayList<ObjectElement>>();
    private ImageView bcakImageView;
    private Button sureButton;
    private boolean isExChangeOrder=false;
    private boolean isInviteHelp=false;
    private Context context=this;
    private String taskId;
    private  int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    private ObjectElement groupData=null;
    private ArrayList<String> TaskOperator=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_status);
        initView();
    }

    /**
     * 初始化信息
     */
    private void getListItems() {
        if(RecCount!=0){
            if((pageIndex-1)*PAGE_SIZE>=RecCount){
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
                    try{
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
                                ToastUtil.showToastLong(R.string.thisGroupHasNoPerson,context);
                            }
                            adapter.setListItems(listItems);
                        }
                        else{
                            ToastUtil.showToastLong(R.string.getDataFail,context);
                        }
                    }catch (Exception e){
                        if(e.getCause()!=null){
                        ToastUtil.showToastLong(e.getCause().toString(),context);}
                    }finally {
                        dismissCustomDialog();
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
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.team_status);
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        adapter=new MultiAdapter(this,listItems,false);
        mListView = (PullToRefreshListView) findViewById(R.id.id_wait_list);
        mListView.setAdapter(adapter);
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                groupAdapter.setSelection(listGroup.get(position));
                pageIndex=1;
                RecCount=0;
                groupData=listGroup.get(position);
                getListItems();
            }
        });
        groupAdapter=new GroupAdapter(this,listGroup);
        mGroupListView.setAdapter(groupAdapter);
        getGroupData(); //设置组别
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
    }
    public void getGroupData() {
        showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
        params.put(Task.TASK_ID,0);
        HttpUtils.get(this, "BaseOrganiseTeam", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    try{
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
                    }catch (Exception e){
                        if(e.getCause()!=null) {
                            ToastUtil.showToastLong(e.getCause().toString(), context);
                        }
                        dismissCustomDialog();
                    }

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
        switch (clikId) {
            case R.id.btn_right_action:{
                finish();
                break;
            }
        }

    }


    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
