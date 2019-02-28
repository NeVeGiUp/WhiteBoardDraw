package com.ligh.whiteboardpainting.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;

import com.ligh.whiteboardpainting.listener.ASketchpadDraw;
import com.ligh.whiteboardpainting.model.StyleObjAttr;


/**
 * 矩形绘制
 */
public class RectCtl extends ASketchpadDraw {
	private Context context;
	private Paint mPaint = new Paint();
	private boolean hasDrawn = false;
	private boolean isFill = false;
	private float startx = 0;
	private float starty = 0;
	private RectF rect = new RectF();
	private StyleObjAttr rectAttr = new StyleObjAttr();

	public RectCtl(Context context, int penSize, int penColor, boolean isFill) {
		this.context = context;
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(penColor);
		if (isFill)
			mPaint.setStyle(Paint.Style.FILL);
		else
			mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 0));//偏移值
		mPaint.setStrokeWidth(penSize);//设置画笔粗细
		this.isFill = isFill;


		rectAttr.setPaintColor(penColor);
		rectAttr.setPaintSize(penSize);
	}
	public RectCtl(StyleObjAttr attr, Canvas canvas){
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(attr.getPaintColor());
		if (attr.isFill())
			mPaint.setStyle(Paint.Style.FILL);
		else
			mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 0));//偏移值
		mPaint.setStrokeWidth(attr.getPaintSize());//设置画笔粗细
		rect.left = attr.getStartX();
		rect.top = attr.getStartY();
		rect.right = attr.getEndX();
		rect.bottom = attr.getEndY();
		draw(canvas);
	}
	public void draw(Canvas canvas) {
		if (null != canvas) {
			canvas.drawRect(rect, mPaint);
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
        if((rect.left != 0 || rect.top != 0) && ((int)rect.left != (int)rect.right && (int)rect.top != (int)rect.bottom)){
			hasDrawn = true;//表示已经操作了
			rectAttr.setIsFill(isFill);
			rectAttr.setStartPoint(rect.left, rect.top);
			rectAttr.setEndPoint(rect.right, rect.bottom);
			rectAttr.setStyleTag("r");
		/*	int stackId = AppDataCache.getInstance().getInt("stackId");
			int userID = stackId + 1;
			AppDataCache.getInstance().putInt("stackId", userID);
			rectAttr.setObjId(userID);
			if(context instanceof BaiBanCheckImageActivity)
				rectAttr.setFilePage(((BaiBanCheckImageActivity)context).number);*/
			attrStack.add(rectAttr);
		}
	}
}