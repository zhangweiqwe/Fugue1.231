package cn.wsgwz.tun.gravity.core;


import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class SocketThreadOutputE extends Thread {
    private InputStream in;
    private OutputStream out;
    private Config config;
    private SocketD socketD;


    //private static long writeLen;

    public SocketThreadOutputE(InputStream in, OutputStream out, Config config, SocketD socketD) {
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

                //SocketD.printf(socketD.off + "--" + len + "-->" + new String(socketD.buffer, 0, len) + "\n<--");


                if ((socketD.buffer[0] == 'G' && socketD.buffer[1] == 'E') || (socketD.buffer[0] == 'P' && socketD.buffer[1] == 'O')) {
                    if (config.isHttpDispose()) {
                        ParamsHelper paramsHelper = ParamsHelper.read(new ByteArrayInputStream(socketD.buffer, 0, len), config,socketD);
                        if (paramsHelper == null) {
                            out.write(socketD.buffer, 0, len);
                            continue;
                        }

                        String request = paramsHelper.toString();

                        //SocketD.printf(socketD.off + "--" + len + "-->" + request + "\n<--");
                        out.write(request.getBytes());
                        paramsHelper.flush(socketD.buffer,out);


                    } else {
                        out.write(socketD.buffer, 0, len);
                    }
                } else if (socketD.buffer[0] == 'C' && socketD.buffer[1] == 'O') {
                    if (config.isHttpsDispose()) {
                        ParamsHelper paramsHelper = ParamsHelper.read(new ByteArrayInputStream(socketD.buffer, 0, len), config,socketD);
                        String request = paramsHelper.toString();
                        paramsHelper.close();
                        out.write(request.getBytes());

                    } else {
                        out.write(socketD.buffer, 0, len);
                    }
                } else {
                    out.write(socketD.buffer, 0, len);

                }


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

