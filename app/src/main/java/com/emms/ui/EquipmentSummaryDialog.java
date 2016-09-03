package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.emms.R;
import com.emms.adapter.SubTaskAdapter;
import com.emms.adapter.TaskAdapter;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.util.DataUtil;
import com.emms.util.ListViewUtility;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/31.
 *
 */
public class EquipmentSummaryDialog extends Dialog {
    private Context context;
    private ListView listView;
    private SubTaskAdapter Adapter;

    private ArrayList<ObjectElement> list=new ArrayList<>();
    public EquipmentSummaryDialog(Context context,ArrayList<ObjectElement> list){
        super(context, R.style.MyDialog);
        setContentView(R.layout.dialog_equipment_summary);
        this.context=context;
        this.list=list;
        initView();
    }
    private void initView(){
        listView=(ListView)findViewById(R.id.list);
        Adapter=new SubTaskAdapter(list) {
            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                SubTaskAdapter.TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.item_equipment_summary, parent, false);
                    holder = new SubTaskAdapter.TaskViewHolder();
                    holder.work_num=(TextView)convertView.findViewById(R.id.text) ;
                    convertView.setTag(holder);
                } else {
                    holder = (SubTaskAdapter.TaskViewHolder) convertView.getTag();
                }
                //待修改
                holder.work_num.setText(DataUtil.isDataElementNull(list.get(position).get(Equipment.EQUIPMENT_NAME)));
                return convertView;
            }
        };
        listView.setAdapter(Adapter);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    public ArrayList<ObjectElement> getList() {
        return list;
    }

    public void setList(ArrayList<ObjectElement> list) {
        this.list = list;
    }
    public ListView getListView() {
        return listView;
    }

    public void setListView(ListView listView) {
        this.listView = listView;
    }

}
