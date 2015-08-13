package com.sohu.live56.view.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by jingbiaowang on 2015/8/13.
 */
public class MarginListView extends ListView {


    public MarginListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
//        return new MarginLayoutParams(getContext(), attrs);
        return super.generateLayoutParams(attrs);
    }
}
