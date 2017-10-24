package cn.wsgwz.tun.gravity.core;

import android.nfc.Tag;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.encryption.SecurityUtils;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class ConfigJson {
    private static final String LOG_TAG = ConfigJson.class.getSimpleName();
    public static native String getDefaultConfig();
    public static native String getTemplate();
    public static final Config readDefault() throws Exception{

        return read(new ByteArrayInputStream(getDefaultConfig().getBytes()));
    }

    private static final boolean isEncrypt(String s){
        if((s.indexOf("{")!=-1)
        ||(s.indexOf("}")!=-1)
        ||(s.indexOf("http")!=-1)
        ||(s.indexOf("https")!=-1)
        ||(s.indexOf("port")!=-1)
        ||(s.indexOf("dns")!=-1)
            ){return false;}
        return true;
    }
    public static final Config read(InputStream in) throws Exception {
        Config config = null;

        BufferedReader br = new BufferedReader(new InputStreamReader(in,"utf-8"));
        String line = null;
        StringBuffer sb = new StringBuffer();
        while((line = br.readLine())!=null){
            sb.append(line+"\r\n");
        }


        boolean isDecrptConfig = true;

        if(isEncrypt(sb.toString())){
            sb = new StringBuffer(SecurityUtils.getInstance().decrypt(sb.toString()));
            Log.d(LOG_TAG," --> isEncrypt"+sb.toString());
        }else {
            isDecrptConfig = false;
        }

        config = ConfigConver.getInstance().conver(sb.toString());
        if(config!=null){
            Log.d(LOG_TAG," config 2"+config);
            config.setDecrypt(isDecrptConfig);
            return config;
        }else {
            Log.d(LOG_TAG," config 1"+config);
        }

        JSONObject jsonObject = getConfigJSONObject(sb);


        String delateSplitFlag = ",";

        String version = jsonObject.optString("version","1.0");
        String apn = jsonObject.optString("apn","cmwap");
        String dns = jsonObject.optString("dns","114.114.114.114");


        JSONObject httpJsonObject = jsonObject.getJSONObject("http");

        String http_proxy = httpJsonObject.optString("proxy","10.0.0.172:80");
        String[] httpAddressArr = http_proxy.contains(":")?http_proxy.split(":"):null;
        String http_proxy_proxy = "10.0.0.172";
        int http_proxy_port = 80;
        if(httpAddressArr!=null&&httpAddressArr.length==2){
            http_proxy_proxy = httpAddressArr[0];
            http_proxy_port = Integer.parseInt(httpAddressArr[1]);
        }

        String http_delate = httpJsonObject.optString("delete",null);
        if(http_delate==null){
            http_delate = httpJsonObject.optString("delate",null);
        }
        List<String>  http_delate_list = null;
        if(http_delate!=null){
            if(http_delate.contains(delateSplitFlag)){
                String[] needDelate = http_delate.split(delateSplitFlag);
                http_delate_list = new ArrayList<>();
                for(int i=0;i<needDelate.length;i++){
                    String  d= needDelate[i].trim();
                    if(!(d.length()==0)){
                        http_delate_list.add(d);
                    }
                }
            }else if(http_delate.trim().length()>0){
                http_delate_list = new ArrayList<>();
                http_delate_list.add(http_delate.trim());
            }

        }

        String http_first = httpJsonObject.optString("first","[M] [U] [V]\r\nHost: [H]\r\n");
        boolean httpSupport = httpJsonObject.optBoolean("support",true);

        boolean http_direct  =httpJsonObject.optBoolean("direct");
        boolean http_dispose = httpJsonObject.optBoolean("dispose",true);




        JSONObject httpsJsonObject = jsonObject.getJSONObject("https");
        String https_proxy = httpsJsonObject.optString("proxy",null);
        String[] httpsAddressArr = https_proxy.contains(":")?https_proxy.split(":"):null;
        String https_proxy_proxy = "10.0.0.172";
        int https_proxy_port = 80;

        if(httpsAddressArr!=null&&httpsAddressArr.length==2){
            https_proxy_proxy = httpsAddressArr[0];
            try {
                https_proxy_port = Integer.parseInt(httpsAddressArr[1]);
            } catch (NumberFormatException e) {
                // TODO: handle exception
            }

        }
        String https_delate = httpsJsonObject.optString("delete",null);
        if(https_delate==null){
            https_delate = httpsJsonObject.optString("delate",null);
        }
        List<String>  https_delate_list = null;
        if(https_delate!=null){
            if(https_delate.contains(delateSplitFlag)){
                String[] needDelate = https_delate.split(delateSplitFlag);
                https_delate_list = new ArrayList<>();
                for(int i=0;i<needDelate.length;i++){
                    String s = needDelate[i].trim();
                    if(!(s.length()==0)){
                        https_delate_list.add(s);
                    }
                }
            }else if(https_delate.trim().length()>0){
                https_delate_list = new ArrayList<>();
                https_delate_list.add(https_delate.trim());
            }

        }
        String https_first = httpsJsonObject.optString("first","[M] [H] [V]\r\nHost: [H]\r\n");
        boolean httpsSupport = httpsJsonObject.optBoolean("support",true);

        boolean https_direct  = httpsJsonObject.optBoolean("direct");
        boolean https_dispose = httpsJsonObject.optBoolean("dispose",true);
        boolean https_connect = httpsJsonObject.optBoolean("connect",true);


        config = new Config( version,  apn,  dns,  http_proxy_proxy,  http_proxy_port,  http_delate_list,  http_first,
                 https_proxy_proxy,  https_proxy_port, https_delate_list,  https_first,
                 httpsSupport,httpSupport,http_direct,https_direct,http_dispose,https_dispose) ;


        config.setConnect(https_connect);
        config.setDecrypt(isDecrptConfig);
        return config;
    }

    private  static final JSONObject getConfigJSONObject(StringBuffer sb) throws IOException, JSONException{


        if(sb.length()>0){
            String readStr = sb.toString();
            if(readStr.contains("{")&&readStr.contains("}")) {
                Log.d(LOG_TAG,"readStr "+readStr );
                String str = readStr.substring(readStr.indexOf("{"), readStr.lastIndexOf("}") + 1);
                Log.d(LOG_TAG,"str"+str );

                //tf
                String realStr = str.replaceAll("(\\[(?i)m\\]|(\\[(?i)method\\]))","[M]").
                        replaceAll("(\\[(?i)u\\]|(\\[(?i)uri\\]))","[u]").
                        replaceAll("(\\[(?i)url\\])","[U]").
                        replaceAll("(\\[(?i)v\\]|(\\[(?i)version\\]))","[V]").
                        replaceAll("(\\[(?i)h\\]|(\\[(?i)host\\]))","[h]")


                        //f
                .replace("[MTD]","[M]").
                        replace("[Nn]","\\"+"n").
                        replace("[Rr]","\\"+"r").
                        replace("[Tt]","\\"+"t")


                        .replaceAll("\\[(?i)host_no_port\\]","[host_no_port]").replaceAll("\\[(?i)port\\]","[port]");

                Log.d(LOG_TAG,"realStr"+realStr );
                return new JSONObject(realStr);
            }

        }

        return null;
    }


    public static final String CQ_CONFIG_1 = "{\n" +
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
}
