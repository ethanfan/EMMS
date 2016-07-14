package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.bean.TaskBean;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Maintain;
import com.emms.schema.Task;
import com.emms.ui.ExpandGridView;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.ui.ScrollViewWithListView;
import com.emms.util.Bimp;
import com.emms.util.FileUtils;
import com.emms.util.LongToDate;
import com.jaffer_datastore_android_sdk.datastore.ObjectElement;
import com.jaffer_datastore_android_sdk.rest.JsonObjectElement;
import com.jaffer_datastore_android_sdk.rxvolley.client.HttpCallback;
import com.jaffer_datastore_android_sdk.rxvolley.client.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/22.
 */
public class TaskDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView menuImageView;
    private ScrollViewWithListView mListview;
    private ScrollView scrollview;
    private ExpandGridView noScrollgridview;
    private GridAdapter adapter;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas;
    private Context mContext;
    private PopMenuTaskDetail popMenuTaskDetail;
    String TaskDetail=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        mContext = this;
        //获取任务详细信息
        TaskDetail=getIntent().getStringExtra(Task.TASK_ID);
        initDatas();
        initView();
        initEvent();
    }

    private void initEvent() {
        Bimp.bmp.clear();
        taskAdapter = new TaskAdapter(datas) {

            @Override
            public View getCustomView(View convertView, int position, ViewGroup parent) {
                TaskViewHolder holder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.item_order_details, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.id_participant);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num_details);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name_details);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_start_time_details);
                    holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time_details);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state_details);
                    convertView.setTag(holder);
                } else {
                    holder = (TaskViewHolder) convertView.getTag();
                }
                String creater = datas.get(position).get("Maintainer").valueAsString();
                if (creater != null) {
                    if (!creater.equals("")) {
                        holder.tv_creater.setText(creater);
                    } else {
                        holder.tv_creater.setText(getResources().getString(R.string.no_creater));
                    }
                } else {

                    holder.tv_creater.setText(getResources().getString(R.string.no_creater));
                }


                holder.tv_device_num.setText(datas.get(position).get(Maintain.MACHINE_CODE).valueAsString());
                holder.tv_device_name.setText(datas.get(position).get(Maintain.MACHINE_NAME).valueAsString());
                //String createTime = LongToDate.longPointDate(datas.get(position).get(Maintain.CREATED_DATE_FIELD_NAME).valueAsLong());
                String createTime=datas.get(position).get(Maintain.CREATED_DATE_FIELD_NAME).valueAsString();
                holder.tv_create_time.setText(createTime);
                //String endTime = LongToDate.longPointDate(datas.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsLong());
                String endTime=datas.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsString();
                holder.tv_end_time.setText(endTime);
                String state = "";
            /*    int tag = datas.get(position).getTaskTag();
                if (tag == 1) {
                    holder.tv_task_state.setTextColor(getResources().getColor(R.color.processing_color));
                    state = getResources().getString(R.string.task_state_details_finish);
                } else if (tag == 0) {
                    holder.tv_task_state.setTextColor(getResources().getColor(R.color.pause_color));
                    state = getResources().getString(R.string.task_state_details_non);
                }*/
                holder.tv_task_state.setText(datas.get(position).get(Maintain.STATUS).valueAsString());
                return convertView;
            }

        };
        mListview.setAdapter(taskAdapter);
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.scrollTo(0, 0);
            }
        });

    }

    private void initDatas() {
   /*     datas = new ArrayList<TaskBean>() {
            {
                add(new TaskBean("何邵勃", "0115", "平车", 0, 144400000, 199900000));
                add(new TaskBean(null, "0115", "平车", 1, 144400000, 199900000));
                add(new TaskBean("何邵勃", "0115", "平车", 1, 144400000, 199900000));
            }
        };*/
        datas=new ArrayList<ObjectElement>();
    }

    private void initView() {
        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.task_details));
        findViewById(R.id.btn_bar_left).setVisibility(View.VISIBLE);
        menuImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        menuImageView.setVisibility(View.VISIBLE);
        menuImageView.setOnClickListener(this);
        scrollview = (ScrollView) findViewById(R.id.scrollview_parent);
        mListview = (ScrollViewWithListView) findViewById(R.id.problem_count);
        noScrollgridview = (ExpandGridView) findViewById(R.id.picture_containt);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        adapter.update1();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.bmp.size()) {
                    new PopupWindows(mContext, noScrollgridview);
                } else {
                    Intent intent = new Intent(mContext,
                            PhotoActivity.class);
                    intent.putExtra("ID", arg2);
                    startActivity(intent);
                }
            }
        });
        popMenuTaskDetail =new PopMenuTaskDetail(this,300){

            @Override
            public void onEventDismiss() {

            }
        };
        String[] mTitles =getResources().getStringArray(R.array.menu_list);

        popMenuTaskDetail.addItems(mTitles);

    }
    protected void onRestart()
    {
        adapter.update1();
        super.onRestart();
    }
    @Override
    public void onClick(View v) {
        int id_click = v.getId();
        if (id_click == R.id.btn_right_action) {
            finish();
        } else if (id_click == R.id.btn_bar_left_action) {

            popMenuTaskDetail.showAsDropDown(v);
        }
    }

    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update1() {
            loading1();
        }

        public int getCount() {
            return (Bimp.bmp.size() + 1);
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder = null;

            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_published_grida,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.image.setVisibility(View.VISIBLE);

            if (position == Bimp.bmp.size()) {
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.mipmap.icon_addpic_unfocused));

            } else {
                holder.image.setImageBitmap(Bimp.bmp.get(position));
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        adapter.notifyDataSetChanged();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        public void loading1() {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        if (Bimp.max == Bimp.drr.size()) {
                            Message message = new Message();
                            message.what = 1;
                            handler.sendMessage(message);
                            break;
                        } else {
                            try {
                                String path = Bimp.drr.get(Bimp.max);
                                System.out.println(path);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));
                                FileUtils.saveBitmap(mContext, bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            } catch (IOException e) {

                                e.printStackTrace();
                            }
                        }
                    }
                }
            }).start();
        }
    }

    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            super(mContext);

            View view = View
                    .inflate(mContext, R.layout.item_popupwindows, null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            update();

            Button bt1 = (Button) view
                    .findViewById(R.id.item_popupwindows_camera);
            Button bt2 = (Button) view
                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
            bt2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(TaskDetailsActivity.this,
                            GetPicActivity.class);
                    startActivity(intent);
                    dismiss();
                }
            });
            bt3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dismiss();
                }
            });

        }
    }

    private static final int TAKE_PICTURE = 0x000000;
    private String path = "";

    public void photo() {

        File dir = new File(mContext.getExternalFilesDir(null) + "/btp/");
        if (!dir.exists()) dir.mkdirs();

        try{
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if ( resultCode == -1) {
                    Bimp.drr.add(path);
                    //在此上传图片到服务器;
                    submitPictureToServer(path);
                }
                break;
        }
    }
