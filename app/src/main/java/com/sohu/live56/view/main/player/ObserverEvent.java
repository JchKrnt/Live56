package com.sohu.live56.view.main.player;

import com.sohu.live56.view.BaseFragment;

/**
 * Created by jingbiaowang on 2015/8/14.
 */
public interface ObserverEvent extends BasePlayEvent {

    public void onPrepareLoad();

    public void onVideoLoading();

    public void onVideoLoaded();

    public void onVideoPause();

    /**
     * 再来一遍。
     */
    public void tryAgain();
}
