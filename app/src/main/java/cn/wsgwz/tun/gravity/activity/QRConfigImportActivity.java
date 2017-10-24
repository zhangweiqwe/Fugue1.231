package cn.wsgwz.tun.gravity.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.helper.ConfigHelper;
import cn.wsgwz.tun.gravity.helper.QRConfigHelper;

public class QRConfigImportActivity extends Activity {

    private static final String TAG = QRConfigImportActivity.class.getSimpleName();

    private static final int FINISH_TIME_CHANGE = 1000;

    private TextView hintTV;

    private Timer timerFinish;
    private int autoFinishTime = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case FINISH_TIME_CHANGE:
                    if(autoFinishTime==0){
                        timerFinish.cancel();
                        finish();
                        break;
                    }
                    String s0 = getString(R.string.import_qrconfig_success);
                    String s1 = autoFinishTime+"秒钟后结束";
                    String s2 = s0+s1;
                    SpannableString spannableString = new SpannableString(s2);

                    spannableString.setSpan(new RelativeSizeSpan(0.6f), s2.indexOf(s1), s2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    hintTV.setText(spannableString);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrconfig_import);
        hintTV = (TextView) findViewById(R.id.hintTV);


    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String scheme = intent.getScheme();


        Log.d(TAG, "scheme="+scheme);
        String dataString = intent.getDataString();
        Log.d(TAG, "dataString="+dataString);
        Uri uri = getIntent().getData();
        Log.d(TAG, "uri="+uri);
        if(uri!=null){
            String name= uri.getQueryParameter("name");
            String content= uri.getQueryParameter("content");
            String descript= uri.getQueryParameter("descript");

            QRConfigHelper.getInstance().saveConfig(name, content, this, new QRConfigHelper.OnConfigImportStateChangeListenner() {
                @Override
                public void success(final File file) {
                    hintTV.setText(getString(R.string.import_qrconfig_success));
                    hintTV.setClickable(true);
                    hintTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ConfigHelper.getInstance().editConfig(QRConfigImportActivity.this,file,false);
                        }
                    });
                    /*timerFinish = new Timer();
                    timerFinish.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            autoFinishTime-=1;
                            handler.sendEmptyMessage(FINISH_TIME_CHANGE);
                        }
                    },2000,1000);*/

                }

                @Override
                public void error(String msg) {
                    hintTV.setText(msg);
                }
            });
            Log.d(TAG, name+"\n"+content+"\n"+descript);
            Toast.makeText(this,descript,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timerFinish!=null){
            timerFinish.cancel();
        }
    }
}
