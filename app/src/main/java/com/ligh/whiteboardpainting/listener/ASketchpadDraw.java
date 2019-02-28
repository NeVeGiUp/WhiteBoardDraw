package com.ligh.whiteboardpainting.listener;

import android.graphics.Canvas;


/**
 * 白板触摸回调抽象类
 */
public abstract class ASketchpadDraw implements ISketchpadDraw{
    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public boolean hasDraw() {
        return false;
    }


    @Override
    public void touchDown(float x, float y) {

    }

    @Override
    public void touchMove(float x, float y) {

    }


    @Override
    public void touchUp(float x, float y) {

    }
}