package cn.wsgwz.tun.gravity.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.wsgwz.tun.R;

/**
 * Created by Administrator on 2017/6/6.
 */

public class ReleaseAdapter extends RecyclerView.Adapter<ReleaseAdapter.ViewHolder> implements CheckBox.OnCheckedChangeListener{
    private static final String TAG = ReleaseAdapter.class.getSimpleName();

    public static final String RELEASE_DEFAULT_VALE = null;//com.tencent.mm
    public static final String RELEASE_KEY = "RELEASE_MM_KEY";
    private Context context;
    private LayoutInflater inflater;
    private List<ApplicationInfo> applicationInfoList;
    private PackageManager pm;
    private List<String> checkedApp = new ArrayList<>();
    private String flag =",";
    private SharedPreferences prefs;

    public ReleaseAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        pm = context.getPackageManager();
        applicationInfoList = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        //applicationInfoList.get(0).flags&ApplicationInfo.FLAG_SYSTEM!=0;//系统应用
        Collections.sort(applicationInfoList,new ApplicationInfoComparable());

        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String str = prefs.getString(RELEASE_KEY, RELEASE_DEFAULT_VALE);

        if(str!=null&&str.trim().length()>0){
            if (str.contains(flag)) {
                String[] arr = str.split(flag);
                checkedApp.addAll(Arrays.asList(arr));
            } else {
                checkedApp.add(str);
            }
        }


    }

    @Override
    public ReleaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.app_item,parent,false));
    }



    @Override
    public void onBindViewHolder(final ReleaseAdapter.ViewHolder holder, int position) {
        final ApplicationInfo info = applicationInfoList.get(position);
        holder.name.setText(pm.getApplicationLabel(info));
        holder.icon.setImageDrawable(pm.getApplicationIcon(info));
        holder.description.setText(info.packageName+"("+info.uid+")");
        holder.ck.setOnCheckedChangeListener(null);
        if(info.packageName.equals("cn.wsgwz.tun")){
            holder.ck.setTag(info.packageName);
            holder.ck.setVisibility(View.GONE);
            holder.ck.setOnCheckedChangeListener(this);
        }else if(checkedApp.contains(info.packageName)){
            holder.ck.setVisibility(View.VISIBLE);
            holder.ck.setTag(info.packageName);
            //holder.ck.setOnCheckedChangeListener(null);
            holder.ck.setChecked(true);
            holder.ck.setOnCheckedChangeListener(this);
        }else {
            holder.ck.setVisibility(View.VISIBLE);
            holder.ck.setTag(info.packageName);
            //holder.ck.setOnCheckedChangeListener(null);
            holder.ck.setChecked(false);
            holder.ck.setOnCheckedChangeListener(this);
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String packageName = (String) buttonView.getTag();
        if(isChecked){
            if (!checkedApp.contains(packageName)){
                checkedApp.add(packageName);
            }
        }else {
            if (checkedApp.contains(packageName)){
                checkedApp.remove(packageName);
            }
        }
        Log.d(TAG,"onCheckedChanged"+ "\t"+packageName);
    }

    @Override
    public int getItemCount() {
        return applicationInfoList==null?0:applicationInfoList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView name,description;
        private ImageView icon;
        private CheckBox ck;
        public ViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);

            icon = (ImageView) itemView.findViewById(R.id.icon);

            ck = (CheckBox) itemView.findViewById(R.id.ck);
        }
    }

    public void save(){
        if(checkedApp==null||prefs==null){return;}
        StringBuilder sb = new StringBuilder();
        if(checkedApp.size()==0){
        }else{
            int z = checkedApp.size();
            for (int i = 0; i < z; i++) {
                if (i + 1 == z) {
                    sb.append(checkedApp.get(i));
                } else {
                    sb.append(checkedApp.get(i));
                    sb.append(flag);
                }
            }
        }
        prefs.edit().putString(RELEASE_KEY,sb.toString()).apply();

        String str = prefs.getString(RELEASE_KEY, RELEASE_DEFAULT_VALE);

        List<String> apps = new ArrayList<>();
        if(str!=null&&str.trim().length()>0){
            if (str.contains(flag)) {
                String[] arr = str.split(flag);
                apps.addAll(Arrays.asList(arr));
            } else {
                apps.add(str);
            }
        }

        StringBuilder sb2 = new StringBuilder();
        for(int i=0;i<apps.size();i++){
            try {
                ApplicationInfo info = pm.getApplicationInfo(apps.get(i),0);
                if(apps.size()==i+1){
                    sb2.append(pm.getApplicationLabel(info));
                    break;
                }
                sb2.append(pm.getApplicationLabel(info)).append(flag);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        if(sb2.toString().trim().length()>0){
            Toast.makeText(context,context.getString(R.string.release)+"：\t"+sb2.toString()+
                            "\r\n\r\n"+context.getString(R.string.restart_vpn_take_effect),
                    Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(context,context.getString(R.string.restart_vpn_take_effect), Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG,checkedApp==null?"":checkedApp.size()+""+prefs.getString(RELEASE_KEY,RELEASE_DEFAULT_VALE));
    }

   private class ApplicationInfoComparable implements Comparator<ApplicationInfo> {


        @Override
        public int compare(ApplicationInfo o1, ApplicationInfo o2) {
            if((o1.flags&ApplicationInfo.FLAG_SYSTEM)!=0&&(o2.flags&ApplicationInfo.FLAG_SYSTEM)==0){//系统应用)
                return 1;
            }else if((o1.flags&ApplicationInfo.FLAG_SYSTEM)==0&&(o2.flags&ApplicationInfo.FLAG_SYSTEM)!=0){
                return -1;
            }else {
                return 0;
            }

        }
    }

}
