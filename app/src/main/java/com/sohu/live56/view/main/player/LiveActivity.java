package com.sohu.live56.view.main.player;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public class LiveActivity extends PlayerActivity implements LiveEvent {

    private int containerViewId;
    private FragmentManager fragmentManager;

    @Override
    protected void onAddFragment(int contentViewId) {

        this.containerViewId = contentViewId;
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LiveFrag liveFrag = LiveFrag.newInstance(false);
        ft.add(contentViewId, liveFrag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onPrepareLive() {
        onViewPrepared();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        VideoWaitFrag waitFrag = VideoWaitFrag.newInstance();
        ft.replace(containerViewId, waitFrag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onLiveLoading() {

    }

    @Override
    public void onLiveLoaded() {

    }

    @Override
    public void puaseLive() {

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void stopVideo() {
        this.finish();
    }


    @Override
    protected void onVideoConnected() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LiveFrag liveFrag = LiveFrag.newInstance(true);
        ft.replace(containerViewId, liveFrag);
        ft.commitAllowingStateLoss();
    }
}
