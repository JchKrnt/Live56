package com.sohu.live56.view.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sohu.live56.R;

/**
 * Created by jingbiaowang on 2015/8/17.
 */
public class ShareDialog extends LiveAlertDialog implements View.OnClickListener {

    private TextView sharedialogtilte;
    private TextView weixintv;
    private TextView friendstv;
    private TextView microblogtv;
    private TextView qqfriendstv;
    private TextView qqzonetv;
    private TextView copylinktv;
    private ImageButton sharecancelbtn;

    public ShareDialog(Context context, int theme) {
        super(context, theme);
    }

    public ShareDialog(Context context) {
        this(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
    }


    @Override
    protected View getCustomeContainerView() {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_share_layout, null);
        initialize(view);

        return view;
    }

    private void initialize(View view) {

        sharedialogtilte = (TextView) view.findViewById(R.id.share_dialog_tilte);
        weixintv = (TextView) view.findViewById(R.id.weixin_tv);
        friendstv = (TextView) view.findViewById(R.id.friends_tv);
        qqfriendstv = (TextView) view.findViewById(R.id.qq_friends_tv);
        microblogtv = (TextView) view.findViewById(R.id.microblog_tv);
        qqzonetv = (TextView) view.findViewById(R.id.qq_zone_tv);
        copylinktv = (TextView) view.findViewById(R.id.copy_link_tv);
        sharecancelbtn = (ImageButton) view.findViewById(R.id.share_cancel_btn);

        weixintv.setOnClickListener(this);
        friendstv.setOnClickListener(this);
        qqfriendstv.setOnClickListener(this);
        microblogtv.setOnClickListener(this);
        qqzonetv.setOnClickListener(this);
        copylinktv.setOnClickListener(this);
        sharecancelbtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.weixin_tv: {

                break;
            }
            case R.id.friends_tv: {

                break;
            }
            case R.id.qq_friends_tv: {

                break;
            }
            case R.id.qq_zone_tv: {

                break;
            }
            case R.id.copy_link_tv: {

                break;
            }
            case R.id.share_cancel_btn: {

                this.dismiss();
                break;
            }


        }
    }
}
