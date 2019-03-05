package com.ligh.whiteboardpainting.model;

/**
 * @ author : lgh_ai
 * @ e-mail : lgh_developer@163.com
 * @ date   : 19-3-1 下午5:29
 * @ desc   :
 */
public class MoveEvent {
    private int eventX;
    private int eventY;

    public MoveEvent(int eventX, int eventY) {
        this.eventX = eventX;
        this.eventY = eventY;
    }

    public int getEventX() {
        return eventX;
    }

    public void setEventX(int eventX) {
        this.eventX = eventX;
    }

    public int getEventY() {
        return eventY;
    }

    public void setEventY(int eventY) {
        this.eventY = eventY;
    }
}
