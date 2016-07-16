package com.emms.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.emms.R;
import com.emms.activity.BaseActivity;
import com.emms.adapter.GroupAdapter;
import com.emms.adapter.MultiAdapter;
import com.emms.bean.AwaitRepair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaffer.deng on 2016/7/15.
 */
public class InvitorActivity extends BaseActivity implements View.OnClickListener{
    ListView mListView;
    ListView mGroupListView;
    MultiAdapter adapter;
    private List<AwaitRepair> listItems;
    private ArrayList<String> listGroup;
    private ImageView bcakImageView;
    private ImageView sureImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitor);

        mListView = (ListView) findViewById(R.id.id_wait_list);
        mGroupListView = (ListView) findViewById(R.id.group_list);
        bcakImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        sureImageView = (ImageView) findViewById(R.id.btn_right_action);
        getListItems(); //获取假数据
        setGroupData(); //设置组别
        adapter = new MultiAdapter(this, listItems);
        mListView.setAdapter(adapter);
        mGroupListView.setAdapter(new GroupAdapter(this,listGroup));

        bcakImageView.setOnClickListener(this);
        sureImageView.setOnClickListener(this);
    }

    /**
     * 初始化信息
     */
    private List<AwaitRepair> getListItems() {

        listItems = new ArrayList<AwaitRepair>();
        for (int i = 0; i < 20; i++) {
            AwaitRepair awaitRepair = new AwaitRepair("何邵" + i, "普通设备维修" + i,0);
            listItems.add(awaitRepair);
        }
        return listItems;
    }

    public void setGroupData() {
        listGroup = new ArrayList<>();
        listGroup.add("机电一组");
        listGroup.add("机电二组");
        listGroup.add("机电三组");
        listGroup.add("机电四组");

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
                        Toast.makeText(InvitorActivity.this, "你选择了："
                                + adapter.getlistItemID().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }
    }

}