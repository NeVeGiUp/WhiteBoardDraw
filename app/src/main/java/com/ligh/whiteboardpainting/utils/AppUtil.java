package com.ligh.whiteboardpainting.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

/**
 * @ author : lgh_ai
 * @ e-mail : lgh_developer@163.com
 * @ date   : 19-2-20 下午4:34
 * @ desc   :
 */
public class AppUtil {

    /**
     * 打开键盘.
     *
     * @param context context
     */
    public static void showSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置样式选择器中textView的初始化图片
     *
     * @param textView
     * @param bottomDrawable
     */
    public static void setTextViewBitmap(TextView textView, Drawable bottomDrawable) {
        bottomDrawable.setBounds(0, 0, 26, 26);//第一是距左边距离，第二是距上边距离，后俩个分别是长宽
        textView.setCompoundDrawables(null, null, null, bottomDrawable);
    }

    public static int getStatusBarHeight(Context context) {
        return (int) Math.ceil(25 * context.getResources().getDisplayMetrics().density);
    }
}
