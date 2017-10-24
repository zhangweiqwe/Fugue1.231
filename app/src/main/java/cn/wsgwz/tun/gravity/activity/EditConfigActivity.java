package cn.wsgwz.tun.gravity.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.ServiceTun;
import cn.wsgwz.tun.Util;
import cn.wsgwz.tun.gravity.adapter.LogAdapter;
import cn.wsgwz.tun.gravity.helper.ConfigHelper;
import cn.wsgwz.tun.gravity.view.OverScrollView;


public class EditConfigActivity extends SlidingAroundBaseActivity {

    private static final String TAG = EditConfigActivity.class.getSimpleName();

    private static final int DEFAULTE_TEXT_SIZE = 12;
    private static final String TEXT_SIZE_KEY = "TEXT_SIZE_KEY";
    private static final int NEED_SETSPANABLE = 1000;


    public static final String FILE_KEY = "file";
    private File file;
    private EditText et;
    private ConfigHelper configHelper;
    private String initString;
    private SharedPreferences prefs;


    private Toolbar toolBar;

    private OverScrollView overScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_config);

        overScrollView = (OverScrollView) findViewById(R.id.overScrollView);
        setBackground(overScrollView);

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitle(getString(R.string.select));
        //toolBar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        setActionBar(toolBar);
        getActionBar().setDisplayHomeAsUpEnabled(true);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        et = (EditText) findViewById(R.id.et);
        et.setTextSize(TypedValue.COMPLEX_UNIT_SP, prefs.getInt(TEXT_SIZE_KEY, DEFAULTE_TEXT_SIZE));

        Intent intent = getIntent();
        if (intent != null) {
            file = (File) intent.getSerializableExtra(FILE_KEY);
            if (file == null || !file.exists()) {
                finish();
            }
            getActionBar().setTitle(file.getName().toString());
        }

        configHelper = ConfigHelper.getInstance();
        initString = configHelper.getConfigContent(file);


        setColor(initString);

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //setProportionSliding(0.68f);
            }
        });


    }


    private void setColor(String str) {


        try {


            if (!(str.contains("{") && str.contains("}"))) {
                et.setTextColor(Color.parseColor("#E6E6E6"));
                et.setText(str);
                return;
            }


            int x0 = et.getSelectionStart();
            int y0 = et.getSelectionEnd();
            SpannableString s = new SpannableString(str);


            if (true) {
                Pattern p = Pattern.compile("(\"(version|apn|dns|http|https|support|direct|dispose|proxy|delete|first|connect)\"\\s*:)");
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    s.setSpan(new ForegroundColorSpan(Color.parseColor("#ECB866")), start + 1, end - 2,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            if (true) {
                Pattern p = Pattern.compile("\"");
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    s.setSpan(new ForegroundColorSpan(Color.parseColor("#9876AA")), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

            if (true) {
                Pattern p = Pattern.compile("\\}|\\{");
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    s.setSpan(new ForegroundColorSpan(Color.parseColor("#A3B1C0")), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }


            if (true) {
                Pattern p = Pattern.compile("(\"\\s*:\\s*\"*)([\\s\\S]*?)(\"|(,(\\s*)\") )");
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start(2);
                    int end = m.end(2);
                    String s2 = m.group(2);
                    if (s2 != null && s2.contains(",") && (s2.startsWith("true") || s2.startsWith("false"))) {
                        String z = s2.substring(0, s2.indexOf(","));
                        end = start + z.length();
                    }
                    //Log.d(TAG,"-->"+m.group(3)+"<");
                    s.setSpan(new ForegroundColorSpan(Color.parseColor("#A5B4C3")), start, end,//#CC7832
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }


            if (true) {
                Pattern p = Pattern.compile("(\\[(?i)m\\])|(\\[(?i)method\\])  |" +
                        "(\\[(?i)u\\])|(\\[(?i)uri\\])|" +
                        "(\\[(?i)url\\])|" +
                        "(\\[(?i)v\\])|(\\[(?i)version\\])|" +
                        "(\\[(?i)h\\])|(\\[(?i)host\\])|" +


                        "(\\[MTD\\])|" +
                        "(\\[Nn\\])|" +
                        "(\\[Rr\\])|" +
                        "(\\[Tt\\])|" +


                        "(\\[(?i)host_no_port\\])|" +
                        "(\\[(?i)port\\])");
                Matcher m = p.matcher(s);
                Log.d(TAG, "+" + s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    //Log.d(TAG, "+" + start + m.group());
                    s.setSpan(
                            //  new UnderlineSpan()//设置下划线
                            //new SubscriptSpan()//设置上下标
                            //new ForegroundColorSpan(Color.parseColor("#ff0000"))

                            new StyleSpan(android.graphics.Typeface.BOLD_ITALIC)
                            , start, end,//#CC7832
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }


            et.setText(s);
            et.setSelection(x0, y0);


        } catch (Exception e) {
            e.printStackTrace();
            String s = e.getMessage().toString();
            Toast.makeText(EditConfigActivity.this, s, Toast.LENGTH_SHORT).show();
            LogAdapter.addItem(new SpannableString(s), null);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_config, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name));
                //builder.setMessage(getString(R.string.content_change_is_need_save));
                File currentFile = configHelper.getCurrentConfigFile(this);

                final boolean isCurrent = currentFile != null && currentFile.getAbsolutePath().equals(file.getAbsolutePath());
                if (ServiceTun.alreadyStart) {

                    if (isCurrent) {
                        CheckBox checkBox = new CheckBox(this);
                        checkBox.setText(this.getString(R.string.re_start));
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
                }

                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        configHelper.saveConfigFile(file, et.getText().toString());

                        if (isCurrent && prefs.getBoolean("reStart", true) && ServiceTun.alreadyStart) {
                            Intent intent = new Intent(EditConfigActivity.this, ServiceTun.class);
                            intent.setAction(ServiceTun.RESTART_SERVICE);
                            startService(intent);
                        }
                        setColor(et.getText().toString());
                        Toast.makeText(EditConfigActivity.this, getString(R.string.already_save), Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                Dialog dialog = builder.create();
                dialog.show();
                break;

            case R.id.shrink:
                int textSize = prefs.getInt(TEXT_SIZE_KEY, DEFAULTE_TEXT_SIZE);
                textSize -= 1;
                et.setTextSize(textSize);
                prefs.edit().putInt(TEXT_SIZE_KEY, textSize).apply();
                ;
                break;
            case R.id.enlarge:
                int textSize2 = prefs.getInt(TEXT_SIZE_KEY, DEFAULTE_TEXT_SIZE);
                textSize2 += 1;
                et.setTextSize(textSize2);
                prefs.edit().putInt(TEXT_SIZE_KEY, textSize2).apply();
                ;
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
