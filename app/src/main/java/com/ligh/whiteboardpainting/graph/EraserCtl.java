package com.ligh.whiteboardpainting.graph;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.ligh.whiteboardpainting.listener.ASketchpadDraw;
import com.ligh.whiteboardpainting.widget.WhiteBoardView;
import com.ligh.whiteboardpainting.model.StyleObjAttr;

import java.util.List;

/**
 * 橡皮擦绘制
 */
public class EraserCtl extends ASketchpadDraw {
	private final static int ERASER_PAINT_SIZE = 3;
	private Context context;
	private WhiteBoardView view;
	private  boolean isSelf;
	private Paint mPaint = new Paint();
	private float startx = 0;
	private float starty = 0;

	private StyleObjAttr eraserAttr = new StyleObjAttr();
	private RectF rect = new RectF();

	public EraserCtl(Context context, WhiteBoardView view, boolean isSelf){
		this.context = context;
		this.view = view;
		this.isSelf = isSelf;
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setPathEffect(new DashPathEffect(new float[]{6,5},0));//设置虚线间隔
		mPaint.setStrokeWidth(ERASER_PAINT_SIZE);//设置画笔粗细
	}

	public void draw(Canvas canvas) {
		if (null != canvas){
			canvas.drawRect(rect, mPaint);
		}
	}

	public boolean hasDraw() {
		return false;
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
		eraserAttr.setStartPoint(startx, starty);
		eraserAttr.setEndPoint(x, y);
		List<Integer> eraser = view.objStack.eraser((int) Math.min(startx, x), (int) Math.min(starty, y), (int) Math.max(startx, x), (int) Math.max(starty, y));
		Log.e("pds", "eraser.size:::"+eraser.size());
//		if(context instanceof BaiBanCheckImageActivity)
//			eraserAttr.setFilePage(((BaiBanCheckImageActivity)context).number);
		eraserAttr.setStyleTag("e");
		if(isSelf && eraser.size() > 0)
			sendData(eraser);
	}

	private void sendData(List<Integer> eraser) {
		eraserAttr.setDelNumber(eraser.size());
			Log.e("pds", "eraser.size()::::"+eraser.size());
		for(int i=0;i<eraser.size();i++){
			eraserAttr.setObjId(eraser.get(i));
			/*byte[] eraserBytes = ByteUtil.getStringToBytes(context, eraserAttr);
			Log.e("pds", "eraserID::::"+eraser.get(i));
			Log.e("pds", "send::::"+ Arrays.toString(eraserBytes));
			if(context instanceof BaiBanCheckImageActivity)
				NetSession.getInstance().SendCommentData(eraserBytes);
			else
				NetSession.getInstance().SendWbData(eraserBytes);*/
		}

	}
}