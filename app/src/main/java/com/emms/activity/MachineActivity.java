package com.emms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.emms.R;
import com.emms.schema.Task;

public class MachineActivity extends NfcActivity implements View.OnClickListener{
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine);
//        //透明状态栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        //透明导航栏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        findViewById(R.id.searh_oldrecord).setOnClickListener(this);
        findViewById(R.id.repair).setOnClickListener(this);
        findViewById(R.id.move_car).setOnClickListener(this);
        findViewById(R.id.other).setOnClickListener(this);

        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id){
            case R.id.searh_oldrecord:
                startActivity(new Intent(MachineActivity.this,SearchRecordActivity.class));
                break;
            case R.id.repair:
            {  Intent intent=new Intent(this,CreateTaskActivity.class);
                intent.putExtra(Task.TASK_CLASS,Task.REPAIR_TASK);
                startActivity(intent);
                break;}
            case R.id.move_car:
            {Intent intent=new Intent(this,CreateTaskActivity.class);
                intent.putExtra(Task.TASK_CLASS,Task.MOVE_CAR_TASK);
                startActivity(intent);
                break;}
            case R.id.other:
            { Intent intent=new Intent(this,CreateTaskActivity.class);
                intent.putExtra(Task.TASK_CLASS,Task.OTHER_TASK);
                startActivity(intent);
                break;}
        }

    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
