package com.ligh.whiteboardpainting.cache;

import android.content.Context;

import com.ligh.whiteboardpainting.app.PaperlessApplication;


/**
 * Created by ghost on 2016/11/15.
 */


/**
 * 应用程序数据缓存
 */
public class IsDownCache extends BaseDataCache {

    private static IsDownCache sDataCache;
    private final static String CACHE_NAME = "IsDownCache";


    public static synchronized IsDownCache getInstance() {
        if (sDataCache == null) {
            Context context = PaperlessApplication.getInstance();
            if (context == null) {
                throw new IllegalArgumentException("context is null!");
            }
            sDataCache = new IsDownCache(context, CACHE_NAME);
        }
        return sDataCache;
    }

    /**
     * @param appContext
     */
    public IsDownCache(Context appContext) {
        super(appContext);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param appContext
     * @param cacheName
     */
    public IsDownCache(Context appContext, String cacheName) {
        super(appContext, cacheName);
    }


    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void setPreferences(String tinydbname) {
        super.setPreferences(tinydbname);
    }

    @Override
    public void putString(String key, String value) {
        super.putString(key, value);
    }

    @Override
    public String getString(String key) {
        return super.getString(key);
    }
}