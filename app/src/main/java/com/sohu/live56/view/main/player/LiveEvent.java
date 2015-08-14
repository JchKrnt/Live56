package com.sohu.live56.view.main.player;

import com.sohu.live56.view.BaseFragment;

/**
 * Created by jingbiaowang on 2015/8/14.
 */

public interface LiveEvent extends BasePlayEvent {

    public void onPrepareLive();

    public void onLiveLoading();

    public void onLiveLoaded();

    public void puaseLive();

}

interface BasePlayEvent extends BaseFragment.OnFragmentInteractionListener {

    public void stopVideo();
}
