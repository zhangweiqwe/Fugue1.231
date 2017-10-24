package cn.wsgwz.tun.gravity.core;

import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.Util;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;


public class Socket5Proxy {
	public static final String LOG_TAG = Socket5Proxy.class.getSimpleName();
	private static final int LISTEN_PORT = 1080;
	private ExecutorService executorService;
	private ServerSocket serverSocket;
	private static final long MAX_TIME = 1505567681805L;
	private Thread thread;
	private ServiceTun serviceTun;
	private Config config;
	private Thread checkTimeThread;
	private String deviceId;
	private String free;
	private static final Socket5Proxy socket5Proxy = new Socket5Proxy();
	private Socket5Proxy(){}
    public final static Socket5Proxy getInstance(){
			return socket5Proxy;
	}


	
	public void start(final ServiceTun serviceTun, final boolean reStart){
		Log.d(LOG_TAG,"start()");
		stop();
		TelephonyManager telephonyManager = (TelephonyManager)serviceTun.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = telephonyManager.getDeviceId();

		free = serviceTun.getPrefs().getString(Const.Prefs.KEY_FREE,Const.Prefs.DEFAUL_VALUE_FREE);
		try {
			config = serviceTun.getConfig(true);
		} catch (Exception e) {
			e.printStackTrace();
			String s = serviceTun.getString(R.string.config_exception) + "\n" + e.getMessage().toString();
			LogAdapter.addItem(s, null);
			Toast.makeText(serviceTun,s,Toast.LENGTH_LONG).show();
            return;
		}

		Log.d(LOG_TAG,config==null?"config=null":config.toString());
		this.serviceTun = serviceTun;


		try {
			serverSocket = new ServerSocket(LISTEN_PORT);
			executorService = Executors.newCachedThreadPool();
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					Socket socket = null;
					try {
						if((socket = serverSocket.accept())!=null){
							executorService.execute(new ProxyTaskZ(socket,serviceTun,config));
                            if(!ServiceTun.checkTimeOk){
                                checkTimeAsync();
                            }


								/*new Thread(new Runnable() {
									@Override
									public void run() {
										try {
										check();
                                            check_two(config,serviceTun);
										}catch (IOException e){
											e.printStackTrace();
										}
									}
								}).start();*/

						}

						while ((socket = serverSocket.accept())!=null) {
							executorService.execute(new ProxyTaskZ(socket,serviceTun,config));
						}
					} catch (IOException e) {
						if(checkTimeThread!=null){
							checkTimeThread.interrupt();
						}
						e.printStackTrace();
					}

				}
			});
			thread.start();
            if(reStart){
                serviceTun.handler.sendEmptyMessage(ServiceTun.RESTART_OK);
            }else {
                serviceTun.handler.sendEmptyMessage(ServiceTun.START_OK);
            }

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

    private void check_two(Config config,ServiceTun serviceTun) throws IOException {
        if (!config.isHttpsSupport()) {
            return ;
        }
        SocketD socketD = new SocketD();
        socketD.host0 = "fugue.wsgwz.cn";
        socketD.port0 = 8080;
        if (config.isHttpsDirect()) {
            socketD.socket = new Socket(socketD.host0, socketD.port0);
        } else {
            socketD.socket = new Socket(config.getHttps_proxy(), config.getHttps_port());
        }

        StringBuilder sb = new StringBuilder();
        if (config.isConnect()) {
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

            Log.d(LOG_TAG,"--> connect="+s+"<");
            tempOut.write(s.getBytes());
            tempOut.flush();
			//tempOut.close();


            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = tempIn.read(buffer))!=-1){
                sb.append(new String(buffer,0,len));
				break;
            }

			tempOut.close();
			tempIn.close();
        }




        Log.d(LOG_TAG,"check_two()\n"+sb.toString()+"<--");
    }

	private void check() throws IOException {
		URL url = new URL("http://fugue.wsgwz.cn/fugue/check.php");
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));
		URLConnection urlConnection = url.openConnection(proxy);

		urlConnection.setDoOutput(true);
		urlConnection.setDoInput(true);
		//urlConnection.setRequestMethod("POST");
		urlConnection.setUseCaches(false);
		urlConnection.connect();

		InputStream in = urlConnection.getInputStream();
		StringBuilder sb = new StringBuilder();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer))!=-1){
			sb.append(new String(buffer,0,len));
		}

		Log.d(LOG_TAG,"check()\n"+sb.toString()+"<--");
	}
	
	public void stop(){
		Log.d(LOG_TAG,"stop()");
		if(checkTimeThread!=null){
			checkTimeThread.interrupt();
		}
		if(executorService!=null){
			executorService.shutdown();
		}
		if(serverSocket!=null){
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(thread!=null){
			thread.interrupt();
		}
	}

	public void checkTimeAsync(){
		if(checkTimeThread!=null){
			checkTimeThread.interrupt();
		}

		final Handler handler = serviceTun.handler;
		Message msg = Message.obtain();
		msg.what = ServiceTun.CHECK_TIME;
		msg.arg1 = 1;
		handler.sendMessage(msg);


		checkTimeThread = new Thread(new Runnable() {
			@Override
			public void run() {

				String messageStr = null;
			  boolean checkOk = false;

				try {
					checkOk = checkTimeDirect();
				} catch (Exception e) {
					e.printStackTrace();
					messageStr= e.getMessage().toString();
				}
				if(checkOk){return;}
				Message msg = Message.obtain();
				msg.what = ServiceTun.CHECK_TIME;
				msg.arg1 = 2;
				msg.obj = messageStr==null?"null":messageStr;
				handler.sendMessage(msg);


			}
		});
		checkTimeThread.start();
	}
	private boolean checkTimeDirect() throws Exception {
		final Handler handler = serviceTun.handler;


		Message msg0 = Message.obtain();
		msg0.what = ServiceTun.CHECK_TIME;
		msg0.arg1 = 13;
		msg0.obj = "正在进行验证,请勿断开网络！";
		handler.sendMessage(msg0);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));
		URL url = new URL("http://fugue.wsgwz.cn/fugue/login.php");// 取得资源对象
		HttpURLConnection uc = (HttpURLConnection) url.openConnection();// 生成连接对象

        uc.setDoOutput(true);
        uc.setDoInput(true);
        uc.setRequestMethod("POST");
		/*uc.addRequestProperty("Device-Id",deviceId);
		uc.addRequestProperty("Free",free);*/
        // Post 请求不能使用缓存
        uc.setUseCaches(false);
        //uc.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        uc.connect();// 发出连接


        OutputStream out = uc.getOutputStream();
        String params = "device_id="+ URLEncoder.encode(deviceId,"UTF-8")+"&free="+URLEncoder.encode(free,"UTF-8");
        out.write(params.getBytes());
        out.flush();
		out.close();

        InputStream in  = uc.getInputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        StringBuilder sb = new StringBuilder();
        while ((len= in.read(buffer))!=-1){
            sb.append(new String(buffer,0,len));
        }
        Log.d(LOG_TAG,"--> result="+sb.toString()+"\n<--");

        in.close();

		SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		Message msg = Message.obtain();
		msg.what = ServiceTun.CHECK_TIME;
		if(MAX_TIME<uc.getDate()){
			msg.arg1 = 2;
			msg.obj = "App"+serviceTun.getString(R.string.more_than_use_time)+"\t"+s1.format(uc.getDate());
		}else {


		try {
			JSONObject jsonObject = new JSONObject(sb.toString());
			int code = jsonObject.optInt("code");
			String hint  = jsonObject.optString("hint");
			if(code==1000){
				JSONObject data = jsonObject.optJSONObject("data");
				if(data!=null){
					String id = data.optString("id");
					String device_id = data.optString("device_id");
					String device_info = data.optString("device_info");
					String regist_time = data.optString("regist_time");
					String expire_time = data.optString("expire_time");

					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

					//long regist_timeL = simpleDateFormat.parse(regist_time).getTime();
					long expire_timeL = simpleDateFormat.parse(expire_time).getTime();
					if(expire_timeL<=uc.getDate()){
						msg.arg1 = 2;
						msg.obj = "此手机"+serviceTun.getString(R.string.more_than_use_time)+"\t"+simpleDateFormat.format(expire_timeL);
					}else {
						msg.obj = hint;
					}
				}
			}else if (code==1004){
				msg.arg1 = 2;
				msg.obj = hint;
			}else if(code==1006){
				msg.obj = hint;
			}


		}catch (Exception e){
			e.printStackTrace();
			msg.arg1 = 2;
			msg.obj = e.getMessage().toString();
		}
		}



		handler.sendMessage(msg);




		Log.d(LOG_TAG,"checkTime 2"+s1.format(uc.getDate()));

		return true;
	}




}
