package cn.wsgwz.tun.gravity.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.io.File;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;
import cn.wsgwz.tun.gravity.core.ConnectHelper;
import cn.wsgwz.tun.gravity.core.Match;
import cn.wsgwz.tun.gravity.encryption.SecurityUtils;

/**
 * Created by Administrator on 2017/6/28.
 */

public class QRConfigHelper {
    private static final int MSG_WHAT_SAVE_SUCCESS=1000;
    private static final int MSG_WHAT_SAVE_ERROR=1001;

    public interface OnConfigImportStateChangeListenner{
        void success(File file);
        void error(String msg);
    }
    private QRConfigHelper(){};
    private static QRConfigHelper qrConfigHelper;
    public static final QRConfigHelper getInstance(){
        if(qrConfigHelper==null){
            synchronized (QRConfigHelper.class){
                qrConfigHelper  = new QRConfigHelper();
            }
        }
        return qrConfigHelper;
    }
    public void saveConfig(final String name, final String aesStr, final Context context, final OnConfigImportStateChangeListenner onConfigImportStateChangeListenner){
        if(name==null||aesStr==null||context == null){
            return;
        }

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case MSG_WHAT_SAVE_SUCCESS:
                        File file = (msg.obj!=null&&msg.obj instanceof File?(File) msg.obj:null);
                        if(onConfigImportStateChangeListenner!=null){
                            onConfigImportStateChangeListenner.success(file);
                        }
                        break;
                    case MSG_WHAT_SAVE_ERROR:
                        if(onConfigImportStateChangeListenner!=null){
                            onConfigImportStateChangeListenner.error(msg.obj==null?null:(String)msg.obj);
                        }
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str = aesStr;

                    //SecurityUtils.getInstance().decrypt(aesStr)
                    ConfigHelper.getInstance().init();
                    File file = new File(Const.MAIN_FOLDER_PATH+"/"+name+Const.Config.CONFIG_FILE_POSTFIX);
                    long timeStart = System.nanoTime();
                    while(file.exists()){
                        if(timeStart+2000000<System.nanoTime()){
                            file.delete();
                            file = new File(Const.MAIN_FOLDER_PATH+"/"+name+Const.Config.CONFIG_FILE_POSTFIX);
                            break;
                        }
                        file = new File(Const.MAIN_FOLDER_PATH+"/"+name+(int)(Math.random()*10000+1)+Const.Config.CONFIG_FILE_POSTFIX);
                    }
                    file.createNewFile();
                    if(ConfigHelper.getInstance().saveConfigFile(file,str)){
                        final File finalFile = file;
                        LogAdapter.addItem(context.getString(R.string.import_qrconfig_success), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ConfigHelper.getInstance().editConfig(context, finalFile,false);
                            }
                        });

                        Message msg = Message.obtain();
                        msg.what=MSG_WHAT_SAVE_SUCCESS;
                        msg.obj = finalFile;
                        handler.sendMessage(msg);
                    }else {
                        Message msg = Message.obtain();
                        msg.what=MSG_WHAT_SAVE_ERROR;
                        msg.obj = "储存出错";
                        handler.sendMessage(msg);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    String s = e.getMessage().toString();
                    LogAdapter.addItem(context.getString(R.string.import_qrconfig_error)+"\t"+s,null);
                    Message msg = Message.obtain();
                    msg.what=MSG_WHAT_SAVE_ERROR;
                    msg.obj = "储存出错";
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }
}
