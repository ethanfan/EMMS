package com.emms.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.emms.R;

/**
 * Created by Administrator on 2016/7/25.
 */
public class CommandActivity extends BaseActivity {
    private TextView group,task_id,task_create_time,task_accept_time,task_complete_time;
    private GridView response_speed,service_attitude,repair_speed;
    private Button comfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        initView();
    }
    public void initView(){
        group=(TextView)findViewById(R.id.group);
        task_id=(TextView)findViewById(R.id.task_id);
        task_create_time=(TextView)findViewById(R.id.task_create_time);
        task_accept_time=(TextView)findViewById(R.id.task_accept_time);
        task_complete_time=(TextView)findViewById(R.id.task_complete_time);
        response_speed=(GridView)findViewById(R.id.response_speed);
        service_attitude=(GridView)findViewById(R.id.service_attitude);
        repair_speed=(GridView)findViewById(R.id.repair_speed);
        comfirm=(Button)findViewById(R.id.comfirm);
    }
}
