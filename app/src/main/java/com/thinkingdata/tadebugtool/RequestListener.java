/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool;

/**
 * < interface >.
 *
 * @author bugliee
 * @create 2022/7/14
 * @since 1.0.0
 */
public interface RequestListener{
    void requestEnd(int code, String msg);
}
