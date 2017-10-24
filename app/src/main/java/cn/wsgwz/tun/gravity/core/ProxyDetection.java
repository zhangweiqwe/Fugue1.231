package cn.wsgwz.tun.gravity.core;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;


public class ProxyDetection {
    static SocketAddress detectProxy(String host,int port) {
        // Construct a new url with https as protocol
        try {
            URL url = new URL(String.format("https://%s:%s",host,port));
            Proxy proxy = getFirstProxy(url);

            if(proxy==null)
                return null;
            SocketAddress addr = proxy.address();
            if (addr instanceof InetSocketAddress) {
                return addr;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Proxy getFirstProxy(URL url) throws URISyntaxException {
        //System.setProperty("java.net.useSystemProxies", "true");
        ProxySelector proxySelector = ProxySelector.getDefault();
        Properties prop = System.getProperties();
        //prop.setProperty("https.proxyHost", "127.0.0.1");
        //prop.setProperty("https.proxyHost", "10.0.0.172");
        //prop.setProperty("https.proxyPort", "65053");
        //prop.setProperty("https.proxyPort", "80");
        List<Proxy> proxylist = proxySelector.select(url.toURI());


        if (proxylist != null) {
            for (Proxy proxy: proxylist) {
                SocketAddress addr = proxy.address();

                //ProxyTaskB.printf("addr"+addr.toString());
                if (addr != null) {
                    return proxy;
                }
            }

        }
        return null;
    }
}