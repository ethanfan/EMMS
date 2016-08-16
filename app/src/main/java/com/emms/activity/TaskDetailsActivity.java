package com.emms.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.datastore_android_sdk.datastore.DataElement;
import com.datastore_android_sdk.datastore.ArrayElement;
import com.emms.R;
import com.emms.adapter.TaskAdapter;
import com.emms.datastore.EPassSqliteStoreOpenHelper;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Equipment;
import com.emms.schema.Task;
import com.emms.ui.ChangeEquipmentDialog;
import com.emms.ui.ExpandGridView;
import com.emms.ui.PopMenuTaskDetail;
import com.emms.ui.ScrollViewWithListView;
import com.emms.util.AnimateFirstDisplayListener;
import com.emms.util.Bimp;
import com.emms.util.Constants;
import com.emms.util.DataUtil;
import com.emms.util.FileUtils;
import com.datastore_android_sdk.datastore.ObjectElement;
import com.datastore_android_sdk.rest.JsonArrayElement;
import com.datastore_android_sdk.rest.JsonObjectElement;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.emms.util.ListViewUtility;
import com.emms.util.RootUtil;
import com.emms.util.ToastUtil;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jaffer.deng on 2016/6/22.
 */
public class TaskDetailsActivity extends NfcActivity implements View.OnClickListener {

    public class ViewHolder {
        TextView deviceCountTextView;
        TextView dealCountTextView;
    }

    private ViewHolder mHolder = new ViewHolder();
    private ImageView menuImageView;
    private ScrollViewWithListView mListview;
    private ScrollView scrollview;
    private ExpandGridView noScrollgridview;
    private GridAdapter adapter;
    private TaskAdapter taskAdapter;
    private ArrayList<ObjectElement> datas;
    private Context mContext;
    private PopMenuTaskDetail popMenuTaskDetail;
    private ChangeEquipmentDialog changeEquipmentDialog;
    private String TaskDetail = null;//任务详细
    private String TaskClass=null;//任务类型
    private Long taskId = null;//任务ID
    private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
    private Map<String, Object> deviceCountMap = new HashMap<String, Object>();//设备数量
    private ArrayList<String> TaskDeviceIdList = new ArrayList<String>();//设备ID列表
    private Map<String, String> Task_DeviceId_TaskEquipmentId = new HashMap<String, String>();//设备ID对应TaskEquipmentID映射
    private int TaskStatus=-1;
    private boolean getEquipmentListFail=false;//约束网络访问是否成功的tag
    //0-开始，1-暂停，2-领料，3-待料，4-结束

    private final String STATUS_DONE = "4";
    private String Main_person_in_charge_Operator_id;//任务主负责人ID
    static private HashMap<String, String> taskEquipmentStatus = new HashMap<String, String>();

    {
        taskEquipmentStatus.put("0", "待处理");
        taskEquipmentStatus.put("1", "处理中");
        taskEquipmentStatus.put("2", "暂停");
        taskEquipmentStatus.put("3", "待料");
        taskEquipmentStatus.put(STATUS_DONE, "完成");
    }
     private HashMap<String, String> Equipment_Operator_Status_Name_ID_map= new HashMap<String, String>();

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options; // DisplayImageOptions是用于设置图片显示的类

    private static final int MSG_UPDATE_DEVICE_SUM_INFO = 10;
    private HashMap<String,HashMap<String,Integer>>TaskEquipment_OperatorID_Status=new HashMap<String,HashMap<String,Integer>>();//任务设备参与人状态map
    private HashMap<String,String> Euqipment_ID_STATUS_map=new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        mContext = this;
        //获取任务详细信息

        TaskDetail = getIntent().getStringExtra("TaskDetail");
        TaskClass=getIntent().getStringExtra(Task.TASK_CLASS);
        taskId = getTaskId(TaskDetail);
        TaskStatus=getIntent().getIntExtra("TaskStatus",-1);
        //初始化imageLoader
        options = new DisplayImageOptions.Builder().cacheInMemory(false) // 设置下载的图片是否缓存在内存中
//                .cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.NONE)
                .showImageOnLoading(R.mipmap.bg_btn)
                // .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(TaskDetailsActivity.this));

