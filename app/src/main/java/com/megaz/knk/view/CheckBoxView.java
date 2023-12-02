package com.megaz.knk.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import androidx.annotation.Nullable;


import com.megaz.knk.R;

public class CheckBoxView extends View implements Checkable {

    //宽度
    private int viewWidth = 0;
    //高度
    private int viewHeight = 0;

    private Paint mBackgroundPaintTrue = null;
    private Paint mBackgroundPaintFalse = null;
    private Paint mSlidePaint = null;

    private int offsetY = 0;
    private int offsetX = 0;

    private int radius = 0;

    //是否可以滑动
    private boolean isMove = false;

    private RectF mLeftRectF = null;
    private RectF mRightRectF = null;

    //默认为true
    private boolean mChecked = true;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    public CheckBoxView(Context context) {
        this(context, null);
    }

    public CheckBoxView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckBoxView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化画笔
        mBackgroundPaintTrue = new Paint();
        mBackgroundPaintTrue.setAntiAlias(true);
        mBackgroundPaintTrue.setColor(context.getColor(R.color.light_blue));
        mBackgroundPaintTrue.setStyle(Paint.Style.FILL);

        mBackgroundPaintFalse = new Paint();
        mBackgroundPaintFalse.setAntiAlias(true);
        mBackgroundPaintFalse.setColor(context.getColor(R.color.gray));
        mBackgroundPaintFalse.setStyle(Paint.Style.FILL);

        mSlidePaint = new Paint();
        mSlidePaint.setAntiAlias(true);
        mSlidePaint.setColor(context.getColor(R.color.gray_white));
        mSlidePaint.setStyle(Paint.Style.FILL);

        //初始化属性
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CheckBoxView);
            int trueColor = array.getColor(R.styleable.CheckBoxView_trueColor, context.getColor(R.color.light_blue));
            int falseColor = array.getColor(R.styleable.CheckBoxView_falseColor, context.getColor(R.color.gray));
            int slideColor = array.getColor(R.styleable.CheckBoxView_slideColor, context.getColor(R.color.gray_white));
            mChecked = array.getBoolean(R.styleable.CheckBoxView_checked, true);
            array.recycle();

            mBackgroundPaintTrue.setColor(trueColor);
            mBackgroundPaintFalse.setColor(falseColor);
            mSlidePaint.setColor(slideColor);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //测量控件的大小
        viewWidth = myMeasure(dip2px(60), widthMeasureSpec);
        viewHeight = viewWidth / 2;

        //设置半径、偏移量
        radius = offsetY = viewHeight / 2;
        if (mChecked) {
            offsetX = radius;
        } else {
            offsetX = viewWidth - radius;
        }

        setMeasuredDimension(viewWidth, viewHeight);
    }

    private int dip2px(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    private int myMeasure(int defaultSize, int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int mSize = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {
                mSize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {
                mSize = defaultSize;
                break;
            }
            case MeasureSpec.EXACTLY: {
                mSize = size;
                break;
            }
        }
        return mSize;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mLeftRectF == null) {
            mLeftRectF = new RectF(0, 0, viewHeight, viewHeight);
        }
        if (mRightRectF == null) {
            mRightRectF = new RectF(viewWidth - viewHeight, 0, viewWidth, viewHeight);
        }

        if (offsetX == radius) {
            //true
            canvas.drawArc(mLeftRectF, 90, 180, true, mBackgroundPaintTrue);
            canvas.drawArc(mRightRectF, 270, 180, true, mBackgroundPaintTrue);
            canvas.drawRect(radius, 0, viewWidth - radius, viewHeight, mBackgroundPaintTrue);
        } else if (offsetX == viewWidth - radius) {
            //false
            canvas.drawArc(mLeftRectF, 90, 180, true, mBackgroundPaintFalse);
            canvas.drawArc(mRightRectF, 270, 180, true, mBackgroundPaintFalse);
            canvas.drawRect(radius, 0, viewWidth - radius, viewHeight, mBackgroundPaintFalse);
        } else {
            //滑动过程中
            canvas.drawArc(mLeftRectF, 90, 180, true, mBackgroundPaintFalse);
            canvas.drawArc(mRightRectF, 270, 180, true, mBackgroundPaintTrue);
            canvas.drawRect(radius, 0, offsetX, viewHeight, mBackgroundPaintFalse);
            canvas.drawRect(offsetX, 0, viewWidth - radius, viewHeight, mBackgroundPaintTrue);
        }
        canvas.drawCircle(offsetX, offsetY, radius, mSlidePaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                hasMove(event);
                break;

            case MotionEvent.ACTION_MOVE:
                if (isMove) {
                    moveToNext(event);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    //控件是点击改变状态，手指抬起后，更改状态
                    /***
                    if (offsetX < viewWidth / 2) {
                        offsetX = viewWidth - radius;
                    } else {
                        offsetX = radius;
                    }***/
                    if (isChecked()){
                        offsetX = viewWidth - radius;
                    }else{
                        offsetX = radius;
                    }
                    invalidate();
                    checkedChange();
                } else {
                    //控件是滑动改变状态，手指抬起后，判断中心位置，根据中心位置来更改控件的状态
                    if (offsetX != radius && offsetX != (viewWidth - radius)) {
                        if (offsetX < viewWidth / 2) {
                            offsetX = radius;
                        } else {
                            offsetX = viewWidth - radius;
                        }
                        invalidate();
                        checkedChange();
                    }
                }
                isMove = false;
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {

        return super.performClick();
    }

    /**
     * 根据按下的区域，判断是否可以滑动
     */
    private void hasMove(MotionEvent event) {
        int downX = (int) event.getX();
        int downY = (int) event.getY();

        int distanceX = Math.abs(offsetX - downX);
        int distanceY = Math.abs(offsetY - downY);

        int distance = (int) Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));

        isMove = distance < radius;
    }

    /**
     * 滑动
     */
    private void moveToNext(MotionEvent event) {
        offsetX = (int) event.getX();
        if (offsetX + radius >= viewWidth) {
            offsetX = viewWidth - radius;
        } else if (offsetX - radius <= 0) {
            offsetX = radius;
        }
        invalidate();
        checkedChange();
    }

    /**
     * 控件状态发生改变
     */
    private void checkedChange() {
        boolean check = offsetX < viewWidth / 2;
        if (mChecked != check) {
            mChecked = check;
            if (mOnCheckedChangeListener != null) {
                mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
            }
        }
    }

    /**
     * 设置控件的状态
     */
    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        if (checked) {
            offsetX = radius;
        } else {
            offsetX = viewWidth - radius;
        }
        invalidate();
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    /**
     * 控件状态监听
     */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CheckBoxView buttonView, boolean isChecked);
    }
}
