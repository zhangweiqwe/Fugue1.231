package cn.wsgwz.tun;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Interpolator;

/**
 * An interpolator where the rate of change starts and ends slowly but
 * accelerates through the middle.
 *
 */
/**
 * 英 [ək'seləreɪt]  美 [əkˈsɛləˌret]
 vt. 使……加快；使……增速

 decelerate
 英 [diː'seləreɪt]  美 [,di'sɛləret]
 vi. 减速，降低速度
 */
public class AccelerateDecelerateInterpolator implements Interpolator {
    public AccelerateDecelerateInterpolator() {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public AccelerateDecelerateInterpolator(Context context, AttributeSet attrs) {
    }

    public float getInterpolation(float input) {
        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }
}