        initDatas();
        initView();
        initEvent();
    }


    private Long getTaskId(String TaskDetail) {


//        if (TaskDetail != null) {
//            JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
//            if (null != jsonObjectElement) {
//                retData = jsonObjectElement.get(Task.TASK_ID).valueAsLong();
//            }
//        }
        String taskIdStr = getIntent().getStringExtra(Task.TASK_ID);
        if (null == taskIdStr || "".equals(taskIdStr.trim()) || "null".equals(taskIdStr.trim())) {
            taskIdStr = "0";
        }

        Long retData = null;
        retData = Long.valueOf(taskIdStr);

        return retData;
    }

    ;

    private void initEvent() {
        Bimp.bmp.clear();
        taskAdapter = new TaskAdapter(datas) {

            @Override
            public View getCustomView(View convertView, final int position, ViewGroup parent) {
                final TaskViewHolder holder;
              //  if (convertView == null) {
                    convertView = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.item_order_details, parent, false);
                    holder = new TaskViewHolder();
                    holder.tv_creater = (TextView) convertView.findViewById(R.id.id_participant);
                    holder.tv_device_num = (TextView) convertView.findViewById(R.id.tv_device_num_details);
                    holder.tv_device_name = (TextView) convertView.findViewById(R.id.tv_device_name_details);
                    holder.tv_create_time = (TextView) convertView.findViewById(R.id.tv_start_time_details);
                    holder.tv_end_time = (TextView) convertView.findViewById(R.id.tv_end_time_details);
                    holder.tv_task_state = (TextView) convertView.findViewById(R.id.tv_task_state_details);
                    holder.listView=(ListView)convertView.findViewById(R.id.equipment_opeartor_list);
              //      convertView.setTag(holder);
              //  } else {
              //      holder = (TaskViewHolder) convertView.getTag();
             //   }
                if(datas.get(position).get("TaskEquipmentOperatorList")!=null&&
                        datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().size()>0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            holder.tv_creater.setVisibility(View.GONE);
                            final ArrayList<ObjectElement> obj=new ArrayList<ObjectElement>();
                            for(int j=0;j< datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().size();j++){
                                obj.add( datas.get(position).get("TaskEquipmentOperatorList").asArrayElement().get(j).asObjectElement());
                            }
                            if(holder.listView.getAdapter()!=null){
                                ((TaskAdapter)holder.listView.getAdapter()).notifyDataSetChanged();
                            }
                            else {
                                holder.listView.setAdapter(new TaskAdapter(obj) {
                                    @Override
                                    public View getCustomView(View convertView1, int position1, ViewGroup parent1) {
                                        TaskViewHolder holder1;
                                        if (convertView1 == null) {
                                            holder1 = new TaskViewHolder();
                                            convertView1 = LayoutInflater.from(TaskDetailsActivity.this).inflate(R.layout.item_equipment_operator_status, parent1, false);
                                            holder1.tv_creater = (TextView) convertView1.findViewById(R.id.operator_name);
                                            holder1.tv_task_state = (TextView) convertView1.findViewById(R.id.operator_status);
                                           convertView1.setTag(holder1);
                                       } else {
                                            holder1 = (TaskViewHolder) convertView1.getTag();
                                       }
                                        holder1.tv_creater.setText(DataUtil.isDataElementNull(obj.get(position1).get("Name")));
                                        holder1.tv_task_state.setText(Equipment_Operator_Status_Name_ID_map.get(DataUtil.isDataElementNull(obj.get(position1).get("Status"))));
                                        return convertView1;
                                    }
                                });
                             }
                            ListViewUtility.setListViewHeightBasedOnChildren(holder.listView);
                        }
                    });
                }else{
                    holder.tv_creater.setVisibility(View.VISIBLE);
                    holder.tv_creater.setText(getResources().getString(R.string.no_creater));
                }
                holder.tv_device_num.setText(DataUtil.isDataElementNull(datas.get(position).get("OracleID")));
                holder.tv_device_name.setText(DataUtil.isDataElementNull(datas.get(position).get(Equipment.EQUIPMENT_NAME)));
                //String createTime = LongToDate.longPointDate(datas.get(position).get(Maintain.CREATED_DATE_FIELD_NAME).valueAsLong());
                String createTime = DataUtil.isDataElementNull(datas.get(position).get("StartTime"));
                holder.tv_create_time.setText(createTime);
                //String endTime = LongToDate.longPointDate(datas.get(position).get(Maintain.MAINTAIN_END_TIME).valueAsLong());

                String equipmentStatus = DataUtil.isDataElementNull(datas.get(position).get("Status"));

                String endTime = "";
                if (STATUS_DONE.equals(equipmentStatus)) {
                    endTime = DataUtil.isDataElementNull(datas.get(position).get("FinishTime"));
                }
                holder.tv_end_time.setText(endTime);
                //  holder.tv_task_state.setText(equipmentStatus);
                holder.tv_task_state.setText(taskEquipmentStatus.get(equipmentStatus));

                return convertView;
            }

        };
        mListview.setAdapter(taskAdapter);
