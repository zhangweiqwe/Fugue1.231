package cn.wsgwz.tun.gravity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.wsgwz.tun.R;
import cn.wsgwz.tun.gravity.bean.HtmlItem;

/**
 * Created by Administrator on 2017/7/2.
 */

public class HtmlAdapter extends RecyclerView.Adapter<HtmlAdapter.ViewHolder> {


    private List<HtmlItem> list;
    private Context context;

    private LayoutInflater layoutInflater;

    public HtmlAdapter(List<HtmlItem> list, Context context) {
        this.list = list;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.view_html_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(list.get(position).getStr());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
