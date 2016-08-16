package com.handmark.pulltorefresh.library.internal;

import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Administrator on 2016/8/17.
 */
public interface AdapterOnSlideListener {
    public void onSlide(AdapterView<?> parent, View view, int position, long id);
}
