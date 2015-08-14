package com.sohu.live56.view.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.LinearLayout;

/**
 * Created by jingbiaowang on 2015/8/14.
 * <p/>
 * Show messages in list type.
 */
public class MessageListView extends LinearLayout {

    public MessageListView(Context context) {
        super(context);
    }

    public MessageListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setLayoutAnimationListener(Animation.AnimationListener animationListener) {
        super.setLayoutAnimationListener(animationListener);

    }
}
