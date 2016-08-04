package com.emms.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.emms.ui.KProgressHUD;

/**
 * Created by Administrator on 2016/8/4.
 */
public class BaseFragment extends Fragment{
    private KProgressHUD hud=null;
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
        initCustomDialog(resId);
        if(hud!=null&&!hud.isShowing()){
            hud.show();
        }
    }
    public void dismissCustomDialog(){
        if(hud!=null&&hud.isShowing()){
            hud.dismiss();
        }
    }
}
