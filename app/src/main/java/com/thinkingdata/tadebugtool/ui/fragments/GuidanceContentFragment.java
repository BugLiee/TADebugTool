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

import org.w3c.dom.Text;

/**
 * < GuidanceContent >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class GuidanceContentFragment extends Fragment {
    private TextView textView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guidance_content, container, false);
        textView = view.findViewById(R.id.guidance_content_tv);
        return view;
    }


    public void setTextViewContent(String text) {
        textView.setText(text);
    }
}
