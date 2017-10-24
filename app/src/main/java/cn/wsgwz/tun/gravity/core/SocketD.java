package cn.wsgwz.tun.gravity.core;

import android.util.Log;

import java.net.Socket;

/**
 * Created by Administrator on 2017/6/4.
 */

public class SocketD {
    private static final String TAG = SocketD.class.getSimpleName();
    public boolean isOther;
    public String host0;
    public int port0;

    public Socket socket;
    public byte[] buffer = new byte[4096];
    public int off = 1;

    public final static synchronized void printf(String str){
        Log.d(TAG,str);
    }
}
