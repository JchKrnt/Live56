package com.sohu.live56.view.login;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseTitleActivity;

/**
 * Created by jingbiaowang on 2015/8/17.
 */
public class LoginActivity extends BaseTitleActivity {


    private TextView loginweixintv;
    private TextView loginmicroblogtv;
    private TextView loginqqtv;
    private Button loginbtn;

    @Override
    protected View onAddContainerView() {
        View view = View.inflate(getApplicationContext(), R.layout.activity_login_layout, null);

        initialize(view);
        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();

        setTitletv(getResources().getString(R.string.login));
    }

    private void initialize(View v) {

        loginweixintv = (TextView) v.findViewById(R.id.login_weixin_tv);
        loginmicroblogtv = (TextView) v.findViewById(R.id.login_microblog_tv);
        loginqqtv = (TextView) v.findViewById(R.id.login_qq_tv);
        loginbtn = (Button) v.findViewById(R.id.login_btn);


    }
}

