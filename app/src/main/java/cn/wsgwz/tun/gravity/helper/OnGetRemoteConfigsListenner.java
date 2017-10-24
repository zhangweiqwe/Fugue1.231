package cn.wsgwz.tun.gravity.helper;

import java.util.List;

import cn.wsgwz.tun.gravity.core.Config;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface OnGetRemoteConfigsListenner {
    void start();
    void finish(String msg ,int state);
}
