/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.bean.TAMockEvent;
import com.thinkingdata.tadebugtool.common.TAConstants;
import com.thinkingdata.tadebugtool.ui.adapter.MockEventPropsRecyclerViewAdapter;
import com.thinkingdata.tadebugtool.ui.widget.popup.PopupPropertiesConstructorView;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.thinkingdata.android.TDConfig;
import cn.thinkingdata.android.TDFirstEvent;
import cn.thinkingdata.android.TDOverWritableEvent;
import cn.thinkingdata.android.TDPresetProperties;
import cn.thinkingdata.android.TDUpdatableEvent;
import cn.thinkingdata.android.ThinkingAnalyticsSDK;

/**
 * < Extension >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class MockContentFragment extends Fragment {

    RecyclerView propsRV;

    TextView currentPropsTV;
    TextView addPropsTV;
    TextView clearPropsTV;
    TextView positiveTV;
    TextView publicPropsTV;

    LinearLayout mockEventLL;
    RelativeLayout publicPropsRL;

    Spinner eventTypeSpinner;

    PopupPropertiesConstructorView constructorView;

    MockEventPropsRecyclerViewAdapter mockEventPropsRecyclerViewAdapter;


    EditText appIDET;
    EditText serverUrlET;
    EditText accountIDET;
    EditText distinctIDET;
    EditText eventNameET;

    TextView eventNameTitleTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mock_content, container, false);
        initView(view);
        setUp();
        return view;
    }

    private void initView(View view) {
        appIDET = view.findViewById(R.id.mock_appid_et);
        serverUrlET = view.findViewById(R.id.mock_url_et);
        accountIDET = view.findViewById(R.id.mock_account_et);
        distinctIDET = view.findViewById(R.id.mock_distinct_et);
        mockEventLL = view.findViewById(R.id.mock_current_event_content_ll);
        eventTypeSpinner = view.findViewById(R.id.choose_event_type_spinner);

        eventNameTitleTV = view.findViewById(R.id.mock_event_name_tv);
        eventNameET = view.findViewById(R.id.mock_event_name_et);

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
                constructorView = new PopupPropertiesConstructorView(getActivity());
                constructorView.setOnPositiveListener(new PopupPropertiesConstructorView.OnPositiveListener() {
                    @Override
                    public void onPositive(JSONObject props) {
                        mockEventPropsRecyclerViewAdapter.addItem(props);
                    }
                });
                constructorView.show(getView());
            }
        });

        clearPropsTV = view.findViewById(R.id.mock_current_prop_clear);
        clearPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clearAll
//                appIDET.setText("");
//                serverUrlET.setText("");
//                accountIDET.setText("");
//                distinctIDET.setText("");
//                eventNameET.setText("");
                mockEventPropsRecyclerViewAdapter.removeAll();
            }
        });
        publicPropsTV = view.findViewById(R.id.mock_public_config_tv);
        publicPropsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicPropsRL.getVisibility() != View.VISIBLE) {
                    publicPropsRL.setVisibility(View.VISIBLE);
                } else {
                    publicPropsRL.setVisibility(View.GONE);
                }
            }
        });
        publicPropsRL = view.findViewById(R.id.mock_public_config_rl);

        positiveTV = view.findViewById(R.id.mock_add_prop_positive);
        positiveTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mockTrack();
            }
        });


    }

    private void showEventConstructorLayout(int pos){
        if (currentPos != pos) {
            mockEventPropsRecyclerViewAdapter.removeAll();
            currentPos = pos;
            eventNameET.setText("");
            switch (currentPos) {
                case 1:
                case 2:
                    eventNameTitleTV.setVisibility(View.VISIBLE);
                    eventNameTitleTV.setText("eventID：");
                    eventNameET.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    eventNameTitleTV.setVisibility(View.VISIBLE);
                    eventNameTitleTV.setText("unsetKey：");
                    eventNameET.setVisibility(View.VISIBLE);
                    break;
                case 3:
                case 4:
                case 6:
                case 7:
                case 8:
                case 9:
                    eventNameTitleTV.setVisibility(View.GONE);
                    eventNameET.setVisibility(View.GONE);
                    break;
                default:
                    eventNameTitleTV.setVisibility(View.VISIBLE);
                    eventNameTitleTV.setText("eventName：");
                    eventNameET.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    private int currentPos = 0;

    private String[] eventTypeList = new String[]{"track", "track_update", "track_overwrite", "user_set", "user_setOnce", "user_unset", "user_add", "user_append", "user_uniq_append", "user_del", "first_event"};

    private void setUp() {
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.item_simple_spinner_simple, R.id.simple_spinner_item_tv, eventTypeList) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_simple_spinner_simple, parent, false);
                TextView tv = view.findViewById(R.id.simple_spinner_item_tv);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText(eventTypeList[position]);
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_simple_spinner_simple, parent, false);
                TextView tv = view.findViewById(R.id.simple_spinner_item_tv);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText(eventTypeList[position]);
                return view;
            }
        };

        eventTypeSpinner.setAdapter(adapter);
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showEventConstructorLayout(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void mockTrack() {
        //mock track
        ThinkingAnalyticsSDK.enableTrackLog(true);
        String appID = appIDET.getText().toString().trim();
        String serverUrl = serverUrlET.getText().toString().trim();
        TDConfig config = TDConfig.getInstance(getActivity(), appID, serverUrl);
        ThinkingAnalyticsSDK instance = ThinkingAnalyticsSDK.sharedInstance(config);
        String eventName = eventNameET.getText().toString().trim();
        String accountID = accountIDET.getText().toString().trim();
        String distinctID = distinctIDET.getText().toString().trim();
        if (!accountID.isEmpty()) {
            instance.login(accountID);
        }
        if (!distinctID.isEmpty()) {
            instance.identify(distinctID);
        }

        JSONObject props = mockEventPropsRecyclerViewAdapter.getProps();
        TAMockEvent mockEvent;

        switch (currentPos) {
            case 1:
            case 2:
                mockEvent = handleMockTrackUpdateOrOverWrite(instance, eventName, props, accountID, appID, serverUrl);
                break;
            case 5:
                mockEvent = handleMockProfileUnSetEvent(instance, eventName, accountID, appID, serverUrl);
                break;
            case 3:
            case 4:
            case 6:
            case 7:
            case 8:
            case 9:
                mockEvent = handleMockProfileSetEvent(instance, props, accountID, appID, serverUrl);
                break;
            default:
                mockEvent = handleMockTrackOrFirstEvent(instance, eventName, props, accountID, appID, serverUrl);
                break;
        }

        if (mockEventTrackListener != null && mockEvent != null) {
            mockEventTrackListener.onTrack(mockEvent);
        }
    }

    private TAMockEvent handleMockProfileUnSetEvent(ThinkingAnalyticsSDK instance, String eventName, String accountID, String appID, String serverUrl) {
        if (TextUtils.isEmpty(eventName)) {
            SnackbarUtil.showSnackBarMid("属性key不能为空");
            return null;
        }
        instance.user_unset(eventName);
        //持久化
        TAMockEvent mockEvent = new TAMockEvent();
        mockEvent.setEventType(eventTypeList[currentPos]);
        if (!accountID.isEmpty()) {
            mockEvent.setAccountID(accountID);
        }
        mockEvent.setDistinctID(instance.getDistinctId());
        mockEvent.setInstanceName(appID);
        mockEvent.setServerUrl(serverUrl);
        mockEvent.setTime(new SimpleDateFormat(TAConstants.TIME_PATTERN, Locale.CHINA).format((System.currentTimeMillis())));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(eventName, 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mockEvent.setProps(jsonObject.toString());
        mockEvent.save();
        return mockEvent;
    }

    private TAMockEvent handleMockProfileSetEvent(ThinkingAnalyticsSDK instance, JSONObject props, String accountID, String appID, String serverUrl) {
        if (currentPos == 3) {
            //user_set
            instance.user_set(props);
        } else if (currentPos == 4) {
            //user_setOnce
            instance.user_setOnce(props);
        } else if (currentPos == 6) {
            //user_add
            instance.user_add(props);
        } else if (currentPos == 7) {
            //user_append
            instance.user_append(props);
        } else if (currentPos == 8) {
            //user_uniq_append
            instance.user_uniqAppend(props);
        } else if (currentPos == 9) {
            //user_del
            instance.user_delete();
        }
        //持久化
        TAMockEvent mockEvent = new TAMockEvent();
        mockEvent.setEventType(eventTypeList[currentPos]);
        if (!accountID.isEmpty()) {
            mockEvent.setAccountID(accountID);
        }
        mockEvent.setDistinctID(instance.getDistinctId());
        mockEvent.setInstanceName(appID);
        mockEvent.setServerUrl(serverUrl);
        mockEvent.setTime(new SimpleDateFormat(TAConstants.TIME_PATTERN, Locale.CHINA).format((System.currentTimeMillis())));
        mockEvent.setProps(props.toString());
        mockEvent.save();
        return mockEvent;
    }

    private TAMockEvent handleMockTrackUpdateOrOverWrite(ThinkingAnalyticsSDK instance, String eventName, JSONObject props, String accountID, String appID, String serverUrl) {
        if (TextUtils.isEmpty(eventName)) {
            SnackbarUtil.showSnackBarMid("事件ID不能为空");
            return null;
        }
        if (currentPos == 1) {
            //这里eventName是事件id
            TDUpdatableEvent event = new TDUpdatableEvent("event_update", props, eventName);
            instance.track(event);
        } else {
            TDOverWritableEvent event = new TDOverWritableEvent("event_overwrite", props, eventName);
            instance.track(event);
        }
        //持久化
        TAMockEvent mockEvent = new TAMockEvent();
        mockEvent.setBFEventID(eventName);
        mockEvent.setEventType(eventTypeList[currentPos]);
        if (!accountID.isEmpty()) {
            mockEvent.setAccountID(accountID);
        }
        mockEvent.setDistinctID(instance.getDistinctId());
        mockEvent.setInstanceName(appID);
        mockEvent.setServerUrl(serverUrl);
        mockEvent.setTime(new SimpleDateFormat(TAConstants.TIME_PATTERN, Locale.CHINA).format((System.currentTimeMillis())));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mockEvent.setPresetProps(presetObject.toString());
        mockEvent.setProps(props.toString());
        mockEvent.save();
        return mockEvent;
    }

    private TAMockEvent handleMockTrackOrFirstEvent(ThinkingAnalyticsSDK instance, String eventName, JSONObject props, String accountID, String appID, String serverUrl) {
        if (TextUtils.isEmpty(eventName)) {
            SnackbarUtil.showSnackBarMid("事件名称不能为空");
            return null;
        }
        if (currentPos == 10) {
            //first check
            instance.track(new TDFirstEvent(eventName, props));
        } else {
            instance.track(eventName, props);
        }
        //持久化
        TAMockEvent mockEvent = new TAMockEvent();
        mockEvent.setEventName(eventName);
        mockEvent.setEventType(eventTypeList[currentPos]);
        if (!accountID.isEmpty()) {
            mockEvent.setAccountID(accountID);
        }
        mockEvent.setDistinctID(instance.getDistinctId());
        mockEvent.setInstanceName(appID);
        if (currentPos == 10) {
            String firstID = props.optString("#first_check_id");
            if (TextUtils.isEmpty(firstID)) {
                mockEvent.setFirstCheckID(instance.getDeviceId());
            } else {
                mockEvent.setFirstCheckID(firstID);
            }
        }
        mockEvent.setServerUrl(serverUrl);
        mockEvent.setTime(new SimpleDateFormat(TAConstants.TIME_PATTERN, Locale.CHINA).format((System.currentTimeMillis())));
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mockEvent.setPresetProps(presetObject.toString());
        mockEvent.setProps(props.toString());
        mockEvent.save();

        return mockEvent;
    }

    public interface MockEventTrackListener{
        void onTrack(TAMockEvent event);
    }

    MockEventTrackListener mockEventTrackListener;

    public void setMockEventTrackListener(MockEventTrackListener mockEventTrackListener) {
        this.mockEventTrackListener = mockEventTrackListener;
    }
}
