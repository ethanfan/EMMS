package com.emms.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emms.R;
import com.emms.activity.SubTaskManageActivity;
import com.emms.httputils.HttpUtils;
import com.emms.schema.Task;
import com.emms.util.ListViewUtility;

import java.util.ArrayList;

public abstract class PopMenuTaskDetail {
	private ArrayList<String> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	private PopAdapter popAdapter;
    private Long TaskId;
	// private OnItemClickListener listener;
	public PopMenuTaskDetail(Context context, int width) {
		// TODO Auto-generated constructor stub
		this.context = context;

		itemList = new ArrayList<String>(5);
		View view = LayoutInflater.from(context)
				.inflate(R.layout.popmenu, null);
		final RelativeLayout layout=(RelativeLayout)view.findViewById(R.id.popup_view_cont);
		layout.setBackgroundColor(Color.argb(80,0,0,0));
		layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		popAdapter =new PopAdapter();
		// 设置 listview
		listView = (ListView) view.findViewById(R.id.listView);
		//listView.setPaddingRelative();
		listView.setBackgroundColor(Color.WHITE);
		listView.setAdapter(popAdapter);
		listView.setFocusableInTouchMode(true);
		listView.setFocusable(true);
		setOnItemClickListener();
      //  ListViewUtility.setListViewHeightBasedOnChildren(listView);
//		popupWindow = new PopupWindow(view, 254, LayoutParams.WRAP_CONTENT);

		popupWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				onEventDismiss();
			}
		});
	}

	public abstract void onEventDismiss();
	public void refreshData(){
		popAdapter.notifyDataSetChanged();
	}
	// 设置菜单项点击监听器
	public void setOnItemClickListener() {
		// this.listener = listener;
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0) {
					WorkloadInput();
				}else if (position == 1) {

				}else if(position==2){

				}else if(position==3){

				}else if(position==4){

				}else if(position==5){
					SubTaskManage();
				}else if(position==6){

				}
			}
		});

	}

	// 批量添加菜单项
	public void addItems(String[] items) {
		for (String s : items)
			itemList.add(s);
	}

	// 批量添加菜单项
	public void addItems(ArrayList items) {
		this.itemList =items;
	}

	// 单个添加菜单项
	public void addItem(String item) {
		itemList.add(item);
	}

	// 下拉式 弹出 pop菜单 parent 右下角
	public void showAsDropDown(View parent) {
		popupWindow.showAsDropDown(parent,
				10,
				// 保证尺寸是根据屏幕像素密度来的
				context.getResources().getDimensionPixelSize(
						R.dimen.popmenu_yoff));

		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}


	public boolean isShowing(){

		return 	popupWindow.isShowing();
	}
	// 隐藏菜单
	public void dismiss() {
		popupWindow.update();
		popupWindow.dismiss();
	}

	// 适配器
	private final class PopAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.popuwindow_task, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.textView);
				holder.imageView = (ImageView) convertView.findViewById(R.id.menu_detail);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.groupItem.setText(itemList.get(position));
			Drawable img  = context.getResources().getDrawable(R.mipmap.more_input);

			if (0 ==position){
				img =context.getResources().getDrawable(R.mipmap.more_input);
			}else if (1 == position){
				img =context.getResources().getDrawable(R.mipmap.more_finish);
			}else if (2 == position){
				img =context.getResources().getDrawable(R.mipmap.more_scan);
			}else if (3 == position){
				img =context.getResources().getDrawable(R.mipmap.more_single_turn);
			}else if (4 == position){
				img =context.getResources().getDrawable(R.mipmap.more_invitation);
			}else if (5 == position){
				img =context.getResources().getDrawable(R.mipmap.sub_task_management);
			}else if (6 == position){
				img =context.getResources().getDrawable(R.mipmap.failure_summary);
			}
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
//
//			holder.groupItem.setCompoundDrawables(img,null,null,null);
			holder.imageView.setImageDrawable(img);
			return convertView;
		}

		private final class ViewHolder {
			TextView groupItem;
			ImageView imageView;
		}
	}
	private void WorkloadInput(){

	}
    private void MissionComplete(){

	}
	private void Scan(){

	}
	private void ExChangeOrder(){

	}
	private void InviteHelp(){

	}
	private void SubTaskManage(){
		Intent intent=new Intent(context, SubTaskManageActivity.class);
		//intent.putExtra(Task.TASK_ID,TaskId);
		context.startActivity(intent);
	}
	private void FailureSummary(){

	}
	private void setTaskIdFromActivity(Long taskId){
		this.TaskId=taskId;
	}

}
