package com.sohu.live56.view.main.player;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseActivity;

/**
 * Created by jingbiaowang on 2015/8/13.
 * <p>
 * in charge of video player.
 */
public class PlayerActivity extends BaseActivity {

    private FrameLayout contentView;

    @Override
    protected View onCreateView() {

        contentView = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_palyer_layout, null);

        return contentView;
    }


}
