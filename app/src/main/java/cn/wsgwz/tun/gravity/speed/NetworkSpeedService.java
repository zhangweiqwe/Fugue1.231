package cn.wsgwz.tun.gravity.speed;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.tun.R;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NetworkSpeedService extends Service {
    public static final int DEFAULT_SPPEED_REFRESH_TIME = 700;
    public static final int DEFAULT_SPPEED_REFRESH_TV_TEXT_SIZE = 12;
    private boolean isStart;


    private WindowManager windowManager;
    private Timer timer;
    private long allTx , allRx   ,lastAllTx,lastAllRx   ,speedTx,speedRx,  tempAllTx,tempAllRX;
    private TextView speedTv;

    private SettingHelper settingHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        settingHelper = SettingHelper.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {
        if(isStart){Toast.makeText(NetworkSpeedService.this,"重复启动",Toast.LENGTH_SHORT).show();return super.onStartCommand(intent, flags, startId);}
        show(intent.getIntExtra("refreshTime",DEFAULT_SPPEED_REFRESH_TIME),intent.getIntExtra("refreshTVSize",DEFAULT_SPPEED_REFRESH_TV_TEXT_SIZE),this);
        isStart = true;
        Toast.makeText(NetworkSpeedService.this,getString(R.string.speed_already_start),Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy();
        isStart = false;
        Toast.makeText(NetworkSpeedService.this,getString(R.string.speed_already_stop),Toast.LENGTH_SHORT).show();
    }

    public void show(final int refreshTime,final int tvSize,final Context context){
        speedTv = new TextView(context);
        speedTv.setTextSize(TypedValue.COMPLEX_UNIT_SP,tvSize);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        PackageManager pm = context.getPackageManager();
        //2.遍历手机操作系统 获取所有的应用程序的uid
        ApplicationInfo appcationInfo = null;
        try {
            appcationInfo = pm.getApplicationInfo(context.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int uid = appcationInfo.uid;    // 获得软件uid
        //proc/uid_stat/10086
        tempAllTx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
        tempAllRX = TrafficStats.getUidRxBytes(uid);//下载的流量 byte

        /*lastAllTx = TrafficStats.getUidTxBytes(uid);
        lastAllRx = TrafficStats.getUidRxBytes(uid);*/


        //方法返回值 -1 代表的是应用程序没有产生流量 或者操作系统不支持流量统计
        final java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.##");
       /*英 [sə'spend]   美 [sə'spɛnd]   全球发音 跟读 口语练习
        vt. 延缓，推迟；使暂停；使悬浮
        vi. 悬浮；禁赛*/

        final WindowManager.LayoutParams wLayoutParams = new WindowManager.LayoutParams();
        wLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        wLayoutParams.format = PixelFormat.RGBA_8888;
        wLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wLayoutParams.gravity = Gravity.START | Gravity.TOP;
        wLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wLayoutParams.x = (int) settingHelper.getSpeedSuspensionX(context);
        wLayoutParams.y = (int) settingHelper.getSpeedSuspensionY(context);




        final int kbUnit = 1024;
        final int mUnit = (int) Math.pow(kbUnit,2);
        final Handler speedRefreshHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 1000:
                        double tA=((double) (allTx-tempAllTx))/mUnit,rA=((double) (allRx-tempAllRX))/mUnit;
                        long tC=(speedTx/kbUnit),rC=(speedRx/kbUnit);
                        StringBuilder sb = new StringBuilder();
                        sb.append("↑"+decimalFormat.format(tA)+"m");
                        sb.append("\n");
                        sb.append("↓"+decimalFormat.format(rA)+"m");
                        sb.append("\n");
                        sb.append("↑"+decimalFormat.format(tC)+"kb/s");
                        sb.append("\n");
                        sb.append("↓"+decimalFormat.format(rC)+"kb/s");

                        speedTv.setText(sb);

                        break;
                }
                super.handleMessage(msg);
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                allTx = TrafficStats.getUidTxBytes(uid);//发送的 上传的流量byte
                allRx = TrafficStats.getUidRxBytes(uid);//下载的流量 byte

                speedTx = allTx - lastAllTx;
                speedRx = allRx - lastAllRx;
                lastAllTx = allTx;
                lastAllRx = allRx;

                speedRefreshHandler.sendEmptyMessage(1000);
            }
        },refreshTime,refreshTime);
        class MyOnTounchListenner implements View.OnTouchListener{
            int lastX = 0, lastY = 0;
            int paramX = 0, paramY = 0;
            long exitTime=0;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = wLayoutParams.x;
                        paramY = wLayoutParams.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        wLayoutParams.x = paramX + dx;
                        wLayoutParams.y = paramY + dy;
                        // 更新悬浮窗位置
                        windowManager.updateViewLayout(speedTv, wLayoutParams);
                        settingHelper.setSpeedSuspensionX(context,wLayoutParams.x);
                        settingHelper.setSpeedSuspensionY(context,wLayoutParams.y);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((System.currentTimeMillis() - exitTime) < 300) {
                            // createWindow(context, type);
                            //LogUtil.printSS("-----------------------<--------->");
                            setColor(context);
                            return true;
                        } else {
                            exitTime = System.currentTimeMillis();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        }

        speedTv.setOnTouchListener(new MyOnTounchListenner());
        String  color = settingHelper.getSuspensionColor(context);
        int colorCode = 0;
        if(color==null||color.length()<7){
            //LogUtil.printSS("---->"+color+"<----");
            setColor(context);
        }else {
            colorCode = Color.parseColor(color);
            speedTv.setTextColor(colorCode);
        }

        windowManager.addView(speedTv,wLayoutParams);
    }

    public void destroy(){
        if(speedTv!=null){windowManager.removeView(speedTv);}
        if(timer!=null){timer.cancel();}
    }

    private final void setColor(Context context){
        String color = "#"+getRandColorCode();
        int colorCode = Color.parseColor(color);
        settingHelper.setSuspensionColor(context,color);
        speedTv.setTextColor(colorCode);

    }
    private final String getRandColorCode(){
        String r,g,b;
        Random random = new Random();
        r = Integer.toHexString(random.nextInt(256)).toUpperCase();
        g = Integer.toHexString(random.nextInt(256)).toUpperCase();
        b = Integer.toHexString(random.nextInt(256)).toUpperCase();

        r = r.length()==1 ? "0" + r : r ;
        g = g.length()==1 ? "0" + g : g ;
        b = b.length()==1 ? "0" + b : b ;

        return r+g+b;
    }
}
