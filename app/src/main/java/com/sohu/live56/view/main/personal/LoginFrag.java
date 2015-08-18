package com.sohu.live56.view.main.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseFragment;
import com.sohu.live56.view.login.TelActivity;
import com.sohu.live56.view.main.MainActivity;
import com.sohu.live56.view.main.MainfragmentCallBack;

/**
 * Created by jingbiaowang on 2015/8/17.
 */
public class LoginFrag extends BaseFragment implements View.OnClickListener {

    private MainfragmentCallBack callBack;
    private TextView loginweixintv;
    private TextView loginmicroblogtv;
    private TextView loginqqtv;
    private Button loginbtn;

    @Override

    public void onSetEventListener(OnFragmentInteractionListener listener) {
        callBack = (MainfragmentCallBack) listener;
    }

    public static LoginFrag instance() {

        LoginFrag loginFrag = new LoginFrag();
        loginFrag.setArguments(new Bundle());
        return loginFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity().getApplicationContext(), R.layout.frag_login_lalyout, null);

        initialize(view);
        return view;
    }

    private void initialize(View v) {

        loginweixintv = (TextView) v.findViewById(R.id.login_weixin_tv);
        loginmicroblogtv = (TextView) v.findViewById(R.id.login_microblog_tv);
        loginqqtv = (TextView) v.findViewById(R.id.login_qq_tv);
        loginbtn = (Button) v.findViewById(R.id.login_btn);

        loginbtn.setOnClickListener(this);
        loginmicroblogtv.setOnClickListener(this);
        loginqqtv.setOnClickListener(this);
        loginweixintv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_btn: {
                telLogin();
                break;
            }

            case R.id.login_weixin_tv: {

                break;
            }

            case R.id.login_microblog_tv: {

                break;
            }

            case R.id.login_qq_tv: {

                break;
            }

        }

    }

    private void telLogin() {
        Intent intent = new Intent(getActivity(), TelActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.LOGIN_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
