package com.ligh.whiteboardpainting.app;

import android.app.Application;
import android.content.Context;


/**
 * TODO<应用程序基类>
 */
public class PaperlessApplication extends Application {
    private static Context mContext;
    private static PaperlessApplication sApplication;


    /**
     * 获取Application实例
     *
     * @return x
     */
    public static PaperlessApplication getInstance() {
        if (sApplication == null) {
            throw new IllegalStateException("Application is not created.");
        }
        return sApplication;
    }

    /**
     * 获取上下文
     */
    public static Context getContext() {
        return mContext;
    }





}