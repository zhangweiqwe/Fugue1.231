package cn.wsgwz.tun.gravity.activity;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.List;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.Util;
import cn.wsgwz.tun.gravity.adapter.ConfigAdapter;
import cn.wsgwz.tun.gravity.core.Config;
import cn.wsgwz.tun.gravity.helper.ConfigHelper;
import cn.wsgwz.tun.gravity.helper.OnGetRemoteConfigsListenner;
import cn.wsgwz.tun.gravity.view.OverScrollView;

import static android.content.ContentValues.TAG;


public class SelectConfigActivity extends SlidingAroundBaseActivity{
    private static final String TAG = SelectConfigActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ConfigAdapter configAdapter;
    private ConfigHelper configHelper;
    private Toolbar toolBar;

    private OverScrollView overScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_config);
        overScrollView = (OverScrollView) findViewById(R.id.overScrollView);
        setBackground(overScrollView);


        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle(getString(R.string.select));
        //toolBar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);

        setActionBar(toolBar);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        configHelper = ConfigHelper.getInstance();

        configAdapter = new ConfigAdapter(configHelper.getConfigs(),SelectConfigActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(configAdapter);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selcet_config,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.create:
                configHelper.createNewConfig(this, new ConfigHelper.OnCreateNewFileListenner() {
                    @Override
                    public void success() {
                        configAdapter.refresh();
                    }
                });
                break;
            case R.id.remote:

                final ProgressDialog progressDialog = new ProgressDialog(SelectConfigActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setTitle(getString(R.string.get_remote_configs));
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                ConfigHelper.getInstance().getRemoteConfigs(new OnGetRemoteConfigsListenner() {
                    @Override
                    public void start() {
                        Log.d(TAG,"start---");
                        progressDialog.show();
                    }

                    @Override
                    public void finish(String msg, int state) {
                        progressDialog.dismiss();
                        Toast.makeText(SelectConfigActivity.this,msg,Toast.LENGTH_LONG).show();
                        if(state==0){
                            configAdapter.refresh();
                        }
                        Log.d(TAG,msg);

                    }


                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
