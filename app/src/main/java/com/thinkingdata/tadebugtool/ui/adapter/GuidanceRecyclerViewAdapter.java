/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.ui.fragments.GuidanceContentFragment;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * < Guidance Adapter >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class GuidanceRecyclerViewAdapter  extends RecyclerView.Adapter<GuidanceRecyclerViewAdapter.GuidanceRecyclerHolder>{

    private List<String> itemList = new ArrayList<>();
    private Context mContext;


   public interface OnItemClickListener{
       void onClick(int position);
   }

    OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public GuidanceRecyclerViewAdapter(Context context, List<String> list) {
        mContext = context;
        itemList.addAll(list);
    }

    @NonNull
    @Override
    public GuidanceRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.guidance_menu_item, parent, false);
        return new GuidanceRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuidanceRecyclerHolder holder, int position) {
            holder.textView.setText(itemList.get(position));
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onClick(position);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class GuidanceRecyclerHolder extends RecyclerView.ViewHolder{
        private TextView textView;

        public GuidanceRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.guidance_menu_item_tv);
        }


    }
}
