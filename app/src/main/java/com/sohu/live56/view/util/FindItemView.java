package com.sohu.live56.view.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jch.utillib.view.CircleImageView;
import com.jch.utillib.view.ImageUtil;
import com.sohu.live56.R;
import com.sohu.live56.util.DisplayUtil;

/**
 * Created by jingbiaowang on 2015/8/12.
 */
public class FindItemView extends ViewGroup {

    private TextView titleTv;
    private TextView stateTv;
    private LinearLayout stateLl;
    private Bitmap bgBitmap;
    private CircleImageView headImg;
    private FrameLayout btmRl;
    private TextView nameTv;
    private TextView addrTv;
    private TextView numTv;
    private HomeTabHost.Position btmPosition = new HomeTabHost.Position();

    public FindItemView(Context context) {
        super(context);
    }

    public FindItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        titleTv = (TextView) findViewById(R.id.square_item_view_title);
        stateTv = (TextView) findViewById(R.id.square_item_view_state_tv);
        stateLl = (LinearLayout) findViewById(R.id.square_item_view_state);
        bgBitmap = ((BitmapDrawable) getBackground()).getBitmap();
        headImg = (CircleImageView) findViewById(R.id.square_item_view_head);
        btmRl = (FrameLayout) findViewById(R.id.square_item_view_btm);
        nameTv = (TextView) findViewById(R.id.square_item_name);
        addrTv = (TextView) findViewById(R.id.square_item_addr);
        numTv = (TextView) findViewById(R.id.square_item_num);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获得此ViewGroup上级容器为其推荐的宽和高，及计算模式
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);

        //计算所有childview的宽和高。
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        //计算出本view的宽和高。宽(match_parent)为父容器计算的值，高(wrap_content)为childeView 的高和bg img 高的最大值。

        int height = 0;
        //用于计算子容器的高
        int cHeight = 0;
        MarginLayoutParams stateLayoutParams = (MarginLayoutParams) stateLl.getLayoutParams();
        cHeight = stateLayoutParams.topMargin + stateLl.getMeasuredHeight() + stateLayoutParams.bottomMargin + headImg.getMeasuredHeight() / 2 + btmRl.getMeasuredHeight();
        float scaled = (float) sizeWidth / (float) bgBitmap.getWidth();
        bgBitmap = DisplayUtil.scaleBitmap(bgBitmap, scaled);

//            setBackground(new BitmapDrawable(getResources(), resizeBmp));         //？
        height = Math.max(cHeight, bgBitmap.getHeight());

        setMeasuredDimension(sizeWidth, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        layoutTitle(titleTv);
        layoutState(stateLl);
        layoutBtm(btmRl);
        layoutHeadImg(headImg);
    }

    private void layoutTitle(View view) {

        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
        int left, top, right, bottom;

        left = layoutParams.leftMargin;
        top = layoutParams.topMargin;
        right = view.getMeasuredWidth() + left;
        bottom = view.getMeasuredHeight() + top;

        view.layout(left, top, right, bottom);
    }

    private void layoutState(View view) {

        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
        int left, top, right, bottom;
        top = layoutParams.topMargin;
        right = getMeasuredWidth() - layoutParams.rightMargin;
        bottom = top + view.getMeasuredHeight();
        left = right - view.getMeasuredWidth();

        view.layout(left, top, right, bottom);
    }

    private void layoutBtm(View view) {

        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
        int left, top, right, bottom;

        left = 0;
        right = getMeasuredWidth();
        bottom = getMeasuredHeight();
        top = bottom - view.getMeasuredHeight();

        view.layout(left, top, right, bottom);

        btmPosition.setPosition(left - layoutParams.leftMargin, top - layoutParams.topMargin, right + layoutParams.rightMargin, bottom + layoutParams.bottomMargin);
        blurBtmBg();
    }

    private void layoutHeadImg(View view) {

        MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
        int left, top, right, bottom;
        left = layoutParams.leftMargin;
        right = left + view.getMeasuredWidth();
        int halfHeight = view.getMeasuredHeight() / 2;
        bottom = btmRl.getTop() + halfHeight;
        top = btmRl.getTop() - halfHeight;

        view.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void blurBtmBg() {

        Bitmap tempBtm = Bitmap.createBitmap(bgBitmap, 0, bgBitmap.getHeight() - btmRl.getMeasuredHeight(), btmRl.getMeasuredWidth(), btmRl.getHeight());

        Bitmap blureBmp = ImageUtil.fastblur(getContext(), tempBtm, 30);

        btmRl.setBackground(new BitmapDrawable(getResources(), blureBmp));
    }

    public void setHeadImg(Bitmap bitmap) {
        headImg.setImageBitmap(bitmap);
    }

    public void setName(String name) {
        nameTv.setText(name);
    }

    public void setAddr(String addr) {
        addrTv.setText(addr);
    }

    public void setCustomerNum(String customerNum) {
        numTv.setText(customerNum);
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public void setItemBgBmp(Bitmap bmp) {
        if (bgBitmap != null && !bgBitmap.isRecycled())
            bgBitmap.recycle();
        bgBitmap = bmp;
        invalidate();
    }

    public TextView getTitleTv() {
        return titleTv;
    }

    public TextView getStateTv() {
        return stateTv;
    }

    public Bitmap getBgBitmap() {
        return bgBitmap;
    }

    public CircleImageView getHeadImg() {
        return headImg;
    }

    public TextView getNameTv() {
        return nameTv;
    }

    public TextView getAddrTv() {
        return addrTv;
    }

    public TextView getNumTv() {
        return numTv;
    }
}
