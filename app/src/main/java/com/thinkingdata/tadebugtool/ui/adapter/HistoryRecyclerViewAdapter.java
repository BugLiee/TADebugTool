/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;

import java.util.ArrayList;
import java.util.List;

/**
 * < History Adapter >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryRecyclerHolder>{

    private List<String> itemList = new ArrayList<>();
    private Context mContext;

    private TextView lastSelectTV;
    private int lastSelectPos;

   public interface OnItemClickListener{
       void onClick(int position);
   }

    OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public HistoryRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    public void notifyDataChanged(List<String> nameList) {
        itemList = nameList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_menu_item, parent, false);
        return new HistoryRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerHolder holder, int position) {
        if (position == lastSelectPos) {
            //设置选中
            lastSelectTV = holder.textView;
            lastSelectTV.setSelected(true);
        }
        holder.textView.setText(itemList.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(position);
                    }
                    //设置选中
                    if (lastSelectPos != position) {
                        lastSelectPos = position;
                        lastSelectTV.setSelected(false);
                        lastSelectTV = holder.textView;
                        lastSelectTV.setSelected(true);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class HistoryRecyclerHolder extends RecyclerView.ViewHolder{
        private TextView textView;

        public HistoryRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.history_menu_item_tv);
        }


    }
}
