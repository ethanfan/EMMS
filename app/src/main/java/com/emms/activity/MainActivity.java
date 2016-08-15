package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.SharedPreferenceManager;
import com.emms.util.ToastUtil;
import com.flyco.tablayout.widget.MsgView;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;

import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/6.
 */
public class MainActivity extends NfcActivity implements View.OnClickListener{

    private Button btn_exit;
    private CardView create_task,repair_tag,maintain_tag,move_car_tag,other_tag,team_status_tag;
    private MsgView repair_msg,maintain_msg,move_car_msg,other_msg;
    private HashMap<Integer,String> taskNum=new HashMap<Integer,String>();
    private static String TASK_NUM="TaskNum";
    private Context context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getTaskCountFromServer();
       // getNewDataFromServer(); //下载DB文件
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getTaskCountFromServer();
    }

    private void initView() {
        btn_exit = (Button) findViewById(R.id.btn_exit);
        create_task = (CardView) findViewById(R.id.create_taskcd);
        repair_tag =(CardView) findViewById(R.id.repair_tag);
        maintain_tag =(CardView) findViewById(R.id.maintain_tag);
        move_car_tag =(CardView) findViewById(R.id.move_car_tag);
        other_tag =(CardView) findViewById(R.id.other_tag);
        team_status_tag =(CardView) findViewById(R.id.team_status_tag);
        btn_exit.setOnClickListener(this);
        create_task.setOnClickListener(this);
        repair_tag.setOnClickListener(this);

        maintain_tag.setOnClickListener(this);
        move_car_tag.setOnClickListener(this);
        other_tag.setOnClickListener(this);
        team_status_tag.setOnClickListener(this);
        //create_task.
        repair_msg=(MsgView)findViewById(R.id.repair_tip);
        maintain_msg=(MsgView)findViewById(R.id.maintian_tip);
        move_car_msg=(MsgView)findViewById(R.id.move_car_tip);
        other_msg=(MsgView)findViewById(R.id.other_tip);


        //显示用户人名，工号
        ((TextView)findViewById(R.id.UserName)).setText(getLoginInfo().getName());
        ((TextView)findViewById(R.id.WorkNum)).setText(getLoginInfo().getOperator_no());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_exit){
            HttpParams params = new HttpParams();
            HttpUtils.delete(this, "Token", params, new HttpCallback() {
                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    SharedPreferenceManager.setCookie(MainActivity.this,null);
                    SharedPreferenceManager.setLoginData(MainActivity.this,null);
                    SharedPreferenceManager.setUserData(MainActivity.this,null);
                   // startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            });
        }else if (id == R.id.create_taskcd){
            //后续删除此入口
            //Intent intent=new Intent(MainActivity.this,CreateTaskActivity.class);
            //startActivity(intent);
            ToastUtil.showToastLong("目前只有公共机能创建任务",this);
        }else if (id == R.id.repair_tag){
            Intent intent=new Intent(MainActivity.this,TaskListActivity.class);
            if(taskNum.get(0)!=null){
            intent.putExtra(TASK_NUM,taskNum.get(0));}
            else {
                intent.putExtra(TASK_NUM,"0/0");
            }
            intent.putExtra(Task.TASK_CLASS,Task.REPAIR_TASK);
            startActivity(intent);
//            Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.maintain_tag){
            Intent intent=new Intent(MainActivity.this,TaskListActivity.class);
            if(taskNum.get(1)!=null){
                intent.putExtra(TASK_NUM,taskNum.get(1));}
            else {
                intent.putExtra(TASK_NUM,"0/0");
            }
            intent.putExtra(Task.TASK_CLASS,Task.MAINTAIN_TASK);
            startActivity(intent);
            //Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.move_car_tag){
            Intent intent=new Intent(MainActivity.this,TaskListActivity.class);
            if(taskNum.get(2)!=null){
                intent.putExtra(TASK_NUM,taskNum.get(2));}
            else {
                intent.putExtra(TASK_NUM,"0/0");
            }
            intent.putExtra(Task.TASK_CLASS,Task.MOVE_CAR_TASK);
            startActivity(intent);
            //Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.other_tag){
            Intent intent=new Intent(MainActivity.this,TaskListActivity.class);
            if(taskNum.get(3)!=null){
                intent.putExtra(TASK_NUM,taskNum.get(3));}
            else {
                intent.putExtra(TASK_NUM,"0/0");
            }
            intent.putExtra(Task.TASK_CLASS,Task.OTHER_TASK);
            startActivity(intent);
           // Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.team_status_tag){
           Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }
    }


   private void getTaskCountFromServer(){
       showCustomDialog(R.string.loadingData);
        HttpParams params=new HttpParams();
      //params.put("id",String.valueOf(getLoginInfo().getId()));
      // String s=SharedPreferenceManager.getUserName(this);
       HttpUtils.get(this, TASK_NUM, params, new HttpCallback() {
           @Override
           public void onSuccess(String t) {
               super.onSuccess(t);
               if (t != null) {
                   JsonObjectElement json = new JsonObjectElement(t);
                   //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                   if (json.get("PageData") != null && json.get("PageData").asArrayElement() != null&&json.get("PageData").asArrayElement().size()>0) {
                       for (int i = 0; i < json.get("PageData").asArrayElement().size(); i++) {
                           //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                           //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                           String taskNumToShow = json.get("PageData").asArrayElement().get(i).asObjectElement().get("S1").valueAsString() + "/" +
                                   json.get("PageData").asArrayElement().get(i).asObjectElement().get("S0").valueAsString();
                           taskNum.put(i, taskNumToShow);
                       }
                       repair_msg.setText(taskNum.get(0));
                       maintain_msg.setText(taskNum.get(1));
                       move_car_msg.setText(taskNum.get(2));
                       other_msg.setText(taskNum.get(3));
                   }
               }
               dismissCustomDialog();
           }

           @Override
           public void onFailure(int errorNo, String strMsg) {
               super.onFailure(errorNo, strMsg);
               if(errorNo==401){
                   ToastUtil.showToastLong(R.string.unauthorization,context);
                   return;
               }
               ToastUtil.showToastLong(R.string.loadingFail,context);
               dismissCustomDialog();
           }
        });
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}
