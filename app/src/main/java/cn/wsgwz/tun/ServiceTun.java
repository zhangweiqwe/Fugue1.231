package cn.wsgwz.tun;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;
import android.telephony.PhoneStateListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.activity.ReleaseActivity;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;
import cn.wsgwz.tun.gravity.adapter.ReleaseAdapter;
import cn.wsgwz.tun.gravity.core.Config;
import cn.wsgwz.tun.gravity.core.ConfigJson;
import cn.wsgwz.tun.gravity.core.Socket5Proxy;
import cn.wsgwz.tun.gravity.helper.ConfigHelper;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.M;


public class ServiceTun extends VpnService implements Handler.Callback {

    private static final String TAG = ServiceTun.class.getSimpleName() + ".Service";
    private Socket5Proxy socket5Proxy = Socket5Proxy.getInstance();
    public static boolean alreadyStart;
    public static boolean checkTimeOk;

    private  TelephonyManager telephonyManager;




    public static final String STOP_SERVICE = "STOP_SERVICE";
    public static final String START_SERVICE = "START_SERVICE";
    public static final String RESTART_SERVICE = "RESTART_SERVICE";

    public static final int START_OK = 2001;
    public static final int RESTART_OK = 3001;
    public static final int CHECK_TIME = 6000;
    private static final int MORE_THAN_MAX_TIME = 4000;



    private Thread start_or_stop_thread;





    public String userAgent;
    private SharedPreferences prefs;;
    private ParcelFileDescriptor parcelFileDescriptor;

    private native void jni_init();

    private native double jni_start(int tun, boolean fwd53, int rcode, int loglevel);

    private native void jni_stop(int tun, boolean clr);

    private native int jni_get_mtu();

    private native int[] jni_get_stats();

    private static native void jni_pcap(String name, int record_size, int file_size);

    private native void jni_socks5(String addr, int port, String username, String password);

    private native void jni_done();


