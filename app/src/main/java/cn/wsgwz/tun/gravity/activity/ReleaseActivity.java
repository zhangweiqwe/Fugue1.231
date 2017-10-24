package cn.wsgwz.tun.gravity.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.Util;
import cn.wsgwz.tun.gravity.adapter.ReleaseAdapter;

public class ReleaseActivity extends SlidingAroundBaseActivity {
    private RecyclerView recyclerView;
    private ReleaseAdapter releaseAdapter;
    private Toolbar toolBar;

    private LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_release);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        setBackground(linearLayout);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle(getString(R.string.release));
        //toolBar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);

        setActionBar(toolBar);

        getActionBar().setDisplayHomeAsUpEnabled(true);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ReleaseActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(ReleaseActivity.this, DividerItemDecoration.VERTICAL));
        releaseAdapter = new ReleaseAdapter(ReleaseActivity.this);
        recyclerView.setAdapter(releaseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        menu.add(10,101,0,getString(R.string.save));
        menu.findItem(101).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 101:
                if(releaseAdapter!=null){
                    releaseAdapter.save();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
