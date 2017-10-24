package cn.wsgwz.tun.gravity.view;



import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/5/8 0008.
 */

public class FitStatusBarView extends View{

    private final float mDensity;

    public FitStatusBarView(Context context) {
        this(context,null);
    }

    public FitStatusBarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FitStatusBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(Color.TRANSPARENT);
        this.mDensity = getContext().getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int statusBarHeight = getStatusBarHeight();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT || isInEditMode()){
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), isInEditMode() ? (int) (20 * mDensity) : statusBarHeight);
        }else{
            setMeasuredDimension(0,0);
        }
    }


    /**
     * 获取状态栏的高度
     *
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = (int) getContext().getResources().getDimension(resourceId);
        }
        return result;
    }
}