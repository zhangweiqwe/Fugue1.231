package cn.wsgwz.tun.gravity.helper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import java.util.List;

import cn.wsgwz.tun.ActivityMain;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;

/**
 * Created by Administrator on 2017/6/1.
 */

public class SMS {

    private static final String TAG = SMS.class.getSimpleName();
    public void sendSMS( Context context,String phoneNumber, String message){
        /*//获取短信管理器
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        //拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);

        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(context, ActivityMain.class), 0);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, pi, null);
        }*/

        String SENT = "sms_sent";
        String DELIVERED = "sms_delivered";

        PendingIntent sentPI = PendingIntent.getActivity(context, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getActivity(context, 0, new Intent(DELIVERED), 0);

        context.registerReceiver(new BroadcastReceiver(){

            @Override
            public void onReceive(Context context, Intent intent) {
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "Activity.RESULT_OK");
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Log.i(TAG, "RESULT_ERROR_GENERIC_FAILURE");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Log.i(TAG, "RESULT_ERROR_NO_SERVICE");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Log.i(TAG, "RESULT_ERROR_NULL_PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Log.i(TAG, "RESULT_ERROR_RADIO_OFF");
                        break;
                }
            }
        }, new IntentFilter(SENT));

        context.registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent){
                switch(getResultCode())
                {
                    case Activity.RESULT_OK:
                        Log.i(TAG, "RESULT_OK");
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(TAG, "RESULT_CANCELED");
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager smsm = SmsManager.getDefault();
        smsm.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);


    }
}
