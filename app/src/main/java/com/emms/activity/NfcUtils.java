package com.emms.activity;

import android.nfc.Tag;
import android.os.Parcelable;

/**
 * Created by lenovo on 2016/7/16.
 *
 */
public class NfcUtils {


    public static String dumpTagData(Parcelable p) {

        Tag tag = (Tag) p;
        byte[] id = tag.getId();
        return getDec(id) + "";
    }

    public static long getDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }


}
