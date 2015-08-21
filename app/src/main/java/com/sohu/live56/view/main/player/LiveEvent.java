package com.sohu.live56.view.main.player;

import com.sohu.live56.view.BaseFragment;

/**
 * Created by jingbiaowang on 2015/8/14.
 * 直播事件。
 */

public interface LiveEvent extends BasePlayEvent {

    public void onPrepareLive();

    public void puaseLive();


}

interface BasePlayEvent extends BaseFragment.OnFragmentInteractionListener {

    public void stopVideo();

    public void onLiveLoading();

    public void onLiveLoaded();
}
