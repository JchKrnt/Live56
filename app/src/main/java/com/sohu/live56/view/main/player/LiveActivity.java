package com.sohu.live56.view.main.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.sohu.live56.R;

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
    public void onPrepareLive(String titleStr) {
        registerRoom(titleStr);
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
    public void onRegistSuccess() {
        onViewPrepared();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        VideoWaitFrag waitFrag = VideoWaitFrag.newInstance();
        ft.replace(containerViewId, waitFrag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onResiterFailure(final String msg) {
        new AlertDialog.Builder(LiveActivity.this)
                .setTitle(getText(R.string.channel_error_title))
                .setMessage(msg)
                .setCancelable(false)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).create().show();

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
