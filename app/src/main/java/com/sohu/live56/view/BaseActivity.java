package com.sohu.live56.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.sohu.live56.R;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public abstract class BaseActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_base_layout);
        //TODO commend operator.

        FrameLayout content = (FrameLayout) findViewById(R.id.live_content);
        content.addView(onCreateView());
    }


    protected abstract View onCreateView();
}
