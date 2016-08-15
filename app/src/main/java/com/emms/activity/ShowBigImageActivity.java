package com.emms.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout.LayoutParams;

import com.emms.R;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.polites.android.GestureImageView;


public class ShowBigImageActivity extends NfcActivity {
    static Bitmap tmpBitMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        setContentView(R.layout.show_big_image);
        
        Bitmap bitmap = getTmpBitmap();
        
        
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
 
        GestureImageView view = (GestureImageView)findViewById(R.id.bigImageShower);//GestureImageView--ͼƬ����Ŵ���С���
        view.setImageBitmap(bitmap);
        view.setLayoutParams(params);
        
        
        view.setOnClickListener(new OnClickListener() {
        	 
            @Override
            public void onClick(View v) {
            	cleanTmpBitmap();
                finish();
            }
        });
    }

	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onPause() {
		super.onPause();
	}

    public static void saveTmpBitmap(Bitmap bitmap){
        tmpBitMap = bitmap;
    }
    public static Bitmap getTmpBitmap(){
        return tmpBitMap;
    }
    public static void cleanTmpBitmap(){
        tmpBitMap = null;
    }

    @Override
    public void resolveNfcMessage(Intent intent) {

    }
}