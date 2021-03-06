package com.sohu.live56.view.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TabHost;

import com.sohu.live56.R;


/**
 * TODO: document your custom view class.
 */
public class HomeTabHost extends ViewGroup implements CompoundButton.OnCheckedChangeListener {

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.tab_right_btn1) {
                leftBtn.setChecked(false);
                leftBtn.setEnabled(true);
                rightBtn.setEnabled(false);
                if (onTabButonCheckedListener != null) {
                    onTabButonCheckedListener.onRightChecked(buttonView);
                }

            } else if (buttonView.getId() == R.id.tab_left_btn1) {
                rightBtn.setChecked(false);
                rightBtn.setEnabled(true);
                leftBtn.setEnabled(false);
                if (onTabButonCheckedListener != null) {
                    onTabButonCheckedListener.onLeftChecked(buttonView);
                }
            }
        }
    }

    public interface OnTabButonCheckedListener {

        public void onLeftChecked(View view);

        public void onRightChecked(View view);

        public void onCenterClickedListener();
    }


    public static class Position {
        int left, top, right, bottom;

        public void setPosition(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    private View contentView = null;
    private View divideView = null;
    private View tabBgView = null;
    private ImageView centerImg = null;
    private CheckBox leftBtn = null;
    private CheckBox rightBtn = null;
    private Position tabBgViewPosition = new Position();
    private Position divideViewPosition = new Position();
    private Position leftBtnPosition = new Position();
    private Position centerBtnPosition = new Position();
    private Position rightBtnPosition = new Position();
    private OnTabButonCheckedListener onTabButonCheckedListener;

    public void setOnTabButonCheckedListener(OnTabButonCheckedListener onTabButonCheckedListener) {
        this.onTabButonCheckedListener = onTabButonCheckedListener;
    }

    public HomeTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 使ViewGroup的LayoutParams支持margin.
     *
     * @param attrs
     * @return
     */
    public LayoutParams generateLayoutParams(AttributeSet attrs) {

        return new MarginLayoutParams(getContext(), attrs);
    }


    @Override
    protected void onFinishInflate() {

        super.onFinishInflate();
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View cView = getChildAt(i);
            switch (cView.getId()) {

                case android.R.id.tabcontent: {
                    contentView = cView;
                    break;
                }
                case R.id.tab_bg_view_divid: {
                    divideView = cView;
                    break;
                }
                case R.id.tab_bg_view: {
                    tabBgView = cView;
                    break;
                }

                case R.id.tab_left_btn1: {
                    leftBtn = (CheckBox) cView;
                    if (leftBtn.isChecked())
                        leftBtn.setEnabled(false);
                    leftBtn.setOnCheckedChangeListener(this);
                    break;
                }

                case R.id.tab_center_btn: {
                    centerImg = (ImageView) cView;
                    centerImg.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onTabButonCheckedListener != null)
                                onTabButonCheckedListener.onCenterClickedListener();
                        }
                    });
                    break;
                }

                case R.id.tab_right_btn1: {
                    rightBtn = (CheckBox) cView;
                    if (rightBtn.isChecked())
                        rightBtn.setEnabled(false);
                    rightBtn.setOnCheckedChangeListener(this);
                    break;
                }
            }
        }
    }

    public void checkRightBtn() {
        rightBtn.setChecked(true);
    }

    public void checkLeftBtn() {
        leftBtn.setChecked(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获取此ViewGroup上级容器为其推荐的宽和高，以及计算模式。
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int totalUsedHeihgt = 0;
        int totalUsedWidth = 0;
        //计算childView的宽和高。
        for (int i = 0; i < getChildCount(); i++) {

            View childView = getChildAt(i);
            if (childView.getId() == android.R.id.tabcontent) {     //i=4;最后计算tabcontent.

                measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, totalUsedHeihgt);
            } else {        //计算出除tabcontent 外的childView的尺寸。
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                if (childView.getId() == R.id.tab_bg_view_divid || childView.getId() == R.id.tab_left_btn1)
                    totalUsedHeihgt += childView.getMeasuredHeight();           //tab用去的height。
            }
        }

        setMeasuredDimension(sizeWidth, sizeHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        layoutTabBgView(tabBgView, leftBtn.getMeasuredHeight());
        layoutTabDividView(divideView);
        layoutContentView(contentView);
        layoutCenterImg(centerImg);
        layoutLeftBtn(leftBtn, centerBtnPosition.left);
        layoutRightBtn(rightBtn, centerBtnPosition.right);
    }


    private void layoutTabBgView(View view, int height) {

//        int width = getWidth();     //mactch parent

        MarginLayoutParams mLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        int left, top, right, bottom;
        left = mLayoutParams.leftMargin
        ;
        top = getHeight() - height - mLayoutParams.bottomMargin;
        right = mLayoutParams.leftMargin + view.getMeasuredWidth();
        bottom = getHeight() - mLayoutParams.bottomMargin;

        view.layout(left, top, right, bottom);

        tabBgViewPosition.setPosition(left - mLayoutParams.leftMargin, top - mLayoutParams.topMargin, right + mLayoutParams.rightMargin, bottom + mLayoutParams.bottomMargin);
    }

    private void layoutTabDividView(View view) {

        MarginLayoutParams mLayoutParams = (MarginLayoutParams) view.getLayoutParams();

        int left, top, right, bottom;
        left = mLayoutParams.leftMargin;
        top = tabBgViewPosition.top - view.getMeasuredHeight() - mLayoutParams.bottomMargin;
        right = mLayoutParams.leftMargin + view.getMeasuredWidth();
        bottom = tabBgViewPosition.top - mLayoutParams.bottomMargin;

        view.layout(left, top, right, bottom);

        divideViewPosition.setPosition(left - mLayoutParams.leftMargin, top - mLayoutParams.topMargin, right + mLayoutParams.rightMargin, bottom + mLayoutParams.bottomMargin);

    }

    private void layoutContentView(View view) {

        MarginLayoutParams mLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        int left, top, right, bottom;

        left = mLayoutParams.leftMargin;
        top = mLayoutParams.topMargin;
        right = getWidth() - mLayoutParams.rightMargin;
        bottom = divideViewPosition.top - mLayoutParams.bottomMargin;

        int height = view.getMeasuredHeight();

        view.layout(left, top, right, bottom);
    }

    private void layoutCenterImg(View view) {

        MarginLayoutParams mLayoutParams = (MarginLayoutParams) view.getLayoutParams();

        int left, top, right, bottom;
        left = (getWidth() - view.getMeasuredWidth()) / 2;
        top = getMeasuredHeight() - view.getMeasuredHeight() - mLayoutParams.bottomMargin;
        right = left + view.getMeasuredWidth();
        bottom = getHeight() - mLayoutParams.bottomMargin;

        view.layout(left, top, right, bottom);

        centerBtnPosition.setPosition(left - mLayoutParams.leftMargin, top - mLayoutParams.topMargin, right + mLayoutParams.rightMargin, bottom + mLayoutParams.bottomMargin);

    }

    /**
     * @param view
     * @param _right half of the center button height.
     */
    private void layoutLeftBtn(View view, int _right) {

        MarginLayoutParams mLayoutParams = (MarginLayoutParams) view.getLayoutParams();

        int left, top, right, bottom;
        left = mLayoutParams.leftMargin;
        top = getHeight() - view.getMeasuredHeight() - mLayoutParams.bottomMargin;
        right = _right - mLayoutParams.rightMargin;
        bottom = getHeight() - mLayoutParams.bottomMargin;

        view.layout(left, top, right, bottom);

        leftBtnPosition.setPosition(left - mLayoutParams.leftMargin, top - mLayoutParams.topMargin, right + mLayoutParams.rightMargin, bottom + mLayoutParams.bottomMargin);
    }


    private void layoutRightBtn(View view, int _left) {

        MarginLayoutParams mLayoutParams = (MarginLayoutParams) view.getLayoutParams();

        int left, top, right, bottom;
        left = _left + mLayoutParams.leftMargin;
        top = getHeight() - view.getMeasuredHeight() - mLayoutParams.bottomMargin;
        right = getWidth() - mLayoutParams.rightMargin;
        bottom = getHeight() - mLayoutParams.bottomMargin;

        view.layout(left, top, right, bottom);
        rightBtnPosition.setPosition(left - mLayoutParams.leftMargin, top - mLayoutParams.topMargin, right + mLayoutParams.rightMargin, bottom + mLayoutParams.bottomMargin);

    }

    public void setLeftBtnChecked() {
        leftBtn.setChecked(true);
    }

    public void setRightBtnChecked() {
        rightBtn.setChecked(true);
    }

}
