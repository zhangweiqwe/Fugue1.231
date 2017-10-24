package cn.wsgwz.tun.gravity.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.prefs.Preferences;

import cn.wsgwz.tun.ActivityMain;
import cn.wsgwz.tun.R;
import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;

/**
 * Created by Administrator on 2017/6/12.
 */

public class UpdateHelper {
    private static final String TAG = UpdateHelper.class.getSimpleName();
    private ApkInfo apkInfo;
    public void check(final Context context, final TextView updateBn){
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(context.getString(R.string.check_update));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
           progressDialog.dismiss();
        }
        final int versionCode = packInfo.versionCode;
        final String versionName = packInfo.versionName;

        //progressDialog.setButton();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1003:
                        apkInfo = (ApkInfo) msg.obj;
                        break;
                    case 1000:
                        progressDialog.dismiss();
                        AlertDialog.Builder builder0 = new AlertDialog.Builder(context);
                        ApkInfo apkInfo1000  = (ApkInfo) msg.obj;
                        builder0.setTitle(apkInfo1000.apkName+"\n当前版本:"+versionName+"("+versionCode+")");
                        builder0.setMessage(apkInfo1000.description);
                        builder0.setPositiveButton(context.getString(R.string.update), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(apkInfo==null||apkInfo.downloadPath==null){
                                    return;
                                }
                                Toast.makeText(context,context.getString(R.string.background_update),Toast.LENGTH_SHORT).show();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String errorStr;
                                        try {
                                            URL url = new URL(apkInfo.downloadPath);
                                            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));

                                            URLConnection urlConnection=null;
                                            if(ServiceTun.alreadyStart){
                                                urlConnection = url.openConnection(proxy);
                                            }else {
                                                urlConnection = url.openConnection();
                                            }
                                            urlConnection.setUseCaches(false);

                                            InputStream in = urlConnection.getInputStream();
                                            File file = new File(Const.MAIN_FOLDER_PATH,apkInfo.apkName);
                                            File fileParent = file.getParentFile();
                                            if(!fileParent.exists()){fileParent.mkdirs();};
                                            if(file.exists()){
                                                file.delete();
                                            }
                                            int contentLength = urlConnection.getContentLength();
                                            FileOutputStream out = new FileOutputStream(file);
                                            byte[] buffer = new byte[409600];
                                            long l1=0;
                                            int len;
                                            Message msg = Message.obtain();
                                            msg.obj = "开始下载！ 共计"+contentLength/(1024)+"KB";
                                            msg.what = 2005;
                                            sendMessage(msg);
                                            NumberFormat numberFormat = NumberFormat.getInstance();
                                            numberFormat.setMaximumFractionDigits(2);//最大//n. 分数；部分；小部分；稍微//n. [计] 数字；手指；[解剖] 足趾（digit的复数）


                                            while ((len=in.read(buffer))!=-1){
                                                out.write(buffer,0,len);
                                                l1+=len;
                                                String result = numberFormat.format((float) l1 / (float) contentLength * 100);
                                                Message msg0 = Message.obtain();
                                                msg0.obj = result+"%";
                                                msg0.what = 2003;
                                                sendMessage(msg0);
                                            }
                                            out.flush();
                                            out.close();
                                            sendEmptyMessage(2000);
                                            return;
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                            errorStr = e.getMessage().toString();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            errorStr = e.getMessage().toString();
                                        }
                                        Message msg =Message.obtain();
                                        msg.what = 2001;
                                        msg.obj = errorStr;
                                        sendMessage(msg);
                                    }
                                }).start();

                            }
                        });
                        builder0.setNegativeButton(context.getString(R.string.no),null);
                        builder0.create().show();
                        break;
                    case 1001:
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle((String)msg.obj);
                        builder1.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder1.create().show();
                        break;

                    case 2000:
                        updateBn.setClickable(true);

                        final File file2000 = new File(Const.MAIN_FOLDER_PATH,apkInfo.apkName);
                        LogAdapter.addItem(context.getString(R.string.download_finish), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                install(file2000,context);
                            }
                        });
                        install(file2000,context);

                        break;
                    case 2001:
                        updateBn.setClickable(true);
                        String str2001 = (String)msg.obj;
                        Toast.makeText(context,str2001,Toast.LENGTH_LONG).show();
                        LogAdapter.addItem(str2001,null);
                        break;
                    case 2003:
                        LogAdapter.addItem((String) msg.obj,null);
                        break;
                    case 2005:
                        LogAdapter.addItem((String) msg.obj,null);
                        updateBn.setClickable(false);
                        break;
                }
            }
        };




            new Thread(new Runnable() {
                @Override
                public void run() {
                    String error = "未知错误";
                    try {
                        URL url = new URL("https://raw.githubusercontent.com/zhangweiqwe/AndroidProxy/master/version.json");
                        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));

                        URLConnection urlConnection=null;
                        if(ServiceTun.alreadyStart){
                            urlConnection = url.openConnection(proxy);
                        }else {
                            urlConnection = url.openConnection();
                        }
                        urlConnection.setUseCaches(false);

                        urlConnection.setUseCaches(false);
                        int len;
                        byte[] buffer = new byte[1024];
                        InputStream in = urlConnection.getInputStream();
                        StringBuilder sb = new StringBuilder();
                        while ((len = in.read(buffer))!=-1){
                            sb.append(new String(buffer,0,len));
                        }


                        JSONObject jsonObject = new JSONObject(sb.toString());
                        int versionCode1 = jsonObject.optInt("versionCode");
                        String description = jsonObject.optString("description");
                        String noUpdatedescription = jsonObject.optString("noUpdatedescription");
                        String downloadPath = jsonObject.optString("downloadPath");
                        String apkName = jsonObject.optString("apkName");

                        ApkInfo apkInfo = new ApkInfo( versionCode1,  description,  noUpdatedescription,  downloadPath,  apkName);
                        Message msgz = Message.obtain();
                        msgz.what = 1003;
                        msgz.obj = apkInfo;
                        handler.sendMessage(msgz);
                        if(versionCode1>versionCode){
                            Message msg = Message.obtain();
                            msg.what = 1000;
                            msg.obj = apkInfo;
                            handler.sendMessage(msg);
                        }else {
                            Message msg = Message.obtain();
                            msg.what = 1001;
                            msg.obj = noUpdatedescription;
                            handler.sendMessage(msg);
                        }
                        return;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        error  = e.getMessage().toString();
                    } catch (IOException e) {
                        e.printStackTrace();
                        error  = e.getMessage().toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        error  = e.getMessage().toString();
                    }

                    Message msg = Message.obtain();
                    msg.what = 1001;
                    msg.obj = error;
                    handler.sendMessage(msg);

                }
            }).start();


    }
    class ApkInfo{
        int versionCode;
        String description;
        String noUpdatedescription;
        String downloadPath;
        String apkName;

        public ApkInfo(int versionCode, String description, String noUpdatedescription, String downloadPath, String apkName) {
            this.versionCode = versionCode;
            this.description = description;
            this.noUpdatedescription = noUpdatedescription;
            this.downloadPath = downloadPath;
            this.apkName = apkName;
        }
    }

    private void install(File file,Context context){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri = FileProvider.getUriForFile(context, "cn.wsgwz.html",file);
            Log.d(TAG,apkUri.toString());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
