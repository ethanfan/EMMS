package com.emms.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.emms.ui.KProgressHUD;
import com.emms.ui.LoadingDialog;

/**
 * Created by Administrator on 2016/8/4.
 *
 */
public class BaseFragment extends Fragment{
    private KProgressHUD hud=null;
    private LoadingDialog loadingDialog=null;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    public KProgressHUD initCustomDialog(int resId) {
        if(hud==null){
        hud=KProgressHUD.create(getActivity());}
        hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(getResources().getString(resId))
                .setCancellable(true);
        return  hud;
    }
    public void showCustomDialog(int resId){
        if(Build.VERSION.SDK_INT>19) {
            initCustomDialog(resId);
            if (hud != null && !hud.isShowing()) {
                hud.show();
            }
        }else {
            if(loadingDialog==null) {
                loadingDialog = new LoadingDialog(getActivity());
            }
            if(!loadingDialog.isShowing()){
                loadingDialog.show();
            }
        }
    }
    public void dismissCustomDialog(){
        if(Build.VERSION.SDK_INT>19) {
            if (hud != null && hud.isShowing()) {
                hud.dismiss();
            }
        }else {
            if(loadingDialog!=null&&loadingDialog.isShowing()){
                loadingDialog.dismiss();
            }
        }
    }
}
