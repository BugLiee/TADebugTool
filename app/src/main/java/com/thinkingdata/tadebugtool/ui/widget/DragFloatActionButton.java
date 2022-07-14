package com.thinkingdata.tadebugtool.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.thinkingdata.tadebugtool.utils.SnackbarUtil;
import com.thinkingdata.tadebugtool.utils.TAUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 可拖动的悬浮按钮，并且会自动贴边，可调整贴边度
 */
public class DragFloatActionButton extends FloatingActionButton {

    //内部解锁相关
    //锁屏9键坐标
    private final int[] loc1 = new int[2];
    private final int[] loc2 = new int[2];
    private final int[] loc3 = new int[2];
    private final int[] loc4 = new int[2];
    private final int[] loc5 = new int[2];
    private final int[] loc6 = new int[2];
    private final int[] loc7 = new int[2];
    private final int[] loc8 = new int[2];
    private final int[] loc9 = new int[2];
    List<Integer> keyList = new ArrayList<>();
    List<Integer> secureList = new ArrayList<>();
    List<int[]> sourceList = new ArrayList<>();
    private boolean unLock = false;
    private int radius = 0;


    private int parentHeight;
    private int parentWidth;

    private int lastX;
    private int lastY;

    private boolean isDrag;
    private ViewGroup parent;

    private boolean isHide = false;
    private boolean isLeft = true;

    private boolean isShowing = true;

    private Context mContext;
    private int screenWidth;
    private int screenHeight;

    public boolean isHide() {
        return isHide;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public DragFloatActionButton(Context context) {
        super(context);
        mContext = context;
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    private void checkKeyLoc(float x, float y) {
        for (int i = 0; i < sourceList.size(); i++) {
            int[] loc = sourceList.get(i);
            if (x >= loc[0] - radius && x <= loc[0] + radius && y >= loc[1] - radius && y <= loc[1] + radius) {
                if (!keyList.contains(i + 1)) {
                    keyList.add(i + 1);
                    break;
                }
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initScreenSize();
        initLoc();
    }

    private void initScreenSize() {
        int[] screenSize = TAUtil.getDeviceSize(mContext);
        screenWidth = screenSize[0];
        screenHeight = screenSize[1];
    }

    private void initLoc() {
        int oWidth = getWidth();
        radius = oWidth / 2;

        int centerLocX = screenWidth / 2;
        int centerLocY = screenHeight / 2;

        loc1[0] = centerLocX - oWidth * 2;
        loc1[1] = centerLocY - oWidth * 2;
        loc2[0] = centerLocX;
        loc2[1] = centerLocY - oWidth * 2;
        loc3[0] = centerLocX + oWidth * 2;
        loc3[1] = centerLocY - oWidth * 2;
        loc4[0] = centerLocX - oWidth * 2;
        loc4[1] = centerLocY;
        loc5[0] = centerLocX;
        loc5[1] = centerLocY;
        loc6[0] = centerLocX + oWidth * 2;
        loc6[1] = centerLocY;
        loc7[0] = centerLocX - oWidth * 2;
        loc7[1] = centerLocY + oWidth * 2;
        loc8[0] = centerLocX;
        loc8[1] = centerLocY + oWidth * 2;
        loc9[0] = centerLocX + oWidth * 2;
        loc9[1] = centerLocY + oWidth * 2;

        sourceList.add(loc1);
        sourceList.add(loc2);
        sourceList.add(loc3);
        sourceList.add(loc4);
        sourceList.add(loc5);
        sourceList.add(loc6);
        sourceList.add(loc7);
        sourceList.add(loc8);
        sourceList.add(loc9);

        secureList.add(4);
        secureList.add(1);
        secureList.add(2);
        secureList.add(3);
        secureList.add(6);
        secureList.add(9);
        secureList.add(8);
        secureList.add(7);
    }

    public void showFromHidden() {
        if (isHide) {
            ObjectAnimator oa;
            if (isLeft) {
                oa = ObjectAnimator.ofFloat(this, "x", getX(),
                        getWidth() / 2f);
            } else {
                oa = ObjectAnimator.ofFloat(this, "x", getX(),
                        parentWidth - getWidth() * 3 / 2f);
            }
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(200);
            oa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    isHide = false;

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isShowing = true;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            oa.start();
        }
    }

    public interface OnUnLockListener{
        void unLock();
    }

    private OnUnLockListener unLockListener;

    public void setUnLockListener(OnUnLockListener unLockListener) {
        this.unLockListener = unLockListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        for (int i = 0; i < keyList.size(); i++) {
            Log.d("bugliee", "keyList " + keyList.get(i));
        }
        checkKeyLoc(rawX, rawY);
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                isDrag = false;
                this.setAlpha(0.9f);
                setPressed(true);
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                if (getParent() != null) {
                    parent = (ViewGroup) getParent();
                    parentHeight = parent.getHeight();
                    parentWidth = parent.getWidth();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                this.setAlpha(0.9f);
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                if (distance > 2 && !isDrag) {
                    isDrag = true;
                }

                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > parentWidth - getWidth() ? parentWidth - getWidth() : x;
                y = getY() < 0 ? 0 : getY() + getHeight() > parentHeight ? parentHeight - getHeight() : y;
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_UP:
                //解锁
                if (!unLock) {
                    if (keyList.size() == 8) {
                        unLock = true;
                        for (int i = 0; i < keyList.size(); i++) {
                            if (!keyList.get(i).equals(secureList.get(i))) {
                                unLock = false;
                                break;
                            }
                        }
                    }
                    if (unLock) {
                        if (unLockListener != null) {
                            SnackbarUtil.showSnackBarMid("恭喜你进入了高级模式");
                            unLockListener.unLock();
                        }
                    }
                }
                if (isDrag) {
                    //恢复按压效果
                    setPressed(false);
                    moveHide(rawX);
                }
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
    }


    private void moveHide(int rawX) {
        if (rawX >= parentWidth / 2) {
            isLeft = false;
            //靠右吸附
            //方式1
            /*animate().setInterpolator(new DecelerateInterpolator())
                    .setDuration(500)
                    .xBy(parentWidth - getWidth() - getX() + getWidth() / 2f)
//                    .xBy(parentWidth - getWidth() - getX() - GetWinPoint.dp2px(getContext(), 20))
                    .start();*/
            //方式2 当前位置到指定位置 x轴移动
            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(),
                    parentWidth - getWidth() / 2f);
//                    GetWinPoint.dp2px(getContext(), 20));
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(300);
            oa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isHide = true;
                    isShowing = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            oa.start();
        } else {
            //靠左吸附
            isLeft = true;
            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(),
                    0 - getWidth() / 2f);
            oa.setInterpolator(new DecelerateInterpolator());
            oa.setDuration(300);
            oa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    isHide = true;
                    isShowing = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            oa.start();

        }
    }
}