//        scrollview.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollview.scrollTo(0, 0);
//            }
//        });

    }

    private void initDatas() {
        {
            Equipment_Operator_Status_Name_ID_map.put("0",getResources().getString(R.string.start));
            Equipment_Operator_Status_Name_ID_map.put("1",getResources().getString(R.string.pause));
            Equipment_Operator_Status_Name_ID_map.put("2",getResources().getString(R.string.quit));
            Equipment_Operator_Status_Name_ID_map.put("3",getResources().getString(R.string.material_requisition));
            Equipment_Operator_Status_Name_ID_map.put("4",getResources().getString(R.string.wait_material));
            Equipment_Operator_Status_Name_ID_map.put("5",getResources().getString(R.string.complete));
        }
        // for test
        //  taskId = 93L;

        getTaskEquipmentFromServerByTaskId();
        if (null == datas) {
            datas = new ArrayList<ObjectElement>();
        }

        // for test
        // taskId = 16L;

        getTaskAttachmentDataFromServerByTaskId();
    }

    private void initView() {

        mHolder.deviceCountTextView = (TextView) findViewById(R.id.device_count);
        mHolder.dealCountTextView = (TextView) findViewById(R.id.deal_count);

        findViewById(R.id.btn_right_action).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_title)).setText(getResources().getString(R.string.task_details));
        menuImageView = (ImageView) findViewById(R.id.btn_bar_left_action);
        if(RootUtil.rootStatus(TaskStatus,1)){
        menuImageView.setVisibility(View.VISIBLE);
        findViewById(R.id.btn_bar_left).setVisibility(View.VISIBLE);
        }else{
            menuImageView.setVisibility(View.GONE);
        }
        menuImageView.setOnClickListener(this);
        scrollview = (ScrollView) findViewById(R.id.scrollview_parent);
        mListview = (ScrollViewWithListView) findViewById(R.id.problem_count);
        noScrollgridview = (ExpandGridView) findViewById(R.id.picture_containt);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        {
            JsonObjectElement taskDetail = new JsonObjectElement(TaskDetail);
            ((TextView) findViewById(R.id.task_group)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.ORGANISE_NAME)));
            ((TextView) findViewById(R.id.task_ID)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_ID)));
            ((TextView) findViewById(R.id.task_start_time)).setText(DataUtil.getDate(DataUtil.isDataElementNull(taskDetail.get(Task.START_TIME))));
            ((TextView) findViewById(R.id.task_create_time)).setText(DataUtil.getDate(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT_TIME))));
            ((TextView) findViewById(R.id.task_creater)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.APPLICANT)));
            ((TextView) findViewById(R.id.task_description)).setText(DataUtil.isDataElementNull(taskDetail.get(Task.TASK_DESCRIPTION)));
            Main_person_in_charge_Operator_id = DataUtil.isDataElementNull(taskDetail.get("Operator_ID"));
        }
        adapter = new GridAdapter(this);
