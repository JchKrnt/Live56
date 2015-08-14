package com.sohu.live56.view.main.player;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jch.utillib.view.CircleImageView;
import com.sohu.live56.R;

/**
 * A  {@link BasePlayerFrag} subclass.
 * create an instance of this fragment.
 */
public class ObserverFrag extends BasePlayerFrag implements View.OnClickListener {


    private CircleImageView videofragimg;
    private TextView videofragname;
    private TextView videofragcareer;
    private ImageButton videofragclose;
    private ImageView videofraglogo;
    private ImageButton videofragshare;
    private TextView videofragnum;
    private LinearLayout videofragbtm;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ObserverFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static ObserverFrag newInstance() {
        ObserverFrag fragment = new ObserverFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ObserverFrag() {
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
        View containerView = inflater.inflate(R.layout.fragment_observer, container, false);
        initialize(containerView);
        return containerView;

    }


    private void initialize(View view) {

        videofragimg = (CircleImageView) view.findViewById(R.id.video_frag_img);
        videofragname = (TextView) view.findViewById(R.id.video_frag_name);
        videofragcareer = (TextView) view.findViewById(R.id.video_frag_career);
        videofragclose = (ImageButton) view.findViewById(R.id.video_frag_close);
        videofraglogo = (ImageView) view.findViewById(R.id.video_frag_logo);
        videofragshare = (ImageButton) view.findViewById(R.id.video_frag_share);
        videofragnum = (TextView) view.findViewById(R.id.video_frag_num);
        videofragbtm = (LinearLayout) view.findViewById(R.id.video_frag_btm);

        videofragclose.setOnClickListener(this);
        videofragshare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.video_frag_close: {
                Toast.makeText(getActivity().getApplicationContext(), "close", Toast.LENGTH_SHORT).show();
                break;
            }

            case R.id.video_frag_share: {
                Toast.makeText(getActivity().getApplicationContext(), "share", Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }
}
