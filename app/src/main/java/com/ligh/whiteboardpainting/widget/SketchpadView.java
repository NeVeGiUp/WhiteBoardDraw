package com.ligh.whiteboardpainting.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.ligh.whiteboardpainting.graph.EraserCtl;
import com.ligh.whiteboardpainting.graph.LineCtl;
import com.ligh.whiteboardpainting.graph.OvalCtl;
import com.ligh.whiteboardpainting.graph.PenCtl;
import com.ligh.whiteboardpainting.graph.RectCtl;
import com.ligh.whiteboardpainting.graph.TextCtl;
import com.ligh.whiteboardpainting.listener.ISketchpadDraw;
import com.ligh.whiteboardpainting.model.StyleObjAttr;
import com.ligh.whiteboardpainting.utils.ConfigUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 自定义画板，实现图形的绘制与显示
 */
public class SketchpadView extends View {

    //设置画笔常量
    public static final int STYLE_PEN = 1;                  //画笔
    public static final int STYLE_ERASER = 2;               //橡皮擦
    public static final int STYLE_TEXT = 3;                 //文字编辑
    public static final int STYLE_FILL_RECT = 4;            //实心矩形
    public static final int STYLE_RECT = 5;                 //矩形
    public static final int STYLE_OVAL = 6;                 //椭圆
    public static final int STYLE_FILL_OVAL = 7;            //实心椭圆
    public static final int STYLE_LINE = 8;                 //直线
    private static int BITMAP_WIDTH = 0;                    //画布高
    private static int BITMAP_HEIGHT = 0;                   //画布宽
    private static int strokeColor = Color.RED;             //画笔颜色
    private static int penSize = ConfigUtil.sizeSelctValue[0];         //画笔大小

    private int strokeType = STYLE_PEN;                      //画笔风格
    public boolean isSelf = false;                           //true 为交互白板, false为个人白板
    //实例新画布
    private boolean isEnableDraw = true;                     //标记是否可以画
    private boolean isDraw = true;                           //终端不可画
    private boolean isTouchUp = false;                       //标记是否手指弹起

    private Bitmap foreBitmap = null;                        //用于显示的bitmap
    private Bitmap bkBitmap = null;                          //用于背后画的bitmap

    public Canvas canvas;                                    //画布
    private Paint bitmapPaint = null;                        //画笔
    public SketchPadObjStack objStack = null;                //栈存放执行的操作
    private ISketchpadDraw curTool = null;                   //记录操作的对象画笔类
    private float vX;
    private float vY;

    //构造方法
    public SketchpadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDrawStrokeEnable(boolean isEnable) {
        isDraw = isEnable;  //确定是否可绘图
    }


    public void setPenSize(int size) {//设置画笔大小
        penSize = size;
    }

    //设置画笔颜色
    public void setStrokeColor(int color) {
        strokeColor = color;
    }

    //得到画笔的大小
    public int getStrokeSize() {
        return penSize;
    }


    //得到画笔的大小
    public static int getStrokeColor() {
        return strokeColor;
    }

    /**
     * 释放资源
     */
    public void recycleBitmap() {
        if (canvas != null) {
            canvas = null;
        }
        Log.e("pds", "已回收?11");
        if (null != bkBitmap) {
            bkBitmap = null;
            Log.e("pds", "已回收?");
        }
        if (null != foreBitmap) {
            foreBitmap.recycle();
            foreBitmap = null;
        }
        setStrokeColor(Color.RED);
        setPenSize(ConfigUtil.sizeSelctValue[0]);
    }

    /**
     * 打开图像文件时，设置为当前视图
     *
     * @param bmp bmp
     */
    public void setBkBitmap(Bitmap bmp) {  //设置背景bitmap
        if (bkBitmap != bmp) {
            bkBitmap = bmp;
            createStrokeBitmap();
        }
    }

    //强制获取焦点,主要用于从文字编辑处强制获取焦点
    public static void requestFocus(View view) {
        if (view != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
        }
    }

