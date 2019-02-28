package com.ligh.whiteboardpainting.graph;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.ligh.whiteboardpainting.listener.ASketchpadDraw;
import com.ligh.whiteboardpainting.model.StyleObjAttr;

import java.util.ArrayList;
import java.util.List;

/**
 * 自由画笔绘制
 */
public class PenCtl extends ASketchpadDraw {
    private Context context;
    private Path path = new Path();
    private Paint mPaint = new Paint();
    private boolean hasDrawn = false;
    private float mX = 0;
    private float mY = 0;
    private List<StyleObjAttr.SavePointModel> penPoint = new ArrayList<>();//系统点
    private StyleObjAttr penAttr;
    private int penSize;
    private int penColor;

    public PenCtl(Context context, int penSize, int penColor) {
        this.context = context;
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penSize);
        this.penSize = penSize;
        this.penColor = penColor;
    }

    public PenCtl(StyleObjAttr penAttr, Canvas canvas) {
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penAttr.getPaintColor());
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penAttr.getPaintSize());
        List<StyleObjAttr.SavePointModel> penPoint = penAttr.getPenPoint();

        int size = penPoint.size();
        Log.e("pds", "收到的线，点的数量：" + size);
        if (size > 2) {
            path.moveTo(penPoint.get(0).x, penPoint.get(0).y);
            for (int i = 1; i < penPoint.size() - 1; i++) {
                StyleObjAttr.SavePointModel start = penPoint.get(i);
                StyleObjAttr.SavePointModel end = penPoint.get(i + 1);
                path.quadTo(start.x, start.y, (end.x + start.x) / 2, (end.y + start.y) / 2);
            }
            path.setLastPoint(penPoint.get(size - 1).x, penPoint.get(size - 1).y);//-1是纠正一下点的像素位置
            canvas.drawPath(path, mPaint);
        } else if (size == 2) {
            canvas.drawLine(penPoint.get(0).x, penPoint.get(0).y, penPoint.get(1).x, penPoint.get(1).y, mPaint);
        }
    }

    public void draw(Canvas canvas) {
        if (null != canvas) {
            canvas.drawPath(path, mPaint);
        }
    }

    /**
     * 获取手指在绑定布局上的滑动速度。
     *
     * @return 滑动速度，以100毫秒移动了多少像素值为单位。
     */
    public boolean hasDraw() {
        return hasDrawn;
    }

    public void touchDown(float x, float y) {//按快了down不初始化，对象在这里初始化会发生add数据覆盖现象
        if (moveInit == 0) {
            penAttr = new StyleObjAttr();
            penAttr.setPaintColor(penColor);
            penAttr.setPaintSize(penSize);
            moveInit++;
        }
        path.moveTo(x, y);
        mX = x;
        mY = y;
        penPoint.add(new StyleObjAttr.SavePointModel((int) x, (int) y));
    }

    private int moveInit = 0;//主要用于在move中初始化对象

    @Override
    public void touchMove(float x, float y) {
        if (moveInit == 0) {
            penAttr = new StyleObjAttr();
            penAttr.setPaintColor(penColor);
            penAttr.setPaintSize(penSize);
            moveInit++;
        }
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= 4 || dy >= 4) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
        penPoint.add(new StyleObjAttr.SavePointModel((int) x + 1, (int) y + 1));
    }

    @Override
    public void touchUp(float x, float y) {
        if (moveInit == 0) {
            penAttr = new StyleObjAttr();
            penAttr.setPaintColor(penColor);
            penAttr.setPaintSize(penSize);
            moveInit++;
        }
        penPoint.add(new StyleObjAttr.SavePointModel((int) x, (int) y));
        hasDrawn = true;
        mX = x;
        mY = y;
        path.lineTo(x, y);
        penAttr.setPenPoint(penPoint);
     /*   int stackId = AppDataCache.getInstance().getInt("stackId");
        int userID = stackId + 1;
        AppDataCache.getInstance().putInt("stackId", userID);
        Log.e("pds", stackId + "userID:"+userID);
        penAttr.setObjId(userID);//计算出不重复id*/
        penAttr.setStyleTag("p");
//        if (context instanceof BaiBanCheckImageActivity) {
//            penAttr.setFilePage(((BaiBanCheckImageActivity) context).number);
//        }
        attrStack.add(penAttr);
        moveInit = 0;
    }
}