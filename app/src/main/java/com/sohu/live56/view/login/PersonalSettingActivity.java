package com.sohu.live56.view.login;

import android.view.View;

import com.sohu.live56.R;
import com.sohu.live56.view.BaseTitleActivity;

/**
 * 个性化设置。
 */

public class PersonalSettingActivity extends BaseTitleActivity {

    @Override
    protected View onAddContainerView() {

        View view = View.inflate(getApplicationContext(), R.layout.activity_personal_setting, null);

        return view;
    }
}
