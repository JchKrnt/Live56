package com.sohu.live56.view.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.sohu.live56.R;
import com.sohu.live56.util.DisplayUtil;

/**
 * Created by jingbiaowang on 2015/8/17.
 */
public abstract class LiveAlertDialog extends Dialog {


    public LiveAlertDialog(Context context) {
        super(context);
    }


    public LiveAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected abstract View getCustomeContainerView();

    public static class Builder {

        private LiveAlertDialog alertDialog;
        private Context context;

        public Builder() {

        }

        public LiveAlertDialog create(LiveAlertDialog dialog) {
            this.alertDialog = dialog;
            this.context = dialog.getContext();
//            FrameLayout layout = new FrameLayout(context);
//            layout.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog_bg, context.getTheme()));

            View contentView = dialog.getCustomeContainerView();

            dialog.setContentView(contentView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().setBackgroundDrawable(contentView.getResources().getDrawable(R.drawable.alert_dialog_bg));

            //resize dialog size.width is 80 percent of the screen width.
            int winWidth = (int) ((double) setDialogSize(dialog.getWindow().getWindowManager()).x
                    * 0.9);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);

            return alertDialog;
        }

        private Point setDialogSize(WindowManager windowManager) {
            Point point = new Point();
            Display display = windowManager.getDefaultDisplay();
            DisplayUtil.getSize(display, point);

            return point;
        }
    }


}
