package com.maiya.call.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.maiya.call.R;
import com.maiya.call.util.ui.UIUtil;


/**
 * @ClassName: {@link ArcProgressBar}
 * @Description: Created by admin at 2020-05-08
 * @Email xiaosw0802@163.com
 */
public class ArcProgressBar extends View {

    private static final String TAG = ArcProgressBar.class.getSimpleName();

    // 主背景
    private Drawable mBackground;
    private int mBackgroundResource;


    // 默认进度
    private Paint mDefProgressPaint;
    private float mDefProgressWidth = 18f;
    private int mDefProgressColor = Color.parseColor("#bbbec3");

    // 当前进度
    private Paint mCurrProgressPaint;
    private float mCurrProgressWidth = 18f;
    private int mCurrProgressColor = Color.parseColor("#1ccf89");

    // 进度指示器
    private Paint mIndicatorPaint;

    // 绘制区域
    private RectF mDrawRectF;
    private RectF mTempRectF;
    private Rect mTextRect;

    private int mMax = 360;
    private int mProgress = 0;
    private float mAngle;

    // 进度文字
    private Paint mTextPaint;
    private boolean mShowPercent = true;

    // 背景是否从进度中部绘制
    private boolean mUseInnerPadding = true;

    public ArcProgressBar(Context context) {
        super(context);
        init(context, null);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ArcProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mDrawRectF.set(getPaddingLeft()
                , getPaddingTop()
                , getMeasuredWidth() - getPaddingRight()
                , getMeasuredHeight() - getPaddingBottom());

        maybeUpdateBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        // background
        drawBackground(canvas);

        // draw
        canvas.save();
        canvas.rotate(-90, mDrawRectF.centerX(), mDrawRectF.centerY());
        float progressOffset = Math.max(mDefProgressWidth, mCurrProgressWidth) / 2;
        // default progress
        drawProgress(canvas, progressOffset, 360, mDefProgressPaint);

        // current progress
        drawProgress(canvas, progressOffset, mAngle, mCurrProgressPaint);

//        drawIndicator(canvas, mAngle);

        canvas.restore();

        drawText(canvas);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mDrawRectF = new RectF();
        mTempRectF = new RectF();
        mTextRect = new Rect();

        initPaint();
        parseAttrs(context, attrs);
    }

    private void initPaint() {
        mDefProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDefProgressPaint.setStyle(Paint.Style.STROKE);
        setDefProgressColor(mDefProgressColor);

        mCurrProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrProgressPaint.setStyle(Paint.Style.STROKE);
        mCurrProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mCurrProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        setCurrProgressColor(mCurrProgressColor);

        mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mIndicatorPaint.setColor(Color.RED);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setDefProgressWidth(mDefProgressWidth);
        setCurrProgressWidth(mCurrProgressWidth);
    }

