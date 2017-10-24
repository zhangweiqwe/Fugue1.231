package cn.wsgwz.tun.gravity.bean;

/**
 * Created by Administrator on 2017/7/2.
 */

public class HtmlItem {
    private String str;

    public HtmlItem(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return "HtmlItem{" +
                "str='" + str + '\'' +
                '}';
    }
}
