package com.emms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Operator;
import com.emms.util.BuildConfig;
import com.emms.util.SharedPreferenceManager;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.MsgView;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.http.VolleyError;

import java.io.File;
import java.util.HashMap;

/**
 * Created by jaffer.deng on 2016/6/6.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button btn_exit;
    private CardView create_task,repair_tag,maintain_tag,move_car_tag,other_tag,team_status_tag;
    private MsgView repair_msg,maintain_msg,move_car_msg,other_msg;
    private HashMap<Integer,String> taskNum=new HashMap<Integer,String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getTaskCountFromServer();
        getNewDataFromServer(); //下载DB文件
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
        repair_msg.setText("aaaaaa");
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
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }

                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);
                }
            });
        }else if (id == R.id.create_taskcd){
            Intent intent=new Intent(MainActivity.this,CreateTaskActivity.class);
            startActivity(intent);
        }else if (id == R.id.repair_tag){
            Intent intent=new Intent(MainActivity.this,RepairTaskACtivity.class);
            if(taskNum.get(0)!=null){
            intent.putExtra("TaskNum",taskNum.get(0));}
            else {
                intent.putExtra("TaskNum","0/0");
            }
            startActivity(intent);
//            Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.maintain_tag){
            Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.move_car_tag){
            Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.other_tag){
            Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }else if (id == R.id.team_status_tag){
            Toast.makeText(MainActivity.this,"该模块正在开发",Toast.LENGTH_SHORT).show();
        }
    }

    //下载DB文件
    private void getNewDataFromServer() {

        File dbFile = new File(getExternalFilesDir(null),"/test.db");
        if(dbFile.exists()){

            return;
        }

        String savepath = dbFile.getParentFile().getAbsolutePath();

        HttpUtils.download(MainActivity.this, savepath, BuildConfig.getConfigurationDownload(), null, new HttpCallback() {
            @Override
            public void onSuccessInAsync(byte[] t) {
                super.onSuccessInAsync(t);
            }

            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
            }

            @Override
            public void onFailure(VolleyError error) {
                super.onFailure(error);
            }
        });
    }
   private void getTaskCountFromServer(){
        HttpParams params=new HttpParams();

        params.put("id",String.valueOf(getLoginInfo().getId()));
      // String s=SharedPreferenceManager.getUserName(this);
       HttpUtils.get(this, "TaskNum", params, new HttpCallback() {
           @Override
           public void onSuccess(String t) {
               super.onSuccess(t);
               if (t != null) {
                   JsonObjectElement json = new JsonObjectElement(t);
                   //获取任务数目，Data_ID对应，1对应维修，2对应维护，3对应搬车，4对应其它
                   if (json.get("PageData") != null && json.get("PageData").asArrayElement() != null) {
                       for (int i = 0; i < json.get("PageData").asArrayElement().size(); i++) {
                           //   taskNum.put(jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement().get("Data_ID").valueAsInt(),
                           //         jsonObjectElement.get("PageData").asArrayElement().get(i).asObjectElement());
                           String taskNumToShow = json.get("PageData").asArrayElement().get(i).asObjectElement().get("ToDoNum").valueAsString() + "/" +
                                   json.get("PageData").asArrayElement().get(i).asObjectElement().get("DoingNum").valueAsString();
                           taskNum.put(i, taskNumToShow);
                       }

                       repair_msg.setText(taskNum.get(0));
                       maintain_msg.setText(taskNum.get(1));
                       move_car_msg.setText(taskNum.get(2));
                       other_msg.setText(taskNum.get(3));
                   }
               }
           }

           @Override
           public void onFailure(int errorNo, String strMsg) {
               super.onFailure(errorNo, strMsg);
           }
        });
    }
}
