package com.sohu.live56.view.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sohu.live56.R;

import pl.droidsonroids.gif.AnimationListener;

/**
 * Created by jingbiaowang on 2015/8/18.
 */
public class JchSwitchButton extends ImageButton {
    /**
     * 开关标识符。
     */
    private boolean switchFlag = true;
    private Drawable mOnDrawable;
    private Drawable mOffDrawable;


    public interface OnJchSwitchListener {

        public void onSwitchEvent(View view, boolean switchFlage);
    }

    private OnJchSwitchListener jchSwitchListener;

    public JchSwitchButton(Context context) {
        super(context);
    }

    public JchSwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JchSwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.JchSwitchButton, defStyleAttr, 0);

        mOnDrawable = a.getDrawable(R.styleable.JchSwitchButton_onBtn);
        mOffDrawable = a.getDrawable(R.styleable.JchSwitchButton_offBtn);
        switchFlag = a.getBoolean(R.styleable.JchSwitchButton_switch_flage, true);
        a.recycle();
        init();
    }

    private void init() {
        super.setBackgroundResource(0);
        setImgByFlag();
        this.setOnClickListener(new SwitchClickListener());
    }

    public void setJchSwitchListener(OnJchSwitchListener listener) {
        this.jchSwitchListener = listener;
    }

    private void setImgByFlag() {

        if (switchFlag) {
            super.setImageDrawable(mOnDrawable);
        } else
            super.setImageDrawable(mOffDrawable);
    }


    public class SwitchClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            switchFlag = !switchFlag;
            switchWithAnim(0, 90, true);
        }
    }

    @Override
    public void setRotationY(float rotationY) {
        super.setRotationY(rotationY);
    }

    private void switchWithAnim(int fromRotation, int toRotation, boolean listenerAble) {

        ObjectAnimator rotate = ObjectAnimator.ofFloat(JchSwitchButton.this, "rotationY", fromRotation, toRotation);
        rotate.setDuration(500);
        if (listenerAble)
            rotate.addListener(new RotationListener());
        rotate.start();
    }

    private class RotationListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

            if (switchFlag) {
                setImageDrawable(mOnDrawable);
            } else {
                setImageDrawable(mOffDrawable);
            }

            switchWithAnim(270, 360, false);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
