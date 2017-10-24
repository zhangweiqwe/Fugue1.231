package cn.wsgwz.tun.gravity.core;

import android.content.Intent;
import android.util.Log;
import android.util.TimeUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.wsgwz.tun.gravity.adapter.LogAdapter;

/**
 * Created by Administrator on 2017/5/11 0011.
 */

public class ConfigConver {
    private static final String LOG_TAG = ConfigConver.class.getSimpleName();

    private static ConfigConver configConver;
    private ConfigConver(){};
    public static final ConfigConver getInstance(){
        if(configConver==null){
            synchronized (ConfigConver.class){
                if(configConver==null){
                    configConver = new ConfigConver();
                }
            }
        }
        return configConver;
    }
    private boolean isTiny(String str){

        Log.d(LOG_TAG,"isTiny str -->"+str+"<--");
        int i0 = str.indexOf("http_ip");
        int i1 = str.indexOf("http_port");
        int i2 = str.indexOf("http_first");

        int z0 = str.indexOf("https_ip");
        int z1 = str.indexOf("https_port");
        int z2 = str.indexOf("https_first");


        if((i0!=-1)&&(i1!=-1)&&(i2!=-1)&&(z0!=-1)&(z1!=-1)&(z2!=-1)){
            Log.d(LOG_TAG,"isTiny true");
            return true;
        }else {
            Log.d(LOG_TAG,"isTiny  false");
        }

        return false;
    }


    public Config conver(String str){
        if(isTiny(str)) {
            String version = "1.0";
            String apn = "cmwap";
            String dns = "114.114.114.114";
            boolean httpsSupport = true;
            boolean httpSupport = true;


            String http_proxy = null;
            if (true) {
                Pattern pattern = Pattern.compile("(?i)http_ip=([\\s\\S]*?);");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    http_proxy = matcher.group(1);
                    Log.d(LOG_TAG, matcher.group(1));
                }
            }

            int http_port = 0;
            if (true) {
                Pattern pattern = Pattern.compile("(?i)http_port=([\\s\\S]*?);");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {

                    try {
                        http_port = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    Log.d(LOG_TAG, matcher.group(1));

                }
            }

            List<String> http_delate = null;
            if (true) {
                Pattern pattern = Pattern.compile("(?i)http_del=\"([\\s\\S]*?)\";");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {

                    String temp = matcher.group(1);
                    String flag = ",";
                    if (temp.contains(flag)) {
                        String[] args = temp.split(flag);
                        for (int i = 0; i < args.length; i++) {
                            if (!args[i].trim().equals("")) {
                                if (http_delate == null) {
                                    http_delate = new ArrayList<>();
                                }
                                http_delate.add(args[i].trim());
                            }
                        }
                    }else if(temp!=null){
                        if(temp.trim().length()>1){
                            http_delate = new ArrayList<>();
                            http_delate.add(temp.trim());
                        }

                    }
                    Log.d(LOG_TAG, matcher.group(1));

                }
            }

            String http_first = null;
            if (true) {
                Pattern pattern = Pattern.compile("(?s)(?i)http_first=\"([\\s\\S]*?)\";");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    http_first = matcher.group(1).replaceAll("(\\[(?i)m\\]|(\\[(?i)method\\]))", "[M]").
                            replaceAll("(\\[(?i)url\\])", "[U]").
                            replaceAll("(\\[(?i)u\\]|(\\[(?i)uri\\]))", "[u]").
                            replaceAll("(\\[(?i)v\\]|(\\[(?i)version\\]))", "[V]").
                            replaceAll("(\\[(?i)h\\]|(\\[(?i)host\\]))", "[h]")

                            .replace("\\r","\r")
                            .replace("\\n","\n")
                            .replace("\\0","\0")
                            .replace("\\t","\t");
                    ;
                    Log.d(LOG_TAG+" http_first-->", matcher.group(1));
                }
            }


            String https_proxy = null;
            if (true) {
                Pattern pattern = Pattern.compile("(?i)https_ip=([\\s\\S]*?);");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    https_proxy = matcher.group(1);
                    Log.d(LOG_TAG, matcher.group(1));
                }
            }
            int https_port = 0;
            if (true) {
                Pattern pattern = Pattern.compile("(?i)https_port=([\\s\\S]*?);");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {

                    try {
                        https_port = Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    Log.d(LOG_TAG, matcher.group(1));

                }
            }
            List<String> https_delate = null;
            if (true) {
                Pattern pattern = Pattern.compile("(?i)https_del=\"([\\s\\S]*?)\";");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {

                    String temp = matcher.group(1);
                    String flag = ",";
                    if (temp.contains(flag)) {
                        String[] args = temp.split(flag);
                        for (int i = 0; i < args.length; i++) {
                            if (!args[i].trim().equals("")) {
                                if (https_delate == null) {
                                    https_delate = new ArrayList<>();
                                }
                                https_delate.add(args[i].trim());
                            }
                        }
                    }else if(temp!=null){
                        if(temp.trim().length()>1){
                            https_delate = new ArrayList<>();
                            https_delate.add(temp.trim());
                        }

                    }
                    Log.d(LOG_TAG, matcher.group(1));

                }
            }
            String https_first = null;
            if (true) {
                Pattern pattern = Pattern.compile("(?s)(?i)https_first=\"([\\s\\S]*?)\";");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    https_first = matcher.group(1).replaceAll("(\\[(?i)m\\])|(\\[(?i)method\\])", "[M]").
                            replaceAll("(\\[(?i)u\\])|(\\[(?i)uri\\])","[u]").
                            replaceAll("(\\[(?i)url\\])","[U]").
                            replaceAll("(\\[(?i)v\\])|(\\[(?i)version\\])", "[V]").
                            replaceAll("(\\[(?i)h\\])|(\\[(?i)host\\])", "[h]")

                    .replace("\\r","\r")
                    .replace("\\n","\n")
                    .replace("\\0","\0")
                    .replace("\\t","\t");
                    Log.d(LOG_TAG+"https_first-->", matcher.group(1));
                }
            }

            if (http_proxy == null || http_port == 0 || http_first == null || https_proxy == null || https_port == 0 || https_first == null) {
                return null;
            }



            Config config = new Config(version, apn, dns, http_proxy, http_port, http_delate, http_first, https_proxy, https_port, https_delate, https_first, httpsSupport, httpSupport,false,false,true,true);

            Log.d(LOG_TAG, config.toString());


            return config;
        }

        return null;
    }
}
