package com.emms.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.datastore_android_sdk.rxvolley.RxVolley;
import com.emms.R;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2016/9/5.
 */
public class EmptyLayout extends RelativeLayout{
    private ViewGroup v;
    private Method retry;
    //private Method back;
    private Object[] retryParams;
    //private Object[] backParams;
    public EmptyLayout(Context context) {
        super(context);
        init(context);
    }

    public EmptyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EmptyLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public EmptyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public EmptyLayout(Context context, ViewGroup parent,Method method1,Object[] method1params){
        super(context);
        retry=method1;
        //back=method2;
        retryParams=method1params;
        //backParams=method1params2;
        parent.addView(init(context));
    }
    private EmptyLayout init(final Context context){
        View view=inflate(context, R.layout.emptyview,v);
        view.findViewById(R.id.retry).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(retry!=null){
                    try {
                        retry.invoke(context.getClass(),retryParams);
                    }catch (Exception e){
                     Log.e("methedException","retry");
                    }
                }
            }
        });
        view.findViewById(R.id.back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).finish();
            }
        });
        this.addView(view);
        return this;
    }
}
