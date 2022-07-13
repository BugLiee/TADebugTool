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
 * <  >.
 *
 * @author bugliee
 * @create 2022/7/11
 * @since 1.0.0
 */
public class TypeMenuRecyclerViewAdapter extends RecyclerView.Adapter<TypeMenuRecyclerViewAdapter.MyViewHolder>{
   Context mContext;

   private TextView lastSelectTV;
   private int lastSelectPos;

   String[] mArray;
   public TypeMenuRecyclerViewAdapter(Context context, String[] array) {
      mContext = context;
      mArray = array;
   }

   public interface OnItemClickListener{
      void onClick(int pos);
   }

   OnItemClickListener itemClickListener;

   public void setItemClickListener(OnItemClickListener itemClickListener) {
      this.itemClickListener = itemClickListener;
   }

   @NonNull
   @Override
   public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(mContext).inflate(R.layout.item_props_constructor_type, parent, false);
      return new MyViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
      holder.setIsRecyclable(false);
      if (position == lastSelectPos) {
         //设置选中
         lastSelectTV = (TextView) holder.itemView;
         lastSelectTV.setSelected(true);
      }
      ((TextView) holder.itemView).setText(mArray[position]);
      ((TextView) holder.itemView).setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (itemClickListener != null) {
               itemClickListener.onClick(position);
            }
            //设置选中
            if (lastSelectPos != position) {
               lastSelectPos = position;
               lastSelectTV.setSelected(false);
               lastSelectTV = (TextView) holder.itemView;
               lastSelectTV.setSelected(true);
            }
         }
      });

   }

   @Override
   public int getItemCount() {
      return mArray.length;
   }

   public static class MyViewHolder extends RecyclerView.ViewHolder {
      public MyViewHolder(@NonNull View itemView) {
         super(itemView);
      }
   }
}
