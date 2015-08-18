package com.sohu.live56.view.main.player;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sohu.live56.R;
import com.sohu.live56.util.LogCat;
import com.sohu.live56.view.BaseActivity;

/**
 * Created by jingbiaowang on 2015/8/13.
 * <p/>
 * in charge of video player.
 */
public abstract class PlayerActivity extends BaseActivity {

    private FrameLayout contentView;
    private GLSurfaceView glviewcall;
    private FrameLayout playerfragcontent;

    @Override
    protected View onCreateView() {
        contentView = (FrameLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_palyer_layout, null);
        contentView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initialize();

        onAddFragment(R.id.player_frag_content);
        LogCat.v("PlayerActivity onCreateView()");
        return contentView;
    }

    protected abstract void onAddFragment(int contentViewId);

    protected abstract void onVideoConnected();

    private void initialize() {

//        glviewcall = (GLSurfaceView) findViewById(R.id.glview_call);
        playerfragcontent = (FrameLayout) findViewById(R.id.player_frag_content);
    }


    protected void stopVideo() {


    }
}
