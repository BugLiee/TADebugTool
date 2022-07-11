/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.ui.widget.InputAppIDItemView;
import com.thinkingdata.tadebugtool.ui.widget.ResizeableLayout;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * < Input >.
 *
 * @author bugliee
 * @create 2022/6/30
 * @since 1.0.0
 */
public class PopupInputView extends PopupWindow {

    private CardView mPopupView;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Activity mActivity;

    private TextView addTV;
    private TextView submitTV;
    private LinearLayout contentLL;

    List<InputAppIDItemView> appIDItems = new ArrayList<>();
    List<String> appIDs = new ArrayList<>();

    public interface OnSubmitClickListener{
        void onClick(List<String> appIDs);
    }

    OnSubmitClickListener onSubmitClickListener;

    public void setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        this.onSubmitClickListener = onSubmitClickListener;
    }

    public PopupInputView(Activity activity) {
        mActivity = activity;
        init();
        setup();
    }

    private void init() {
        mPopupView = (CardView) LayoutInflater.from(mActivity).inflate(R.layout.popup_input_appid, null);
        contentLL = mPopupView.findViewById(R.id.input_appid_content_ll);
        appIDItems.add((InputAppIDItemView) contentLL.getChildAt(0));
        addTV = mPopupView.findViewById(R.id.popup_input_appid_bottom_add);
        submitTV = mPopupView.findViewById(R.id.popup_input_appid_bottom_submit);
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

    private void setup() {
        setContentView(mPopupView);
        setWidth(screenWidth * 8 / 10);
        setFocusable(true);
        setHeight(screenHeight / 6);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1f;
                mActivity.getWindow().setAttributes(lp);
            }
        });
        addTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        submitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appIDs.clear();
                //read data
                for (InputAppIDItemView view : appIDItems) {
                    String appID = view.getETContent();
                    if (!TextUtils.isEmpty(appID)) {
                        appIDs.add(appID);
                    }
                }
                if (appIDs.size() == 0) {
                    // show toast
                    SnackbarUtil.showSnackbarShort("请先输入有效的AppID后再次确认！");
                } else {
                    //callback
                    if (onSubmitClickListener != null) {
                        onSubmitClickListener.onClick(appIDs);
                    }
                }
            }
        });
    }

    public void addItem() {
        if (appIDItems.size() < 1) {
            InputAppIDItemView inputAppIDItemView = new InputAppIDItemView(mActivity);
            inputAppIDItemView.setCId(appIDItems.size());
            inputAppIDItemView.setup(new InputAppIDItemView.OnRemoveClickListener() {
                @Override
                public void onClick(int id) {
                    appIDItems.remove(id);
                    contentLL.removeViewAt(id);
                    for (int i = 0; i < appIDItems.size(); i++) {
                        appIDItems.get(i).setCId(i);
                    }
                    update(getWidth(), getHeight() - contentLL.getChildAt(0).getHeight());
                }
            });
            contentLL.addView(inputAppIDItemView);
            appIDItems.add(inputAppIDItemView);
            update(getWidth(), getHeight() + contentLL.getChildAt(0).getHeight());
        } else {
            SnackbarUtil.showSnackbarMid("当前版本仅支持单实例模式 ... 如有需求请联系开发人员");
        }
    }


    public void show() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.8f;
        mActivity.getWindow().setAttributes(lp);
        showAtLocation(mActivity.findViewById(R.id.root_msg_rl), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, screenHeight / 10);
    }

}
