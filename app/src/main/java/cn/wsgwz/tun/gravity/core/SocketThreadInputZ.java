package cn.wsgwz.tun.gravity.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class SocketThreadInputZ extends Thread {
    private byte[] buffer = new byte[409600];
    private InputStream in;
    private OutputStream out;

    public SocketThreadInputZ(InputStream in, OutputStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() {
        try {
            int len;
            while ((len = in.read(buffer)) != -1 ) {
                    out.write(buffer, 0, len);
                //SocketD.printf(new String(buffer,0,len));
            }
            out.flush();
        } catch (IOException e) {
            //e.printStackTrace();
        }finally {


            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

              if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


        }
    }
}  