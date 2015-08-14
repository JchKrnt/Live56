package com.sohu.live56.view.main.player;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sohu.live56.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoWaitFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoWaitFrag extends Fragment {


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VideoWaitFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoWaitFrag newInstance() {
        VideoWaitFrag fragment = new VideoWaitFrag();
        return fragment;
    }

    public VideoWaitFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video_wait, container, false);
    }


}
