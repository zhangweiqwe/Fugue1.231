package cn.wsgwz.tun;

/*
    This file is part of NetGuard.

    NetGuard is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    NetGuard is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with NetGuard.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2015-2017 by Marcel Bokhorst (M66B)
*/

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.activity.HtmlActivity;
import cn.wsgwz.tun.gravity.activity.ReleaseActivity;
import cn.wsgwz.tun.gravity.activity.SelectConfigActivity;
import cn.wsgwz.tun.gravity.activity.SlidingAroundBaseActivity;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;
import cn.wsgwz.tun.gravity.encryption.SecurityUtils;
import cn.wsgwz.tun.gravity.helper.ConfigHelper;
import cn.wsgwz.tun.gravity.helper.QRConfigHelper;
import cn.wsgwz.tun.gravity.helper.UpdateHelper;
import cn.wsgwz.tun.gravity.view.MainSlidingPaneLayout;
import cn.wsgwz.tun.gravity.view.OverScrollView;
import cn.wsgwz.tun.gravity.view.switchbutton.SwitchButton;

public class ActivityMain extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener, View.OnLongClickListener,
        SlidingPaneLayout.PanelSlideListener
{
    private static final String TAG = ActivityMain.class.getSimpleName();


    private SharedPreferences prefs;


    private Intent intentTunService;

    private SwitchButton swEnabled;


    private RecyclerView recyclerView;


    private ProgressBar progressBar;

    public static final int REQUEST_CODE_SELECT_WALLPAPER = 10014;

    private static final int REQUEST_PERMISSIONS = 16110;
    private static final String[] PERMISSIONS =  new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE
    };



    private static final int REQUEST_VPN = 1;





    private TextView update;
    private Receiver receiver;


   // private MainSlidingPaneLayout slidingPaneLayout;
    //private OverScrollView contentOverScrollView;

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

  /*  @Override
    protected void onStart() {
        super.onStart();
        //overridePendingTransition(R.anim.main_start_animation,0);
    }*/



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setWindowAnimations(R.style.payDialogStyleAnimation1);


        ((ApplicationEx) this.getApplicationContext()).addActivity(this);

        Util.setBackground(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);


        setContentView(R.layout.main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for(int i=0;i<PERMISSIONS.length;i++){
                    if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, PERMISSIONS[i])) {
                        ActivityCompat.requestPermissions(this,PERMISSIONS, REQUEST_PERMISSIONS);
                    }
                }
        }
        swEnabled = (SwitchButton) findViewById(R.id.swEnabled);
        boolean enabled = prefs.getBoolean(Const.Prefs.KEY_ENABLED, false);
        swEnabled.setChecked(enabled);
        swEnabled.setOnCheckedChangeListener(this);

        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Receiver.ACTION_BUSY);
        intentFilter.addAction(Receiver.ACTION_FREE);
        registerReceiver(receiver, intentFilter);

       /* contentOverScrollView = (OverScrollView) findViewById(R.id.contentOverScrollView);
        contentOverScrollView.setBackgroundColor(Color.parseColor("#11000000"));*/
      /*  slidingPaneLayout = (MainSlidingPaneLayout) findViewById(R.id.slidingPaneLayout);
        slidingPaneLayout.setPanelSlideListener(this);
        slidingPaneLayout.setScreenCanMotionMoveX(1f);
        slidingPaneLayout.setShadowResourceLeft(R.drawable.main_menu_sliding_left_shadow);*/

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LogAdapter logAdapter = LogAdapter.getInstance();
        recyclerView.setAdapter(logAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);



        findViewById(R.id.icon).setOnClickListener(this);
        findViewById(R.id.icon).setOnLongClickListener(this);
        TextView select = (TextView) findViewById(R.id.select);
        select.setOnClickListener(this);




        TextView pass = (TextView) findViewById(R.id.pass);
        pass.setOnClickListener(this);
        findViewById(R.id.release).setOnClickListener(this);
        findViewById(R.id.menu_about).setOnClickListener(this);
        TextView wallpaper = (TextView) findViewById(R.id.wallpaper);
        wallpaper.setOnClickListener(this);
        wallpaper.setOnLongClickListener(this);

        CheckBox capacity_state_ck = (CheckBox) findViewById(R.id.capacity_state_ck);
        capacity_state_ck.setChecked(prefs.getBoolean(Const.Prefs.KEY_CAPACITY_STATE, Const.Prefs.DEFAUL_VALUE_CAPACITY_STATE));
        capacity_state_ck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.d(TAG, "capacity_state_ck  onCheckedChanged" + isChecked);
                prefs.edit().putBoolean(Const.Prefs.KEY_CAPACITY_STATE, isChecked).apply();
                prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED, false).apply();
            }
        });


        update = (TextView) findViewById(R.id.update);

        update.setOnClickListener(this);




        intentTunService = new Intent(this, ServiceTun.class);

        prefs.registerOnSharedPreferenceChangeListener(this);

        if (prefs.getBoolean(Const.Prefs.KEY_FIRST_USE, true)) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {


                    prefs.edit().putBoolean(Const.Prefs.KEY_FIRST_USE, false).apply();
                    return false;
                }
            });
        }

        if (!enabled) {
            Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
                @Override
                public boolean queueIdle() {
                    startService(intentTunService);
                    return false;
                }
            });

        }


        //startActivity(new Intent(this, HtmlActivity.class));


        //ConfigJsonHelper.getInstance().conver();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.pass:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.pass_hint));
                final EditText editText = new EditText(this);
                editText.setHint(getString(R.string.in_put_pass));
                editText.setText(prefs.getString(Const.Prefs.KEY_FREE,Const.Prefs.DEFAUL_VALUE_FREE));
                builder.setView(editText);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prefs.edit().putString(Const.Prefs.KEY_FREE,editText.getText().toString()).apply();
                    }
                });
                builder.setNegativeButton(getString(R.string.no),null);
                Dialog dialog = builder.create();
                dialog.show();

                break;
            case R.id.update:
                new UpdateHelper().check(ActivityMain.this, update);
                break;
            case R.id.release:
                startActivity(new Intent(this, ReleaseActivity.class));
                break;
            case R.id.menu_about:
                startActivity(new Intent(ActivityMain.this, AboutActivity.class));
                break;


            case R.id.wallpaper:
                if (true) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    try {
                        startActivityForResult(intent, REQUEST_CODE_SELECT_WALLPAPER);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.select:
                Intent intent2 = new Intent(ActivityMain.this, SelectConfigActivity.class);
                startActivity(intent2);
                break;

        }
    }

    private void showConfigConverJsonWindow() {
        PopupWindow popupWindow;
        View view = getLayoutInflater().inflate(R.layout.view_config_conver_json, null);

        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);


        final EditText josn_name, josn_explain, josn_text, josn_conver_result;
        josn_name = (EditText) view.findViewById(R.id.josn_name);
        josn_explain = (EditText) view.findViewById(R.id.josn_explain);
        josn_text = (EditText) view.findViewById(R.id.josn_text);
        josn_conver_result = (EditText) view.findViewById(R.id.josn_conver_result);
        popupWindow.showAsDropDown(findViewById(R.id.icon));

        final Button begin_json_conver = (Button) view.findViewById(R.id.begin_json_conver);
        begin_json_conver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("name", josn_name.getText().toString());
                    jsonObject.put("explain", josn_explain.getText().toString());
                    jsonObject.put("text", josn_text.getText().toString());
                    josn_conver_result.setText(jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        final Button config_encrypt = (Button) view.findViewById(R.id.config_encrypt);
        config_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    josn_explain.setText(SecurityUtils.getInstance().encrypt(josn_name.getText().toString()));
                }catch (Exception e){
                    LogAdapter.addItem(e.getMessage().toString(),null);
                }
            }
        });

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.icon:
                showConfigConverJsonWindow();
                break;
            case R.id.wallpaper:
                if(true){
                    String uriPath = prefs.getString(Const.Prefs.KEY_BG,null);
                    if(uriPath==null){return false;}
                    String bgPath = prefs.getString(Const.Prefs.KEY_BG,null);
                    if(bgPath==null){return false;}
                    File bgFile = new File(bgPath);
                    if(bgFile.exists()){
                        bgFile.delete();
                        String s = getString(R.string.already_delate_wallpaper);
                        LogAdapter.addItem(s,null,this);
                        recreate();
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "--> onCheckedChanged" + isChecked);
        if (isChecked) {
            if ((!ServiceTun.checkTimeOk && !Util.isConnected(this))) {
                String s = getString(R.string.please_open_network_retry);
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                LogAdapter.addItem(new SpannableString(s), null);
                swEnabled.setChecked(false);
                return;
            }
            if (ServiceTun.alreadyStart) {
                return;
            }
            Intent intent = VpnService.prepare(ActivityMain.this);
            if (intent != null) {
                startActivityForResult(intent, REQUEST_VPN);
            } else {
                onActivityResult(REQUEST_VPN, RESULT_OK, null);
            }
        } else {
            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED, isChecked).apply();
            if (!ServiceTun.alreadyStart) {
                return;
            }
            intentTunService.setAction(ServiceTun.STOP_SERVICE);
            startService(intentTunService);
            LogAdapter.clear(ActivityMain.this, false);
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String name) {
        Log.i(TAG, "Preference " + name + "=" + prefs.getAll().get(name));
        if (Const.Prefs.KEY_ENABLED.equals(name)) {
            boolean enabled = prefs.getBoolean(name, false);
            if (swEnabled != null && swEnabled.isChecked() != enabled) {
                swEnabled.setChecked(enabled);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_VPN) {
            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED, resultCode == RESULT_OK).apply();
            if (resultCode == RESULT_OK) {
                intentTunService.setAction(ServiceTun.START_SERVICE);
                startService(intentTunService);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.msg_vpn_cancelled, Toast.LENGTH_LONG).show();
                swEnabled.setChecked(false);
            }
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_SELECT_WALLPAPER) {
            Uri uri = data.getData();
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                File bgFile = new File(Const.MAIN_FOLDER_PATH + "/" + Const.Prefs.KEY_BG);
                FileOutputStream out = new FileOutputStream(bgFile);
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                in.close();
                prefs.edit().putString(Const.Prefs.KEY_BG, bgFile.toString()).apply();
                Util.setBackground(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            for(int i=0;i<grantResults.length;i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                 AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.the_lack_of_permission);
                    builder.setMessage(R.string.please_allow_permission);
                    builder.setCancelable(false);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    builder.create().show();
                    break;
                }
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        ((ApplicationEx) this.getApplicationContext()).removeActivity(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        unregisterReceiver(receiver);
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
    }

    @Override
    public void onPanelOpened(View panel) {

    }

    @Override
    public void onPanelClosed(View panel) {

    }


    public class Receiver extends BroadcastReceiver {
        static final String ACTION_BUSY = "cn.wsgwz.tun.ActivityMain.ACTION_BUSY";
        static final String ACTION_FREE = "cn.wsgwz.tun.ActivityMain.ACTION_FREE";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                switch (action) {
                    case ACTION_BUSY:
                        progressBar.setVisibility(View.VISIBLE);
                        LogAdapter.clear(ActivityMain.this, false);
                        break;
                    case ACTION_FREE:
                        progressBar.setVisibility(View.INVISIBLE);
                        break;
                }

            }

        }
    }


}
