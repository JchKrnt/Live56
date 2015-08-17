package com.sohu.live56.view.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseTitleActivity;

/**
 * 個人信息
 */
public class PersonalInfoActivity extends BaseTitleActivity {

    @Override
    protected View onAddContainerView() {

        View view = View.inflate(getApplicationContext(), R.layout.activity_personal_info, null);

        return view;
    }
}
