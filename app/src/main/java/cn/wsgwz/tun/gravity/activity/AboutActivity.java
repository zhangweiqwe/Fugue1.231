package cn.wsgwz.tun.gravity.activity;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import cn.wsgwz.tun.ActivityMain;
import cn.wsgwz.tun.R;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;
import cn.wsgwz.tun.gravity.view.OverScrollView;


public class AboutActivity extends SlidingAroundBaseActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    private TextView tv;

    private Toolbar toolBar;

    private OverScrollView overScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        overScrollView = (OverScrollView) findViewById(R.id.overScrollView);
        setBackground(overScrollView);

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle(getString(R.string.menu_about));
        setActionBar(toolBar);

        getActionBar().setDisplayHomeAsUpEnabled(true);



        tv = (TextView) findViewById(R.id.tv);


        TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        final String DEVICE_ID = tm.getDeviceId();
        /*String str =
                "我思故我在\n\n"+
                        "DEVICE_ID\t"+DEVICE_ID+"\n";

        SpannableString spannableString = new SpannableString(str);
        int start = str.indexOf(DEVICE_ID);
        int end = start+DEVICE_ID.length();
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ClipboardManager clipboardManager = (ClipboardManager)AboutActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("DEVICE_ID", DEVICE_ID));
                Toast.makeText(AboutActivity.this,AboutActivity.this.getString(R.string.already_copy)+"\n"+DEVICE_ID,Toast.LENGTH_SHORT).show();
            }
        },start,end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(spannableString);*/

        tv.setText("我思故我在\n\n");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*menu.clear();
        menu.add(10,101,0,getString(R.string.feedback));
        menu.findItem(101).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 101:
                String url="mqqwpa://im/chat?chat_type=wpa&uin=857899299";
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }catch (ActivityNotFoundException e){
                    Toast.makeText(AboutActivity.this,getString(R.string.please_install_qq),Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }




}
