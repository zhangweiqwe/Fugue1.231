package cn.wsgwz.tun;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.tun.gravity.encryption.SecurityUtils;


/**
 * Created by Administrator on 2017/6/12.
 */

public class ConfigJsonHelper {
    private static final ConfigJsonHelper configJsonHelper = new ConfigJsonHelper();
    private ConfigJsonHelper(){}
    public static final ConfigJsonHelper getInstance(){
        return configJsonHelper;
    }
    private static final String TAG = ConfigJsonHelper.class.getSimpleName();
    private List<Config> configList = new ArrayList<>();
    public void conver(){

        String str0 = "{\n" +
                " \"apn\": \"cmwap\",\n" +
                " \"dns\": \"114.114.114.114\",\n" +
                "\n" +
                " \"http\":{\n" +
                " \"proxy\":\"10.0.0.172:80\",\n" +
                " \"delete\":\"Host\",\n" +
                " \"first\":\"[M] \\r\\t[H][U] [V]\\r\\nConnection: Keep-Alive\\r\\t移动@\\Host: \\r \\t[M]\\nPOST http://wap.10086.cn:80/ HTTP/1.1\\nHost: wap.10086.cn:80\\r\\n\\nHost: wap.10086.cn:80\\r\\n\"\n" +
                " },\n" +
                "\n" +
                " \"https\":{\n" +
                " \"proxy\":\"10.0.0.172:80\",\n" +
                " \"delete\":\"Host\",\n" +
                " \"first\":\"CONNECT \\r\\t[H] [V]\\r\\nConnection: Keep-Alive\\r\\t移动@\\Host: \\r \\t[M]\\nPOST http://iphone.cmvideo.cn:80/ HTTP/1.1\\r\\n\"\n" +
                " }\n" +
                "\n" +
                " }";
        configList.add(new Config("模板","模板","{\n" +
                " \"apn\": \"cmwap\",\n" +
                " \"dns\": \"114.114.114.114\",\n" +
                "\n" +
                " \"http\":{\n" +
                " \"proxy\":\"10.0.0.172:80\",\n" +
                " \"delete\":\"Host\",\n" +
                " \"first\":\"[M] [U] [V]\\r\\nHost: [H]\\r\\n\"\n" +
                " },\n" +
                "\n" +
                " \"https\":{\n" +
                " \"proxy\":\"10.0.0.172:80\",\n" +
                " \"delete\":\"Host\",\n" +
                " \"first\":\"CONNECT [H] [V]\\r\\nHost: [H]\\r\\n\"\n" +
                " }\n" +
                "\n" +
                " }",false));

        configList.add(new Config("高级模板","高级模板"," {\n" +
                "\n" +
                "  \"apn\": \"cmwap\",\n" +
                "  \"dns\": \"114.114.114.114\",\n" +
                "\n" +
                "  \"http\":{\n" +
                "  \"support\":true,\n" +
                "  \"direct\":false,\n" +
                "  \"dispose\":true,\n" +
                "  \"proxy\":\"10.0.0.172:80\",\n" +
                "  \"delete\":\"Host\",\n" +
                "  \"first\":\"[M] [U] [V]\\r\\nHost: [H]\\r\\n\"\n" +
                "  },\n" +
                "\n" +
                "  \"https\":{\n" +
                "  \"support\":true,\n" +
                "  \"direct\":false,\n" +
                "  \"connect\":true,\n" +
                "  \"dispose\":true,\n" +
                "  \"proxy\":\"10.0.0.172:80\",\n" +
                "  \"delete\":\"Host\",\n" +
                "  \"first\":\"CONNECT [H] [V]\\r\\nHost: [H]\\r\\n\"\n" +
                "  }\n" +
                "\n" +
                " }",false));
        try {
            configList.add(new Config("重庆移动","作者：移动",str0,true));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for(int i=0;i<configList.size();i++){
                Config config = configList.get(i);
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("name",config.name);
                jsonObject1.put("explain",config.explain);
                jsonObject1.put("text",config.text);
                jsonObject1.put("isEncrypt",config.isEncrypt);
                jsonArray.put(i,jsonObject1);
            }
            jsonObject.put("config",jsonArray);
            Log.d(TAG,jsonObject.toString());
            //Log.d(TAG, SecurityUtils.getInstance().encrypt(jsonObject.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Config{
     private String name;
     private String explain;
     private String text;
     private boolean isEncrypt;

        public Config(String name, String explain, String text,boolean isEncrypt) {
            this.name = name;
            this.explain = explain;
            this.isEncrypt = isEncrypt;
            if(isEncrypt){
                try {
                    this.text =  SecurityUtils.getInstance().encrypt(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                this.text = text;
            }
        }
    }
}
