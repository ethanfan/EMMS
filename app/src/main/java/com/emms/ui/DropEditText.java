package com.emms.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.emms.R;
import com.emms.adapter.MenuAdapter;

import java.util.ArrayList;

public  class DropEditText extends FrameLayout implements View.OnClickListener{
	private EditText mEditText;  // 输入框
	private ImageView mDropImage; // 右边的图片按钮

	private  MenuAdapter mAdapter;
	private int mDrawableLeft;
	private int mDropMode; // flow_parent or wrap_content
	private String mHit;
	private int mTexthitColor = 999999;
	public Context mContext;
	public ArrayList datas;
	public DropEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public DropEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		LayoutInflater.from(context).inflate(R.layout.edit_layout, this);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropEditText, defStyle, 0);
		mDrawableLeft = ta.getResourceId(R.styleable.DropEditText_drawableRight, R.mipmap.ic_launcher);
		mDropMode = ta.getInt(R.styleable.DropEditText_dropMode, 0);
		mHit = ta.getString(R.styleable.DropEditText_hint);
		mTexthitColor = ta.getColor(R.styleable.DropEditText_hintColor,mTexthitColor);
		ta.recycle();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		mEditText = (EditText) findViewById(R.id.dropview_edit);
		mDropImage = (ImageView) findViewById(R.id.dropview_image);
		
		mEditText.setSelectAllOnFocus(true);
		mDropImage.setImageResource(mDrawableLeft);
	
		if(!TextUtils.isEmpty(mHit)) {
			mEditText.setHint(mHit);
			mEditText.setHintTextColor(mTexthitColor);
		}
		
		mDropImage.setOnClickListener(this);
//		mPopView.setOnItemClickListener(this);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		// 如果布局发生改
		// 并且dropMode是flower_parent
		// 则设置ListView的宽度
		if(changed && 0 == mDropMode) {

		}
	}

	public void setDatas(Context context,int width,ArrayList datas) {
		popMenu = new PopMenu(context,width);
		this.datas = datas;
		popMenu.addItems(datas);
		popMenu.setOnItemClickListener(popmenuItemClickListener);
	}
	/**
	 * 获取输入框内的内容
	 * @return String content
	 */
	public String getText() {
		return mEditText.getText().toString();
	}

	public PopMenu popMenu;
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.dropview_image) {
			if(datas !=null && popMenu !=null){
				if (datas.size()>0) {
					popMenu.showAsDropDown(this);
					adjust();
				}
			}

		}
	}
	private void adjust(){
		InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
	}
	// 弹出菜单监听器
	OnItemClickListener popmenuItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			mEditText.setText(datas.get(position).toString());
			popMenu.dismiss();
		}
	};

	public EditText getmEditText(){
		return mEditText;
	}
}