    public Handler handler;
    private boolean  phoneStateListenerFirst;
    private boolean phoneStateListenerStop;



    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MORE_THAN_MAX_TIME:
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                Toast.makeText(ServiceTun.this, Const.App.NAME_AND_VERSION_AND_CODE+ "\t" + "使用时间截止\n" + simpleDateFormat.format(new Date(((long) msg.obj))) + "\n" + "核心未启动!!!", Toast.LENGTH_LONG).show();
                stop(false);
                break;
            case START_OK:
                LogAdapter.addItem(getString(R.string.log_hint_start_success), null);
                break;
            case RESTART_OK:
                LogAdapter.addItem(getString(R.string.log_hint_restart_success), null);
                Toast.makeText(this,getString(R.string.log_hint_restart_success),Toast.LENGTH_SHORT).show();
                break;
            case CHECK_TIME:
                if (msg.arg1 == 0) {
                    LogAdapter.addItem((String) msg.obj, null);
                    checkTimeOk = true;
                } else if (msg.arg1 == 2) {
                    final String s0 =  (String) (msg.obj);
                    LogAdapter.addItem(s0,null,this);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            LogAdapter.addItem(getString(R.string.app_the_forthcoming), null);
                        }
                    }, 2000);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //throw  new RuntimeException(s0);
                            ApplicationEx applicationEx = (ApplicationEx) ServiceTun.this.getApplicationContext();
                            applicationEx.onTerminate();
                        }
                    }, 7000);
                } else if (msg.arg1 == 13) {
                    LogAdapter.addItem( (String) msg.obj, null);
                }
        }
        return true;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() ");
        phoneStateListenerFirst = true;
        phoneStateListenerStop = false;
        handler = new Handler(this);
        jni_init();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        userAgent = WebSettings.getDefaultUserAgent(this);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager .listen(phoneStateListener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);
        //AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //am.setInexactRepeating(AlarmManager.RTC, SystemClock.elapsedRealtime() + watchdog * 60 * 1000, watchdog * 60 * 1000, pi);


    }


    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            Log.d(TAG, "(state == TelephonyManager.DATA_CONNECTED) phoneStateListenerFirst " +phoneStateListenerFirst+" "+ state+" phoneStateListenerStop "+phoneStateListenerStop);
            if (phoneStateListenerFirst) {
                phoneStateListenerFirst = false;
                //Log.d(TAG,"phoneStateListenerFirst "+phoneStateListenerFirst);
                return;
            }
            if(phoneStateListenerStop){
                phoneStateListenerStop = false;
                return;
            }
            if (!prefs.getBoolean(Const.Prefs.KEY_CAPACITY_STATE, Const.Prefs.DEFAUL_VALUE_CAPACITY_STATE)) {

                if (state == TelephonyManager.DATA_CONNECTED) {
                    if (prefs.getBoolean(Const.Prefs.KEY_ENABLED, false)) {
                        if (alreadyStart) {
                            return;
                        }
                        jni_start();
                    }
                } else if (state == TelephonyManager.DATA_DISCONNECTED) {
                    if (prefs.getBoolean(Const.Prefs.KEY_ENABLED, false)) {
                        jni_stop();
                    }
                }

                return;
            }else {
                if (state == TelephonyManager.DATA_CONNECTED) {
                    if (prefs.getBoolean(Const.Prefs.KEY_ENABLED, false)) {
                        start();
                    }
                } else if (state == TelephonyManager.DATA_DISCONNECTED) {
                    stop(false);
                    if (prefs.getBoolean(Const.Prefs.KEY_ENABLED, false)) {
                        LogAdapter.clear(ServiceTun.this, false);
                        LogAdapter.addItem(getString(R.string.log_hint_wait_mobile_internet_connected), null);
                    }
                }
            }
            }




        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
        }
    };

    @Override
    public void onRevoke() { Log.d(TAG, "onRevoke()");super.onRevoke();}


    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand  --");
        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        Log.d(TAG, "onStartCommand  -->" + intent.getAction());
        String action = intent.getAction();
        if (action == null) {
            return super.onStartCommand(intent, flags, startId);
        }
        switch (action) {
            case START_SERVICE:
                if (!prefs.getBoolean(Const.Prefs.KEY_CAPACITY_STATE, Const.Prefs.DEFAUL_VALUE_CAPACITY_STATE)) {
                    LogAdapter.clear(ServiceTun.this, false);
                    start();
                    break;
                }
                if (Util.isConnected(ServiceTun.this) && !Util.isWifiActive(ServiceTun.this)) {
                    start();
                } else {
                    LogAdapter.clear(ServiceTun.this, false);
                    LogAdapter.addItem(getString(R.string.log_hint_wait_mobile_internet_connected), null);
                }
                break;
            case STOP_SERVICE:
                if (!prefs.getBoolean(Const.Prefs.KEY_CAPACITY_STATE, Const.Prefs.DEFAUL_VALUE_CAPACITY_STATE)) {
                    LogAdapter.clear(ServiceTun.this, false);
                    stop(false);
                    break;
                }
                stop(false);
                break;
            case RESTART_SERVICE:
                if (!prefs.getBoolean(Const.Prefs.KEY_CAPACITY_STATE, Const.Prefs.DEFAUL_VALUE_CAPACITY_STATE)) {
                    socket5Proxy.start(ServiceTun.this, true);
                    break;
                }
                socket5Proxy.start(ServiceTun.this, true);
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }








    private boolean jni_start() {
        if(parcelFileDescriptor==null){
            return false;
        }
        alreadyStart = true;
        jni_socks5("127.0.0.1", 1080, "", "");
        //jni_socks5("113.205.250.35", 8001, "", "");
        //jni_socks5("192.168.99.180", 8002, "", "");
        double maxTime = jni_start(parcelFileDescriptor.getFd(), false, 3, 7);
        long maxTimeL = (long) maxTime;
        long currentTimeMsec = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        String timeStr = simpleDateFormat.format(new Date(maxTimeL));
        Log.d(TAG,"使用时间+"+timeStr+"");

        if (maxTimeL < currentTimeMsec) {
            Message msg = Message.obtain();
            msg.obj = maxTimeL;
            msg.what = MORE_THAN_MAX_TIME;
            handler.sendMessage(msg);
            return false;
        }
        return true;

    }

    private void start() {

        if(start_or_stop_thread!=null){
            start_or_stop_thread.interrupt();
        }
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        break;
                    case 1001:
                        notifyStartState(0);
                        try {
                            if (parcelFileDescriptor != null) {parcelFileDescriptor.close();}
                        }catch (IOException e){
                            e.printStackTrace();
                            notifyStartState(1);
                            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED,false).apply();
                        }
                        break;
                    case 1002:
                        try {
                            parcelFileDescriptor = getBuilder().establish();
                            Log.d(TAG, "parcelFileDescriptor=" + parcelFileDescriptor);
                        } catch (SecurityException ex) {
                            ex.printStackTrace();
                            LogAdapter.addItem(ex.getMessage(), null);
                            notifyStartState(1);
                            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED,false).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                            LogAdapter.addItem( e.getMessage().toString(), null);
                            notifyStartState(1);
                            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED,false).apply();
                        }
                        break;
                    case 1003:
                        if (parcelFileDescriptor != null) {
                            if (jni_start()) {
                                socket5Proxy.start(ServiceTun.this, false);
                            }
                        }else {
                            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED,false).apply();
                        }
                        notifyStartState(1);
                        break;

                }
            }
        };

        start_or_stop_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                h.sendEmptyMessage(1001);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                h.sendEmptyMessage(1002);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                h.sendEmptyMessage(1003);

            }
        });
        start_or_stop_thread.start();
    }


    private void notifyStartState(int state){
        switch (state){
            case 0:
                Intent intent0 = new Intent(ActivityMain.Receiver.ACTION_BUSY);
                ServiceTun.this.sendBroadcast(intent0);
                break;
            case 1:
                Intent intent1 = new Intent(ActivityMain.Receiver.ACTION_FREE);
                ServiceTun.this.sendBroadcast(intent1);
                break;
        }
    }

    private void stop(final boolean jniDone) {
        if(start_or_stop_thread!=null){
            start_or_stop_thread.interrupt();
        }
        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1000:
                        jni_stop();
                        break;
                    case 1001:
                        stopVpn();
                        socket5Proxy.stop();
                        if(jniDone){
                            jni_done();
                            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED, false).apply();
                        }
                        Log.d(TAG,"--stop3>");

                        break;

                }
            }
        };
        start_or_stop_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"--stop1>");
                h.sendEmptyMessage(1000);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                h.sendEmptyMessage(1001);
                Log.d(TAG,"--stop2>");
            }
        });

        start_or_stop_thread.start();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
        phoneStateListenerStop = true;
        stop(true);
        telephonyManager .listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);


    }


    private Builder getBuilder() throws Exception{
        Builder builder = ServiceTun.this.new Builder();
        builder.setMtu(jni_get_mtu());
        builder.addAddress("10.1.10.1", 32);
        builder.addRoute("0.0.0.0", 0);
        builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128);
        builder.addRoute("0:0:0:0:0:0:0:0", 0);

        Config config = getConfig(false);
        if (config != null) {
            String dnsS = config.getDns();
            if (!dnsS.trim().equals("")) {
                String flag = ",";
                if (dnsS.contains(flag)) {
                    String[] args = dnsS.split(flag);
                    for (int i = 0; i < args.length; i++) {
                        String dnsI = args[i].trim();
                        if (!dnsI.equals("")) {
                            builder.addDnsServer(dnsI.trim());
                        }
                    }
                } else {
                    builder.addDnsServer(dnsS.trim());
                }
            }
        }

        builder.addDnsServer("2001:4860:4860::8888");
        builder.addDnsServer("2001:4860:4860::8844");

        builder.setSession(Const.App.NAME_AND_VERSION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = builder.allowBypass();
            try {
                builder.addDisallowedApplication(Const.App.PACKAGE_NAME);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (true) {
                PackageManager packageManager = this.getPackageManager();
                String str = prefs.getString(ReleaseAdapter.RELEASE_KEY, ReleaseAdapter.RELEASE_DEFAULT_VALE);
                if (str!=null&&str.trim().length() > 0) {
                    if (str.contains(",")) {
                        String[] packages = str.split(",");
                        for (int i = 0; i < packages.length; i++) {
                            try {
                                ApplicationInfo info = packageManager.getApplicationInfo(packages[i], 0);
                                builder.addDisallowedApplication(packages[i]);
                                LogAdapter.addItem(getString(R.string.release) + "\t" + packageManager.getApplicationLabel(info) + "\t" + info.packageName, null);
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            ApplicationInfo info = packageManager.getApplicationInfo(str, 0);
                            builder.addDisallowedApplication(str);
                            LogAdapter.addItem(getString(R.string.release) + "\t" + packageManager.getApplicationLabel(info) + "\t" + str, null);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

        }


        builder.setConfigureIntent(PendingIntent.getActivity(ServiceTun.this, 0, new Intent(ServiceTun.this, ActivityMain.class), PendingIntent.FLAG_UPDATE_CURRENT));
        return builder;
    }

    public Config getConfig(boolean needLog) throws Exception{
        Config config = null;
        String path = prefs.getString(Const.Prefs.KEY_CURRENT_CONFIG_PATH,null);
            final File configFile = path==null?null:new File(path);
            if (configFile==null||!configFile.exists()) {
                config = ConfigJson.readDefault();
                //Log.d(TAG,config==null?"config=null":config.toString());
                if(needLog){
                    LogAdapter.addItem(getString(R.string.log_hint_load_default_config), null);
                }
            } else {
                config = ConfigJson.read(new FileInputStream(configFile));
                if(needLog){

                    String s = config.toStringw();
                    SpannableString sS = new SpannableString(s);
                    //sS.setSpan(new UnderlineSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sS.setSpan(new RelativeSizeSpan(0.7f), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    LogAdapter.addItem(sS,new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfigHelper.getInstance().editConfig(ServiceTun.this, configFile, true);
                        }
                    });
                }
            }
        return config;
    }


    private void jni_stop() {
        if (parcelFileDescriptor != null) {
            try {
                jni_stop(parcelFileDescriptor.getFd(), true);
            } catch (Throwable ex) {
                ex.printStackTrace();
                jni_stop(-1, true);
            }
        } else {
            jni_stop(-1, true);
        }
        alreadyStart = false;
    }

    private void stopVpn() {
        if (parcelFileDescriptor != null) {
            try {
                parcelFileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            parcelFileDescriptor = null;
        }
    }



    public SharedPreferences getPrefs() {
        return prefs;
    }

    // Called from native code
    private void nativeExit(String reason) {

    }

    private void nativeError(int error, String message) {
    }

    // Called from native code
    private void logPacket(Packet packet) {
    }

    // Called from native code
    private void dnsResolved(ResourceRecord rr) {
        //rr.AName ="www.baidu.com";
        //rr.QName ="www.baidu.com";
        //ProxyTaskB.printf(rr.AName);
    }

    // Called from native code
    private boolean isDomainBlocked(String name) {
        //ProxyTaskB.printf(name.toString());
        return false;
    }

    private boolean isSupported(int protocol) {
        return (protocol == 1  /*ICMPv4 */ ||
                protocol == 59 /* ICMPv6 */ ||
                protocol == 6 /* TCP */ ||
                protocol == 17 /* UDP */
        );
    }

    private void accountUsage(Usage usage) {
        //ProxyTaskB.printf(usage.toString2());
    }

    // Called from native code
    private Allowed isAddressAllowed(Packet packet) {
        return new Allowed();
    }
    // Called from native code


}
