package com.emms.httputils;

import android.app.Activity;
import android.content.Context;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.datastore_android_sdk.rxvolley.RxVolley;
import com.datastore_android_sdk.rxvolley.client.HttpCallback;
import com.datastore_android_sdk.rxvolley.client.HttpParams;
import com.datastore_android_sdk.rxvolley.client.ProgressListener;
import com.datastore_android_sdk.rxvolley.http.DefaultRetryPolicy;
import com.emms.R;
import com.emms.ui.KProgressHUD;
import com.emms.util.BuildConfig;
import com.emms.util.Md5Utils;
import com.emms.util.SharedPreferenceManager;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by jaffer.deng on 2016/6/3.
 *
 */

/*
Rxjava详情请看
https://github.com/kymjs/RxVolley/blob/master/RxVolley%E4%BD%BF%E7%94%A8%E6%8C%87%E5%8D%97.md
*/
public  class HttpUtils {

    public static void login(Context context,String userName,String passWord,HttpCallback callback ){

        HttpParams params = new HttpParams();
       params.put("UserName", userName);
        params.put("Password", Md5Utils.md5(passWord).toUpperCase());
       // params.put("UserName", "Max Ooi");
      // params.put("Password", "99295219CBAD91205AFD9A5629910AC2");
        params.put("AutoLogin", "true");
        params.put("PasswordEncrypt","true");
        params.putHeaders("Origin", "http://EMMSAPP");
        params.putHeaders("Referer", "http://EMMSAPP");
        RxVolley.setContext(context);
        RxVolley.post(BuildConfig.getServerAPIEndPoint(context)+"Token",params,callback);
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
        RxVolley.post(BuildConfig.getServerAPIEndPoint(context)+"Token",params,callback);
//        new RxVolley.Builder()
//                .url(BuildConfig.getConfigurationEndPoint())
//                .httpMethod(RxVolley.Method.POST) //default GET or POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
//                .contentType(RxVolley.ContentType.JSON)//default FORM or JSON
//                .params(params)
//                .retryPolicy(new DefaultRetryPolicy(25000,0,1f))
//                .callback(callback)
//                .encoding("UTF-8") //default
//                .doTask();
    }
    public static void post( final Context context,final String table, final HttpParams params,final HttpCallback callback){

        String cookie = SharedPreferenceManager.getCookie(context);
        if (cookie !=null) {
            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            params.putHeaders("Cookie",cookie);
            RxVolley.setContext(context);
                new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint(context) + table)
                    .httpMethod(RxVolley.Method.POST) //default GET or POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                    .contentType(RxVolley.ContentType.JSON)//default FORM or JSON
                    .params(params)
                    .retryPolicy(new DefaultRetryPolicy(25000,0,1f))
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
                        Toast.makeText(context,R.string.pleaseRelogin,Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                Toast.makeText(context,R.string.pleaseRelogin,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void get( final Context context,final String table, final HttpParams params,final HttpCallback callback){
        String cookie = SharedPreferenceManager.getCookie(context);
        if (cookie !=null) {
            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            params.putHeaders("Cookie", cookie);
            params.put("T", String.valueOf(new Date().getTime()));
            RxVolley.setContext(context);
            new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint(context) +table) //接口地址
                    //请求类型，如果不加，默认为 GET 可选项：
                    //POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                    .httpMethod(RxVolley.Method.GET)
                    //设置缓存时间: 默认是 get 请求 5 分钟, post 请求不缓存
                    //内容参数传递形式，如果不加，默认为 FORM 表单提交，可选项 JSON 内容
                    .contentType(RxVolley.ContentType.JSON)
                    .retryPolicy(new DefaultRetryPolicy(20000,0,1f))
                    .params(params) //上文创建的HttpParams请求参数集
                    //是否缓存，默认是 get 请求 5 缓存分钟, post 请求不缓存
                    //.shouldCache(true)
                    .callback(callback) //响应回调
                    .encoding("UTF-8") //编码格式，默认为utf-8
                    .doTask();  //执行请求操作
        }else {
            Toast.makeText(context,R.string.pleaseRelogin,Toast.LENGTH_SHORT).show();
        }
    }

    public static void download(Context context,String storeFilePath, String url, ProgressListener
            progressListener, HttpCallback callback){
        RxVolley.setContext(context);
        RxVolley.download(storeFilePath,url,progressListener,callback);
    }
    public static void delete( final Context context,final String table, final HttpParams params,final HttpCallback callback){

        String cookie = SharedPreferenceManager.getCookie(context);
        if (cookie !=null) {
            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            params.putHeaders("Cookie",cookie);
            RxVolley.setContext(context);
            new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint(context) +table) //接口地址
                    //请求类型，如果不加，默认为 GET 可选项：
                    //POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                    .httpMethod(RxVolley.Method.DELETE)
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
            Toast.makeText(context,R.string.pleaseRelogin,Toast.LENGTH_SHORT).show();
        }
    }

    public static void downloadData(File fileName, String m_url, final Context context) {
        String result="";
        OutputStream outputStream=null;
        try {

            final KProgressHUD hud=KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel(context.getResources().getString(R.string.DownloadDataBase))
                    .setCancellable(true).setAutoDismiss(true);
            //final boolean tag=true;
    /*        Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    if (tag){
                    hud.show();
                    tag=false;}
                }
            };

            ((Activity)context).runOnUiThread(runnable);*/

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                      Toast toast=Toast.makeText(context,R.string.DownloadDataBase,Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
           //URL url = new URL("https://cloud.linkgoo.cn/app/emms/EMMS.zip");
            URL url = new URL("https://edpazure.esquel.cn/apps/hrcampus/prod/EMMS.zip");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data");
            InputStream inputStream=conn.getInputStream();
            outputStream=new FileOutputStream(fileName);
            byte[] buffer=new byte[1024];
            int len;
            while((len=inputStream.read(buffer))!=-1){
                outputStream.write(buffer,0,len);}
            Log.e("正在下载","??");
            outputStream.flush();
            outputStream.close();
            conn.disconnect();
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast=Toast.makeText(context,"数据文件下载完毕",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
        //    hud.dismiss();

            upZipFile(fileName,fileName.getParentFile().getAbsolutePath(),context);
        } catch (Exception e) {
            System.out.println("error！" + e);
        }
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void upZipFile(File zipFile, String folderPath, final Context context) throws ErrnoException,DataFormatException,IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        InputStream in = null;
        OutputStream out = null;
        try {
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                in = zf.getInputStream(entry);
                String str = folderPath + File.separator + entry.getName();
                str = new String(str.getBytes("8859_1"), "UTF-8");
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                out = new FileOutputStream(desFile);
                byte buffer[] = new byte[1024];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
            }
         }catch (Exception e){
          throw new IOException();
        } finally {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
        }
    }
    public static void getWithoutCookies( final Context context,final String table, final HttpParams params,final HttpCallback callback){

            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            params.put("T", String.valueOf(new Date().getTime()));
            RxVolley.setContext(context);
            new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint(context) +table) //接口地址
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
        }

    public static void getWithoutCookiesByUrl( final Context context,final String url, final HttpParams params,final HttpCallback callback){

        params.putHeaders("Origin", "http://EMMSAPP");
        params.putHeaders("Referer", "http://EMMSAPP");
        params.put("T", String.valueOf(new Date().getTime()));
        RxVolley.setContext(context);
        new RxVolley.Builder()
                .url(url) //接口地址
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
    }


    public static void postWithoutCookie( final Context context,final String table, final HttpParams params,final HttpCallback callback){

            params.putHeaders("Origin", "http://EMMSAPP");
            params.putHeaders("Referer", "http://EMMSAPP");
            RxVolley.setContext(context);
            new RxVolley.Builder()
                    .url(BuildConfig.getServerAPIEndPoint(context) + table)
                    .httpMethod(RxVolley.Method.POST) //default GET or POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
                    .contentType(RxVolley.ContentType.JSON)//default FORM or JSON
                    .params(params)
                    .retryPolicy(new DefaultRetryPolicy(25000,0,1f))
                    .callback(callback)
                    .encoding("UTF-8") //default
                    .doTask();
    }
}