/**
 * bitmap转为base64
 * @param bitmap
 * @return
 */
 public static String bitmapToBase64(Bitmap bitmap) {

          String result = null;
            ByteArrayOutputStream baos = null;
            try {
                   if (bitmap != null) {
                          baos = new ByteArrayOutputStream();
                          bitmap.compress(Bitmap.CompressFormat.PNG, 20, baos);

                          baos.flush();
                          baos.close();

                          byte[] bitmapBytes = baos.toByteArray();
                          result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
                       }
                } catch (IOException e) {
                   e.printStackTrace();
                } finally {
                   try {
                            if (baos != null) {
                                  baos.flush();
                                  baos.close();
                                }
                      } catch (IOException e) {
                         e.printStackTrace();
                       }
               }
           return result;
       }

    /**
      * base64转为bitmap
      * @param base64Data
      * @return
      */
           public static Bitmap base64ToBitmap(String base64Data) {
          byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
           return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
       }
     private void submitPictureToServer(String path){
         try{Bitmap bitmap=Bimp.revitionImageSize(path);
             String base64=bitmapToBase64(bitmap);
             HttpParams params=new HttpParams();
             if(TaskDetail!=null){
             JsonObjectElement jsonObjectElement=new JsonObjectElement(TaskDetail);}
           /*  params.put(Task.TASK_ID,jsonObjectElement.get(Task.TASK_ID).valueAsString());}
             else{
                 params.put(Task.TASK_ID,16);
             }
             params.put("TaskAttachment_ID",0);
             params.put("ImgBase64",base64);*/
             JsonObjectElement jsonObjectElement=new JsonObjectElement();
             jsonObjectElement.set(Task.TASK_ID,16);
             jsonObjectElement.set("TaskAttachment_ID",0);
             jsonObjectElement.set("ImgBase64",base64);
                 params.putJsonParams(jsonObjectElement.toJson());
             HttpUtils.post(this, "TaskAttachment", params, new HttpCallback() {
                 @Override
                 public void onFailure(int errorNo, String strMsg) {
                     super.onFailure(errorNo, strMsg);
                 }

                 @Override
                 public void onSuccess(String t) {
                     super.onSuccess(t);
                 }
             });
             //上传String
         }catch (IOException e){

         }
     }



}
