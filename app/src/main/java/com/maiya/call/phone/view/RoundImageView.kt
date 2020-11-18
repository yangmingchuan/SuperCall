package com.maiya.call.phone.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import com.maiya.call.R
import com.preface.megatron.R

/**
 * @ClassName: [RoundImageView]
 * @Description:
 *
 * Created by admin at 2020-05-08
 * @Email xiaosw0802@163.com
 */
@SuppressLint("AppCompatCustomView")
class RoundImageView @JvmOverloads constructor (
        ctx: Context, attrs: AttributeSet? = null
) : ImageView(ctx, attrs){

    private var mClipPath = Path()
    private var mRadiusRectF = RectF()
    private var mBackgroundRadius = 0f

    init {
        parseAttrs(context, attrs)
    }

    private inline fun parseAttrs(ctx: Context, attrs: AttributeSet? = null) {
        attrs?.run {
            context.obtainStyledAttributes(this, R.styleable.RoundImageView)?.run {
                try {
                    mBackgroundRadius = getDimension(R.styleable.RoundImageView_android_radius, mBackgroundRadius)
                } finally {
                    recycle()
                }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val l = paddingLeft.toFloat()
        val t = paddingTop.toFloat()
        val r = measuredWidth - paddingRight.toFloat()
        val b = measuredHeight - paddingBottom.toFloat()
        val w = r - l
        val h = b - t

        with(mClipPath) {
            reset()
            val diameter = mBackgroundRadius * 2
            moveTo(diameter, t)
            lineTo(w - diameter, t)

            // 右上
            mRadiusRectF.set(w - diameter
                    , t
                    , r
                    , t + diameter)
            arcTo(mRadiusRectF, -90f, 90f, false)

            lineTo(r, b - diameter)

            // 右下
            mRadiusRectF.set(w - diameter, b - diameter, r, b)
            arcTo(mRadiusRectF, 0f, 90f, false)

            lineTo(l + diameter, b)

            // 左下
            mRadiusRectF.set(l, b - diameter, l + diameter, b)
            arcTo(mRadiusRectF, 90f, 90f, false)

            lineTo(l, t + diameter)

            mRadiusRectF.set(l, t, l + diameter, t + diameter)
            arcTo(mRadiusRectF, 180f, 90f, false)

            close()
        }

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            save()
            canvas.clipPath(mClipPath)
            super.onDraw(canvas)
            restore()
        }
    }

}