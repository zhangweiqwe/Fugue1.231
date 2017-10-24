package cn.wsgwz.tun.gravity.bean;

import android.text.SpannableString;
import android.view.View;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class LogItem {
    private SpannableString spannableString;
    private View.OnClickListener onClickListener;

    public LogItem(SpannableString spannableString, View.OnClickListener onClickListener) {
        this.spannableString = spannableString;
        this.onClickListener = onClickListener;
    }

    public SpannableString getSpannableString() {
        return spannableString;
    }

    public void setSpannableString(SpannableString spannableString) {
        this.spannableString = spannableString;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public String toString() {
        return "LogItem{" +
                "spannableString=" + spannableString +
                ", onClickListener=" + onClickListener +
                '}';
    }
}
