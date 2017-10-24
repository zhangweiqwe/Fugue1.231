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

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;

public class ApplicationEx extends Application {
    private static final String TAG = "NetGuard.App";
    private List<Activity> activities = new ArrayList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getCurProcessName(this);
        if(processName.equals(Const.App.PACKAGE_NAME)){
            Util.load();
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putBoolean(Const.Prefs.KEY_ENABLED,false).apply();
            LogAdapter.init(this);
        }



    }



    public void addActivity(Activity activity) {
        activities.add(activity);
    }
    public void removeActivity(Activity activity) {
        activities.remove(activity);
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        for (Activity activity : activities) {
            if(activity!=null){
                activity.finish();
            }
        }
        System.exit(0);
    }

    private  String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
