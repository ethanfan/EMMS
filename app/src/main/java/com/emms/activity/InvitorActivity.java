package com.emms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.ObjectElement;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jaffer.deng on 2016/7/15.
 */
public class InvitorActivity extends BaseActivity implements View.OnClickListener{
    ListView mListView;
    ListView mGroupListView;
    MultiAdapter adapter=null;
    GroupAdapter groupAdapter;
    private ArrayList<ObjectElement> listItems=new ArrayList<ObjectElement>();
    private ArrayList<ObjectElement> listGroup=new ArrayList<ObjectElement>();
    // private HashMap<ObjectElement,ArrayList<ObjectElement>> List_Group_Items=new HashMap<ObjectElement,ArrayList<ObjectElement>>();
    private ImageView bcakImageView;
    private ImageView sureImageView;
    private boolean isExChangeOrder=false;
    private boolean isInviteHelp=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitor);
        //标识，判断当前界面操作是转单还是邀请协助，若为转单，只能选一人，若为邀请协助，可多选
        isExChangeOrder=getIntent().getBooleanExtra("isExChangeOrder",false);
        isInviteHelp=getIntent().getBooleanExtra("isInviteHelp",false);
          postInviteDataToServer();
        mListView = (ListView) findViewById(R.id.id_wait_list);
        mGroupListView = (ListView) findViewById(R.id.group_list);
        mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getListItems(listGroup.get(position-1));
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
    }

    /**
     * 初始化信息
     */
    private void getListItems(ObjectElement objectElement) {
        HttpParams params=new HttpParams();

        params.put("org_id",objectElement.get("Organise_ID").valueAsString());
        HttpUtils.get(this, "OperatorStatus", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                    JsonObjectElement json=new JsonObjectElement(t);
                    if(json.get("PageData")!=null&&json.get("PageData").asArrayElement().size()>0){
                        listItems.clear();
                        for(int i=0;i<json.get("PageData").asArrayElement().size();i++){
                            listItems.add(json.get("PageData").asArrayElement().get(i).asObjectElement());
                        }
                        if(adapter==null){
                        adapter=new MultiAdapter(InvitorActivity.this,listItems);
                        mListView.setAdapter(adapter);}
                        else {
                        adapter.setListItems(listItems);
                        adapter.notifyDataSetChanged();}
                    }
                    // if(json!=null)
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }

    public void getGroupData() {
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
                       getListItems(listGroup.get(0));
                       groupAdapter.setDatas(listGroup);
                       groupAdapter.notifyDataSetChanged();
                    }

                   // if(json!=null)
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
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
                        postInviteDataToServer();
                        Toast.makeText(InvitorActivity.this, "你选择了："
                                + adapter.getlistItemID().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }
    }
    public void postInviteDataToServer(){

    }
}
