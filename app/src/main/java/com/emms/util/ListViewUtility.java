package com.emms.util;

	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.Adapter;
	import android.widget.GridView;
	import android.widget.ListAdapter;
	import android.widget.ListView;

	public class ListViewUtility {
		public static void setListViewHeightBasedOnChildren(ListView listView) {
			// 获取ListView对应的Adapter
			ListAdapter listAdapter = listView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0); // 计算子项View 的宽高
				totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
			// listView.getDividerHeight()获取子项间分隔符占用的高度
			// params.height最后得到整个ListView完整显示需要的高度
			listView.setLayoutParams(params);
		}
		public static void setGridViewHeightBasedOnChildren(GridView gridView,int RowNum) {
			// 获取ListView对应的Adapter
			ListAdapter listAdapter = gridView.getAdapter();
			if (listAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			if(listAdapter.getCount()>0){
				View listItem = listAdapter.getView(0, null, gridView);
				listItem.measure(0, 0); // 计算子项View 的宽高
				totalHeight=listItem.getMeasuredHeight()*RowNum;
			}
			ViewGroup.LayoutParams params = gridView.getLayoutParams();
			params.height = totalHeight;
			// listView.getDividerHeight()获取子项间分隔符占用的高度
			// params.height最后得到整个ListView完整显示需要的高度
			gridView.setLayoutParams(params);
		}
		public static int getListViewHeight(ListView listView){
			ListAdapter listAdapter=listView.getAdapter();
			if(listAdapter==null){return 0;}
			int totalHeight=0;
			for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
				View listItem = listAdapter.getView(i, null, listView);
				listItem.measure(0, 0); // 计算子项View 的宽高
				totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
			}
			return totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		}
	}


