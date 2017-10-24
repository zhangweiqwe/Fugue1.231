package cn.wsgwz.tun.gravity.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.gravity.Const;

public class HtmlActivity extends SlidingAroundBaseActivity implements Handler.Callback,
        View.OnClickListener {

    private static final String TAG = HtmlActivity.class.getSimpleName();

    private static final int HTML_LOAD_STATE_CAHNGE = 1000;

    private Toolbar toolBar;

    // private OverScrollView overScrollView;
    private LinearLayout parent_LL;


    private EditText analysis_url, video_url,confirm_video_url, url_result,analysis_result;
    private Button ok;


    private Handler handler;

    private Thread beginAnalysisThread;


    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case HTML_LOAD_STATE_CAHNGE:

                if (msg.arg1 == 0) {
                    String analysis_urlStr = analysis_url.getText().toString();
                    String urlStr = analysis_result.getText().toString();
                    openUrl(analysis_urlStr + "" + urlStr, HtmlActivity.this);
                } else if (msg.arg1 == 1) {

                } else if (msg.arg1 == 5) {
                    url_result.setText(((StringBuilder) (msg.obj)).toString());
                    analysis_result.setText("http://v.youku.com/v_show/id_XMjg1MjMxOTg4NA==.html");
                }

                if (msg.arg1 == 0 || msg.arg1 == 1) {
                    Toast.makeText(this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);
        //overScrollView = (OverScrollView) findViewById(R.id.overScrollView);
        parent_LL = (LinearLayout) findViewById(R.id.parent_LL);
        setBackground(parent_LL);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle(getString(R.string.vip_video_analysis));
        setActionBar(toolBar);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        handler = new Handler(this);


        analysis_result = (EditText) findViewById(R.id.analysis_result);
        analysis_url = (EditText) findViewById(R.id.analysis_url);
        confirm_video_url = (EditText) findViewById(R.id.confirm_video_url);
        video_url = (EditText) findViewById(R.id.video_url);
        url_result = (EditText) findViewById(R.id.url_result);


        video_url.setText("http://v.youku.com/v_show/id_XMjgzMDE0NjQxNg==.html?spm=a2hww.20023042.md223508.1~3!4~5~5~A");

        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);


    }

    private void beginAnalysisAsync(final String url) {
        if (analysis_url == null) {
            return;
        }
        if (beginAnalysisThread != null) {
            beginAnalysisThread.interrupt();
        }
        beginAnalysisThread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    beginAnalysis(url);
                    Message msg = Message.obtain();
                    msg.what = HTML_LOAD_STATE_CAHNGE;
                    msg.obj = HtmlActivity.this.getString(R.string.success);
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = Message.obtain();
                    msg.what = HTML_LOAD_STATE_CAHNGE;
                    msg.arg1 = 1;
                    msg.obj = e.getMessage();
                    handler.sendMessage(msg);
                }
            }
        });

        beginAnalysisThread.start();
    }


    private File saveVipHtmlPage(String str) throws IOException {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = "Vip";
        File file = new File(Const.MAIN_HTML_FOLDER_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(file.getAbsolutePath(), name + "(" + format.format(new Date()) + ")" + ".Html");
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        InputStream in = new ByteArrayInputStream(str.getBytes());
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();

        return file;
    }

    private void beginAnalysis(String urlStr) throws IOException {

        URL url = new URL(urlStr);
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 1080));
        URLConnection urlConnection = null;
        if (ServiceTun.alreadyStart) {
            urlConnection = url.openConnection(proxy);
        } else {
            urlConnection = url.openConnection();
        }

        urlConnection.setUseCaches(false);
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);

        urlConnection.connect();


        InputStream in = urlConnection.getInputStream();

        byte[] buffer = new byte[1024];
        int len = 0;

        StringBuilder sb = new StringBuilder();
        while ((len = in.read(buffer)) != -1) {
            String z = new String(buffer, 0, len);
            sb.append(z);
            //Log.d(TAG,"-->"+z);
        }
        //Log.d(TAG,"--> over"+sb.toString());


        in.close();


        if (true) {
            Message msg = Message.obtain();
            msg.what = HTML_LOAD_STATE_CAHNGE;
            msg.obj = sb;
            msg.arg1 = 5;
            handler.sendMessage(msg);
        }


        File file = saveVipHtmlPage(sb.toString());

        if (true) {
            Message msg = Message.obtain();
            msg.what = HTML_LOAD_STATE_CAHNGE;
            msg.obj = file;
            msg.arg1 = 3;
            handler.sendMessage(msg);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                String analysis_urlStr = analysis_url.getText().toString();
                String confirm_video_urlStr = confirm_video_url.getText().toString();

                if (TextUtils.isEmpty(analysis_urlStr)) {
                    Toast.makeText(this, getString(R.string.analysis_wbset_str_not_null), Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!TextUtils.isEmpty(confirm_video_urlStr)){
                    openUrl(analysis_urlStr+""+confirm_video_urlStr,HtmlActivity.this);
                    return;
                }

                String video_urlStr = video_url.getText().toString();

                analysis_result.setText(null);
                beginAnalysisAsync(video_urlStr);


                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (beginAnalysisThread != null) {
            beginAnalysisThread.interrupt();
        }
    }

    private void openUrl(String url, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    private void openHtml(File file, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, "cn.wsgwz.tun", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "text/html");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "text/html");
        }
        context.startActivity(intent);
    }

}
