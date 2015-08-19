package com.sohu.live56.view.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseTitleActivity;
import com.sohu.live56.view.main.MainActivity;

/**
 * Created by jingbiaowang on 2015/8/17.
 */
public class LoginActivity extends BaseTitleActivity implements View.OnClickListener {


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

        loginbtn.setOnClickListener(this);
        loginmicroblogtv.setOnClickListener(this);
        loginqqtv.setOnClickListener(this);
        loginweixintv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.login_btn: {
                loginTel();

                break;
            }
            case R.id.login_microblog_tv: {

                break;
            }
            case R.id.login_qq_tv: {


                break;
            }
            case R.id.login_weixin_tv: {
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.CENTER_LOGIN_CODE && resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            this.finish();
        }
    }

    private void loginTel() {
        Intent intent = new Intent(LoginActivity.this, TelActivity.class);
        startActivityForResult(intent, MainActivity.CENTER_LOGIN_CODE);
    }


}

