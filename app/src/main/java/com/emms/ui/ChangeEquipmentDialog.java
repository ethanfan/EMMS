package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.sqlite.SqliteStore;
import com.emms.R;
import com.emms.activity.AppAplication;
import com.emms.activity.TaskDetailsActivity;
import com.emms.adapter.StatusAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.WorkInfo;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.DataDictionary;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class ChangeEquipmentDialog extends Dialog {
    private ChangeEquipmentDialog dialog = this;
    private Context context;
    private ListView change_Equipment_status;
    private StatusAdapter taskAdapter;
    private String TaskId;
    private ArrayList<String> status=new ArrayList<String>();
    public ChangeEquipmentDialog(Context context, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        Collections.addAll(status,context.getResources().getStringArray(R.array.equip_status));
        initview();
    }

    public void initview() {
        findViewById(R.id.dismissView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        change_Equipment_status=(ListView)findViewById(R.id.status_list);
        taskAdapter=new StatusAdapter(status) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                StatusAdapter.ViewHolder holder;
                if(convertView==null){
                    convertView = LayoutInflater.from(context).inflate(
                            R.layout.change_equipment_status_dialog_item, null);
                    holder = new StatusAdapter.ViewHolder();

                    convertView.setTag(holder);

                    holder.status = (TextView) convertView
                            .findViewById(R.id.status);

                } else {
                    holder = (StatusAdapter.ViewHolder) convertView.getTag();
                }
                holder.status.setText(status.get(position));
                return convertView;
            }
        };
        change_Equipment_status.setAdapter(taskAdapter);
        ((TextView)findViewById(R.id.status)).setText(R.string.cancel);
        findViewById(R.id.status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



    public void submitSubTaskData() {
        HttpParams params = new HttpParams();
        JsonObjectElement jsonObjectElement = new JsonObjectElement();

        HttpUtils.post(context, "TaskItem", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
            }
        });
    }



}