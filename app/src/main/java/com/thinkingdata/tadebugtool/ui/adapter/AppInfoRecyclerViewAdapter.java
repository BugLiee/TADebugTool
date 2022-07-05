/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.content.Context;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * < Adapter >.
 *
 * @author bugliee
 * @create 2022/6/21
 * @since 1.0.0
 */
public class AppInfoRecyclerViewAdapter extends RecyclerView.Adapter<AppInfoRecyclerViewAdapter.MyViewHolder> {
    private Context mContext;
    private List<ResolveInfo> appList = new ArrayList<>();

    public AppInfoRecyclerViewAdapter(Context context, List<ResolveInfo> appList) {
        mContext = context;
        this.appList.addAll(appList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_app_info, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ServiceInfo serviceInfo = appList.get(position).serviceInfo;
        String appName = String.valueOf(serviceInfo.applicationInfo.loadLabel(mContext.getPackageManager()));
        String packageName = serviceInfo.applicationInfo.packageName;
        Drawable appIcon = serviceInfo.applicationInfo.loadIcon(mContext.getPackageManager());
        holder.appIconIV.setBackground(appIcon);
        holder.appNameTV.setText(appName);
        holder.packageNameTV.setText(packageName);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(packageName, appName, appIcon);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView appNameTV;
        public TextView packageNameTV;
        public ImageView appIconIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconIV = itemView.findViewById(R.id.app_icon_iv);
            appNameTV = itemView.findViewById(R.id.app_name_tv);
            packageNameTV = itemView.findViewById(R.id.app_packagename_tv);
        }
    }


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(String packageName, String appName, Drawable appIcon);
    }

}
