package com.sohu.live56.view;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.sohu.live56.R;
import com.sohu.live56.util.LogCat;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public abstract class BaseActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_layout);
        //TODO commend operator.
        LogCat.v("BaseActivity oncreate()");

        FrameLayout content = (FrameLayout) findViewById(R.id.live_content);
        content.addView(onCreateView());

    }



    protected abstract View onCreateView();
}