//        adapter.update1();
        noScrollgridview.setAdapter(adapter);
        noScrollgridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == dataList.size()) {
                    if(TaskStatus!=1){
                        ToastUtil.showToastLong(R.string.OnlyDealingTaskCanAddPhoto,mContext);
                        return;
                    }
                    new PopupWindows(mContext, noScrollgridview);
                } else {
                    ImageView image = (ImageView) arg1.findViewById(R.id.item_grida_image);
                    imageClick(image);

//                    Intent intent = new Intent(mContext,
//                            PhotoActivity.class);
//                    intent.putExtra("ID", arg2);
//                    startActivity(intent);
                }
            }
        });
        popMenuTaskDetail = new PopMenuTaskDetail(this, 300, TaskDetail,TaskClass) {

            @Override
            public void onEventDismiss() {

            }
        };
        popMenuTaskDetail.setIs_Main_person_in_charge_Operator_id(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),Main_person_in_charge_Operator_id));
        String[] mTitles = getResources().getStringArray(R.array.menu_list);

        popMenuTaskDetail.addItems(mTitles);

    }

    protected void onRestart() {
//        adapter.update1();
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
        //        private List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        private LayoutInflater inflater; // 视图容器
        private int selectedPosition = -1;// 选中的位置
        private boolean shape;

        private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

//        public void setData(List<Map<String, Object>> dataList) {
//            this.dataList = dataList;
//        }

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

//        public void update1() {
//            //loading1();
//        }

        public int getCount() {
            return dataList.size() + 1;
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

//        public void setSelectedPosition(int position) {
//            selectedPosition = position;
//        }
//
//        public int getSelectedPosition() {
//            return selectedPosition;
//        }

        /**
         * ListView Item设置
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            //final int coord = position;
            ViewHolder holder;

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

            if (position == dataList.size()) {
//                holder.image.setImageBitmap(BitmapFactory.decodeResource(
//                        getResources(), R.mipmap.icon_addpic_unfocused));

                // String addImageUrl =  "mipmap://" + R.mipmap.icon_addpic_unfocused;
                String imgUrl = "drawable://" + R.drawable.icon_addpic_unfocused;
                //addImageUrlToDataList(imgUrl);
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            } else {
                String imgUrl = (String) dataList.get(position).get("imageUrl");
                imageLoader.displayImage(imgUrl, holder.image, options,
                        animateFirstListener);
            }

            return convertView;
        }

        public class ViewHolder {
            public ImageView image;
        }

    }

    private void addImageUrlToDataList(String path) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("imageUrl", path);
        dataList.add(dataList.size(), dataMap);
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
//            Button bt2 = (Button) view
//                    .findViewById(R.id.item_popupwindows_Photo);
            Button bt3 = (Button) view
                    .findViewById(R.id.item_popupwindows_cancel);
            bt1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    photo();
                    dismiss();
                }
            });
//            bt2.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Intent intent = new Intent(TaskDetailsActivity.this,
//                            GetPicActivity.class);
//                    startActivity(intent);
//                    dismiss();
//                }
//            });
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

        try {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(dir, String.valueOf(System.currentTimeMillis())
                    + ".jpg");
            path = file.getPath();
            Uri imageUri = Uri.fromFile(file);
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            openCameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:{
                if (resultCode == -1) {
                    //Bimp.drr.add(path);

                    //将图片地址增加到图片列表
                    Bimp.drr.add(path);
                    String path = Bimp.drr.get(Bimp.max);
                    System.out.println(path);
                    try {
                        Bitmap bm = Bimp.revitionImageSize(path);
                        Bimp.bmp.add(bm);
                        String fileName = path.substring(
                                path.lastIndexOf("/") + 1,
                                path.lastIndexOf("."));
                        FileUtils.saveBitmap(mContext, bm, "" + fileName);
                        Bimp.max += 1;

                        //压缩目录的路径--在saveBitmap方法中写死了的
                        String SDPATH = mContext.getExternalFilesDir(null)
                                + "/btp/formats/";

                        addImageUrlToDataList("file://" + SDPATH + fileName + ".JPEG");
                        if (null != adapter) {
//                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    //在此上传图片到服务器;
                    submitPictureToServer(path);
                }
                break;}
            case Constants.REQUEST_CODE_EXCHANGE_ORDER:{
                if(requestCode==1){
                setResult(2);
                finish();
                }
                break;
            }
        }
    }

    /**
     * bitmap转为base64
     *
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
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void submitPictureToServer(String path) {
        try {
            Bitmap bitmap = Bimp.revitionImageSize(path);
            String base64 = bitmapToBase64(bitmap);
            HttpParams params = new HttpParams();
            if (TaskDetail != null) {
                JsonObjectElement jsonObjectElement = new JsonObjectElement(TaskDetail);
            }
           /*  params.put(Task.TASK_ID,jsonObjectElement.get(Task.TASK_ID).valueAsString());}
             else{
                 params.put(Task.TASK_ID,16);
             }
             params.put("TaskAttachment_ID",0);
             params.put("ImgBase64",base64);*/
            JsonObjectElement jsonObjectElement = new JsonObjectElement();
            jsonObjectElement.set(Task.TASK_ID, taskId);
            jsonObjectElement.set("TaskAttachment_ID", 0);
            jsonObjectElement.set("ImgBase64", base64);
            jsonObjectElement.set("AttachmentType", "jpg");

            //      jsonObjectElement.set("",);
        //    String t = SharedPreferenceManager.getLoginData(this);
       //     JsonObjectElement json = new JsonObjectElement(t);
       //     String operatorId = json.get("Operator_ID").valueAsString();

       //     jsonObjectElement.set("UploadOperator", operatorId);

            params.putJsonParams(jsonObjectElement.toJson());
            HttpUtils.post(this, "TaskAttachment", params, new HttpCallback() {
                @Override
                public void onFailure(int errorNo, String strMsg) {
                    super.onFailure(errorNo, strMsg);

                    Toast toast = Toast.makeText(TaskDetailsActivity.this, "上传图片失败,请检查网络", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                @Override
                public void onSuccess(String t) {
                    super.onSuccess(t);
                    JsonObjectElement json = new JsonObjectElement(t);
                    if(json.get("Success").valueAsBoolean()){

                        Toast toast = Toast.makeText(TaskDetailsActivity.this, "上传图片成功", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }else{

                        Toast toast = Toast.makeText(TaskDetailsActivity.this, "上传图片失败", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }




                }
            });
            //上传String
        } catch (IOException e) {

        }
    }


    private void getTaskEquipmentFromServerByTaskId() {

        if (null == taskId) {
            return;
        }
        showCustomDialog(R.string.loadingData);
        HttpParams params = new HttpParams();
        params.put("task_id", taskId.toString());
        params.put("pageSize",1000);
        params.put("pageIndex",1);
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        HttpUtils.get(mContext, "TaskDetailList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);

                if (t != null) {
                 //   JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                //    if (!jsonObjectElement.get("PageData").isNull()) {
                        ArrayElement jsonArrayElement = new JsonArrayElement(t);
                        if (jsonArrayElement != null && jsonArrayElement.size() > 0) {
                            datas.clear();
                            TaskDeviceIdList.clear();
                            int dealDeviceCount = 0;
                            boolean taskComplete=true;
                            for (int i = 0; i < jsonArrayElement.size(); i++) {
                                if(!DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status")).equals(STATUS_DONE)){
                                    taskComplete=false;
                                }
                                datas.add(jsonArrayElement.get(i).asObjectElement());
                               // TaskDeviceIdList.add(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)));
                                TaskDeviceIdList.add(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)));
                                Task_DeviceId_TaskEquipmentId.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                        DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("TaskEquipment_ID")));
                                String equipmentStatus = DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get("Status"));
                                Euqipment_ID_STATUS_map.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                        equipmentStatus);
                              if(jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList")!=null&&
                                      jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().size()>0) {
                                 HashMap<String,Integer> Equipment_OperatorID_Status=new HashMap<String, Integer>();
                                  for (int j = 0; j < jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().size(); j++) {
                                      ObjectElement json = jsonArrayElement.get(i).asObjectElement().get("TaskEquipmentOperatorList").asArrayElement().get(j).asObjectElement();
                                      Equipment_OperatorID_Status.put(DataUtil.isDataElementNull(json.get("Operator_ID")), Integer.valueOf(DataUtil.isDataElementNull(json.get("Status"))));
                                  }
                                  TaskEquipment_OperatorID_Status.put(DataUtil.isDataElementNull(jsonArrayElement.get(i).asObjectElement().get(Equipment.EQUIPMENT_ID)),
                                          Equipment_OperatorID_Status);
                              }
                                if (STATUS_DONE.equals(equipmentStatus)) {
                                    dealDeviceCount++;
                                }

                            }
                            if(popMenuTaskDetail!=null){
                            popMenuTaskDetail.setTaskComplete(taskComplete);
                            }
                            if (null != taskAdapter) {
//                            adapter.setData(dataList);
                                taskAdapter.notifyDataSetChanged();
                            }

                            deviceCountMap.put("deviceCount", String.valueOf(jsonArrayElement.size()));
                            deviceCountMap.put("dealCount", String.valueOf(dealDeviceCount));

                            //在这里刷新设备汇总数据
                            Message message = new Message();
                            message.what = MSG_UPDATE_DEVICE_SUM_INFO;
                            mHandler.sendMessage(message);
                        }
                    }
                getEquipmentListFail=false;
                dismissCustomDialog();
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {

                super.onFailure(errorNo, strMsg);

                dismissCustomDialog();
                getEquipmentListFail=true;
                Toast toast = Toast.makeText(TaskDetailsActivity.this, "获取设备列表失败，访问超时", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }

    private void getTaskAttachmentDataFromServerByTaskId() {
        if (null == taskId) {
            return;
        }

        HttpParams params = new HttpParams();
        // params.put("id", taskId.toString());
        //params.putHeaders("cookies",SharedPreferenceManager.getCookie(this));
        params.put("task_id",taskId.toString());
        HttpUtils.get(mContext, "TaskImgsList", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
               // Log.e("returnString", t);
                if (t != null) {
                    JsonObjectElement jsonObjectElement = new JsonObjectElement(t);
                    ArrayElement jsonArrayElement = jsonObjectElement.get("PageData").asArrayElement();
                    if (jsonArrayElement != null && jsonArrayElement.size() > 0) {
                        for (int i = 0; i < jsonArrayElement.size(); i++) {
                            Map<String, Object> dataMap = new HashMap<String, Object>();

                            String path = jsonArrayElement.get(i).asObjectElement().get("FileName").valueAsString();
                            addImageUrlToDataList(path);

                        }

                        //在这里刷新图片列表
//                        Message message = new Message();
//                        message.what = 1;
//                        handler.sendMessage(message);
                        if (null != adapter) {
//                            adapter.setData(dataList);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {

                // test_json();
                super.onFailure(errorNo, strMsg);
            }
        });
    }


    //刷nfc卡处理
    public void resolveNfcMessage(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
        if(TaskStatus!=1){
            ToastUtil.showToastLong(R.string.OnlyDealingTaskCanAddEquipment,this);
            return;
        }
            Parcelable tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String iccardID = NfcUtils.dumpTagData(tag);

            addTaskEquipment(iccardID);

//            MessageUtils.showToast(iccardID,this);

        }
    }

    private void addTaskEquipment(String iccardID) {
        //   ChangeEquipmentDialog changeEquipmentDialog=new ChangeEquipmentDialog(this,R.layout.change_equipment_status_dialog,R.style.MyDialog);
        //  changeEquipmentDialog.show();
        if(getEquipmentListFail){
            ToastUtil.showToastLong("重新获取设备列表中,请稍候",this);
            getTaskEquipmentFromServerByTaskId();
            return;
        }
//        postTaskEquipment("aaaa","0",0);
        String rawQuery = "SELECT * FROM Equipment WHERE  ICCardID ='" + iccardID + "'";
        ListenableFuture<DataElement> elemt = getSqliteStore().performRawQuery(rawQuery,
                EPassSqliteStoreOpenHelper.SCHEMA_EQUIPMENT, null);
        Futures.addCallback(elemt, new FutureCallback<DataElement>() {

            @Override
            public void onSuccess(DataElement dataElement) {

                if (dataElement != null && dataElement.isArray()
                        && dataElement.asArrayElement().size() > 0) {
                    final ObjectElement objectElement = dataElement.asArrayElement().get(0).asObjectElement();
                    //进行判断，若任务未有该设备号，添加
                  //  if(!TaskDeviceIdList.contains(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))){
                    if(!TaskDeviceIdList.contains(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))){
                    postTaskEquipment(objectElement.get(Equipment.EQUIPMENT_ID).valueAsString(),"0",0);}
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).equals(STATUS_DONE)){
                                    ToastUtil.showToastLong(R.string.CanNotChangeEquipmentStatus,mContext);
                                    return;
                                }
                                if(changeEquipmentDialog==null) {
                                    changeEquipmentDialog = new ChangeEquipmentDialog(TaskDetailsActivity.this, R.layout.dialog_equipment_status, R.style.MyDialog);
                                    changeEquipmentDialog.setDatas(String.valueOf(taskId), objectElement.get(Equipment.EQUIPMENT_ID).valueAsString(),
                                            Task_DeviceId_TaskEquipmentId.get(objectElement.get(Equipment.EQUIPMENT_ID).valueAsString()));
                                   // changeEquipmentDialog.setMainPersonInChargeOperatorId(Main_person_in_charge_Operator_id.equals(String.valueOf(getLoginInfo().getId())));
                                    changeEquipmentDialog.setMainPersonInChargeOperatorId(RootUtil.rootMainPersonInTask(String.valueOf(getLoginInfo().getId()),Main_person_in_charge_Operator_id));
                                    changeEquipmentDialog.setOnSubmitInterface(new dialogOnSubmitInterface() {
                                        @Override
                                        public void onsubmit() {
                                            getTaskEquipmentFromServerByTaskId();
                                        }
                                    });
                                    //设置当前操作员在设备中状态，默认为-1即未参与
//                                    int status=-1;
//                                    if(TaskEquipment_OperatorID_Status.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))!=null){
//                                    if(TaskEquipment_OperatorID_Status.get(
//                                            DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).containsKey(String.valueOf(getLoginInfo().getId()))){
//                                        status=TaskEquipment_OperatorID_Status.get(
//                                                DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).get(String.valueOf(getLoginInfo().getId()));
//                                    }
//                                    }
//                                    changeEquipmentDialog.setOperator_Status(status);
                                    int EquipmentStatus=-1;
                                    EquipmentStatus=Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                    changeEquipmentDialog.setEquipemntStatus(EquipmentStatus);
                                    changeEquipmentDialog.show();
                                }else {
                                    changeEquipmentDialog.setDatas(String.valueOf(taskId),
                                            objectElement.get(Equipment.EQUIPMENT_ID).valueAsString(),
                                            Task_DeviceId_TaskEquipmentId.get(objectElement.get(Equipment.EQUIPMENT_ID).valueAsString()));
//                                    int status=-1;
//                                    if(TaskEquipment_OperatorID_Status.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID)))!=null){
//                                        if(TaskEquipment_OperatorID_Status.get(
//                                                DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).containsKey(String.valueOf(getLoginInfo().getId()))){
//                                            status=TaskEquipment_OperatorID_Status.get(
//                                                    DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))).get(String.valueOf(getLoginInfo().getId()));
//                                        }
//                                    }
//                                    changeEquipmentDialog.setOperator_Status(status);
                                    int EquipmentStatus=-1;
                                    EquipmentStatus=Integer.valueOf(Euqipment_ID_STATUS_map.get(DataUtil.isDataElementNull(objectElement.get(Equipment.EQUIPMENT_ID))));
                                    changeEquipmentDialog.setEquipemntStatus(EquipmentStatus);
                               //     changeEquipmentDialog.setMainPersonInChargeOperatorId(Main_person_in_charge_Operator_id);
                                    if(!changeEquipmentDialog.isShowing()){
                                    changeEquipmentDialog.show();}
                                }
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "目前该设备没有机台号", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });


    }

    private void postTaskEquipment(String equipmentID, String TaskEquipment_ID, int status) {

        HttpParams params = new HttpParams();

        JsonObjectElement taskEquepment = new JsonObjectElement();
//创建任务提交数据：任务创建人，任务类型“T01”那些，几台号（数组），
        taskEquepment.set(Task.TASK_ID, taskId);
        //  taskDetail.set(Task.TASK_TYPE,TaskType);

        //若任务未有设备，则输入为0，表示添加
        taskEquepment.set("TaskEquipment_ID", 0);
        //若已有设备，申请状态变更
        taskEquepment.set("Equipment_ID", equipmentID);
        //taskEquepment.set("Equipment_ID", equipmentID);
        taskEquepment.set("Status",0);
       // taskEquepment.set("Operator_ID",getLoginInfo().getId());
        //taskEquepment.set();
        params.putJsonParams(taskEquepment.toJson());

        HttpUtils.post(this, "TaskEquipment", params, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                super.onSuccess(t);
                if(t!=null){
                ToastUtil.showToastLong(R.string.AddEquipmentSuccess,mContext);
                getTaskEquipmentFromServerByTaskId();
                }
            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
               ToastUtil.showToastLong(R.string.err_add_task_equipment,mContext);
                    }
                });
            }
        });
    }

    // 点击放大图片
    public void imageClick(View v) {
        Bitmap bmp = null;
        if (v instanceof LinearLayout) {
            LinearLayout tmpV = (LinearLayout) v;
            bmp = ((BitmapDrawable) tmpV.getBackground()).getBitmap();
        } else {
            ImageView image = (ImageView) v;
            bmp = ((BitmapDrawable) image.getDrawable()).getBitmap();
        }

        ShowBigImageActivity.saveTmpBitmap(bmp);
        Intent showBigImageIntent = new Intent(TaskDetailsActivity.this,
                ShowBigImageActivity.class);

        startActivity(showBigImageIntent);
    }

    //主线程中的handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {
                case MSG_UPDATE_DEVICE_SUM_INFO: {

                    //设备数数
                    mHolder.deviceCountTextView.setText(String.valueOf(deviceCountMap.get("deviceCount")));
                    //已处理数量
                    mHolder.dealCountTextView.setText(String.valueOf(deviceCountMap.get("dealCount")));

                    break;
                }

            }
        }

    };
    private void getTaskOperatorHistoryList(){
        HttpParams params=new HttpParams();
        params.put("taskEquipment_id",0);
        params.put("pageSize",10);
        params.put("pageIndex",1);
        HttpUtils.get(this, "TaskOperatorHistoryList", params, new HttpCallback() {
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
    private void getTaskEquipmentOperatorStatusTrail(){

    }
}
