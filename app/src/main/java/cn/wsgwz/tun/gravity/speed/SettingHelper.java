package cn.wsgwz.tun.gravity.speed;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import cn.wsgwz.tun.ApplicationEx;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.provider.AppContentProvider;


/**
 * Created by Jeremy Wang on 2017/1/9.
 */

public class SettingHelper {
    private static final Uri settingUriSuspensioColor = Uri.parse("content://"+ Const.App.PACKAGE_NAME+"/"+ AppContentProvider.PATH_SETTING_SUSPENSION_COLOR);
    private static final Uri settingUriSpeedSuspensionX = Uri.parse("content://"+ Const.App.PACKAGE_NAME+"/"+ AppContentProvider.PATH_SETTING_SEED_X_LOCATION);
    private static final Uri settingUriSpeedSuspensionY = Uri.parse("content://"+ Const.App.PACKAGE_NAME+"/"+ AppContentProvider.PATH_SETTING_SEED_Y_LOCATION);
    private SettingHelper(){}
    private static SettingHelper settingHelper = null;
    public static final SettingHelper getInstance(){
        if(settingHelper==null){
            synchronized (SettingHelper.class){
                if (settingHelper==null){
                    settingHelper = new SettingHelper();
                }
            }
        }
        return settingHelper;
    }


    private void setBoolean(Context context,boolean b,String key,Uri uri){
        int i = b?0:1;
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,i);
        contentResolver.update(uri,contentValues, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    private boolean getBoolean(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        int i = 0;
        Cursor cursor = contentResolver.query(uri,null, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"},
                null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            i  = cursor.getInt(cursor.getColumnIndex(key));
        }
        return i==0?true:false;
    }

    private void setString(Context context,String s,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,s);
        contentResolver.update(uri,contentValues, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    private String getString(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        String configPath = null;
        Cursor cursor = contentResolver.query(uri,null, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            configPath  = cursor.getString(cursor.getColumnIndex(key));
        }
        return configPath;
    }
    private void setFloat(Context context,float f,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,f);
        contentResolver.update(uri,contentValues, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }

    private float getFloat(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        float x = 0;
        Cursor cursor = contentResolver.query(uri,null, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"}, null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            x  = cursor.getFloat(cursor.getColumnIndex(key));
        }
        return x;
    }



    private void setInt(Context context,int i,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(key,i);
        contentResolver.update(uri,contentValues, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"});
    }
    private int getInt(Context context,String key,Uri uri){
        ContentResolver contentResolver = context.getContentResolver();
        int i = 0;
        Cursor cursor = contentResolver.query(uri,null, AppContentProvider.DbHelper._ID+"=?",new String[]{"1"},
                null,null);
        boolean b = cursor.moveToFirst();
        if (b){
            i  = cursor.getInt(cursor.getColumnIndex(key));
        }
        return i;
    }



    public void setSuspensionColor( Context context,String color){
        setString(context,color, AppContentProvider.DbHelper._SUSPENSION_COLOR,settingUriSuspensioColor);
    }
    public String getSuspensionColor(Context context){
        return getString(context, AppContentProvider.DbHelper._SUSPENSION_COLOR,settingUriSuspensioColor);
    }



    public void setSpeedSuspensionX( Context context,float f){
        setFloat(context,f, AppContentProvider.DbHelper._SPEED_VIEW_X_LOCATION,settingUriSpeedSuspensionX);
    }
    public float getSpeedSuspensionX(Context context){
        return getFloat(context, AppContentProvider.DbHelper._SPEED_VIEW_X_LOCATION,settingUriSpeedSuspensionX);
    }

    public void setSpeedSuspensionY( Context context,float f){
        setFloat(context,f, AppContentProvider.DbHelper._SPEED_VIEW_Y_LOCATION,settingUriSpeedSuspensionY);
    }
    public float getSpeedSuspensionY(Context context){
        return getFloat(context, AppContentProvider.DbHelper._SPEED_VIEW_Y_LOCATION,settingUriSpeedSuspensionY);
    }

}
