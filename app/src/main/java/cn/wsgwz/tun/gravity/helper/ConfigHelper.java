package cn.wsgwz.tun.gravity.helper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.activity.EditConfigActivity;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;
import cn.wsgwz.tun.gravity.core.ConfigJson;

/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class ConfigHelper {
    private static final String LOG_TAG = ConfigHelper.class.getSimpleName();

    private File mainFolder ;
    private ConfigHelper(){init();};
    private static ConfigHelper configHelper;
    private Thread getRemoteConfigsThread;
    public static final ConfigHelper getInstance(){
        if(configHelper==null){
            synchronized (ConfigHelper.class){
                if(configHelper==null){
                    configHelper = new ConfigHelper();
                }
            }
        }
        return configHelper;
    }
    public void init(){
        mainFolder = new File(Const.MAIN_FOLDER_PATH);
        if(!mainFolder.exists()){
            mainFolder.mkdirs();
        }
    }
    public List<File> getConfigs(){
        return getConfigs(mainFolder);
    }
    private List<File> getConfigs(File file){
        if(file==null||!file.exists()){return null;}
        final List<File> list = new ArrayList<>();
        file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if(pathname.isFile()){
                    if(pathname.getName().endsWith(Const.Config.CONFIG_FILE_POSTFIX)||pathname.getName().endsWith(Const.Config.CONFIG_FILE_POSTFIX_2)){
                        list.add(pathname);
                        return true;
                    }
                }else if(pathname.isDirectory()){
                    list.addAll(getConfigs(pathname));
                }

                return false;
            }
        });
        return list;
    }

    public String getConfigContent(File file){
        if(file==null||!file.exists()){
            return null;
        }
        StringBuffer sb = null;
        try {
            FileInputStream in = new FileInputStream(file);
            sb = new StringBuffer();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer))!=-1){
                sb.append(new String(buffer,0,len));
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public boolean saveConfigFile(File file,String content){
        if(file==null||!file.exists()||content==null){
            return false;
        }


        try {
            FileOutputStream out = new FileOutputStream(file,false);
            ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());

            int len =0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer))!=-1){
                out.write(buffer,0,len);
            }
            out.flush();
            out.close();
            in.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public File getCurrentConfigFile(Context context){
        if(context==null){return null;}
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String path = prefs.getString(Const.Prefs.KEY_CURRENT_CONFIG_PATH,null);
        if(path==null){return null;}
        File file = new File(path);
        return  file.exists()?file:null;
    }
    public void setCurrentConfigFile(Context context,File file){
        if(context==null){return;}
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(Const.Prefs.KEY_CURRENT_CONFIG_PATH,file.getAbsolutePath()).apply();
    }
    public void editConfig(Context context,File configFile,boolean newTask){
        if(context==null||configFile==null||!configFile.exists()){
            return;
        }
        Intent intent = new Intent(context, EditConfigActivity.class);
        intent.putExtra(EditConfigActivity.FILE_KEY,configFile);
        if(newTask){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


    public interface OnCreateNewFileListenner{
        void success();
    }
    public void createNewConfig(final Context context, final OnCreateNewFileListenner onCreateNewFileListenner){
        if(context==null){
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.create_new_config));
        final EditText editText = new EditText(context);
        editText.setHint(context.getString(R.string.please_input_file_name));
        builder.setView(editText);
        builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = editText.getText().toString();
                if(fileName.trim().equals("")){
                    Toast.makeText(context,context.getString(R.string.name_not_allowd_null),Toast.LENGTH_LONG).show();
                    return;
                }
                init();
                File file = new File(mainFolder.getAbsolutePath(),fileName+Const.Config.CONFIG_FILE_POSTFIX);
                if(file.exists()){
                    Toast.makeText(context,context.getString(R.string.file_already_exits),Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(onCreateNewFileListenner!=null){
                    onCreateNewFileListenner.success();
                }
                if(saveConfigFile(file, ConfigJson.getTemplate())){
                    editConfig(context,file,false);
                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.no),null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public boolean copyConfig(File file){
        if(file==null||!file.exists()){
            return false;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        File newFile  = null;
        String parent = file.getParent();
        if(parent.startsWith(Const.MAIN_REMOTE_FOLDER_PATH)){
            parent = Const.MAIN_FOLDER_PATH;
        }
        newFile = new File(parent,(int)(Math.random()*100)+file.getName());

        if(!newFile.exists()){
            return copyFile(file,newFile);
        }
        return false;
    }

    public static final boolean  copyFile(File file,File newFile){
        if(file==null||newFile==null||!file.exists()){
            return false;
        }
        try {
            FileOutputStream out = new FileOutputStream(newFile);
            FileInputStream in = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = in.read(buffer)) != -1){
                out.write(buffer,0,len);
            }
            out.flush();
            out.close();
            in.close();
            return true;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void getRemoteConfigs(final OnGetRemoteConfigsListenner onGetRemoteConfigsListenner) {
        if(onGetRemoteConfigsListenner==null){return;}
        if(getRemoteConfigsThread!=null){
            getRemoteConfigsThread.interrupt();
        }
        onGetRemoteConfigsListenner.start();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        onGetRemoteConfigsListenner.finish((String) msg.obj,msg.arg1);
                        break;
                }
            }
        };

        getRemoteConfigsThread = new Thread(new Runnable() {
            @Override
            public void run() {


        URL url = null;
        try {
            url = new URL("https://raw.githubusercontent.com/zhangweiqwe/AndroidProxy/master/config.json");
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));

            URLConnection urlConnection=null;
            if(ServiceTun.alreadyStart){
                urlConnection = url.openConnection(proxy);
            }else {
                urlConnection = url.openConnection();
            }
            urlConnection.setUseCaches(false);

            InputStream in = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int len;
            StringBuilder sb = new StringBuilder();
            while ((len = in.read(buffer))!=-1){
                sb.append(new String(buffer,0,len));
            }
            in.close();

            int z = saveRemoteConfigs(sb.toString());
            if(z==-1){
                Message msg = Message.obtain();
                msg.what=1000;
                msg.obj = "获取失败!";
                msg.arg1 = 1;
                handler.sendMessage(msg);

            }else {
                Message msg = Message.obtain();
                msg.what=1000;
                msg.obj = z==0?"远程为空！":"获取成功,已保存在本地！\n共"+z+"个模式！";
                handler.sendMessage(msg);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Message msg = Message.obtain();
            msg.what=1000;
            msg.obj = e.getMessage().toString();
            msg.arg1 = 1;
            handler.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
            Message msg = Message.obtain();
            msg.what=1000;
            msg.obj = e.getMessage().toString();
            msg.arg1 = 1;
            handler.sendMessage(msg);
        }
            }
        });
        getRemoteConfigsThread.start();
    }

    private int saveRemoteConfigs(String str){
        if(str==null){
            return -1;
        }
        init();
        File file = new File(Const.MAIN_REMOTE_FOLDER_PATH);
        clearFiles(file.getAbsolutePath());
        file.mkdirs();
        Log.d(LOG_TAG,"-->"+str+"<-----\n");
        try {
            JSONObject jsonObject = new JSONObject(str);

            int u = 0;
            JSONArray jsonArray = jsonObject.getJSONArray("config");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);
                File file1 = new File(file.getAbsolutePath(),jsonObject1.optString("name","未知1")+Const.Config.CONFIG_FILE_POSTFIX);
                FileOutputStream out = new FileOutputStream(file1);
                if(jsonObject1.optBoolean("isEncrypt")){
                    out.write(jsonObject1.optString("text","未知3").getBytes());
                }else {
                    out.write(("#"+jsonObject1.optString("explain","未知2")+"\r\n\r\n"+jsonObject1.optString("text","未知3")).getBytes());
                }

                out.flush();
                out.close();
                u++;
            }
            return u;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


    //删除文件和目录
    public static void clearFiles(String workspaceRootPath){
        File file = new File(workspaceRootPath);
        if(file.exists()){
            deleteFile(file);
        }
    }
    public static void deleteFile(File file){
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(int i=0; i<files.length; i++){
                deleteFile(files[i]);
            }
        }
        file.delete();
    }


}
