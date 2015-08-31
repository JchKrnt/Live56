package com.sohu.live56.view.main.player;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseFragment;

/**
 * Created by jingbiaowang on 2015/8/13.
 * <p>
 * the common controller for palyer .Instance of stop , share.
 */
public class BasePlayerFrag extends BaseFragment {

    private BasePlayEvent playEvent;

    @Override
    public void onSetEventListener(OnFragmentInteractionListener listener) {
        playEvent = (BasePlayEvent) listener;
    }

}


