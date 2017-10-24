package cn.wsgwz.tun.gravity.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import cn.wsgwz.tun.ActivityMain;
import cn.wsgwz.tun.ApplicationEx;
import cn.wsgwz.tun.R;
import cn.wsgwz.tun.Util;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.view.MainSlidingPaneLayout;


/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class SlidingAroundBaseActivity extends Activity implements SlidingPaneLayout.PanelSlideListener {

    private static final String TAG = SlidingAroundBaseActivity.class.getSimpleName();

    private View leftView;
    private MainSlidingPaneLayout slidingPaneLayout;

    private float proportionSliding = 0.478f;
    private float lastX;
    private int screenCanMotionMoveX;
    private boolean needFinish;

    private DisplayMetrics displayMetrics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initSwipeBackFinish();
        super.onCreate(savedInstanceState);
        //getWindow().setWindowAnimations(R.style.payDialogStyleAnimation);
        ((ApplicationEx) this.getApplicationContext()).addActivity(this);


        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenCanMotionMoveX = (int) (displayMetrics.widthPixels * proportionSliding);
    }



    /*public void setProportionSliding(float proportionSliding) {
        this.proportionSliding = proportionSliding;
        screenCanMotionMoveX = (int) (displayMetrics.widthPixels*proportionSliding);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ApplicationEx) this.getApplicationContext()).removeActivity(this);
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastX  = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                if((ev.getX()-lastX)>screenCanMotionMoveX){
                    needFinish = true;
                }else {
                    needFinish = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(needFinish){
                    finish();
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }*/


    protected void setBackground(View v) {
        Util.setBackground(v);
    }

    /**
     * 初始化滑动返回
     */
    private void initSwipeBackFinish() {
        if (isSupportSwipeBack()) {
            slidingPaneLayout = new MainSlidingPaneLayout(this);
            //通过反射改变mOverhangSize的值为0，这个mOverhangSize值为菜单到右边屏幕的最短距离，默认
            //是32dp，现在给它改成0
            try {
                //属性
                Field f_overHang = SlidingPaneLayout.class.getDeclaredField("mOverhangSize");
                f_overHang.setAccessible(true);
                f_overHang.set(slidingPaneLayout, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            slidingPaneLayout.setShadowResourceLeft(R.drawable.sliding_left_shadow);
            slidingPaneLayout.setPanelSlideListener(this);
            slidingPaneLayout.setSliderFadeColor(Color.TRANSPARENT);

            leftView = new View(this);
            leftView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            leftView.setBackgroundColor(Color.parseColor("#66000000"));
            slidingPaneLayout.addView(leftView, 0);

            ViewGroup decor = (ViewGroup) getWindow().getDecorView();
            ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);

            decorChild.setBackgroundColor(Color.TRANSPARENT);
            decor.removeView(decorChild);
            decor.addView(slidingPaneLayout);
            slidingPaneLayout.addView(decorChild, 1);
        }
    }

    /**
     * 是否支持滑动返回
     *
     * @return
     */
    protected boolean isSupportSwipeBack() {
        return true;
    }

    @Override
    public void onPanelClosed(View view) {

    }

    @Override
    public void onPanelOpened(View view) {
        finish();
        //this.overridePendingTransition(0, R.anim.slide_out_right);
    }

    @Override
    public void onPanelSlide(View view, float v) {
        leftView.setAlpha(1 - v);
        if(v>0.98){
            slidingPaneLayout.setShadowDrawableLeft(null);
        }else if (v > 0.95) {
            slidingPaneLayout.setShadowResourceLeft(R.drawable.sliding_left_shadow_over);
        }
    }
}
