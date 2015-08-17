package com.sohu.live56.view.login;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseTitleActivity;

/**
 * 联合登录。
 */

public class UnitLogin extends BaseTitleActivity implements View.OnClickListener {


    private EditText unitaccountet;
    private EditText unitpwdet;
    private Button loginbtn;

    @Override
    protected View onAddContainerView() {

        View view = View.inflate(getApplicationContext(), R.layout.activity_unit_login, null);

        initialize(view);
        return view;
    }

    private void initialize(View view) {
        unitaccountet = (EditText) view.findViewById(R.id.unit_account_et);
        unitpwdet = (EditText) view.findViewById(R.id.unit_pwd_et);
        loginbtn = (Button) view.findViewById(R.id.login_btn);

        unitaccountet.setOnClickListener(this);
        unitpwdet.setOnClickListener(this);
        loginbtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitletv(getResources().getString(R.string.title_unit_login));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.unit_account_et: {


                break;
            }
            case R.id.unit_pwd_et: {

                break;
            }

            case R.id.login_btn: {
                break;
            }
        }
    }
}



