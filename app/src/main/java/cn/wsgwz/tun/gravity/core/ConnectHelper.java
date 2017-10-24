package cn.wsgwz.tun.gravity.core;

import cn.wsgwz.tun.ServiceTun;

/**
 * Created by Administrator on 2017/6/9.
 */

public class ConnectHelper {
    public static String getConnectRequest(String host,int port, ServiceTun serviceTun,Config config){
        String tempStr = config.getHttps_first() ;
        tempStr  = tempStr.replace("[M]","CONNECT");
        String host_port = host+":"+port;
        tempStr = Match.host_no_port_port_https(tempStr,host_port);
        tempStr  = tempStr.replace("[V]","HTTP/1.1");

        tempStr = tempStr+
                "User-Agent: " + serviceTun.userAgent + "\r\n" +
                "\r\n";

        //SocketD.printf("-->"+tempStr+"<--1");
        return tempStr;
    }
}
