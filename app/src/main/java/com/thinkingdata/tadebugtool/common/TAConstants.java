/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.common;

import java.util.ArrayList;
import java.util.List;

/**
 * < Constants >.
 *
 * @author bugliee
 * @create 2022/6/28
 * @since 1.0.0
 */
public class TAConstants {

    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String KEY_TA_TOOL_ACTION = "cn.thinkingdata.android.TAToolServer";
    public static final int VIEW_RADIUS = 90;

    public static final int FILTER_TYPE_TIME_SORT = 0;
    public static final int FILTER_TYPE_PROP = 1;
    public static final int FILTER_TYPE_EVENT_TYPE = 2;

    public static final String[] FILTER_TIME_ARRAY = new String[]{"时间升序", "时间降序"};
    public static final String[] FILTER_EVENT_TYPE_ARRAY = new String[]{"全量事件", "自动采集事件", "用户事件", "自定义事件"};

    public static final ArrayList<String> AUTO_TRACK_EVENT_LIST = new ArrayList() {{
        add("ta_app_start");
        add("ta_app_view");
        add("ta_app_end");
        add("ta_app_install");
        add("ta_app_crash");
        add("ta_app_click");
    }};

    public static final ArrayList<String> PROFILE_EVENT_LiST = new ArrayList() {{
        add("user_add");
        add("user_set");
        add("user_setOnce");
        add("user_unset");
        add("user_append");
        add("user_del");
        add("user_uniq_append");
    }};

    public static final ArrayList<String> ADVANCE_EVENT_LiST = new ArrayList() {{
        add("track_update");
        add("track_track_overwrite");
    }};

}
