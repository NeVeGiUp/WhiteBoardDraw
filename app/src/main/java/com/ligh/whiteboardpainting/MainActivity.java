package com.ligh.whiteboardpainting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ligh.whiteboardpainting.adapter.StyleSelectBaseAdapter;
import com.ligh.whiteboardpainting.utils.AppUtil;
import com.ligh.whiteboardpainting.utils.ConfigUtil;
import com.ligh.whiteboardpainting.utils.ScreenUtil;
import com.ligh.whiteboardpainting.widget.SketchpadView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private SketchpadView sketchpadView;
    private LinearLayout controllerLl;
    private ImageView moveIv;
    private ImageView penIv;
    private ImageView eraserIv;
    private TextView otherToolsTv;
    private TextView penSizeTv;
    private TextView penColorTv;
    private Button saveBtn;
    private Button lookBtn;
    private Button shutdownBtn;
    private FrameLayout contentFl;

    private final static int OTHER_TOOLS_SELECT_INDEX = 0;
    private final static int PEN_SIZE_SELECT_INDEX = 1;
    private final static int PEN_COLOR_SELECT_INDEX = 2;

    private MediaProjectionManager projectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //横屏
        initView();
        sketchpadView.initialize(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        }
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        controllerLl = findViewById(R.id.ll_style_controller);
        sketchpadView = findViewById(R.id.sketchpad_view);
        moveIv = findViewById(R.id.iv_style_move);
        penIv = findViewById(R.id.iv_style_pen);
        eraserIv = findViewById(R.id.iv_style_eraser);
        otherToolsTv = findViewById(R.id.tv_style_other_tools);
        penSizeTv = findViewById(R.id.tv_style_pen_size);
        penColorTv = findViewById(R.id.tv_style_pen_color);
        saveBtn = findViewById(R.id.btn_style_save);
        lookBtn = findViewById(R.id.btn_style_look);
        shutdownBtn = findViewById(R.id.btn_style_shut_down);
        contentFl = findViewById(R.id.fl_baiban_all_view);
        AppUtil.setTextViewBitmap(otherToolsTv, ContextCompat.getDrawable(context, R.mipmap.pen_icon_tool_int_0_nor));
        AppUtil.setTextViewBitmap(penSizeTv, ContextCompat.getDrawable(context, R.mipmap.pen_icon_pen_int_1_nor));
        AppUtil.setTextViewBitmap(penColorTv, ContextCompat.getDrawable(context, R.mipmap.pen_icon_cool_int_2_nor));

        penIv.setOnClickListener(this);
        eraserIv.setOnClickListener(this);
        otherToolsTv.setOnClickListener(this);
        penSizeTv.setOnClickListener(this);
        penColorTv.setOnClickListener(this);
        saveBtn.setOnClickListener(this);
        lookBtn.setOnClickListener(this);
        shutdownBtn.setOnClickListener(this);
        moveIv.setOnTouchListener(new StyleMoveListener(getIntent().getBooleanExtra("pizhuIn", false)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            controllerLl.setElevation(9);  //总体阴影设置
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        sketchpadView.recycleBitmap();
        sketchpadView.objStack.clearAll(false);
        sketchpadView = null;
    }


    private boolean isPenClickChangeColor = false;
    private boolean isEraserClickChangeColor = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_style_pen:  //画笔
                if (isPenClickChangeColor) {
                    penIv.setImageResource(R.mipmap.pen_icon_pen_hig);
                    eraserIv.setImageResource(R.mipmap.pen_ocon_eraser_nor);
                    otherToolsTv.setTextColor(penSizeTv.getTextColors());
                    isPenClickChangeColor = false;
                    isEraserClickChangeColor = true;
                }
                sketchpadView.setStrokeType(SketchpadView.STYLE_PEN);
                break;
            case R.id.iv_style_eraser:  //橡皮擦
                if (isEraserClickChangeColor) {
                    eraserIv.setImageResource(R.mipmap.pen_ocon_eraser_hig);
                    penIv.setImageResource(R.mipmap.pen_icon_pen_nor);
                    otherToolsTv.setTextColor(penSizeTv.getTextColors());
                    isEraserClickChangeColor = false;
                    isPenClickChangeColor = true;
                }
                sketchpadView.setStrokeType(SketchpadView.STYLE_ERASER);
                break;
            case R.id.tv_style_other_tools:  //其它工具
                showPupopWindow(otherToolsTv, R.layout.style_pupopwindow_grid, OTHER_TOOLS_SELECT_INDEX, ConfigUtil.styleSelect);
                break;
            case R.id.tv_style_pen_size:  //画笔大小

                showPupopWindow(penSizeTv, R.layout.style_pupopwindow_grid, PEN_SIZE_SELECT_INDEX, ConfigUtil.sizeSelct);
                break;
            case R.id.tv_style_pen_color:  //画笔颜色
                showPupopWindow(penColorTv, R.layout.style_pupopwindow_grid, PEN_COLOR_SELECT_INDEX, ConfigUtil.colorSelct);
                break;
            case R.id.btn_style_save:  //保存按钮
                break;
            case R.id.btn_style_look:  //清屏
                sketchpadView.objStack.clearPageDraw(-1, true);
                break;
            case R.id.btn_style_shut_down:  //关闭
                exitActivity();
                break;
        }
        SketchpadView.requestFocus(controllerLl);
    }


    /**
     * 显示可选样式
     *
     * @param textView    文本view
     * @param layoutResID 布局id
     */
    private void showPupopWindow(final TextView textView, int layoutResID, int select, int[] data) {
        View contentView = LayoutInflater.from(context).inflate(layoutResID, null);
        GridView grid_style_pup = (GridView) contentView.findViewById(R.id.list_style_pup);
        grid_style_pup.setAdapter(new StyleSelectBaseAdapter(context, data));
        int dimensionWidth;
        int dimensionHeight;
        if (select == OTHER_TOOLS_SELECT_INDEX) {
            dimensionWidth = (int) getResources().getDimension(R.dimen.style_select_grid);
            dimensionHeight = (int) getResources().getDimension(R.dimen.style_select_grid_samll) * 2;//俩排，高度放大成2倍
        } else {
            dimensionWidth = (int) getResources().getDimension(R.dimen.style_select_grid);
            dimensionHeight = (int) getResources().getDimension(R.dimen.style_select_grid_samll) * 3;//三排，高度放大成3倍
        }
        final PopupWindow popupWindow = new PopupWindow(contentView, dimensionWidth, dimensionHeight,
                true);
        popupWindow.setFocusable(true);//获取焦点
        popupWindow.setOutsideTouchable(true);//设置点击PopupWindow以外的地方关闭PopupWindow
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, R.mipmap.style_popupwindow_bg));
        grid_style_pup.setTag(popupWindow);
        grid_style_pup.setOnItemClickListener(new StyleSelectItemClickListener(textView, popupWindow, data, select));
        popupWindow.showAsDropDown(textView, 0, 5);//y轴偏移量
        popupWindow.update();
    }


    private class StyleSelectItemClickListener implements AdapterView.OnItemClickListener {
        private TextView textView;
        private PopupWindow popupWindow;
        private int[] data;
        private int select;

        StyleSelectItemClickListener(TextView textView, PopupWindow popupWindow, int[] data, int select) {
            this.textView = textView;
            this.popupWindow = popupWindow;
            this.data = data;
            this.select = select;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Drawable bottomDrawable = ContextCompat.getDrawable(context, data[position]);
            bottomDrawable.setBounds(0, 0, 26, 26);  //第一是距左边距离，第二是距上边距离，后俩个分别是长宽
            textView.setCompoundDrawables(null, null, null, bottomDrawable);
//            UiUtil.setTextViewDrawable(context,textView,data[position]);
            popupWindow.dismiss();

            switch (select) {
                case OTHER_TOOLS_SELECT_INDEX://其它工具选择的结果
                    switch (position) {
                        case 0://文字编辑
                            sketchpadView.setStrokeType(SketchpadView.STYLE_TEXT);
                            break;
                        case 1://直线
                            sketchpadView.setStrokeType(SketchpadView.STYLE_LINE);
                            break;
                        case 2://矩形
                            sketchpadView.setStrokeType(SketchpadView.STYLE_RECT);
                            break;
                        case 3://实心矩形
                            sketchpadView.setStrokeType(SketchpadView.STYLE_FILL_RECT);
                            break;
                        case 4://椭圆
                            sketchpadView.setStrokeType(SketchpadView.STYLE_OVAL);
                            break;
                        case 5://实心椭圆
                            sketchpadView.setStrokeType(SketchpadView.STYLE_FILL_OVAL);
                            break;
                    }
                    isPenClickChangeColor = true;
                    isEraserClickChangeColor = true;
                    penIv.setImageResource(R.mipmap.pen_icon_pen_nor);
                    eraserIv.setImageResource(R.mipmap.pen_ocon_eraser_nor);
                    otherToolsTv.setTextColor(ContextCompat.getColor(context, R.color.style_all_pressed_color));
                    break;
                case PEN_SIZE_SELECT_INDEX://画笔大小的选择
                    sketchpadView.setPenSize(ConfigUtil.sizeSelctValue[position]);//根据position取Map里的相应数据
                    break;
                case PEN_COLOR_SELECT_INDEX://画笔颜色的选择
                    sketchpadView.setStrokeColor(ConfigUtil.colorSelctValue[position]);
                    break;
            }

        }
    }


    private GestureDetector mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("pds", "返回了true");
            return true;
        }
    });

    /**
     * 移动样式板
     */
    private class StyleMoveListener implements View.OnTouchListener {
        private int startX;
        private int startY;
        private int statusBarHeight = 0;

        private StyleMoveListener(boolean pizhuIn) {
            if (pizhuIn)
                statusBarHeight = AppUtil.getStatusBarHeight(context);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mGestureDetector.onTouchEvent(event)) {
                return true;
            }
//            Log.e("pds", "执行?");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    moveIv.setImageResource(R.mipmap.pen_icon_move_hig);
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int) (event.getRawX() - startX);
                    int y = (int) (event.getRawY() - startY);
                    int left = controllerLl.getLeft();
                    int top = controllerLl.getTop();
                    int screenWidth = ScreenUtil.getScreenWidth(context);
                    int screenHeight = ScreenUtil.getScreenHeight(context);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) controllerLl
                            .getLayoutParams();
                    int moveX = x + left;
                    int moveY = y + top;
                    if (moveX < 0) {
                        moveX = 1;
                    } else if (moveX > screenWidth - controllerLl.getWidth()) {
                        moveX = screenWidth - controllerLl.getWidth();
                    }
                    if (moveY < 0) {
                        moveY = 1;
                    } else if (moveY > screenHeight - controllerLl.getHeight() - statusBarHeight) {
                        moveY = screenHeight - controllerLl.getHeight() - statusBarHeight;
                    }
                    params.leftMargin = moveX;
                    params.topMargin = moveY;
                    controllerLl.setLayoutParams(params);
                    startX = (int) event.getRawX();
                    startY = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    moveIv.setImageResource(R.mipmap.pen_icon_move_nor);
                    break;
            }
            return true;
        }
    }

    private void exitActivity() {
        sketchpadView.recycleBitmap();  //释放资源
        sketchpadView.objStack.clearAll(false);
        this.finish();
        System.gc();
    }


}
