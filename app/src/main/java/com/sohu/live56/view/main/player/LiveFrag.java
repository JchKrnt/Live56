package com.sohu.live56.view.main.player;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link LiveFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LiveFrag extends BaseFragment {

    public static LiveFrag newInstance() {
        LiveFrag fragment = new LiveFrag();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public LiveFrag() {
        // Required empty public constructor
    }

    @Override
    public void onSetEventListener(OnFragmentInteractionListener listener) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_live, container, false);
    }

}
