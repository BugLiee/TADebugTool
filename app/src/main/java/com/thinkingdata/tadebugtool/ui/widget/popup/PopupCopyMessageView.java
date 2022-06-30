/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.thinkingdata.tadebugtool.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * < Copy Message >.
 *
 * @author bugliee
 * @create 2022/6/29
 * @since 1.0.0
 */
public class PopupCopyMessageView extends PopupWindow {
    private View mPopupView;
    private Activity mActivity;
    private View parentView;
    private TextView messageTV;
    private MyHandler handler;

    public interface OnPopupDismissListener{
        void onDismiss();
    }

    OnPopupDismissListener onPopupDismissListener;

    public void setOnPopupDismissListener(OnPopupDismissListener listener) {
        onPopupDismissListener = listener;
    }

    PopupCopyMessageView(Activity activity) {
        mActivity = activity;
        handler = new MyHandler();
        init();
    }

    private void init() {
        mPopupView = LayoutInflater.from(mActivity).inflate(R.layout.popup_copy_msg, null);
        messageTV = mPopupView.findViewById(R.id.copy_msg_tv);
        setContentView(mPopupView);
        setAnimationStyle(R.style.PopupCopyMessageViewStyle);
        setFocusable(true);
        messageTV.setText("复制成功！");
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (onPopupDismissListener != null) {
                    onPopupDismissListener.onDismiss();
                }
            }
        });
    }

    public void show(View parentView) {
        this.parentView = parentView;
        if (parentView != null) {
            setHeight(parentView.getHeight());
            setWidth(getHeight() * 4);
            showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(0);
                }
            },600);
        }
    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    }


}
