/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;
import com.thinkingdata.tadebugtool.ui.adapter.MockEventListRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.adapter.MockEventPropsRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupPropertiesConstructorView;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupPropertiesTypeChooserView;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import cn.thinkingdata.android.TDConfig;
import cn.thinkingdata.android.TDPresetProperties;
import cn.thinkingdata.android.ThinkingAnalyticsSDK;

/**
 * < eventList >.
 *
 * @author bugliee
 * @create 2022/7/5
 * @since 1.0.0
 */
public class MockFragment extends Fragment {
    RadioGroup radioGroup;
    RecyclerView propsRV;
    TextView currentPropsTV;
    TextView addPropsTV;
    TextView clearPropsTV;
    TextView positiveTV;
    TextView publicPropsTV;
    TextView mockEventTitleTV;
    TextView mockHistoryTitleTV;
    RecyclerView mockHistoryEventRV;
    LinearLayout mockEventLL;
    LinearLayout publicPropsLL;

    PopupPropertiesTypeChooserView chooserView;
    PopupPropertiesConstructorView constructorView;

    MockEventPropsRecyclerViewAdapter mockEventPropsRecyclerViewAdapter;
    MockEventListRecyclerViewAdapter mockEventListRecyclerViewAdapter;


    EditText appIDET;
    EditText serverUrlET;
    EditText accountIDET;
    EditText distinctIDET;
    EditText eventNameET;

