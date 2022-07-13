/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_EVENT_TYPE_ARRAY;
import static com.thinkingdata.tadebugtool.common.TAConstants.FILTER_TIME_ARRAY;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.common.TAConstants;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

/**
 * < Input >.
 *
 * @author bugliee
 * @create 2022/6/30
 * @since 1.0.0
 */
public class PopupFilterView extends PopupWindow {

    private CardView mPopupView;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private Activity mActivity;

    private Spinner timeSpinner;
    private Spinner eventTypeSpinner;
    private EditText propET;

    private TextView titleTV;
    private TextView submitTV;

    private String currentTimeFilter = FILTER_TIME_ARRAY[0];
    private String currentEventTypeFilter = FILTER_EVENT_TYPE_ARRAY[0];
    private String currentPropFilter = "";

    private int mType;

    public interface OnSubmitClickListener{
        void onClick(int type, String value);
    }

    OnSubmitClickListener onSubmitClickListener;

    public void setOnSubmitClickListener(OnSubmitClickListener onSubmitClickListener) {
        this.onSubmitClickListener = onSubmitClickListener;
    }

    public PopupFilterView(Activity activity) {
        mActivity = activity;
        init();
        setup();
    }

    public void setType(int type) {
        mType = type;
        if (mType == TAConstants.FILTER_TYPE_TIME_SORT) {
            timeSpinner.setVisibility(View.VISIBLE);
            eventTypeSpinner.setVisibility(View.GONE);
            propET.setVisibility(View.GONE);
            titleTV.setText("时间排序");
        } else if (mType == TAConstants.FILTER_TYPE_PROP) {
            timeSpinner.setVisibility(View.GONE);
            eventTypeSpinner.setVisibility(View.GONE);
            propET.setVisibility(View.VISIBLE);
            titleTV.setText("请输入过滤的字段");
        } else if (mType == TAConstants.FILTER_TYPE_EVENT_TYPE) {
            timeSpinner.setVisibility(View.GONE);
            eventTypeSpinner.setVisibility(View.VISIBLE);
            propET.setVisibility(View.GONE);
            titleTV.setText("请选择事件类型");
        } else {
            SnackbarUtil.showSnackBarMid("显示过滤器失败，请重启或者联系开发人员");
            return;
        }
    }

    private void init() {
        mPopupView = (CardView) LayoutInflater.from(mActivity).inflate(R.layout.popup_choose_filter, null);
        timeSpinner = mPopupView.findViewById(R.id.filter_timer_spinner);
        eventTypeSpinner = mPopupView.findViewById(R.id.filter_event_type_spinner);
        propET = mPopupView.findViewById(R.id.filter_prop_et);
        titleTV = mPopupView.findViewById(R.id.filter_title_tv);
        submitTV = mPopupView.findViewById(R.id.popup_filter_bottom_submit);
        int[] screenSize = TAUtil.getDeviceSize(mActivity);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

    private void setup() {
        setContentView(mPopupView);
        setWidth(screenWidth / 2);
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
        submitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSubmitClickListener != null) {
                    if (mType == TAConstants.FILTER_TYPE_TIME_SORT) {
                        onSubmitClickListener.onClick(mType, currentTimeFilter);
                    } else if (mType == TAConstants.FILTER_TYPE_PROP) {
                        currentPropFilter = propET.getText().toString().trim();
                        onSubmitClickListener.onClick(mType, currentPropFilter);
                    } else if (mType == TAConstants.FILTER_TYPE_EVENT_TYPE) {
                        onSubmitClickListener.onClick(mType, currentEventTypeFilter);
                    }
                }
                dismiss();
            }
        });
        initData();
    }

    private void initData() {
        ArrayAdapter timeAdapter = new ArrayAdapter(mActivity, R.layout.item_simple_spinner_simple, R.id.simple_spinner_item_tv, FILTER_TIME_ARRAY) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.item_simple_spinner_simple, parent, false);
                tv.setTextColor(Color.BLACK);
                tv.setText(FILTER_TIME_ARRAY[position]);
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.item_simple_spinner_simple, parent, false);
                tv.setTextColor(Color.BLACK);
                tv.setText(FILTER_TIME_ARRAY[position]);
                return tv;
            }
        };
        timeSpinner.setAdapter(timeAdapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentTimeFilter = FILTER_TIME_ARRAY[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter eventTypeAdapter = new ArrayAdapter(mActivity, R.layout.item_simple_spinner_simple, R.id.simple_spinner_item_tv, FILTER_EVENT_TYPE_ARRAY){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.item_simple_spinner_simple, parent, false);
                tv.setTextColor(Color.BLACK);
                tv.setText(FILTER_EVENT_TYPE_ARRAY[position]);
                return tv;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) LayoutInflater.from(mActivity).inflate(R.layout.item_simple_spinner_simple, parent, false);
                tv.setTextColor(Color.BLACK);
                tv.setText(FILTER_EVENT_TYPE_ARRAY[position]);
                return tv;
            }
        };
        eventTypeSpinner.setAdapter(eventTypeAdapter);
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentEventTypeFilter = FILTER_EVENT_TYPE_ARRAY[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*propET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }


    public void show() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.8f;
        mActivity.getWindow().setAttributes(lp);
        showAtLocation(mActivity.findViewById(R.id.root_msg_rl), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, screenHeight / 6);
    }

}