    private void parseAttrs(Context context, AttributeSet attrs) {
        if (null == context || null == attrs) {
            return;
        }
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ArcProgressBar);
        if (null == ta) {
            return;
        }
        try {
            setBackgroundDrawable(ta.getDrawable(R.styleable.ArcProgressBar_android_background));
            super.setBackground(new TransparentColorDrawable());

            setDefProgressWidth(ta.getDimension(R.styleable.ArcProgressBar_defProgressWidth, mDefProgressWidth));
            setCurrProgressWidth(ta.getDimension(R.styleable.ArcProgressBar_currProgressWidth, mCurrProgressWidth));

            setUseInnerPadding(ta.getBoolean(R.styleable.ArcProgressBar_useInnerPadding, mUseInnerPadding));

            setDefProgressColor(ta.getColor(R.styleable.ArcProgressBar_defProgressColor, mDefProgressColor));

            setCurrProgressColor(ta.getColor(R.styleable.ArcProgressBar_currProgressColor, mCurrProgressColor));

            setTextColor(ta.getColor(R.styleable.ArcProgressBar_android_textColor, Color.BLACK));
            setTextSize(ta.getDimension(R.styleable.ArcProgressBar_android_textSize, UIUtil.dip2px(context, 12)));
            setShowProgressText(ta.getBoolean(R.styleable.ArcProgressBar_showPercent, mShowPercent));
        } finally {
            ta.recycle();
        }
    }

    private void drawBackground(Canvas canvas) {
        if (null == mBackground) {
            return;
        }
        mBackground.draw(canvas);
    }

    private void drawProgress(Canvas canvas, float offset, float sweepAngle, Paint paint) {
        if (null == canvas || null == paint || null == mTempRectF || null == mDrawRectF) {
            return;
        }
        mTempRectF.set(mDrawRectF.left + offset
                , mDrawRectF.top + offset
                , mDrawRectF.right - offset
                , mDrawRectF.bottom - offset);
        canvas.drawArc(mTempRectF, 0, sweepAngle, false, paint);
    }

    private void drawText(Canvas canvas) {
        if (canvas == null || !isShowPercent()) {
            return;
        }
        if (mTempRectF == null) {
            mTextRect = new Rect();
        }
        String text = ((int) (mProgress * 100f / mMax)) + "%";
        mTextPaint.getTextBounds(text, 0, text.length(), mTextRect);
        float x = (mDrawRectF.width() - mTextRect.width()) / 2;
        float y = mDrawRectF.centerY() - mTextRect.centerY();
        canvas.drawText(text, x, y, mTextPaint);
    }

    private void drawIndicator(Canvas canvas, float angle) {
        if (null == canvas) {
            return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.rotate(angle + 2, mDrawRectF.centerX(), mDrawRectF.centerY());
        float w = Math.max(mDefProgressWidth, mCurrProgressWidth);
        canvas.drawCircle(mDrawRectF.width() - w / 2, mDrawRectF.centerY(), w / 2, mIndicatorPaint);
        canvas.restoreToCount(saveCount);
    }

    private void maybeUpdateBounds() {
        if (mBackground == null) {
            return;
        }
        if (null == mTempRectF) {
            mTempRectF = new RectF();
        }
        if (null == mDrawRectF) {
            mDrawRectF = new RectF();
        }
        if (mUseInnerPadding) {
            float w = Math.max(mDefProgressWidth, mCurrProgressWidth);
            float bgOffset = (w - mCurrProgressPaint.getStrokeWidth()) / 2;
            mTempRectF.set(mDrawRectF.left + bgOffset
                    , mDrawRectF.top + bgOffset
                    , mDrawRectF.right - bgOffset
                    , mDrawRectF.bottom - bgOffset);
            mBackground.setBounds((int) mTempRectF.left, (int) mTempRectF.top, (int) mTempRectF.right, (int) mTempRectF.bottom);
        } else {
            mBackground.setBounds((int) mDrawRectF.left, (int) mDrawRectF.top, (int) mDrawRectF.right, (int) mDrawRectF.bottom);
        }
    }

    @Override
    public void setBackground(Drawable background) {
        if (background instanceof TransparentColorDrawable) {
            return;
        }
        if (null != mBackground && mBackground.equals(background)) {
            return;
        }
        mBackground = background;
        maybeUpdateBounds();
        postInvalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        if (mBackground instanceof ColorDrawable) {
            ((ColorDrawable) mBackground.mutate()).setColor(color);
            mBackgroundResource = 0;
        } else {
            setBackground(new ColorDrawable(color));
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        if (resid != 0 && resid == mBackgroundResource) {
            return;
        }

        Drawable d = null;
        if (resid != 0) {
            d = getResources().getDrawable(resid);
        }
        setBackground(d);

        mBackgroundResource = resid;
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        setBackground(background);
    }

    @Override
    public void setDrawingCacheBackgroundColor(int color) {
    }

    public void setDefProgressWidth(float progressWidth) {
        if (mDefProgressWidth == progressWidth) {
            return;
        }
        mDefProgressWidth = progressWidth;
        if (null == mDefProgressPaint || null == mCurrProgressPaint) {
            return;
        }
        mDefProgressPaint.setStrokeWidth(mDefProgressWidth);
    }

    public void setCurrProgressWidth(float progressWidth) {
        if (mCurrProgressWidth == progressWidth) {
            return;
        }
        mCurrProgressWidth = progressWidth;
        if (null == mDefProgressPaint || null == mCurrProgressPaint) {
            return;
        }
        mCurrProgressPaint.setStrokeWidth(mCurrProgressWidth);
    }


    public void setDefProgressColor(int defProgressColor) {
        mDefProgressColor = defProgressColor;
        if (null == mDefProgressPaint) {
            return;
        }
        mDefProgressPaint.setColor(mDefProgressColor);
    }

    public void setCurrProgressColor(int currProgressColor) {
        mCurrProgressColor = currProgressColor;
        if (null == mCurrProgressPaint) {
            return;
        }
        mCurrProgressPaint.setColor(mCurrProgressColor);
    }

    public void setUseInnerPadding(boolean useInnerPadding) {
        this.mUseInnerPadding = useInnerPadding;
    }

    public void setMax(int max) {
        if (mMax == max) {
            return;
        }
        mMax = max;
    }

    public void setProgress(int progress) {
        if (mProgress == progress) {
            return;
        }
        mProgress = progress;
        mAngle = 360f * progress / mMax;
        postInvalidate();
    }

    public void updateProgress(int max, int progress) {
        if (mMax == max && mProgress == progress) {
            return;
        }
        mMax = max;
        mProgress = progress;
        mAngle = 360f * progress / mMax;
        postInvalidate();
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        postInvalidate();
    }

    public void setTextSize(float textSize) {
        mTextPaint.setTextSize(textSize);
        postInvalidate();
    }

    public boolean isShowPercent() {
        return mShowPercent;
    }

    public void setShowProgressText(boolean showPercent) {
        if (mShowPercent == showPercent) {
            return;
        }
        mShowPercent = showPercent;
        postInvalidate();
    }

    private static class TransparentColorDrawable extends ColorDrawable {

        public TransparentColorDrawable() {
            super(Color.TRANSPARENT);
        }
    }
}
