/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget.popup;

import static com.thinkingdata.tadebugtool.common.TAConstants.KEY_TA_TOOL_ACTION;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.thinkingdata.tadebugtool.R;
import com.thinkingdata.tadebugtool.RequestListener;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.thinkingdata.android.aidl.ITAToolServer;

/**
 * < PopupWindow >.
 *
 * @author bugliee
 * @create 2022/6/14
 * @since 1.0.0
 */
public class PopupInfoView extends CardView implements View.OnLongClickListener{
    public static volatile PopupInfoView instance = null;
    private final Activity mActivity;
    private String mApplicationInfoStr;

    private View header;

    private TextView appNameTV;
    private TextView appVersionNameTV;
    private TextView appVersionCodeTV;
    private TextView sdkVersionTV;
    private TextView deviceIDTV;
    private TextView isMultiProcessTV;
    private TextView enableBGStartTV;
    private TextView sdkModeTV;
    private TextView sdkTrackStatusTV;

    private Spinner appIDSpinner;
    private Spinner serverUrlSpinner;
    private String packageName = "";

    private boolean isFirstInit = true;

    private ClipboardManager clipboardManager;
    private PopupCopyMessageView copyMessageView;

    public PopupInfoView(@NonNull Activity activity) {
        super(activity);
        mActivity = activity;
    }

    public void init(String packageName) {
        this.packageName = packageName;
        LayoutInflater.from(mActivity).inflate(R.layout.popup_info, this, true);
        header = findViewById(R.id.popup_info_header);
        appNameTV = findViewById(R.id.app_name_value_tv);
        appVersionNameTV = findViewById(R.id.app_version_name_value_tv);
        appVersionCodeTV = findViewById(R.id.app_version_code_value_tv);
        sdkVersionTV = findViewById(R.id.lib_version_value_tv);
        deviceIDTV = findViewById(R.id.device_id_value_tv);
        appIDSpinner = findViewById(R.id.app_id_value_spinner);
        serverUrlSpinner = findViewById(R.id.server_url_value_spinner);
        isMultiProcessTV = findViewById(R.id.is_multi_process_value_tv);
        enableBGStartTV = findViewById(R.id.enable_bg_start_value_tv);
        sdkModeTV = findViewById(R.id.sdk_mode_value_tv);
        sdkTrackStatusTV = findViewById(R.id.track_status_value_tv);
        setLongClick();
        initInfo();
    }

    private void setLongClick() {
        appNameTV.setOnLongClickListener(this);
        appVersionNameTV.setOnLongClickListener(this);
        appVersionCodeTV.setOnLongClickListener(this);
        sdkVersionTV.setOnLongClickListener(this);
        deviceIDTV.setOnLongClickListener(this);
        isMultiProcessTV.setOnLongClickListener(this);
        enableBGStartTV.setOnLongClickListener(this);
    }

    private void clipData(View view) {
        if (!(view instanceof TextView)) {
            return;
        }
        ((TextView) view).setTextColor(Color.GRAY);
        if (clipboardManager == null) {
            clipboardManager = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        }
        ClipData clipData = ClipData.newPlainText("tatool text", ((TextView) view).getText().toString());
        clipboardManager.setPrimaryClip(clipData);
        if (copyMessageView == null) {
            copyMessageView = new PopupCopyMessageView(mActivity);
        }
        copyMessageView.setOnPopupDismissListener(new PopupCopyMessageView.OnPopupDismissListener() {
            @Override
            public void onDismiss() {
                ((TextView) view).setTextColor(Color.BLACK);
            }
        });
        copyMessageView.show(header);
    }


    private void initInfo() {
        if (mTAToolServer == null) {
            Intent intent = new Intent();
            intent.setAction(KEY_TA_TOOL_ACTION);
            intent.setPackage(packageName);
            mActivity.getApplicationContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            setInfo();
        }
    }

