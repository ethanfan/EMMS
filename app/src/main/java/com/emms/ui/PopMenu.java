package com.emms.ui;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.emms.R;
import com.datastore_android_sdk.datastore.ObjectElement;

import java.util.ArrayList;

public class PopMenu {
	private ArrayList<ObjectElement> itemList;
	private Context context;
	private PopupWindow popupWindow;
	private ListView listView;
	private PopAdapter popAdapter;
	private String itemName;
	// private OnItemClickListener listener;
	public PopMenu(Context context,int width) {
		// TODO Auto-generated constructor stub
		this.context = context;

		itemList = new ArrayList<ObjectElement>(5);

		View view = LayoutInflater.from(context)
				.inflate(R.layout.dropmenu, null);

		popAdapter =new PopAdapter();
		// 设置 listview
		listView = (ListView) view.findViewById(R.id.listView);
		listView.setAdapter(popAdapter);
		listView.setFocusableInTouchMode(true);
		listView.setFocusable(true);

//		popupWindow = new PopupWindow(view, 254, LayoutParams.WRAP_CONTENT);
		popupWindow = new PopupWindow(view, width, LayoutParams.WRAP_CONTENT);

		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
	}

	public void refreshData(){
		popAdapter.notifyDataSetChanged();
	}
	// 设置菜单项点击监听器
	public void setOnItemClickListener(OnItemClickListener listener) {
		// this.listener = listener;
		listView.setOnItemClickListener(listener);

	}



	// 批量添加菜单项
	public void addItems(ArrayList items,String itemName) {
		this.itemList =items;
		this.itemName = itemName;
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
						R.layout.pomenu_item, null);
				holder = new ViewHolder();

				convertView.setTag(holder);

				holder.groupItem = (TextView) convertView
						.findViewById(R.id.textView);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.groupItem.setText(itemList.get(position).get(itemName).valueAsString());

			return convertView;
		}

		private final class ViewHolder {
			TextView groupItem;
		}
	}
}
