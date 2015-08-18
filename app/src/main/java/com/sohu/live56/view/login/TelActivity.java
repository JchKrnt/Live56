package com.sohu.live56.view.login;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jch.utillib.util.VaildUtil;
import com.sohu.live56.R;
import com.sohu.live56.bean.UserInfo;
import com.sohu.live56.view.BaseTitleActivity;
import com.sohu.live56.view.main.MainActivity;

/**
 * 手机号登录
 */
public class TelActivity extends BaseTitleActivity implements View.OnClickListener {


    private Button loginbtn;
    private TextView inputchecktv;
    private EditText inputtel;
    private EditText inputchecket;

    @Override
    protected View onAddContainerView() {
        View view = View.inflate(getApplicationContext(), R.layout.activity_tel, null);

        initialize(view);

        return view;
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTitletv(getResources().getString(R.string.tel_login));
    }

    private void initialize(View view) {

        loginbtn = (Button) view.findViewById(R.id.login_btn);
        inputchecktv = (TextView) view.findViewById(R.id.input_check_tv);
        inputtel = (EditText) view.findViewById(R.id.input_tel);
        inputchecket = (EditText) view.findViewById(R.id.input_check_et);

        loginbtn.setOnClickListener(this);
        inputchecktv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.login_btn: {

                login();
                break;
            }

            case R.id.input_check_tv: {  //获取验证码。

            }
        }

    }

    private void login() {

        String telNum = inputtel.getText().toString();
        String checkNum = inputchecket.getText().toString();
        String telResult = VaildUtil.validPhone(telNum);
        String checkResult = VaildUtil.validCheckCode(checkNum);
        if (!("").equals(telResult)) {
            Toast.makeText(getApplicationContext(), telResult, Toast.LENGTH_SHORT).show();
            return;
        } else if (!"".equals(checkResult)) {
            Toast.makeText(getApplicationContext(), checkResult, Toast.LENGTH_SHORT).show();
            return;
        } else {
            // check num.
            Intent intent = new Intent();
            UserInfo userInfo = new UserInfo();
            userInfo.setTelNum(telNum);
            userInfo.setLoginType(UserInfo.LoginType.TEL);
            intent.putExtra(MainActivity.DATA_Key, userInfo);
            setResult(RESULT_OK, intent);
            finish();
        }

    }
}
