package com.ligh.whiteboardpainting.graph;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ligh.whiteboardpainting.listener.ASketchpadDraw;
import com.ligh.whiteboardpainting.model.StyleObjAttr;


/**
 * 椭圆绘制
 */
public class OvalCtl extends ASketchpadDraw {
	private Context context;
	private Paint mPaint=new Paint();
	private boolean hasDrawn = false;
	private boolean isFill = false;
	private RectF rect = new RectF();
	private float startx, starty;
	private StyleObjAttr ovalAttr = new StyleObjAttr();
	public OvalCtl(Context context, int penSize, int penColor, boolean isFill){
		this.context = context;
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(penColor);
		if(isFill)
			mPaint.setStyle(Paint.Style.FILL);
		else
			mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(penSize);//设置画笔粗细
		this.isFill = isFill;

		ovalAttr.setPaintColor(penColor);
		ovalAttr.setPaintSize(penSize);
	}
	public OvalCtl(StyleObjAttr attr, Canvas canvas){
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(attr.getPaintColor());
		if(attr.isFill())
			mPaint.setStyle(Paint.Style.FILL);
		else
			mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(attr.getPaintSize());//设置画笔粗细
		rect.left = attr.getStartX();
		rect.top = attr.getStartY();
		rect.right = attr.getEndX();
		rect.bottom = attr.getEndY();
		draw(canvas);
	}

	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas){
			if(rect.left == 0 || rect.top == 0){//防止双击过快，原点从0 0开始绘制
				rect.left = rect.right;
				rect.top = rect.bottom;
			}
			canvas.drawOval(rect, mPaint);
		}
	}
	public boolean hasDraw() {
		return hasDrawn;
	}

	public void touchDown(float x, float y) {
		startx = x;
		starty = y;
	}
	public void touchMove(float x, float y) {
		rect.left = Math.min(startx, x);
		rect.top = Math.min(starty, y);
		rect.right = Math.max(startx, x);
		rect.bottom = Math.max(starty, y);
	}
	public void touchUp(float x, float y) {
		rect.right = Math.max(startx, x);
		rect.bottom = Math.max(starty, y);
		if((rect.left != 0 || rect.top != 0) && ((int)rect.left != (int)rect.right && (int)rect.top != (int)rect.bottom)){
            hasDrawn = true;//表示已经操作了
            ovalAttr.setStartPoint(rect.left, rect.top);
            ovalAttr.setEndPoint(rect.right, rect.bottom);
            ovalAttr.setIsFill(isFill);
            ovalAttr.setStyleTag("o");
           /* int stackId = AppDataCache.getInstance().getInt("stackId");
            int userID = stackId + 1;
            AppDataCache.getInstance().putInt("stackId", userID);
            ovalAttr.setObjId(userID);
            if(context instanceof BaiBanCheckImageActivity)
                ovalAttr.setFilePage(((BaiBanCheckImageActivity)context).number);*/
            attrStack.add(ovalAttr);
		}
	}
}