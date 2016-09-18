package com.emms.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/11.
 *
 */
public class MeasurePointActivity extends NfcActivity implements View.OnClickListener{
    private PullToRefreshListView Measure_Point_ListView;
    private ArrayList<ObjectElement> measure_point_list=new ArrayList<>();
    private TaskAdapter adapter;
    private Context context=this;
    private int PAGE_SIZE=10;
    private int pageIndex=1;
    private int RecCount=0;
    private Handler handler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure_point);
        initView();
        TestData();
    }
    private void initView(){
        ((TextView)findViewById(R.id.tv_title)).setText(R.string.measure_point_list);
        Measure_Point_ListView=(PullToRefreshListView)findViewById(R.id.measure_point_list);
        findViewById(R.id.btn_right_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter=new TaskAdapter(measure_point_list) {
            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_measure_point_list, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.measure_id);
                    holder.tv_group = (TextView) convertView.findViewById(R.id.measure_point_name);
                    holder.tv_task_describe = (TextView) convertView.findViewById(R.id.measure_point_unit);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.measure_point_status);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.measure_point_result);
                    holder.tv_task_state=(TextView)convertView.findViewById(R.id.sequence_number);
                    holder.tv_repair_time=(TextView)convertView.findViewById(R.id.measure_point_type);
                    convertView.setTag(holder);
                }else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                holder.tv_task_state.setText(String.valueOf(position+1));
                holder.tv_creater.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("measure_point_id")));
                holder.tv_group.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("measure_point_name")));
                holder.tv_task_describe.setText(DataUtil.isDataElementNull(measure_point_list.get(position).get("measure_point_unit")));
                if(DataUtil.getDate(DataUtil.isDataElementNull(measure_point_list.get(position).get("measure_point_status"))).equals("1")){
                    holder.tv_create_time.setText("已检测");
                    holder.tv_create_time.setTextColor(getResources().getColor(R.color.order_color));
                }else {
                    holder.tv_create_time.setText("未检测");
                    holder.tv_create_time.setTextColor(getResources().getColor(R.color.esquel_red));
                }

                holder.tv_device_name.setText(DataUtil.getDate(DataUtil.isDataElementNull(measure_point_list.get(position).get("measure_point_result"))));
                holder.tv_repair_time.setText(DataUtil.getDate(DataUtil.isDataElementNull(measure_point_list.get(position).get("measure_point_type"))));
                return convertView;
            }
        };
        Measure_Point_ListView.setAdapter(adapter);
        Measure_Point_ListView.setMode(PullToRefreshListView.Mode.BOTH);
        Measure_Point_ListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //上拉加载更多
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex=1;
                        //getVerfyTaskListFromServer();
                        Measure_Point_ListView.onRefreshComplete();
                    }
                });
            }
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //getVerfyTaskListFromServer();
                        Measure_Point_ListView.onRefreshComplete();
                    }
                },0);
            }
        });
        Measure_Point_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(context,MeasurePointContentActivity.class);
//                intent.putExtra(Task.TASK_ID,measure_point_list.get(position-1).get(Task.TASK_ID).valueAsString());
//                intent.putExtra("TaskDetail",measure_point_list.get(position-1).asObjectElement().toString());
//                intent.putExtra(Task.TASK_CLASS,"T01");
                intent.putExtra("measure_point_detail",measure_point_list.get(position-1).toString());
                startActivity(intent);
            }
        });
        //getVerfyTaskListFromServer();
    }
    private void TestData(){
         for(int i=0;i<2;i++){
             JsonObjectElement objectElement=new JsonObjectElement();
             objectElement.set("measure_point_id",i);
             objectElement.set("measure_point_name","测点"+i);
             objectElement.set("measure_point_unit","m/s^2");
             objectElement.set("measure_point_status","1");
             objectElement.set("measure_point_result","正常");
             objectElement.set("measure_point_type","观察测点");
             measure_point_list.add(objectElement);
         }
        for(int i=0;i<5;i++){
            JsonObjectElement objectElement=new JsonObjectElement();
            objectElement.set("measure_point_id",i);
            objectElement.set("measure_point_name","测点"+i);
            objectElement.set("measure_point_unit","m/s^2");
            objectElement.set("measure_point_status","2");
            objectElement.set("measure_point_type","观察测点");
            measure_point_list.add(objectElement);
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void resolveNfcMessage(Intent intent) {

    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        switch (id) {
            case R.id.btn_right_action: {
                finish();
                break;
            }
        }
    }
}
