package com.sohu.live56.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by jingbiaowang on 2015/8/12.
 */
public class DisplayUtil {

    /**
     * 按比例放大和缩小bitmap.
     *
     * @param bitmap
     * @param scale
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, float scale) {

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

}
