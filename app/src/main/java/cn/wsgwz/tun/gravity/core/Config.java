package cn.wsgwz.tun.gravity.core;

import android.util.Log;

import java.io.File;
import java.util.List;

/**
 * Created by Jeremy Wang on 2016/11/3.
 */

public class Config {
    private static final String TAG = Config.class.getSimpleName();
    private File file;

    private String version;
    private String apn;
    private String dns;

    private String http_proxy;
    private int http_port;
    private List<String> http_delate;
    private String http_first;

    private String https_proxy;
    private int https_port;
    private List<String> https_delate;
    private String https_first;


    private boolean httpsSupport = true;
    private boolean httpSupport = true;

    private boolean httpDirect;
    private boolean httpsDirect;

    private boolean httpDispose = true;
    private boolean httpsDispose = true;

    private boolean connect = true;

    private boolean isDecrypt = false;


    public Config(String version, String apn, String dns, String http_proxy, int http_port, List<String> http_delate, String http_first, String https_proxy, int https_port, List<String> https_delate, String https_first, boolean httpsSupport, boolean httpSupport, boolean httpDirect, boolean httpsDirect, boolean httpDispose, boolean httpsDispose) {
        this.version = version;
        this.apn = apn;
        this.dns = dns;
        this.http_proxy = http_proxy;
        this.http_port = http_port;
        this.http_delate = http_delate;
        this.http_first = http_first;
        this.https_proxy = https_proxy;
        this.https_port = https_port;
        this.https_delate = https_delate;
        this.https_first = https_first;
        this.httpsSupport = httpsSupport;
        this.httpSupport = httpSupport;
        this.httpDirect = httpDirect;
        this.httpsDirect = httpsDirect;
        this.httpDispose = httpDispose;
        this.httpsDispose = httpsDispose;

    }

    public String getVersion() {
        return version;
    }

    public String getApn() {
        return apn;
    }

    public String getDns() {
        return dns;
    }

    public String getHttp_proxy() {
        return http_proxy;
    }

    public int getHttp_port() {
        return http_port;
    }

    public List<String> getHttp_delate() {
        return http_delate;
    }

    public String getHttp_first() {
        return http_first;
    }

    public String getHttps_proxy() {
        return https_proxy;
    }

    public int getHttps_port() {
        return https_port;
    }

    public List<String> getHttps_delate() {
        return https_delate;
    }

    public String getHttps_first() {
        return https_first;
    }


    public boolean isHttpsSupport() {
        return httpsSupport;
    }

    public boolean isHttpSupport() {
        return httpSupport;
    }


    public boolean isHttpDirect() {
        return httpDirect;
    }

    public void setHttpDirect(boolean httpDirect) {
        this.httpDirect = httpDirect;
    }

    public boolean isHttpsDirect() {
        return httpsDirect;
    }

    public void setHttpsDirect(boolean httpsDirect) {
        this.httpsDirect = httpsDirect;
    }

    public boolean isHttpDispose() {
        return httpDispose;
    }

    public boolean isHttpsDispose() {
        return httpsDispose;
    }


    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public boolean isDecrypt() {
        return isDecrypt;
    }

    public void setDecrypt(boolean decrypt) {
        isDecrypt = decrypt;
    }


    @Override
    public String toString() {
        return "Config{" +
                "file=" + file +
                ", version='" + version + '\'' +
                ", apn='" + apn + '\'' +
                ", dns='" + dns + '\'' +
                ", http_proxy='" + http_proxy + '\'' +
                ", http_port=" + http_port +
                ", http_delate=" + http_delate +
                ", http_first='" + http_first + '\'' +
                ", https_proxy='" + https_proxy + '\'' +
                ", https_port=" + https_port +
                ", https_delate=" + https_delate +
                ", https_first='" + https_first + '\'' +
                ", httpsSupport=" + httpsSupport +
                ", httpSupport=" + httpSupport +
                ", httpDirect=" + httpDirect +
                ", httpsDirect=" + httpsDirect +
                ", httpDispose=" + httpDispose +
                ", httpsDispose=" + httpsDispose +
                ", connect=" + connect +
                ", isDecrypt=" + isDecrypt +
                '}';
    }

    public String toStringw() {
        //Log.d(TAG,toString());
        if(isDecrypt){
            return "加密模式";
        }
        return
                        "\n---->\n" +
                        "\n"+
                         dns + "\n" +
                        "\n" +
                        http_proxy + ':' + http_port + "\n" +
                        (http_first == null ? http_first : http_first.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("[U]", "[Url]").replace("[u]", "[U]").replace("[h]", "[H]")) + "\n" +
                        "\n" +
                        https_proxy + ':' + https_port + "\n" +
                        (https_first == null ? https_first : https_first.replace("\r", "\\r").replace("\n", "\\n").replace("\t", "\\t").replace("[h]", "[H]"))  + "\n" + "\n" +
                        "<-------------------\n"


                ;
    }

}
