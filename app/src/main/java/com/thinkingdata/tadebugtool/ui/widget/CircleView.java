/*
 * Copyright (C) 2022 ThinkingData
 */

package com.thinkingdata.tadebugtool.ui.widget;

import static com.thinkingdata.tadebugtool.common.TAConstants.VIEW_RADIUS;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.thinkingdata.tadebugtool.R;

/**
 * < a circle view >.
 *
 * @author bugliee
 * @create 2022/4/22
 * @since 1.0.0
 */
public class CircleView extends View {
    public static int mRadius = VIEW_RADIUS;

    private int mColor = Color.WHITE;
    public CircleView(Context context) {
        super(context);
        init(null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CircleView);
            mColor = typedArray.getColor(R.styleable.CircleView_bgColor, Color.WHITE);

            typedArray.recycle();
        }
        setBackgroundColor(mColor);
        setClickable(false);
    }

    @Override
    public void draw(Canvas canvas) {
        Path path = new Path();
        path.addCircle(mRadius, mRadius, mRadius, Path.Direction.CCW);
        canvas.clipPath(path, Region.Op.INTERSECT);
        super.draw(canvas);
    }


    public void setRadius(int radius) {
        mRadius = radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }


    public void setBG(int color) {
        setBackgroundColor(color);
    }
}