    public void initialize(int width, int height) {
//        int stackId = AppDataCache.getInstance().getInt("userID") * 1000;
//        AppDataCache.getInstance().putInt("stackId", stackId);
        BITMAP_WIDTH = width;
        BITMAP_HEIGHT = height;
//        if (!(getContext() instanceof SureSignActivity)) {
//            centerX = BITMAP_WIDTH / 2;
//            centerY = BITMAP_HEIGHT / 2;
//        }
        canvas = new Canvas();//实例画布用于整个绘图操作
        bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  //实例化画笔用于bitmap设置画布canvas
        objStack = new SketchPadObjStack(this);//实例化队列
        createStrokeBitmap();
        setStrokeType(strokeType);
    }

    public void createStrokeBitmap() {
        foreBitmap = combineBitmap();//合并图片
        canvas.setBitmap(foreBitmap);//绘制的基础
    }

    /**
     * 合并两张bitmap为一张
     *
     * @return Bitmap
     */
    private Bitmap combineBitmap() {
        if (bkBitmap == null) {
            return Bitmap
                    .createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
        }
        int bgWidth = bkBitmap.getWidth();
        int bgHeight = bkBitmap.getHeight();
        Bitmap newmap = Bitmap
                .createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bkBitmap, BITMAP_WIDTH / 2 - bgWidth / 2, BITMAP_HEIGHT / 2 - bgHeight / 2, null);
//        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        canvas.restore();
        return newmap;
    }

    //启动设置画笔的颜色和大小    调用修改
    public void setStrokeType(int type) {
        strokeColor = getStrokeColor();
        penSize = getStrokeSize();
        switch (type) {
            case STYLE_PEN://笔，任意
                curTool = new PenCtl(getContext(), penSize, strokeColor);
                break;
            case STYLE_RECT://矩形
                curTool = new RectCtl(getContext(), penSize, strokeColor, false);
                break;
            case STYLE_FILL_RECT://实心矩形矩形
                curTool = new RectCtl(getContext(), penSize, strokeColor, true);
                break;
            case STYLE_OVAL://椭圆形
                curTool = new OvalCtl(getContext(), penSize, strokeColor, false);
                break;
            case STYLE_FILL_OVAL://实心椭圆形
                curTool = new OvalCtl(getContext(), penSize, strokeColor, true);
                break;
            case STYLE_LINE://直线
                curTool = new LineCtl(getContext(), penSize, strokeColor);
                break;
            case STYLE_ERASER://橡皮擦
                curTool = new EraserCtl(getContext(), this, isSelf);
                break;
            case STYLE_TEXT://文字
                curTool = new TextCtl(getContext(), this, strokeColor);
                break;
        }
        //用于记录操作动作名称
        strokeType = type;
    }


    private Matrix matrix = new Matrix();
    public float scale = 1;
    public float centerX = 0, centerY = 0;
    private PointF mLastMovePoint = new PointF();       //记录上一次滚动的位置
    private PointF mLastPoint = new PointF();           //记录上一次滚动的位置
    private PointF mScaleCenter = new PointF();
    private float bitmapPointX;                         //图片原点相对于屏幕的坐标
    private float bitmapPointY;

    private float sendX;
    private float sendY;


    private MotionEvent mPreviousUpEvent;

    public boolean onTouchEvent(MotionEvent event) {
        //防止Exception dispatching input event.错误
        try {
            //判断是否总体可绘图
            if (isDraw) {
                isTouchUp = false;
                float x = event.getX();
                float y = event.getY();
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        if (mPreviousUpEvent != null
                                && isConsideredDoubleTap(mPreviousUpEvent, event)) {
                        }

                        mLastPoint.set(x, y);
                        //根据strokeType进行重新生成对象且记录下操作对象
                        setStrokeType(strokeType);
                        if (scale == 1) {
                            curTool.touchDown(x, y);
                        } else {
                            curTool.touchDown(x / scale - bitmapPointX / scale, y / scale - bitmapPointY / scale);
                        }
                        vX = BITMAP_WIDTH * scale + bitmapPointX;
                        vY = BITMAP_HEIGHT * scale + bitmapPointY;
                        isEnableDraw = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //单指在屏幕上拖动则为绘制状态
                        if (event.getPointerCount() == 1) {
                            if (vX < x || vY < y || bitmapPointX > x || bitmapPointY > y) {
                                return true;
                            }
                            if (isEnableDraw) {
                                if (scale == 1) {
                                    curTool.touchMove(x, y);
                                } else {
                                    curTool.touchMove(x / scale - bitmapPointX / scale, y / scale - bitmapPointY / scale);
                                }
                            }
                            //双指在屏幕上拖动则为非绘制状态
                        } else if (event.getPointerCount() == 2) {
                            isEnableDraw = false;
                            /*if (getContext() instanceof BaiBanCheckImageActivity) {
                                //两个缩放点间的距离
                                float distance = PinchImageView.MathUtils.getDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                                //保存缩放点中点
                                float[] lineCenter = PinchImageView.MathUtils.getCenterPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                                mLastMovePoint.set(lineCenter[0], lineCenter[1]);
                                //防止初次点击即被无效
                                if(scale > 1) {
                                    //左移动
                                    if (bitmapPointX < 0) {
                                        //手指中点超出画布,则无效
                                        if (vX < mLastMovePoint.x) {
                                            return false;
                                        }
                                        //右移动
                                    } else {
                                        vX = bitmapPointX;
                                        //手指中点超出画布,则无效
                                        if (vX > mLastMovePoint.x) {
                                            return false;
                                        }
                                    }
                                }
                                scale(mScaleCenter, mScaleBase, distance, mLastMovePoint);
                            }*/
                            //2根以上手指也是非绘制状态
                        } else {
                            isEnableDraw = false;
                        }
                        //move实时绘制
                        invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        mPreviousUpEvent = MotionEvent.obtain(event);
                        //主要为了屏蔽第二根手指按到屏幕时，俩根手指的距离绘制的直线
                        if (isEnableDraw) {
                            if (scale == 1) {
                                curTool.touchUp(x, y);
                            } else {
                                curTool.touchUp(x / scale - bitmapPointX / scale, y / scale - bitmapPointY / scale);
                            }
                            if (curTool.hasDraw()) {
                                if (isSelf)
                                    //交互每一次push到数据里操作，把数据穿过服务器再到终端，根据栈里的数据reDraw——文字编辑  橡皮擦需要独立send
                                    sendData();
                            }
                            if (STYLE_ERASER != strokeType && STYLE_TEXT != strokeType)
                                curTool.draw(canvas);
                        }
                        isTouchUp = true;
                        isEnableDraw = true;
                        invalidate();
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN://一个非主要的手指按下了
                        if (event.getPointerCount() == 2) {
                            isEnableDraw = false;
                            // 当有两个手指按在屏幕上时，计算两指之间的距离
                            saveScaleContext(event.getX(0), event.getY(0), event.getX(1), event.getY(1));//----
                            if (scale == 1) {
                                curTool.touchUp(x, y);
                            } else {
                                curTool.touchUp(x / scale - bitmapPointX / scale, y / scale - bitmapPointY / scale);
                            }
                            if (curTool.hasDraw()) {
                                if (isSelf)
                                    //交互每一次push到数据里操作，把数据穿过服务器再到终端，根据栈里的数据reDraw——文字编辑  橡皮擦需要独立send
                                    sendData();
                            }
                            if (STYLE_ERASER != strokeType && STYLE_TEXT != strokeType)
                                curTool.draw(canvas);
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP://一个非主要的手指抬起来了
                        break;
                }
            }
        } catch (Error e) {
            Log.e("pds", "error:::" + e.toString());
        }
        return true;
    }

    private float mScaleBase;

    /**
     * 记录缩放前的一些信息
     * <p>
     * 保存基础缩放值.
     * 保存图片缩放中点.
     *
     * @param x1 缩放第一个手指
     * @param y1 缩放第一个手指
     * @param x2 缩放第二个手指
     * @param y2 缩放第二个手指
     */
    private void saveScaleContext(float x1, float y1, float x2, float y2) {
        //记录基础缩放值,其中图片缩放比例按照x方向来计算
        //理论上图片应该是等比的,x和y方向比例相同
        //但是有可能外部设定了不规范的值.
        //但是后续的scale操作会将xy不等的缩放值纠正,改成和x方向相同
        mScaleBase = PinchImageView.MathUtils.getMatrixScale(matrix)[0] / PinchImageView.MathUtils.getDistance(x1, y1, x2, y2);
        //两手指的中点在屏幕上落在了图片的某个点上,图片上的这个点在经过总矩阵变换后和手指中点相同
        //现在我们需要得到图片上这个点在图片是fit center状态下在屏幕上的位置
        //因为后续的计算都是基于图片是fit center状态下进行变换
        //所以需要把两手指中点除以外层变换矩阵得到mScaleCenter
        float[] center = PinchImageView.MathUtils.inverseMatrixPoint(PinchImageView.MathUtils.getCenterPoint(x1, y1, x2, y2), matrix);
        mScaleCenter.set(center[0], center[1]);
    }

    private boolean isConsideredDoubleTap(MotionEvent firstUp, MotionEvent secondDown) {
        if (secondDown.getEventTime() - firstUp.getEventTime() > 200) {
            return false;
        }
        int deltaX = (int) firstUp.getX() - (int) secondDown.getX();
        int deltaY = (int) firstUp.getY() - (int) secondDown.getY();
        Log.e("pds", "误触值：" + (deltaX * deltaX + deltaY * deltaY));
        return deltaX * deltaX + deltaY * deltaY < 500;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (matrix != null) {
            canvas.setMatrix(matrix);
        }
        if (null != foreBitmap) {
            canvas.drawBitmap(foreBitmap, 0, 0, bitmapPaint);//绘制的基础
        }
        if (null != curTool) {
            if (STYLE_TEXT != strokeType) {
                if (!isTouchUp) {   //调用绘图功能
                    curTool.draw(canvas);
                }
            }
        }
    }

    public void sendData() {
        StyleObjAttr styleObjAttr = ISketchpadDraw.attrStack.get(ISketchpadDraw.attrStack.size() - 1);
       /* byte[] stringToBytes = ByteUtil.getStringToBytes(getContext(), styleObjAttr);
        Log.e("pds", stringToBytes.length + "stringToBytes:" + Arrays.toString(stringToBytes));
        if (getContext() instanceof BaiBanActivity) {
            Log.e("pds", "发送电子白板");
            NetSession.getInstance().SendWbData(stringToBytes);
        } else {
            Log.e("pds", "发送批注白板");
            NetSession.getInstance().SendCommentData(stringToBytes);
        }*/
    }

    /**
     * 设置交互白板还是个人白板
     */

    public class SketchPadObjStack {
        private SketchpadView sketchPad = null;  //视图对象

        private SketchPadObjStack(SketchpadView sketchPad) {
            this.sketchPad = sketchPad;
        }

        /**
         * 清空栈
         *
         * @param isReDraw 是否重绘
         */
        public void clearAll(boolean isReDraw) {
            int size = ISketchpadDraw.attrStack.size();
//            boolean isOpenWay = getContext() instanceof BaiBanCheckImageActivity;
            for (int i = size - 1; i >= 0; i--) {
                //true  文档页的绘制信息清除
               /* if(isOpenWay) {
                    if(ISketchpadDraw.attrStack.get(i).getFilePage() != -1)
                        ISketchpadDraw.attrStack.remove(i);
                    //白板页的绘制信息清除
                }else{*/
                if (ISketchpadDraw.attrStack.get(i).getFilePage() == -1)
                    ISketchpadDraw.attrStack.remove(i);
//                }
            }
            if (isReDraw)
                reDraw();
        }

        List<StyleObjAttr.SavePointModel> generatePoint;

        //对点生成的前提是手指画出的系统所记录的点的机制——按照曲线幅度大小生成系统点的多少...然后用系统生成的相邻两点，生成中心点，递归，直到出现相同点递归完毕
        private void getGeneratePoint(int startx, int starty, int endx, int endy) {
            int length = (int) Math.sqrt(Math.pow((startx - endx), 2) + Math.pow((starty - endy), 2)) / 10;//等分点成对，再取长度的1/5个等分点
            int centerx = (startx + endx) / 2;
            int centery = (starty + endy) / 2;
            if (startx != centerx && starty != centery && length != 0) {
                getGeneratePoint(startx, starty, centerx, centery);
                getGeneratePoint(centerx, centery, endx, endy);
            } else {
                //结束这一段曲线的递归
                return;
            }
            generatePoint.add(new StyleObjAttr.SavePointModel(centerx, centery));
        }

        /**
         * 橡皮擦操作
         */
        public List<Integer> eraser(int leftX, int leftY, int rightX, int rightY) {//根据down和up操作生成一个矩形
            int page = -1;
           /* if (getContext() instanceof BaiBanCheckImageActivity) {//强制获取文档在擦除时的页数
                page = ((BaiBanCheckImageActivity) getContext()).number;
            }*/
            List<Integer> delObjectIndex = new ArrayList<>();
            Region eraserRegion = new Region(leftX, leftY, rightX, rightY);
            int size = ISketchpadDraw.attrStack.size();
            Log.e("pds", "attrStack:" + size);
            for (int i = size - 1; i >= 0; i--) {  //循环读取画布内生成的对象，分别与矩形比对
                StyleObjAttr styleObjAttr = ISketchpadDraw.attrStack.get(i);
                int paintSize = styleObjAttr.getPaintSize();
                int startx = styleObjAttr.getStartX() - paintSize / 2;//减去画笔大小 增加判断精准度
                int starty = styleObjAttr.getStartY() - paintSize / 2;
                int endx = styleObjAttr.getEndX() - paintSize / 2;
                int endy = styleObjAttr.getEndY() - paintSize / 2;
                Region objectRegion = new Region(startx, starty, endx, endy);
                String styleTag = styleObjAttr.getStyleTag();
                switch (styleTag) {
                    case "t":
                    case "r":
                        if (!eraserRegion.quickReject(startx, starty, endx, endy)) {
                            if (styleObjAttr.isFill() || !objectRegion.quickContains(leftX, leftY, rightX, rightY)) {//是否实心  ||   是否 没包含
                                if (styleObjAttr.getFilePage() == page) {//用于区分文档page页面
                                    ISketchpadDraw.attrStack.remove(i);
                                    delObjectIndex.add(styleObjAttr.getObjId());
                                }
                            }
                        }
                        break;
                    case "o":
                        RectF f = new RectF(startx, starty, endx, endy);
                        Path path = new Path();
                        path.addOval(f, Path.Direction.CCW);//逆时针方向  CW 顺时针
                        Region ovalRegion = new Region();
                        ovalRegion.setPath(path, objectRegion);
                        RegionIterator iterator = new RegionIterator(ovalRegion);
                        Rect rect = new Rect();
                        if (objectRegion.quickContains(leftX, leftY, rightX, rightY)) {//椭圆包含橡皮擦，
                            if (styleObjAttr.isFill()) {//且为实心状态下，直接消除
                                if (styleObjAttr.getFilePage() == page) {//用于区分文档page页面
                                    ISketchpadDraw.attrStack.remove(i);
                                    delObjectIndex.add(styleObjAttr.getObjId());
                                }
                            }
                        } else {
                            while (iterator.next(rect)) {
                                if (!eraserRegion.quickReject(rect)) {
                                    if (styleObjAttr.getFilePage() == page) {//用于区分文档page页面
                                        ISketchpadDraw.attrStack.remove(i);
                                        delObjectIndex.add(styleObjAttr.getObjId());
                                    }
                                    break;
                                }
                            }
                        }
                        break;
                    case "l":
                        generatePoint = new LinkedList<>();
                        int startX = styleObjAttr.getStartX();
                        int startY = styleObjAttr.getStartY();
                        int endX = styleObjAttr.getEndX();
                        int endY = styleObjAttr.getEndY();
                        generatePoint.add(new StyleObjAttr.SavePointModel(startX, startY));
                        generatePoint.add(new StyleObjAttr.SavePointModel(endX, endY));
                        getGeneratePoint(startX, startY, endX, endY);
                        int pointSize = generatePoint.size();
                        for (int p = 0; p < pointSize; p++) {
                            if (eraserRegion.contains(generatePoint.get(p).x, generatePoint.get(p).y)) {//是否包含线中的点
                                if (styleObjAttr.getFilePage() == page) {//用于区分文档page页面
                                    delObjectIndex.add(styleObjAttr.getObjId());
                                    ISketchpadDraw.attrStack.remove(i);
                                }
                                break;
                            }
                        }
                        break;
                    case "p":
                        generatePoint = new LinkedList<>();
                        List<StyleObjAttr.SavePointModel> penPoint = styleObjAttr.getPenPoint();
                        int penPointSize = penPoint.size() - 1;
                        Log.e("pds", "penPointSize:" + penPointSize);
                        generatePoint.addAll(penPoint);
                        int penPointGenerateSize = generatePoint.size();
                        Log.e("pds", "penPointGenerateSize:" + penPointGenerateSize);
                        for (int p = 0; p < penPointGenerateSize; p++) {
                            if (eraserRegion.contains(generatePoint.get(p).x, generatePoint.get(p).y)) {//是否包含线中的点
                                if (styleObjAttr.getFilePage() == page) {//用于区分文档page页面
                                    ISketchpadDraw.attrStack.remove(i);
                                    delObjectIndex.add(styleObjAttr.getObjId());
                                }
                                break;
                            }
                        }
                        break;
                }
            }
          /*  if (getContext() instanceof BaiBanCheckImageActivity)
                ((BaiBanCheckImageActivity) getContext()).changeShowImage();
            else {*/
            if (delObjectIndex.size() > 0)
                reDraw();
//            }
            return delObjectIndex;
        }

        //page为-1则是清屏白板
        public void clearPageDraw(int page, boolean isSend) {
            List<Integer> delId = new ArrayList<>();
            int size = ISketchpadDraw.attrStack.size();
            Log.e("pds", "数据大小：:" + size);
            for (int i = size - 1; i >= 0; i--) {
                if (page == ISketchpadDraw.attrStack.get(i).getFilePage()) {
                    delId.add(ISketchpadDraw.attrStack.get(i).getObjId());
                    ISketchpadDraw.attrStack.remove(i);
                }
            }
            reDraw(page, false, true);
            if (isSend) {
                StyleObjAttr attr = new StyleObjAttr();
                attr.setStyleTag("e");
                attr.setFilePage(page);
                int delSize = delId.size();
                Log.e("pds", delSize + "----" + delId.toString());
                attr.setDelNumber(delSize);
                for (int i = 0; i < delSize; i++) {
                    attr.setObjId(delId.get(i));
                    /*byte[] stringToBytes = ByteUtil.getStringToBytes(getContext(), attr);
                    if (getContext() instanceof BaiBanCheckImageActivity)
                        NetSession.getInstance().SendCommentData(stringToBytes);
                    else
                        NetSession.getInstance().SendWbData(stringToBytes);*/
                }
            }
        }

        public void clearDraw() {
            /*if (getContext() instanceof BaiBanCheckImageActivity){
                int size = ISketchpadDraw.attrStack.size();

                StyleObjAttr attr = new StyleObjAttr();
                attr.setStyleTag("e");
                attr.setFilePage(-1);
                attr.setDelNumber(size);
                for (int i = size - 1; i >= 0; i--) {
                    attr.setObjId(ISketchpadDraw.attrStack.get(i).getObjId());
                    byte[] stringToBytes = ByteUtil.getStringToBytes(getContext(), attr);
                    NetSession.getInstance().SendCommentData(stringToBytes);
                }
            }*/
        }

        private void reDraw() {
            createStrokeBitmap();
            int size = ISketchpadDraw.attrStack.size();
            for (int i = 0; i < size; i++) {
                reTurnDraw(ISketchpadDraw.attrStack.get(i), false);
            }
            sketchPad.postInvalidate();
        }

        /**
         * 根据page页重绘对象
         *
         * @param page           page
         * @param isAdd          isAdd 是否加入到栈中
         * @param postInvalidate 异步刷新还是同步刷新，主要为了解决批注切换时异步刷新导致的闪屏
         */
        public void reDraw(int page, boolean isAdd, boolean postInvalidate) {
            createStrokeBitmap();
            int size = ISketchpadDraw.attrStack.size();
            Log.e("pds", "pageReDraw:" + ISketchpadDraw.attrStack.size());
            for (int i = 0; i < size; i++) {
                if (page == ISketchpadDraw.attrStack.get(i).getFilePage()) {
                    reTurnDraw(ISketchpadDraw.attrStack.get(i), isAdd);
                }
            }
            if (postInvalidate)
                sketchPad.postInvalidate();
            else
                sketchPad.invalidate();
        }

        //整理传递来的所有需要删除的对象id
        private List<Integer> eraserId = new ArrayList<>();

        /**
         * 橡皮擦擦除交互操作，则重绘交互白板
         *
         * @param attr  attr
         * @param isAdd 是否把交互操作加入栈中
         */
        public void reTurnDraw(StyleObjAttr attr, boolean isAdd) {
            String styleTag = attr.getStyleTag();
            if (isAdd && !styleTag.equals("e")) {
                ISketchpadDraw.attrStack.add(attr);
                Log.e("pds", "size::" + ISketchpadDraw.attrStack.size());
            }
           /* if (getContext() instanceof BaiBanCheckImageActivity) {//主要用于对主讲在本地绘制之后开启主讲，发送过来的数据区别一下页数
                if (((BaiBanCheckImageActivity) getContext()).number != attr.getFilePage()) {
                    return;
                }
            }*/
            switch (styleTag) {
                case "p":
                    new PenCtl(attr, sketchPad.canvas);
                    break;
                case "l":
                    new LineCtl(attr, sketchPad.canvas);
                    break;
                case "o":
                    new OvalCtl(attr, sketchPad.canvas);
                    break;
                case "r":
                    new RectCtl(attr, sketchPad.canvas);
                    break;
                case "t":
                    new TextCtl(attr, sketchPad.canvas);
                    break;
                case "e":
                    eraserId.add(attr.getObjId());//
                    if (attr.getDelNumber() == eraserId.size()) {//终端接到主讲的应要删除的数量 ==  传递过来的删除总数量时，  擦除数据
                        for (int i = 0; i < eraserId.size(); i++) {
                            for (int j = 0; j < ISketchpadDraw.attrStack.size(); j++) {
                                if (ISketchpadDraw.attrStack.get(j).getObjId() == eraserId.get(i)) {
                                    ISketchpadDraw.attrStack.remove(j);
                                }
                            }
                        }
                        reDraw(attr.getFilePage(), false, true);
                        eraserId.clear();
                    }
                    break;
            }
        }
    }

}
