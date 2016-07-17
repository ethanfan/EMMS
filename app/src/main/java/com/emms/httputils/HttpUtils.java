package com.emms.httputils;

import android.content.Context;
import android.widget.Toast;

import com.emms.util.BuildConfig;
import com.emms.util.SharedPreferenceManager;
import com.datastore_android_sdk.rxvolley.RxVolley;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.client.ProgressListener;

import java.util.Map;


/**
 * Created by jaffer.deng on 2016/6/3.
 */

/*
Rxjava详情请看
https://github.com/kymjs/RxVolley/blob/master/RxVolley%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97.md
*/
public  class HttpUtils {

    public static void login(Context context,String userName,String passWord,HttpCallback callback ){

        HttpParams params = new HttpParams();
       params.put("UserName", userName);
        params.put("Password", passWord);
       // params.put("UserName", "Max Ooi");
      // params.put("Password", "99295219CBAD91205AFD9A5629910AC2");
        params.put("AutoLogin", "true");
        params.put("PasswordEncrypt","true");
        params.putHeaders("Origin", "http://EMMSAPP");
        params.putHeaders("Referer", "http://EMMSAPP");
        RxVolley.setContext(context);
        RxVolley.post(BuildConfig.getConfigurationEndPoint(),params,callback);
    }

    public static void getCookie(final Context context,HttpCallback callback){
        String userName = SharedPreferenceManager.getUserName(context);
        String passWord =SharedPreferenceManager.getPassWord(context);

        HttpParams params = new HttpParams();
        params.put("UserName", userName);
        params.put("Password", passWord);
        params.put("AutoLogin", "true");

        params.putHeaders("Origin", "http://EMMSAPP");
        params.putHeaders("Referer", "http://EMMSAPP");
        RxVolley.setContext(context);
        RxVolley.post(BuildConfig.getConfigurationEndPoint(),params,callback);
    }
    public static void post( final Context context,final String table, final HttpParams params,final HttpCallback callback){

        String cookie = SharedPreferenceManager.getCookie(context);
        if (cookie !=null) {
            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            params.putHeaders("Cookie",cookie);
            RxVolley.setContext(context);
            new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint() + table)
                    .httpMethod(RxVolley.Method.POST) //default GET or POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                    .contentType(RxVolley.ContentType.JSON)//default FORM or JSON
                    .params(params)
                    .callback(callback)
                    .encoding("UTF-8") //default
                    .doTask();
        }else {
            String  userName =SharedPreferenceManager.getUserName(context);
            String  passWord = SharedPreferenceManager.getPassWord(context);
            if (null !=userName &&  null != passWord) {
                getCookie(context, new HttpCallback() {
                    @Override
                    public void onSuccess(Map<String, String> headers, byte[] t) {
                        super.onSuccess(headers, t);
                        if (!headers.isEmpty()) {
                            SharedPreferenceManager.setCookie(context, headers.get("Set-Cookie"));
                            post(context, table, params, callback);
                        }
                    }

                    @Override
                    public void onFailure(int errorNo, String strMsg) {
                        super.onFailure(errorNo, strMsg);
                        Toast.makeText(context,"请重新登录",Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(context,"请重新登录",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void get( final Context context,final String table, final HttpParams params,final HttpCallback callback){

        String cookie = SharedPreferenceManager.getCookie(context);
        if (cookie !=null) {
            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            params.putHeaders("Cookie",cookie);
            RxVolley.setContext(context);
            new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint() +table) //接口地址
                            //请求类型，如果不加，默认为 GET 可选项：
                            //POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                    .httpMethod(RxVolley.Method.GET)
                    //设置缓存时间: 默认是 get 请求 5 分钟, post 请求不缓存
                   // .cacheTime(6)
                            //内容参数传递形式，如果不加，默认为 FORM 表单提交，可选项 JSON 内容
                    .contentType(RxVolley.ContentType.JSON)
                    .params(params) //上文创建的HttpParams请求参数集
                            //是否缓存，默认是 get 请求 5 缓存分钟, post 请求不缓存
                    //.shouldCache(true)
                    .callback(callback) //响应回调
                    .encoding("UTF-8") //编码格式，默认为utf-8
                    .doTask();  //执行请求操作
        }else {
            Toast.makeText(context,"请重新登录",Toast.LENGTH_SHORT).show();
        }
    }

    public static void download(Context context,String storeFilePath, String url, ProgressListener
            progressListener, HttpCallback callback){
        RxVolley.setContext(context);
        RxVolley.download(storeFilePath,url,progressListener,callback);
    }


}
