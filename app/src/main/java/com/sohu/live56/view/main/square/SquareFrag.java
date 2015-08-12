package com.sohu.live56.view.main.square;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sohu.live56.R;

/**
 * 广场 fragment .
 * <p/>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SquareFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SquareFrag extends Fragment {
    private TextView titletv;
    private ListView squarelv;
    private FrameLayout squarenodatefl;
    // TODO: Rename parameter arguments, choose names that match

    public static SquareFrag newInstance() {
        SquareFrag fragment = new SquareFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SquareFrag() {
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
        View view = inflater.inflate(R.layout.fragment_square, container, false);
        initialize(view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initialize(View view) {

        titletv = (TextView) view.findViewById(R.id.title_tv);
        squarelv = (ListView) view.findViewById(R.id.square_lv);
        squarenodatefl = (FrameLayout) view.findViewById(R.id.square_nodate_fl);
    }


}
