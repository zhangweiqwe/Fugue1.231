package cn.wsgwz.tun.gravity.adapter;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.gravity.Const;
import cn.wsgwz.tun.gravity.activity.AboutActivity;
import cn.wsgwz.tun.gravity.helper.ConfigHelper;

import static android.os.Build.VERSION.SDK;
import static android.os.Build.VERSION_CODES.M;


/**
 * Created by Administrator on 2017/4/21 0021.
 */

public class ConfigAdapter extends RecyclerView.Adapter<ConfigAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = ConfigAdapter.class.getSimpleName();

    private List<File> list;
    private Context context;
    private LayoutInflater inflater;
    private ConfigHelper configHelper;
    private File currentFile;

    public ConfigAdapter(List<File> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        configHelper = ConfigHelper.getInstance();
        currentFile = configHelper.getCurrentConfigFile(context);
    }

    public void refresh() {
        this.list = configHelper.getConfigs();
        this.notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.view_config_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        File file = list.get(position);
        if (currentFile != null && file.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
            holder.hintSelectIV.setVisibility(View.VISIBLE);

            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.rl.callOnClick();
                }
            };
            holder.parent_rl.setOnClickListener(onClickListener);
            holder.parent_rl.setOnLongClickListener(this);
        } else {
            holder.hintSelectIV.setVisibility(View.GONE);

            holder.parent_rl.setOnClickListener(this);
            holder.parent_rl.setOnLongClickListener(this);
        }
        holder.rl.setTag(file);
        holder.parent_rl.setTag(file);


        if (file.getAbsolutePath().startsWith(Const.MAIN_REMOTE_FOLDER_PATH)) {
            holder.title.setText(file.getName() + "(远程)");
        } else {
            holder.title.setText(file.getName());
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl, parent_rl;

        private ImageView txtIV, hintSelectIV;

        private TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            txtIV = (ImageView) itemView.findViewById(R.id.txtIV);
            rl = (RelativeLayout) itemView.findViewById(R.id.rl);
            title = (TextView) itemView.findViewById(R.id.title);
            hintSelectIV = (ImageView) itemView.findViewById(R.id.hintSelectIV);
            parent_rl = (RelativeLayout) itemView.findViewById(R.id.parent_rl);

            rl.setOnClickListener(ConfigAdapter.this);
        }
    }

    @Override
    public void onClick(final View v) {
        final File file = (File) v.getTag();
        switch (v.getId()) {

           /* case R.id.title:
            case R.id.txtIV:*/
            case R.id.parent_rl:
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.app_name));
                builder.setMessage(context.getString(R.string.set_is_current_file));

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (ServiceTun.alreadyStart) {
                    CheckBox checkBox = new CheckBox(context);
                    checkBox.setText(context.getString(R.string.re_start));
                    checkBox.setTextColor(Color.BLACK);
                    checkBox.setChecked(prefs.getBoolean("reStart", true));

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            prefs.edit().putBoolean("reStart", isChecked).apply();
                        }
                    });
                    builder.setView(checkBox);
                }


                builder.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        configHelper.setCurrentConfigFile(context, file);
                        currentFile = configHelper.getCurrentConfigFile(context);
                        ConfigAdapter.this.notifyDataSetChanged();

                        if (prefs.getBoolean("reStart", true) && ServiceTun.alreadyStart) {
                            Intent intent = new Intent(context, ServiceTun.class);
                            intent.setAction(ServiceTun.RESTART_SERVICE);
                            context.startService(intent);
                        }
                        ConfigAdapter.this.notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton(context.getString(R.string.no), null);
                builder.create().show();
                break;

            case R.id.rl:
                configHelper.editConfig(context, file, false);
                break;
        }

    }

    @Override
    public boolean onLongClick(final View v) {
        final File file = (File) v.getTag();
        switch (v.getId()) {
            /*case R.id.title:
            case R.id.txtIV:*/
            case R.id.parent_rl:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setNegativeButton(context.getString(R.string.copy), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (configHelper.copyConfig(file)) {
                            list = configHelper.getConfigs();
                            ConfigAdapter.this.notifyDataSetChanged();
                        }
                    }
                }).setNeutralButton(context.getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri imageUri = Uri.fromFile(file);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        intent.setType("*/*");
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.app_name)));
                    }
                })
                        .setPositiveButton(context.getString(R.string.delate), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                file.delete();
                                list.remove(file);
                                ConfigAdapter.this.notifyDataSetChanged();
                            }
                        }).create().show();
                break;
        }
        return false;
    }
}
