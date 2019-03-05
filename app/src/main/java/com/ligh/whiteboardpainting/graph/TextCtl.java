package com.ligh.whiteboardpainting.graph;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.ligh.whiteboardpainting.listener.ASketchpadDraw;
import com.ligh.whiteboardpainting.utils.AppUtil;
import com.ligh.whiteboardpainting.R;
import com.ligh.whiteboardpainting.widget.WhiteBoardView;
import com.ligh.whiteboardpainting.model.StyleObjAttr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本绘制
 */
public class TextCtl extends ASketchpadDraw {
    private boolean isSelf;
    private Context context;
    private FrameLayout parentView;
    private WhiteBoardView view;
    private StyleObjAttr textAttr;
    private Paint mPaint = new Paint();
    private int height;
    private int penColor;

    public TextCtl(Context context, WhiteBoardView view, int penColor) {
        this.context = context;
        this.parentView = (FrameLayout) view.getParent();
        this.view = view;
        this.penColor = penColor;
        this.isSelf = view.isSelf;
    }

    public TextCtl(StyleObjAttr strContent, Canvas canvas) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(strContent.getPaintSize());
        textPaint.setAntiAlias(true);
        textPaint.setDither(true);
        textPaint.setColor(strContent.getPaintColor());
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeJoin(Paint.Join.ROUND);
        textPaint.setStrokeCap(Paint.Cap.ROUND);
        textPaint.setStrokeWidth(2);
        StaticLayout layout = new StaticLayout(strContent.getEditText(), textPaint, canvas.getWidth() - strContent.getStartX(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
        canvas.save();
        canvas.translate(strContent.getStartX(), strContent.getStartY());
        layout.draw(canvas);
        canvas.restore();
    }

    @Override
    public boolean hasDraw() {
        return false;
    }

    @Override
    public void touchDown(float x, float y) {
        WhiteBoardView.requestFocus(parentView);
    }

    private EditText editText;

    @Override
    public void touchUp(float x, float y) {
        newEditView((int) x, (int) y);
    }

    public void draw(Canvas canvas) {
        if (null != canvas) {
            view.canvas.drawText(textAttr.getEditText(), textAttr.getStartX(), textAttr.getStartY() + height / 2, mPaint);
        }
    }

    private void newEditView(int x, int y) {
        textAttr = new StyleObjAttr();
        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = x;
        params.topMargin = y;
        params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
        params.width = FrameLayout.LayoutParams.WRAP_CONTENT;
        editText = new EditText(context);
        editText.setHint("请输入...");
        editText.setTextColor(WhiteBoardView.getStrokeColor());
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.setTextColor(penColor);
        editText.setBackgroundColor(ContextCompat.getColor(context, R.color.common_transparent));
        AppUtil.showSoftInput(context);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {//失去焦点
                    String text = editText.getText().toString();
                    if (text.trim().length() > 0) {
                        float textSize = editText.getTextSize();
                        height = editText.getHeight();
                        TextPaint textPaint = new TextPaint();//用drawText代替主要是为了终端方便擦除,不然view对象很难擦除
                        textPaint.setTextSize(textSize);
                        textPaint.setAntiAlias(true);
                        textPaint.setDither(true);
                        textPaint.setColor(penColor);
                        textPaint.setStyle(Paint.Style.FILL);
                        textPaint.setStrokeJoin(Paint.Join.ROUND);
                        textPaint.setStrokeCap(Paint.Cap.ROUND);
                        textPaint.setStrokeWidth(2);
                        StaticLayout layout = new StaticLayout(text, textPaint, view.canvas.getWidth() - editText.getLeft(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                        view.canvas.save();
                        view.canvas.translate(editText.getLeft(), editText.getTop());
                        layout.draw(view.canvas);
                        view.canvas.restore();
                        textAttr.setRectPoint(editText.getLeft(), editText.getTop(), editText.getRight(), editText.getBottom());
                        textAttr.setPaintColor(penColor);
                        textAttr.setPaintSize((int) textSize);
                      /*  if (context instanceof BaiBanCheckImageActivity)
                            textAttr.setFilePage(((BaiBanCheckImageActivity) context).number);*/
                        textAttr.setEditText(text);
                        textAttr.setStyleTag("t");
                       /* int stackId = AppDataCache.getInstance().getInt("stackId");
                        int userID = stackId + 1;
                        AppDataCache.getInstance().putInt("stackId", userID);
                        textAttr.setObjId(userID);*/
                        attrStack.add(textAttr);
                        if (isSelf)
                            sendData();
                    }
                    parentView.removeView(v);
                }
            }
        });
        editText.setFilters(new InputFilter[]{emojiFilter});
        parentView.addView(editText, params);
    }

    private InputFilter emojiFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                Toast.makeText(context, "不支持输入表情", Toast.LENGTH_SHORT).show();
                return "";
            }
            return null;
        }
    };

    private void sendData() {
     /*   byte[] textBytes = ByteUtil.getStringToBytes(context, textAttr);
        Log.e("pds", textBytes.length + "eraser:" + Arrays.toString(textBytes));
        if (context instanceof BaiBanCheckImageActivity)
            NetSession.getInstance().SendCommentData(textBytes);
        else
            NetSession.getInstance().SendWbData(textBytes);
*/
    }

}