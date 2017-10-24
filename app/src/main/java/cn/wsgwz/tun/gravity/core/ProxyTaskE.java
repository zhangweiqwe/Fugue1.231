package cn.wsgwz.tun.gravity.core;

import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.gravity.Const;

public class ProxyTaskE implements Runnable {
    public static final String LOG_TAG = ProxyTaskE.class.getSimpleName();
    //5, 2, 0, 2
    private Socket socket;
    private ServiceTun serviceTun;
    private Config config;


    public ProxyTaskE(Socket socket, ServiceTun serviceTun, Config config) {
        this.socket = socket;
        this.serviceTun = serviceTun;
        this.config = config;
    }

    @Override
    public void run() {
        InputStream a_in = null, b_in = null;
        OutputStream a_out = null, b_out = null;
        SocketD socketD = null;
        SocketThreadOutputE out = null;
        SocketThreadInputE in = null;
        try {
            a_in = socket.getInputStream();
            a_out = socket.getOutputStream();
            if (a_in.read() == 0x5) {
                socketD = sock5_check(a_in, a_out, serviceTun, config);
                if (socketD == null) {
                    //SocketD.printf("-->");
                    return;
                }


                b_in = socketD.socket.getInputStream();
                b_out = socketD.socket.getOutputStream();
                // 交换流数据
                out = new SocketThreadOutputE(a_in, b_out, config, socketD);
                out.start();
                in = new SocketThreadInputE(b_in, a_out);
                in.start();
                out.join();
                in.join();


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            /*if(a_in!=null){try{a_in.close();}catch (IOException e){e.printStackTrace();}}
            if(b_out!=null){try{b_out.close();}catch (IOException e){e.printStackTrace();}}
            if(b_in!=null){try{b_in.close();}catch (IOException e){e.printStackTrace();}}
            if(a_out!=null){try{a_out.close();}catch (IOException e){e.printStackTrace();}}*/


            if (socketD != null && socketD.socket != null) {
                try {
                    socketD.socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }


    private final static SocketD sock5_check(InputStream in, OutputStream out, ServiceTun serviceTun, Config config) throws IOException {
        byte[] tmp = new byte[3];
        in.read(tmp);
        out.write(new byte[]{0x05, 0x00});
        out.flush();

        tmp = new byte[4];
        in.read(tmp);

        SocketD socketD = new SocketD();

        socketD.host0 = getHost(tmp[3], in); //5101 ipv4
        tmp = new byte[2];
        in.read(tmp);
        socketD.port0 = ByteBuffer.wrap(tmp).asShortBuffer().get() & 0xFFFF;

        ByteBuffer rsv = ByteBuffer.allocate(10);


        try {
            rsv.put((byte) 0x05);

            rsv.put((byte) 0x00);

            rsv.put((byte) 0x00);
            rsv.put((byte) 0x01);
           /* socketD.socket = new Socket(socketD.host0, socketD.port0);
            rsv.put(socketD.socket.getLocalAddress().getAddress());
            Short localPort = (short) ((socketD.socket.getLocalPort()) & 0xFFFF);
            rsv.putShort(localPort);*/


            out.write(rsv.array());
            out.flush();

            int z = in.read(socketD.buffer, 0, socketD.off);

            if (z == -1) {
                return null;
            }


            switch (socketD.buffer[0]) {
                case 'G'://GET
                case 'P'://POST
                case 'C':
                    break;
                default:
                    socketD.isOther = true;
                    break;
            }


            /*if(socketD.port0!=80){
                socketD.isOther = true;
            }*/


            if (socketD.isOther) {
                if (!config.isHttpsSupport()) {
                    return null;
                }
                if (config.isHttpsDirect()) {
                    socketD.socket = new Socket(socketD.host0, socketD.port0);
                } else {
                    socketD.socket = new Socket(config.getHttps_proxy(), config.getHttps_port());
                }
                if (config.isConnect()) {
                    doConnect(socketD, serviceTun, config);
                }


            } else {
                if (!config.isHttpSupport()) {
                    return null;
                }


                if (config.isHttpDirect()) {
                    socketD.socket = new Socket(socketD.host0, socketD.port0);
                } else {
                        /*socketD.socket = SocketChannel.open().socket();
                        serviceTun.protect(socketD.socket);
                        socketD.socket.connect(new InetSocketAddress(config.getHttp_proxy(), config.getHttp_port()));*/
                    socketD.socket = new Socket(config.getHttp_proxy(), config.getHttp_port());

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        return socketD;
    }

    private final static String getHost(byte type, InputStream in) throws IOException {
        String host = null;
        byte[] tmp = null;

        switch (type) {
            case 0x01:// IPV4协议
                tmp = new byte[4];
                in.read(tmp);
                host = InetAddress.getByAddress(tmp).getHostAddress();
                break;
            case 0x03:// 使用域名
                int l = in.read();
                tmp = new byte[l];
                in.read(tmp);
                host = new String(tmp);
                break;
            case 0x04:// 使用IPV6
                tmp = new byte[16];
                in.read(tmp);
                host = InetAddress.getByAddress(tmp).getHostAddress();
                break;
            default:
                break;
        }
        return host;
    }

    private final static void doConnect(SocketD socketD, ServiceTun serviceTun, Config config) throws IOException {

        OutputStream tempOut = socketD.socket.getOutputStream();
        InputStream tempIn = socketD.socket.getInputStream();
        String s = null;
        if (config.isHttpsDispose()) {
            s = ConnectHelper.getConnectRequest(socketD.host0, socketD.port0, serviceTun, config);
        } else {
            s = "CONNECT " + socketD.host0 + ":" + socketD.port0 + " HTTP/1.1\r\n" +
                    "Host: " + socketD.host0 + ":" + socketD.port0 + "\r\n" +
                    "User-Agent: " + serviceTun.userAgent + "\r\n" +
                    "\r\n";
        }

        tempOut.write(s.getBytes());
        tempOut.flush();

        //int c;
        boolean isEnd = false;
        loop:
        while (true) {
            switch ((tempIn.read())) {
                case -1:
                    break loop;
                case '\n':
                    break loop;
                case '\r':
                    int c2 = tempIn.read();
                    if ((c2 != '\n') && c2 != -1) {
                        break;
                    } else if (c2 == -1) {
                        break loop;
                    } else {
                        if (isEnd) {
                            break loop;
                        } else {
                            isEnd = true;
                            break;
                        }
                    }
                default:
                    break;
            }
        }


    }

}