    Activity mActivity;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mock, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mActivity = getActivity();
        appIDET = view.findViewById(R.id.mock_appid_et);
        serverUrlET = view.findViewById(R.id.mock_url_et);
        accountIDET = view.findViewById(R.id.mock_account_et);
        distinctIDET = view.findViewById(R.id.mock_distinct_et);
        eventNameET = view.findViewById(R.id.mock_event_name_et);
        mockEventTitleTV = view.findViewById(R.id.mock_current_title);
        mockHistoryTitleTV = view.findViewById(R.id.mock_history_title);
        mockHistoryEventRV = view.findViewById(R.id.mock_history_rv);
        mockEventLL = view.findViewById(R.id.mock_current_event_content_ll);
        mockEventTitleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mockEventLL.getVisibility() != View.VISIBLE) {
                    mockEventLL.setVisibility(View.VISIBLE);
                } else {
                    mockEventLL.setVisibility(View.GONE);
                }
            }
        });
        mockHistoryTitleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mockHistoryEventRV.getVisibility() != View.VISIBLE) {
                    mockHistoryEventRV.setVisibility(View.VISIBLE);
                } else {
                    mockHistoryEventRV.setVisibility(View.GONE);
                }
            }
        });
        mockHistoryEventRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        if (mockEventListRecyclerViewAdapter == null) {
            mockEventListRecyclerViewAdapter = new MockEventListRecyclerViewAdapter(getActivity());
        }
        mockHistoryEventRV.setAdapter(mockEventListRecyclerViewAdapter);
        List<TAMockEvent> list = new ArrayList<>(LitePal.findAll(TAMockEvent.class));
        mockEventListRecyclerViewAdapter.addItem(list);

        radioGroup = view.findViewById(R.id.mock_rg);
        radioGroup.check(radioGroup.getChildAt(0).getId());
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.d("bugliee", "onCheckedChanged ==> " + checkedId);
            }
        });
        currentPropsTV = view.findViewById(R.id.mock_current_prop_tv);
        currentPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (propsRV.getVisibility() != View.VISIBLE) {
                    propsRV.setVisibility(View.VISIBLE);
                } else {
                    propsRV.setVisibility(View.GONE);
                }
            }
        });
        propsRV = view.findViewById(R.id.mock_props_rv);
        propsRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        if (mockEventPropsRecyclerViewAdapter == null) {
            mockEventPropsRecyclerViewAdapter = new MockEventPropsRecyclerViewAdapter(getActivity());
        }
        propsRV.setAdapter(mockEventPropsRecyclerViewAdapter);

        addPropsTV = view.findViewById(R.id.mock_add_prop_tv);
        addPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chooserView == null) {
                    chooserView = new PopupPropertiesTypeChooserView(getActivity());
                }
                chooserView.setOnPositiveListener(new PopupPropertiesTypeChooserView.OnPositiveListener() {
                    @Override
                    public void onPositive(String type) {
                        chooserView.dismiss();
                        //显示对应类型的构建界面
                        constructorView = new PopupPropertiesConstructorView(getActivity(), type);
                        constructorView.setOnPositiveListener(new PopupPropertiesConstructorView.OnPositiveListener() {
                            @Override
                            public void onPositive(JSONObject props) {
                                constructorView.dismiss();
                                mockEventPropsRecyclerViewAdapter.addItem(props);
                            }
                        });
                        constructorView.show(getView());
                    }
                });
                chooserView.show(getView());
            }
        });

        clearPropsTV = view.findViewById(R.id.mock_current_prop_clear);
        clearPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearAll
                appIDET.setText("");
                serverUrlET.setText("");
                accountIDET.setText("");
                distinctIDET.setText("");
                eventNameET.setText("");
                mockEventPropsRecyclerViewAdapter.removeAll();
            }
        });
        publicPropsTV = view.findViewById(R.id.mock_public_config_tv);
        publicPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScaleAnimation animation;
                if (publicPropsLL.getVisibility() != View.VISIBLE) {
                    publicPropsLL.setVisibility(View.VISIBLE);
                    animation= new ScaleAnimation(1.0f, 1.0f, 0f, 1.0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                            0f);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } else {
                    animation= new ScaleAnimation(1.0f, 1.0f, 1.0f, 0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
                            0f);
                    animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            publicPropsLL.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                animation.setDuration(300);
                animation.setFillAfter(true);
                publicPropsLL.startAnimation(animation);
            }
        });

        publicPropsLL = view.findViewById(R.id.mock_public_config_ll);

        positiveTV = view.findViewById(R.id.mock_add_prop_positive);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mock track
                ThinkingAnalyticsSDK.enableTrackLog(true);
                String appID = appIDET.getText().toString().trim();
                String serverUrl = serverUrlET.getText().toString().trim();
                TDConfig config = TDConfig.getInstance(mActivity, appID, serverUrl);
                ThinkingAnalyticsSDK instance = ThinkingAnalyticsSDK.sharedInstance(config);
                String eventName = eventNameET.getText().toString().trim();
                String accountID = accountIDET.getText().toString().trim();
                String distinctID = distinctIDET.getText().toString().trim();
                instance.login(accountID);
                instance.identify(distinctID);
                JSONObject props = mockEventPropsRecyclerViewAdapter.getProps();
                JSONObject destProps;
                instance.track(eventName, props);
                //持久化
                TAMockEvent mockEvent = new TAMockEvent();
                mockEvent.setEventName(eventName);
                mockEvent.setEventType(eventName);
                mockEvent.setAccountID(accountID);
                mockEvent.setDistinctID(distinctID);
                mockEvent.setInstanceName(appID);
                mockEvent.setServerUrl(serverUrl);
                mockEvent.setTimeStamp(String.valueOf(System.currentTimeMillis()));
                TDPresetProperties presetProperties = instance.getPresetProperties();
                JSONObject presetObject = new JSONObject();
                try {
                    presetObject.put("appVersionName", presetProperties.appVersion);
                    presetObject.put("deviceID", presetProperties.deviceId);
                    presetObject.put("os", presetProperties.os);
                    presetObject.put("osVersion", presetProperties.osVersion);
                    presetObject.put("fps", presetProperties.fps);
                    presetObject.put("isSimulator", presetProperties.isSimulator);
                    presetObject.put("screenWidth", presetProperties.screenWidth);
                    presetObject.put("screenHeight", presetProperties.screenHeight);
                    presetObject.put("bundleId", presetProperties.bundleId);
                    presetObject.put("zoneOffset", presetProperties.zoneOffset);
                    presetObject.put("carrier", presetProperties.carrier);
                    presetObject.put("deviceModel", presetProperties.deviceModel);
                    presetObject.put("manufacture", presetProperties.manufacture);
                    presetObject.put("installTime", presetProperties.installTime);
                    presetObject.put("networkType", presetProperties.networkType);
                    presetObject.put("ram", presetProperties.ram);
                    presetObject.put("disk", presetProperties.disk);
                    presetObject.put("systemLanguage", presetProperties.systemLanguage);
                    destProps = new JSONObject(props.toString());
                    TAUtil.mergeJSONObject(presetObject, destProps, TimeZone.getTimeZone("GMT:+8:00"));
                } catch (JSONException e) {
                    destProps = new JSONObject();
                    e.printStackTrace();
                }
                mockEvent.setProps(destProps.toString());
                mockEvent.save();
                //更新数据
                List<TAMockEvent> events = new ArrayList<>();
                events.add(mockEvent);
                mockEventListRecyclerViewAdapter.addItem(events);
            }
        });
    }
}
