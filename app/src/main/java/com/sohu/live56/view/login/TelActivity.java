package com.sohu.live56.view.login;

import android.view.View;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseTitleActivity;

/**
 * 手机号登录
 */
public class TelActivity extends BaseTitleActivity {


    @Override
    protected View onAddContainerView() {
        View view = View.inflate(getApplicationContext(), R.layout.activity_tel, null);

        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitletv(getResources().getString(R.string.tel_login));
    }
}
