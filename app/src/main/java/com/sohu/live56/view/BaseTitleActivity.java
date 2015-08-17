package com.sohu.live56.view;/**
 * Created by jingbiaowang on 2015/8/17.
 */

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.sohu.live56.R;

import static android.widget.FrameLayout.*;

public abstract class BaseTitleActivity extends BaseActivity {


    private ImageButton backbtn;
    private TextView titletv;
    private TextView titlerightbtn;
    private FrameLayout basecontainer;
    private OnRightBtnClickEvent rightBtnClickEvent;
    private MyOnClick myOnClick = new MyOnClick();

    public interface OnRightBtnClickEvent {
        public void onClickEvent(View view);
    }

    @Override
    protected View onCreateView() {

        View titleContainerLayout = View.inflate(getApplicationContext(), R.layout.base_title_layout, null);

        initialize(titleContainerLayout);

        return titleContainerLayout;
    }

    protected abstract View onAddContainerView();

    private void initialize(View view) {

        backbtn = (ImageButton) view.findViewById(R.id.back_btn);
        titletv = (TextView) view.findViewById(R.id.title_tv);
        titlerightbtn = (TextView) view.findViewById(R.id.title_right_btn);
        basecontainer = (ScrollView) view.findViewById(R.id.base_container);
        basecontainer.addView(onAddContainerView(), new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.MATCH_PARENT));

        backbtn.setOnClickListener(myOnClick);
        titlerightbtn.setOnClickListener(myOnClick);

    }

    protected void setTitletv(String title) {
        titletv.setText(title);
    }

    public void setRightBtnText(String text, OnRightBtnClickEvent event) {
        titlerightbtn.setText(text);
        this.rightBtnClickEvent = event;
    }

    private class MyOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.back_btn: {

                    onBackPressed();
                    finish();
                    break;
                }

                case R.id.title_right_btn: {
                    if (rightBtnClickEvent != null)
                        rightBtnClickEvent.onClickEvent(v);
                    break;
                }


            }
        }
    }
}
