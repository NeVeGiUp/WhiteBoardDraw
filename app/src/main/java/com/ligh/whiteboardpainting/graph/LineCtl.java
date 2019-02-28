package com.ligh.whiteboardpainting.graph;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.ligh.whiteboardpainting.listener.ASketchpadDraw;
import com.ligh.whiteboardpainting.model.StyleObjAttr;


/**
 * 直线绘制
 */
public class LineCtl extends ASketchpadDraw {
	private Context context;
	private Paint mPaint=new Paint();
	private boolean hasDrawn = false;
	private float startx = 0;
	private float starty = 0;
	private float endx = 0;
	private float endy = 0;
	private StyleObjAttr lineAttr = new StyleObjAttr();

	public LineCtl(Context context, int penSize, int penColor){
		this.context = context;
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(penColor);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(penSize);//设置画笔粗细

		lineAttr.setPaintColor(penColor);
		lineAttr.setPaintSize(penSize);
	}
	public LineCtl(StyleObjAttr attr, Canvas canvas){
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(attr.getPaintColor());
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(attr.getPaintSize());//设置画笔粗细
		if (null != canvas){
			canvas.drawLine(attr.getStartX(),attr.getStartY(),attr.getEndX(),attr.getEndY(), mPaint);
		}
	}
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas){
			canvas.drawLine(startx,starty,endx,endy, mPaint);
		}
	}
	public boolean hasDraw() {
		// TODO Auto-generated method stub
		return hasDrawn;
	}


	public void touchDown(float x, float y) {
		// TODO Auto-generated method stub
		startx=x;
		starty=y;
		endx=x;
		endy=y;
		lineAttr.setStartX(x);
		lineAttr.setStartY(y);
	}
	public void touchMove(float x, float y) {
		// TODO Auto-generated method stub
		endx=x;
		endy=y;
	}
	public void touchUp(float x, float y) {
		// TODO Auto-generated method stub
		endx=x;
		endy=y;
//		Log.e("pds", "startx - endx:"+(startx - endx));
		if(startx - endx != 0) {//点击的无距离则不发送数据，也不绘制
			hasDrawn = true;//表示已经操作了
			lineAttr.setEndX(x);
			lineAttr.setEndY(y);

			lineAttr.setStyleTag("l");
		/*	int stackId = AppDataCache.getInstance().getInt("stackId");
			int userID = stackId + 1;
			AppDataCache.getInstance().putInt("stackId", userID);
			lineAttr.setObjId(userID);
			if (context instanceof BaiBanCheckImageActivity)
				lineAttr.setFilePage(((BaiBanCheckImageActivity) context).number);*/
			attrStack.add(lineAttr);
		}
	}
}