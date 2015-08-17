package com.sohu.live56.view.main.player;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sohu.live56.util.LogCat;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public class ObserverActivity extends PlayerActivity implements ObserverEvent {

    private FragmentManager fm;

    private int contentViewId;

    @Override
    protected void onAddFragment(final int fContentId) {
        contentViewId = fContentId;
        fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        VideoWaitFrag frag = VideoWaitFrag.newInstance();
        ft.add(fContentId, frag);
        ft.commit();

        videoTimer();

    }

    @Override
    protected void onVideoConnected() {
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ObserverFrag observerFrag = ObserverFrag.newInstance();
        ft.replace(contentViewId, observerFrag);
//        ft.addToBackStack("observerFrag");
        ft.commit();
    }


    @Override
    public void onPrepareLoad() {

    }

    @Override
    public void onVideoLoading() {

    }

    @Override
    public void onVideoLoaded() {

    }

    @Override
    public void onVideoPause() {

    }

    @Override
    public void tryAgain() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void stopVideo() {

        onBackPressed();
        this.finish();
    }

    /**
     * 四秒后切换。
     */
    private void videoTimer() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onVideoConnected();
            }
        }, 4000);
    }

}
