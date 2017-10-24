package cn.wsgwz.tun.gravity.core;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class Match {

    private static final String LOG_TAG = Match.class.getSimpleName();
    public static final StringBuilder match(ParamsHelper paramsHelper,Config config){
        String connectFirstLine;
        if(paramsHelper.requestTypeInt==2){
            connectFirstLine = match(config.getHttps_first(),paramsHelper);
        }else {
            connectFirstLine = match(config.getHttp_first(),paramsHelper);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(connectFirstLine);

        HashMap<String,String> hashMap = paramsHelper.getHashMap();

       /* for(String key:hashMap.keySet()){
            sb.append(key+paramsHelper.flag1+hashMap.get(key)+paramsHelper.endOfLine);
        }*/

        Set<Map.Entry<String,String>> entrySet = hashMap.entrySet();
        for(Map.Entry<String, String> entry:entrySet){
            String key=entry.getKey();
            String value=entry.getValue();
            sb.append(key+paramsHelper.flag1+value+paramsHelper.endOfLine);
        }

        sb.append(paramsHelper.endOfLine);

        return sb;
    }


    private static final String match(String str, ParamsHelper paramsHelper) {
        if (str == null) {
            return null;
        }
        str = str.replace("[M]", paramsHelper.getRequestType());
        str = str.replace("[V]", paramsHelper.getHttpVersion());

        if (paramsHelper.requestTypeInt==2) {
            str = host_no_port_port_https(str,paramsHelper.getUrl());
        } else {
            str = str.replace("[u]", paramsHelper.getUri());
            str = str.replace("[U]", paramsHelper.getUrl());
            if(paramsHelper.getUrl_host()!=null){
                str = str.replace("[h]", paramsHelper.getUrl_host());
                str = host_no_port_port(str,paramsHelper.getUrl_host(),paramsHelper);
            }else if(paramsHelper.getHost()!=null){
                str = str.replace("[h]", paramsHelper.getHost());
                str = host_no_port_port(str,paramsHelper.getHost(),paramsHelper);
            }else {
                str = str.replace("[h]", "未找到Host(error)");
                str = host_no_port_port(str, null,paramsHelper);
            }
        }
        return str;
    }

    private static String host_no_port_port(String s,String host,ParamsHelper paramsHelper){
        if(host==null){
            s = s.replaceAll("(\\[host_no_port\\])|(\\[port\\])",paramsHelper.flag3);


        }else {
            if(host.contains(paramsHelper.flag4)){
                String[] args = host.split(paramsHelper.flag4);
                s = s.replace("[host_no_port]",args[0]);
                s = s.replace("[port]",args[1]);
            }else {
                s = s.replace("[host_no_port]",host);
                s = s.replace("[port]",paramsHelper.flag3);
            }
        }
        return s;
    }

    public static String host_no_port_port_https(String s,String host){
        String flag4 = ":";
        if(host!=null&&host.contains(flag4)){
            String[] args = host.split(flag4);
            s = s.replace("[host_no_port]",args[0]);
            s = s.replace("[port]",args[1]);

            s = s.replaceAll("(\\[h\\])", host);
        }else {
            s = s.replaceAll("(\\[host_no_port\\])|(\\[port\\])|(\\[h\\])","");
        }
        return s;
    }



}
