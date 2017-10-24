package cn.wsgwz.tun.gravity.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import cn.wsgwz.tun.R;

/**
 * Created by Administrator on 2017/6/26.
 */

public class MainSlidingPaneLayout extends SlidingPaneLayout {
    private DisplayMetrics displayMetrics;
    private float lastX;
    private static final float proportionSliding =  0.08f;
    private float screenCanMotionMoveX;
    private void init(Context context){
        displayMetrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenCanMotionMoveX = (displayMetrics.widthPixels*proportionSliding);
    }

    public void setScreenCanMotionMoveX(float proportionSliding){
        screenCanMotionMoveX = (displayMetrics.widthPixels*proportionSliding);
    }

    public MainSlidingPaneLayout(Context context) {
        super(context);
        init(context);
    }

    public MainSlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainSlidingPaneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /*if(!this.isOpen()){
            switch (MotionEventCompat.getActionMasked(ev)) {
                case MotionEvent.ACTION_DOWN: {
                    lastX = ev.getX();
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    if (lastX > screenCanMotionMoveX ) {
                        MotionEvent cancelEvent = MotionEvent.obtain(ev);
                        cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                        return super.onInterceptTouchEvent(cancelEvent);
                    }
                }
            }
        }*/


        return super.onInterceptTouchEvent(ev);
    }

}
