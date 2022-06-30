package com.thinkingdata.tadebugtool.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.utils.GetWinPoint;

public class ResizeableLayout extends LinearLayout {
    private int parentHeight;
    private int lastY;
    private boolean isDrag;

    public ResizeableLayout(Context context) {
        super(context);
    }

    public ResizeableLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ResizeableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                setPressed(true);
                getParent().requestDisallowInterceptTouchEvent(true);
                lastY = rawY;
                if (getParent() != null) {
                    parentHeight = GetWinPoint.GetWinWH(getContext()).y;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                int dy = rawY - lastY;
                int distance = dy;
                if (distance > 0 && !isDrag) {
                    isDrag = true;
                }
                float y = getY() + dy;
                y = getY() < 0 ? 0 : y;
                if (y <= 0)
                    setY(0);
                else
                    setY(y);
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    setPressed(false);
                    moveHide(rawY);
                }
                break;
        }
        return isDrag || super.onTouchEvent(event);
    }


    private void moveHide(int rawY) {

        if (rawY >= (parentHeight * 3 / 4)) {
            //close
            if (listener != null) {
                listener.callback();
                //reset Y
                setY(0);
            }
        }
    }


    public void setShowHideLayoutListener(ShowHideLayoutListener showHideLayoutListener) {
        this.listener = showHideLayoutListener;
    }

    ShowHideLayoutListener listener;

    public interface ShowHideLayoutListener{
        void callback();
    }

}
