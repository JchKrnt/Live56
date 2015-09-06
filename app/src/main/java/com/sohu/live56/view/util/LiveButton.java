package com.sohu.live56.view.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import com.sohu.live56.R;

/**
 * Created by jingbiaowang on 2015/8/19.
 */
public class LiveButton extends ImageButton {
    /**
     * 直播状态。
     */
    public enum LiveStatus {
        PREPARE, PLAY, PAUSE
    }


    private Drawable mPrepareDrawable;
    private Drawable mPalyDrawable;
    private Drawable mPauseDrawable;

    private LiveStatus mState = LiveStatus.PREPARE;

    public LiveButton(Context context) {
        super(context);
    }

    public LiveButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LiveButton, defStyleAttr, 0);


        mPrepareDrawable = a.getDrawable(R.styleable.LiveButton_prepare_src);
        mPalyDrawable = a.getDrawable(R.styleable.LiveButton_play_src);
        mPauseDrawable = a.getDrawable(R.styleable.LiveButton_pause_src);

        a.recycle();
        init();
    }

    @Override
    public final void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
    }

    private void init() {
        super.setBackgroundResource(0);
        setLiveState(mState);
    }

    public void setLiveState(LiveStatus state) {
        this.mState = state;
        switch (mState) {
            case PREPARE: {
                setImageDrawable(mPrepareDrawable);
                break;
            }
            case PAUSE: {
                setImageDrawable(mPauseDrawable);
                break;
            }
            case PLAY: {
                setImageDrawable(mPalyDrawable);
                break;
            }
        }
    }

}
