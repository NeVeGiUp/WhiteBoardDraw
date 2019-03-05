package com.ligh.whiteboardpainting.widget;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.util.Log;

import com.ligh.whiteboardpainting.graph.LineCtl;
import com.ligh.whiteboardpainting.graph.OvalCtl;
import com.ligh.whiteboardpainting.graph.PenCtl;
import com.ligh.whiteboardpainting.graph.RectCtl;
import com.ligh.whiteboardpainting.graph.TextCtl;
import com.ligh.whiteboardpainting.listener.ISketchpadDraw;
import com.ligh.whiteboardpainting.model.StyleObjAttr;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 设置交互白板还是个人白板
 */

public class WhiteBoardStack {


    public WhiteBoardView sketchPad;                          //视图对象
    private List<Integer> eraserId = new ArrayList<>();         //整理传递来的所有需要删除的对象id
    List<StyleObjAttr.SavePointModel> generatePoint;


    public WhiteBoardStack(WhiteBoardView sketchPad) {
        this.sketchPad = sketchPad;
    }


    /**
     * 对点生成的前提是手指画出的系统所记录的点的机制——按照曲线幅度大小生成系统点的多少...
     * 然后用系统生成的相邻两点，生成中心点，递归，直到出现相同点递归完毕
     *
     * @param startx
     * @param starty
     * @param endx
     * @param endy
     */
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
        if (delObjectIndex.size() > 0)
            reDraw();
        return delObjectIndex;
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


    /**
     * page为-1则是清屏白板
     *
     * @param page
     * @param isSend
     */
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


    private void reDraw() {
        sketchPad.createStrokeBitmap();
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
        sketchPad.createStrokeBitmap();
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