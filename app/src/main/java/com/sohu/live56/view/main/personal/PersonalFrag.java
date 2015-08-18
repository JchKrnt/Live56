package com.sohu.live56.view.main.personal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jch.utillib.view.CircleImageView;
import com.sohu.live56.R;
import com.sohu.live56.view.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link PersonalFrag#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * 个人中心。
 */
public class PersonalFrag extends BaseFragment {
    private ImageButton personcentersetting;
    private CircleImageView personalcenterheadimg;
    private TextView personalcenternametv;
    private TextView personalcentersubnametv;
    private TextView personalcenterweixin;
    private TextView personalcentermicroblog;
    private TextView personalcenterqq;
    private ViewStub personallvvs;
    private ViewStub nodatavs;

    public static PersonalFrag newInstance() {
        PersonalFrag fragment = new PersonalFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PersonalFrag() {
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
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        initialize(view);
        return view;
    }

    @Override
    public void onSetEventListener(OnFragmentInteractionListener listener) {

    }


    private void initialize(View view) {

        personcentersetting = (ImageButton) view.findViewById(R.id.person_center_setting);
        personalcenterheadimg = (CircleImageView) view.findViewById(R.id.personal_center_head_img);
        personalcenternametv = (TextView) view.findViewById(R.id.personal_center_name_tv);
        personalcentersubnametv = (TextView) view.findViewById(R.id.personal_center_subname_tv);
        personalcenterweixin = (TextView) view.findViewById(R.id.personal_center_weixin);
        personalcentermicroblog = (TextView) view.findViewById(R.id.personal_center_microblog);
        personalcenterqq = (TextView) view.findViewById(R.id.personal_center_qq);
//        personallvvs = (ViewStub) view.findViewById(R.id.personal_lv_vs);
        nodatavs = (ViewStub) view.findViewById(R.id.nodata_vs);

        //no data.
        nodatavs.inflate();
    }
}