    public static ITAToolServer mTAToolServer;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTAToolServer = ITAToolServer.Stub.asInterface(service);
            setInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTAToolServer = null;
        }
    };

    private void setInfo() {
        try {
            mApplicationInfoStr = mTAToolServer.getAppAndSDKInfo();
            if (!mApplicationInfoStr.isEmpty()) {
                JSONObject appInfoJsonObject = new JSONObject(mApplicationInfoStr);
                appNameTV.setText(String.valueOf(appInfoJsonObject.opt("app_name")));
                appVersionNameTV.setText(String.valueOf(appInfoJsonObject.opt("app_version_name")));
                appVersionCodeTV.setText(String.valueOf(appInfoJsonObject.opt("app_version_code")));
                sdkVersionTV.setText(String.valueOf(appInfoJsonObject.opt("lib_version")));
                deviceIDTV.setText(String.valueOf(appInfoJsonObject.opt("device_id")));
                isMultiProcessTV.setText(String.valueOf(appInfoJsonObject.opt("is_multi_process")));
                enableBGStartTV.setText(String.valueOf(appInfoJsonObject.opt("enable_bg_start")));
                sdkModeTV.setText(String.valueOf(appInfoJsonObject.opt("sdk_mode")));
                sdkTrackStatusTV.setText(String.valueOf(appInfoJsonObject.opt("track_status")));

                String[] appIDs = String.valueOf(appInfoJsonObject.opt("app_id")).split(",");
                String[] urls = String.valueOf(appInfoJsonObject.opt("server_url")).split(",");
                ArrayAdapter urlAdapter = new ArrayAdapter(mActivity, R.layout.item_spinner, R.id.info_spinner_item, urls){

                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        //展示在前的布局
                        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_spinner, parent, false);
                        TextView statusTV = view.findViewById(R.id.info_spinner_item_status);
                        TextView urlTV = view.findViewById(R.id.info_spinner_item);
                        urlTV.setText(urls[position]);
                        TAUtil.checkUrl(mActivity, urls[position], new RequestListener() {
                            @Override
                            public void requestEnd(int code, String msg) {
                                if (code == 200) {
                                    statusTV.setText("正常");
                                    statusTV.setTextColor(Color.GREEN);
                                } else {
                                    statusTV.setText("异常");
                                    statusTV.setTextColor(Color.RED);
                                }
                            }
                        });
                        return view;
                    }


                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        //展开后的子项布局
                        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_spinner, parent, false);
                        TextView statusTV = view.findViewById(R.id.info_spinner_item_status);
                        TextView urlTV = view.findViewById(R.id.info_spinner_item);
                        urlTV.setText(urls[position]);
                        TAUtil.checkUrl(mActivity, urls[position], new RequestListener() {
                            @Override
                            public void requestEnd(int code, String msg) {
                                if (code == 200) {
                                    statusTV.setText("正常");
                                    statusTV.setTextColor(Color.GREEN);
                                } else {
                                    statusTV.setText("异常");
                                    statusTV.setTextColor(Color.RED);
                                }
                            }
                        });
                        return view;
                    }
                };

                ArrayAdapter adapter = new ArrayAdapter(mActivity, R.layout.item_spinner, R.id.info_spinner_item, appIDs){
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_spinner, parent, false);
                        TextView statusTV = view.findViewById(R.id.info_spinner_item_status);
                        TextView urlTV = view.findViewById(R.id.info_spinner_item);
                        urlTV.setText(appIDs[position]);
                        TAUtil.checkAppIDAndUrl(urls[position], appIDs[position], new RequestListener() {
                            @Override
                            public void requestEnd(int code, String msg) {
                                if (code == 200) {
                                    statusTV.setText("正常");
                                    statusTV.setTextColor(Color.GREEN);
                                } else {
                                    statusTV.setText("异常");
                                    statusTV.setTextColor(Color.RED);
                                }
                            }
                        });
                        return view;
                    }


                    @Override
                    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_spinner, parent, false);
                        TextView statusTV = view.findViewById(R.id.info_spinner_item_status);
                        TextView urlTV = view.findViewById(R.id.info_spinner_item);
                        urlTV.setText(appIDs[position]);
                        TAUtil.checkAppIDAndUrl(urls[position], appIDs[position], new RequestListener() {
                            @Override
                            public void requestEnd(int code, String msg) {
                                if (code == 200) {
                                    statusTV.setText("正常");
                                    statusTV.setTextColor(Color.GREEN);
                                } else {
                                    statusTV.setText("异常");
                                    statusTV.setTextColor(Color.RED);
                                }
                            }
                        });
                        return view;
                    }
                };
                appIDSpinner.setAdapter(adapter);
                serverUrlSpinner.setAdapter(urlAdapter);

                appIDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (!isFirstInit) {
                            clipData(appIDSpinner.getSelectedView());
                        } else {
                            isFirstInit = false;
                        }
                        try {
                            mApplicationInfoStr = mTAToolServer.getAppAndSDKInfoWithAppID(appIDs[position]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (!mApplicationInfoStr.isEmpty()) {
                            //update something
                            JSONObject appInfoJsonObject = null;
                            try {
                                appInfoJsonObject = new JSONObject(mApplicationInfoStr);
                            } catch (JSONException e) {
//                                e.printStackTrace();
                            }
                            serverUrlSpinner.setSelection(position);
                            if (appInfoJsonObject != null) {
                                isMultiProcessTV.setText(String.valueOf(appInfoJsonObject.opt("isMultiProcess")));
                                sdkModeTV.setText(String.valueOf(appInfoJsonObject.opt("sdkMode")));
                                sdkTrackStatusTV.setText(String.valueOf(appInfoJsonObject.opt("trackState")));
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                appIDSpinner.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (appIDs.length > 0) {
                            clipData(appIDSpinner.getSelectedView());
                        }
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unbindThis() {
        if (mTAToolServer != null) {
            try {
                mTAToolServer.unbindThis();
                mTAToolServer = null;
                mActivity.getApplicationContext().unbindService(serviceConnection);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        instance = null;
    }

    public static PopupInfoView getInstance(Activity activity) {
        if (instance == null) {
            synchronized (PopupInfoView.class){
                if (instance == null) {
                    instance = new PopupInfoView(activity);
                }
            }
        }
        return instance;
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.app_name_value_tv:
            case R.id.app_version_name_value_tv:
            case R.id.app_version_code_value_tv:
            case R.id.lib_version_value_tv:
            case R.id.device_id_value_tv:
            case R.id.is_multi_process_value_tv:
            case R.id.enable_bg_start_value_tv:
                clipData(v);
                break;
        }
        return false;
    }
}
