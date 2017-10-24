package cn.wsgwz.tun.gravity.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.net.URI;
import java.net.URL;

import cn.wsgwz.tun.R;

public class WebViewActivity extends SlidingAroundBaseActivity {
    public static final String URL_KEY = "URL_KEY";
    private WebView webView;
    private String url;
    private FrameLayout frameLayoutParent,frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_web_view);



        Object obj = getIntent().getSerializableExtra(URL_KEY);
        url = obj == null ? null : (String) obj;

        frameLayoutParent = (FrameLayout) findViewById(R.id.frameLayoutParent);
        setBackground(frameLayoutParent);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);


        webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(0);


        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);



        webView.setWebChromeClient(new MyWebChromeClient());

        if(url!=null){
            webView.loadUrl(url);
        }
    }

    public class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webView.setVisibility(View.INVISIBLE);

            frameLayout.addView(view);
            webView.setVisibility(View.INVISIBLE);

        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            webView.setVisibility(View.VISIBLE);
            frameLayout.setVisibility(View.INVISIBLE);
            frameLayout.removeAllViews();
        }

        @Override
        public View getVideoLoadingProgressView() {
            return super.getVideoLoadingProgressView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        }
        return false;
    }
}
