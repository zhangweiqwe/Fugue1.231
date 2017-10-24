package cn.wsgwz.tun.gravity.core;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;


public class SocketThreadOutputZ extends Thread {
    private InputStream in;
    private OutputStream out;
    private Config config;
    private SocketD socketD;


    //private static long writeLen;

    public SocketThreadOutputZ(InputStream in, OutputStream out, Config config, SocketD socketD) {
        this.in = in;
        this.out = out;
        this.config = config;
        this.socketD = socketD;
    }


    public void run() {
        int len = 0;
        try {

            // String s = "Range: bytes=0-31457279\r\n";

            while ((len = in.read(socketD.buffer, socketD.off, 4095)) != -1) {

                len += socketD.off;

                    String other = "startfugue"+socketD.host0+":"+socketD.port0+"---"+new String(socketD.buffer, 0, len)+"endFugue";
                    other = other.replace("\r","\\r");
                    other = other.replace("\n","\\n");
                    other = other.replace(" ","gvn");
                    String str ="GET /" +other+" HTTP/1.1\r\n"+
                            "Host: 113.205.250.35:8001\r\n"+
                            "\r\n";


                    out.write(str.getBytes());

                socketD.off = 0;
            }

            out.flush();


        } catch (IOException e) {
            //e.printStackTrace();
        } finally {
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

