package com.emms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.emms.R;
import com.emms.httputils.HttpUtils;
import com.emms.util.BuildConfig;
import com.emms.util.SharedPreferenceManager;
import com.jaffer_datastore_android_sdk.rxvolley.client.HttpCallback;
import com.jaffer_datastore_android_sdk.rxvolley.client.HttpParams;
import com.jaffer_datastore_android_sdk.rxvolley.http.VolleyError;

import java.io.File;

/**
 * Created by jaffer.deng on 2016/6/6.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener{

    private Button btn_exit;
    private CardView create_task,repair_tag,maintain_tag,move_car_tag,other_tag,team_status_tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        getNewDataFromServer(); //下载DB文件
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_exit){
            HttpParams params = new HttpParams();
            HttpUtils.post(this, "Token/Delete", params, new HttpCallback() {
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
            startActivity(new Intent(MainActivity.this,CreateTaskActivity.class));
        }else if (id == R.id.repair_tag){
            startActivity(new Intent(MainActivity.this,RepairTaskACtivity.class));
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
}
