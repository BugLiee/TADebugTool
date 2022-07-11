/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.utils;

import android.util.Log;

import com.thinkingdata.tadebugtool.bean.TAEvent;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * < eventUtil >.
 *
 * @author bugliee
 * @create 2022/7/4
 * @since 1.0.0
 */
public class EventUtil {

    // TAG  V D I W E
    //    public static String cmdOrder = "logcat -s *:V";
    public static final String defaultTag = "ThinkingAnalytics";
    public static final String[] taTags = new String[]{
            "ThinkingAnalytics"
            ,"ThinkingAnalyticsSDK"
            ,"ThinkingAnalytics.SystemInformation"
            ,"ThinkingAnalytics.TAEncryptUtils"
            ,"ThinkingAnalytics.DataHandle"
            ,"ThinkingAnalytics.DatabaseAdapter"
            ,"ThinkingAnalytics.PathFinder"
            ,"ThinkingAnalytics.TDConfig"
            ,"ThinkingAnalytics.TDPresetProperties"
            ,"ThinkingAnalytics.ThinkingDataActivityLifecycleCallbacks"
            ,"ThinkingAnalytics.ThinkingDataRuntimeBridge"
            ,"ThinkingAnalytics.TAPushLifecycle"
            ,"ThinkingAnalytics.process"
            ,"ThinkingAnalytics.TAEncryptUtils"
            ,"ThinkingAnalytics.ThinkingDataEncrypt"
            ,"ThinkingAnalytics.SyncData"
            ,"ThinkingAnalytics.HttpService"
            ,"ThinkingAnalytics.PropertyUtils"
            ,"ThinkingAnalytics.TAReflectUtils"
            ,"ThinkingAnalytics.NTP"
            ,"ThinkingAnalytics.TDNTPClient"
            ,"ThinkingAnalytics.ExceptionHandler"
            ,"ThinkingAnalytics.TDUniqueEvent"
            ,"ThinkingAnalytics.TDWebAppInterface"
    };
    //
    private static Pattern pattern = Pattern.compile("\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s\\d{5}\\s\\d{5}" + "\\s[VIDWE]\\s" + defaultTag + "(SDK)*\\.*\\S*?:\\s");
    private static Pattern prefixPattern = Pattern.compile("\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s\\d{5}\\s\\d{5}" + "\\s[VIDWE]\\s" + defaultTag + "(SDK)*\\.*\\S*?:\\s+\\{");
    private static Pattern suffixPattern = Pattern.compile("\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3}\\s\\d{5}\\s\\d{5}" + "\\s[VIDWE]\\s" + defaultTag + "(SDK)*\\.*\\S*?:\\s+\\}");


    public static synchronized TAEvent castStr2Event(String str) {
        TAEvent event = new TAEvent(new JSONObject());
        String c = "07-02 06:32:54.940 24064 24064 I ThinkingAnalyticsSDK: No mAutoTrackEventListener\n" +
                "    07-02 06:32:54.940 24064 24064 I ThinkingAnalyticsSDK: No mAutoTrackEventTrackerListener\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle: Data enqueued(3356):\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle: {\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:     \"#type\": \"track\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:     \"#time\": \"2022-07-02 06:32:54.908\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:     \"#distinct_id\": \"instance_id\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:     \"#event_name\": \"ta_app_start\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:     \"properties\": {\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#lib_version\": \"2.8.1\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#carrier\": \"T-Mobile\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#os\": \"Android\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#device_id\": \"410cd770d8c3aec6\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#screen_height\": 2560,\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#bundle_id\": \"cn.thinkingdata.android.demo\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#device_model\": \"sdk_gphone64_arm64\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#screen_width\": 1440,\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#system_language\": \"en\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#install_time\": \"2022-06-17 10:04:13.313\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#simulator\": true,\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#lib\": \"Android\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#manufacturer\": \"Google\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#os_version\": \"12\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#app_version\": \"1.0\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#fps\": 60,\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"key1\": \"self value1\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#network_type\": \"3G\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#ram\": \"0.6\\/1.9\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#disk\": \"0.1\\/0.8\",\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#resume_from_background\": true,\n" +
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle:         \"#screen_name\": \"cn.thinkingdata.android.demo.MainActivity\","+
                "    07-02 06:32:55.102 24064 24112 I ThinkingAnalytics.DataHandle: }";

        Matcher matcherPrefix = prefixPattern.matcher(c);
        if (matcherPrefix.find()) {
            //找到头
            String findStr = matcherPrefix.group();
            String subContent = c.substring(c.indexOf(findStr));
            //时间
            String timeStr = subContent.substring(0, 18);
            //尝试找结尾
            Matcher matcherSuffix = suffixPattern.matcher(subContent);
            if (matcherSuffix.find()) {
                //找到结尾 ，添加}
                subContent = subContent.substring(0, subContent.indexOf(matcherSuffix.group()));
                subContent += "}";
            }
            //替换 时间和tag
            subContent = subContent.replace(findStr.replace("{", ""), "");


        }
        return event;
    }
}
