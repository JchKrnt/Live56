package com.sohu.live56.view.main;

import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.sohu.live56.R;
import com.sohu.live56.view.main.personal.PersonalFrag;
import com.sohu.live56.view.main.square.SquareFrag;
import com.sohu.live56.view.util.HomeTabHost;

/**
 * 主页。
 */
public class MainActivity extends FragmentActivity implements HomeTabHost.OnTabButonCheckedListener {

    private HomeTabHost tabHost;
    private FrameLayout tabContent;
    private CheckBox squareCb;
    private ImageView liveCb;
    private CheckBox mineCb;

    private PersonalFrag personalFrag;
    private SquareFrag squareFrag;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
    }

    private void initialize() {
        tabHost = (HomeTabHost) findViewById(android.R.id.tabhost);
        tabContent = (FrameLayout) findViewById(android.R.id.tabcontent);
        squareCb = (CheckBox) findViewById(R.id.tab_left_btn1);
        liveCb = (ImageView) findViewById(R.id.tab_center_btn);
        mineCb = (CheckBox) findViewById(R.id.tab_right_btn1);

        tabHost.setOnTabButonCheckedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        squareFrag = SquareFrag.newInstance();
        ft.replace(android.R.id.tabcontent, squareFrag);
        personalFrag = PersonalFrag.newInstance();
        ft.commit();
    }

    @Override
    public void onLeftChecked(View view) {
        ft = getSupportFragmentManager().beginTransaction();
        if (squareFrag == null)
            squareFrag = SquareFrag.newInstance();
        ft.replace(android.R.id.tabcontent, squareFrag);
        ft.commit();

    }

    @Override
    public void onRightChecked(View view) {
        ft = getSupportFragmentManager().beginTransaction();
        if (personalFrag == null)
            personalFrag = PersonalFrag.newInstance();
        ft.replace(android.R.id.tabcontent, personalFrag);
        ft.commit();
    }
}
