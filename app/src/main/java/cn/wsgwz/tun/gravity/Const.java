package cn.wsgwz.tun.gravity;

import android.os.Environment;


/**
 * Created by Administrator on 2017/6/25.
 */

public class Const {
    public static final String MAIN_FOLDER_PATH = Environment.getExternalStorageDirectory().toString()+"/"+ App.NAME;
    public static final String MAIN_HTML_FOLDER_PATH = MAIN_FOLDER_PATH+"/html";
    public static final String MAIN_REMOTE_FOLDER_PATH = Const.MAIN_FOLDER_PATH+"/"+"remoteConfigs";
   public class App{
      public static final String PACKAGE_NAME = "cn.wsgwz.tun";
      public static final String NAME = "Fugue";
       public static final String VERSION_NAME = "1.22";
       public static final int VERSION_CODE = 10000025;
       public static final String NAME_AND_VERSION = NAME+" "+VERSION_NAME;
       public static final String NAME_AND_VERSION_AND_CODE = NAME_AND_VERSION+" "+VERSION_CODE;

   }

   public class Config{
       public static final String CONFIG_FILE_POSTFIX = ".txt";
       public static final String CONFIG_FILE_POSTFIX_2 = ".conf";
   }

   public class Prefs{
       public static final String KEY_ENABLED = "ENABLED";
       public static final String KEY_CURRENT_CONFIG_PATH = "KEY_CURRENT_CONFIG_PATH";
       public static final String KEY_BG = "KEY_BG";

       public static final String KEY_FIRST_USE = "KEY_FIRST_USE";
       public static final String KEY_FREE = "free";
       public static final String DEFAUL_VALUE_FREE ="464284028";

       public static final String KEY_ActivityMain_TRANPARENT = "KEY_ActivityMain_TRANPARENT";
       public static final boolean DEFAUL_VALUE_ActivityMain_IS_TRANPARENT = true;
       public static final String KEY_SlidingAroundBaseActivity_TRANPARENT = "KEY_SlidingAroundBaseActivity_TRANPARENT";
       public static final boolean DEFAUL_VALUE_SlidingAroundBaseActivity_IS_TRANPARENT = true;

       public static final String KEY_CAPACITY_STATE = "KEY_CAPACITY_STATE";
       public static final boolean DEFAUL_VALUE_CAPACITY_STATE = true;


   }

   public class Spannable {
       public static final int COLOR_ERROR = 0xFF0000;
       public static final int COLOR_WARN = 0x88FF0000;
       public static final int COLOR_INFO = 0xFFFFFF;
   }





}
