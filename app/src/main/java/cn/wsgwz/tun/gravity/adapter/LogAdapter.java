package cn.wsgwz.tun.gravity.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.wsgwz.tun.ActivityMain;
import cn.wsgwz.tun.R;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.bean.LogItem;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ViewHolder> implements View.OnLongClickListener {

    private static final String TAG = LogAdapter.class.getSimpleName();

    public static final List<LogItem> LOG_LIST = new ArrayList<>();
    public static final List<LogItem> DEFAULT_LOG_LIST = new ArrayList<>();
    private static final int HANDELR_WHAT_LOG_DEBUG = 1000;
    private static final int HANDELR_WHAT_LOG = 1001;


    static {
        /*LOG_LIST.add(new LogItem("深碍@ 重庆专用版 copyright © 2017-2018", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url="mqqwpa://im/chat?chat_type=wpa&uin=1554182226";
                try{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }catch (ActivityNotFoundException e){
                    Toast.makeText(v.getContext(),v.getContext().getString(R.string.please_install_qq),Toast.LENGTH_LONG).show();
                }
            }
        }));*/



            if (true) {
                String z = System.getProperty("os.arch");
                String s = Build.MODEL + "\t" + Build.VERSION.RELEASE + "\t" + z;
                SpannableString sS = new SpannableString(s);
                //sS.setSpan(new UnderlineSpan(), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sS.setSpan(new RelativeSizeSpan(0.9f), s.indexOf(z), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                DEFAULT_LOG_LIST.add(new LogItem(sS, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APN_SETTINGS);
                            v.getContext().startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }));
            }

        if (true) {
            String s = Const.App.NAME + "\t" + Const.App.VERSION_NAME + "\t" + Const.App.VERSION_CODE;
            SpannableString sS = new SpannableString(s);


            sS.setSpan(new RelativeSizeSpan(0.7f), s.indexOf(Const.App.VERSION_CODE + ""), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            DEFAULT_LOG_LIST.add(new LogItem(sS, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    try {
                        context.startActivity(new Intent(context, AboutActivity.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogAdapter.addItem(e.getMessage().toString(), null);
                    }

                }
            }    )   );
        }




        LOG_LIST.addAll(DEFAULT_LOG_LIST);

    }


    private LayoutInflater layoutInflater;


    private static final LogAdapter logAdapter = new LogAdapter();

    public static final LogAdapter getInstance() {
        return logAdapter;
    }

    private LogAdapter() {
    }

    public static final void init(Context context) {
        logAdapter.layoutInflater = LayoutInflater.from(context);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.view_log_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LogItem logItem = LOG_LIST.get(position);
        holder.tv.setText(logItem.getSpannableString());
        holder.tv.setOnClickListener(logItem.getOnClickListener());
        holder.tv.setOnLongClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        Toast.makeText(v.getContext(), v.getContext().getString(R.string.already_clear_log), Toast.LENGTH_SHORT).show();
        LogAdapter.clear(v.getContext(), false);
        return false;
    }

    @Override
    public int getItemCount() {
        return LOG_LIST.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }


    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDELR_WHAT_LOG:
                    LOG_LIST.add((LogItem) msg.obj);
                    logAdapter.notifyDataSetChanged();
                    //if(!logAdapter.recyclerView.canScrollVertically(1)){
                    /*if(logAdapter.recyclerView!=null){
                        logAdapter.recyclerView.smoothScrollToPosition(LOG_LIST.size());
                    }else {
                        LOG_LIST.add(new LogItem("logAdapter.recyclerView"+logAdapter.recyclerView,null));
                    }*/

                    //}

                    break;
                case HANDELR_WHAT_LOG_DEBUG:
                    LOG_LIST.add((LogItem) msg.obj);
                    logAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    public static final void addItem(SpannableString spannableString, View.OnClickListener onClickListener) {
        Message message = new Message();
        message.what = HANDELR_WHAT_LOG;
        message.obj = new LogItem(spannableString, onClickListener);
        handler.sendMessage(message);
    }

    public static final void addItem(String s, View.OnClickListener onClickListener) {
        Message message = new Message();
        message.what = HANDELR_WHAT_LOG;
        message.obj = new LogItem(new SpannableString(s), onClickListener);
        handler.sendMessage(message);
    }

    public static final void addItem(String s, View.OnClickListener onClickListener, Context context) {
        Message message = new Message();
        message.what = HANDELR_WHAT_LOG;
        message.obj = new LogItem(new SpannableString(s), onClickListener);
        handler.sendMessage(message);

        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }




    /*public static final void share(Context context){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<LOG_LIST.size();i++){
            stringBuilder.append(LOG_LIST.get(i).getString()+"\r\n");
        }
            sendIntent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
            sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
    }*/

    public static final void clear(Context context, boolean needHint) {
        if (needHint) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.app_name));
            builder.setMessage(context.getString(R.string.confirm_clear));
            builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    LOG_LIST.clear();
                    logAdapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(context.getString(R.string.no), null);
            builder.create().show();
        } else {
            LOG_LIST.clear();
            LOG_LIST.addAll(DEFAULT_LOG_LIST);
            logAdapter.notifyDataSetChanged();
        }
    }


}
