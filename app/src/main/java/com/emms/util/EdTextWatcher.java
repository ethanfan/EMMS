package com.emms.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * Created by jaffer.deng on 2016/6/13.
 */
public class EdTextWatcher implements TextWatcher {

    private EditText mEditText;
    private ArrayList<String> mDatas;
    public EdTextWatcher(EditText editText , ArrayList<String> datas) {
        this.mEditText = editText;
        this.mDatas = datas ;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
