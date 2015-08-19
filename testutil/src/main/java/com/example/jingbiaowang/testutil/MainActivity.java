package com.example.jingbiaowang.testutil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addBtn;
    private LinearLayout contentLl;

    private int initCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }


    private void init() {
        addBtn = (Button) findViewById(R.id.add_btn);
        contentLl = (LinearLayout) findViewById(R.id.content_ll);
        addBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.add_btn: {

                TextView textView = new TextView(getApplicationContext());
                textView.setBackgroundColor(getResources().getColor(R.color.red));
                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setText("text " + String.valueOf(initCount));
                contentLl.addView(textView);
                initCount++;
                break;

            }
        }
    }
}
