/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thinkingdata.tadebugtool.R;

/**
 * < HistoryContent >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class HistoryContentFragment extends Fragment {
    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_content, container, false);
        textView = view.findViewById(R.id.history_content_tv);
        return view;
    }


    public void setTextViewContent(String text) {
        textView.setText(text);
    }
}
