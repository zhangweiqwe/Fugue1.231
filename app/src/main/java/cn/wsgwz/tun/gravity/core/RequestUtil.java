package cn.wsgwz.tun.gravity.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/6/15.
 */

public class RequestUtil {
    private  static String getHost(String url){
        if(url==null){
            return null;
        }
        String host = null;
        Pattern p =  Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if(matcher.find()){
            host = matcher.group();
        }
        return host;
    }
}
