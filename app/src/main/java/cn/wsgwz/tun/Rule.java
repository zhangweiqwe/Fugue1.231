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

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.XmlResourceParser;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Rule {
    private static final String TAG = "NetGuard.Rule";

    public PackageInfo info;
    public String name;
    public String description;
    public boolean system;
    public boolean internet;
    public boolean enabled;
    public Intent launch;
    public Intent settings;
    public Intent datasaver;
    public boolean pkg = true;

    public boolean wifi_default = false;
    public boolean other_default = false;
    public boolean screen_wifi_default = false;
    public boolean screen_other_default = false;
    public boolean roaming_default = false;

    public boolean wifi_blocked = false;
    public boolean other_blocked = false;
    public boolean screen_wifi = false;
    public boolean screen_other = false;
    public boolean roaming = false;
    public boolean lockdown = false;

    public boolean apply = true;
    public boolean notify = true;

    public boolean relateduids = false;
    public String[] related = null;

    public long hosts;
    public boolean changed;

    public boolean expanded = false;

    private static List<PackageInfo> cachePackageInfo = null;
    private static Map<PackageInfo, String> cacheLabel = new HashMap<>();
    private static Map<PackageInfo, String> cacheDescription = new HashMap<>();
    private static Map<String, Boolean> cacheSystem = new HashMap<>();
    private static Map<String, Boolean> cacheInternet = new HashMap<>();
    private static Map<PackageInfo, Boolean> cacheEnabled = new HashMap<>();
    private static Map<String, Intent> cacheIntentLaunch = new HashMap<>();
    private static Map<String, Intent> cacheIntentSettings = new HashMap<>();
    private static Map<String, Intent> cacheIntentDatasaver = new HashMap<>();
    private static Map<Integer, String[]> cachePackages = new HashMap<>();

    private static List<PackageInfo> getPackages(Context context) {
        synchronized (context.getApplicationContext()) {
            if (cachePackageInfo == null) {
                PackageManager pm = context.getPackageManager();
                cachePackageInfo = pm.getInstalledPackages(0);
            }
            return new ArrayList<>(cachePackageInfo);
        }
    }

    private static String getLabel(PackageInfo info, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheLabel.containsKey(info)) {
                PackageManager pm = context.getPackageManager();
                cacheLabel.put(info, info.applicationInfo.loadLabel(pm).toString());
            }
            return cacheLabel.get(info);
        }
    }

    private static String getDescription(PackageInfo info, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheDescription.containsKey(info)) {
                PackageManager pm = context.getPackageManager();
                CharSequence description = info.applicationInfo.loadDescription(pm);
                cacheDescription.put(info, description == null ? null : description.toString());
            }
            return cacheDescription.get(info);
        }
    }

    private static boolean isSystem(String packageName, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheSystem.containsKey(packageName))
                cacheSystem.put(packageName, Util.isSystem(packageName, context));
            return cacheSystem.get(packageName);
        }
    }

    private static boolean hasInternet(String packageName, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheInternet.containsKey(packageName))
                cacheInternet.put(packageName, Util.hasInternet(packageName, context));
            return cacheInternet.get(packageName);
        }
    }

    private static boolean isEnabled(PackageInfo info, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheEnabled.containsKey(info))
                cacheEnabled.put(info, Util.isEnabled(info, context));
            return cacheEnabled.get(info);
        }
    }

    private static Intent getIntentLaunch(String packageName, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheIntentLaunch.containsKey(packageName))
                cacheIntentLaunch.put(packageName, context.getPackageManager().getLaunchIntentForPackage(packageName));
            return cacheIntentLaunch.get(packageName);
        }
    }

    private static Intent getIntentSettings(String packageName, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cacheIntentSettings.containsKey(packageName)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                if (intent.resolveActivity(context.getPackageManager()) == null)
                    intent = null;
                cacheIntentSettings.put(packageName, intent);
            }
            return cacheIntentSettings.get(packageName);
        }
    }

    private static Intent getIntentDatasaver(String packageName, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            synchronized (context.getApplicationContext()) {
                if (!cacheIntentDatasaver.containsKey(packageName)) {
                    Intent intent = new Intent(
                            Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS,
                            Uri.parse("package:" + packageName));
                    if (intent.resolveActivity(context.getPackageManager()) == null)
                        intent = null;
                    cacheIntentDatasaver.put(packageName, intent);
                }
                return cacheIntentDatasaver.get(packageName);
            }
        else
            return null;
    }

    private static String[] getPackages(int uid, Context context) {
        synchronized (context.getApplicationContext()) {
            if (!cachePackages.containsKey(uid))
                cachePackages.put(uid, context.getPackageManager().getPackagesForUid(uid));
            return cachePackages.get(uid);
        }
    }

    public static void clearCache(Context context) {
        Log.i(TAG, "Clearing cache");
        synchronized (context.getApplicationContext()) {
            cachePackageInfo = null;
            cacheLabel.clear();
            cacheDescription.clear();
            cacheSystem.clear();
            cacheInternet.clear();
            cacheEnabled.clear();
            cacheIntentLaunch.clear();
            cacheIntentSettings.clear();
            cacheIntentDatasaver.clear();
            cachePackages.clear();
        }
    }

    private Rule(PackageInfo info, Context context) {
        this.info = info;
        if (info.applicationInfo.uid == 0) {
            this.name = context.getString(R.string.title_root);
            this.description = null;
            this.system = true;
            this.internet = true;
            this.enabled = true;
            this.launch = null;
            this.settings = null;
            this.datasaver = null;
            this.pkg = false;
        } else if (info.applicationInfo.uid == 1013) {
            this.name = context.getString(R.string.title_mediaserver);
            this.description = null;
            this.system = true;
            this.internet = true;
            this.enabled = true;
            this.launch = null;
            this.settings = null;
            this.datasaver = null;
            this.pkg = false;
        } else if (info.applicationInfo.uid == 9999) {
            this.name = context.getString(R.string.title_nobody);
            this.description = null;
            this.system = true;
            this.internet = true;
            this.enabled = true;
            this.launch = null;
            this.settings = null;
            this.datasaver = null;
            this.pkg = false;
        } else {
            this.name = getLabel(info, context);
            this.description = getDescription(info, context);
            this.system = isSystem(info.packageName, context);
            this.internet = hasInternet(info.packageName, context);
            this.enabled = isEnabled(info, context);
            this.launch = getIntentLaunch(info.packageName, context);
            this.settings = getIntentSettings(info.packageName, context);
            this.datasaver = getIntentDatasaver(info.packageName, context);
        }
    }



    private void updateChanged(boolean default_wifi, boolean default_other, boolean default_roaming) {
        changed = (wifi_blocked != default_wifi ||
                (other_blocked != default_other) ||
                (wifi_blocked && screen_wifi != screen_wifi_default) ||
                (other_blocked && screen_other != screen_other_default) ||
                ((!other_blocked || screen_other) && roaming != default_roaming) ||
                hosts > 0 || lockdown || !apply);
    }

    public void updateChanged(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean screen_on = prefs.getBoolean("screen_on", false);
        boolean default_wifi = prefs.getBoolean("whitelist_wifi", true) && screen_on;
        boolean default_other = prefs.getBoolean("whitelist_other", true) && screen_on;
        boolean default_roaming = prefs.getBoolean("whitelist_roaming", true);
        updateChanged(default_wifi, default_other, default_roaming);
    }

    @Override
    public String toString() {
        // This is used in the port forwarding dialog application selector
        return this.name;
    }
}
