package com.emms.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.emms.R;
/** 自定义对话框
 * Created by laomingfeng on 2016/5/24.
 */
public class CustomDialog extends Dialog {
    private final static int default_width = 160; //默认宽度
    private final static int default_height = 120;//默认高度
    private CustomDialog dialog=this;
    private ListView listView;
    private Context context;
    private Button cancle_button;
    private RelativeLayout relativelayout;
    private RelativeLayout IknowButtonLayout;
    public CustomDialog(Context context, int layout, int style) {
        this(context, default_width, default_height, layout, style);
    }

    public CustomDialog(Context context, int width, int height, int layout, int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
       // params.gravity = Gravity.CENTER;
       // window.setAttributes(params);
      //  cancle_button = (Button) findViewById(R.id.cannel_button);
        cancle_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }


}