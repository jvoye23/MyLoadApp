package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var downloadProgress = 0f
    private var text = resources.getString(R.string.button_download)
    private val textPosition = PointF(0.0f, 0.0f)
    private var textWidth = 0f
    private var beforeLoadBackgroundColor = 0
    private var afterLoadBackgroundColor = 0
    private var textColor = 0
    private var loadingCircleColor = 0

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            beforeLoadBackgroundColor = getColor(R.styleable.LoadingButton_beforeLoadBackground, 0)
            afterLoadBackgroundColor = getColor(R.styleable.LoadingButton_afterLoadBackground, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, 0)
        }
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimensionPixelSize(R.dimen.default_text_size).toFloat()
        color = textColor
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = afterLoadBackgroundColor
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = loadingCircleColor
    }

    private val valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old: ButtonState, new: ButtonState ->
        when (new) {
            ButtonState.Loading -> {
                showLoadingButton()
            }
            ButtonState.Completed -> {
                hideLoadingButton()
            }
            ButtonState.Clicked -> {
            }
        }
    }

    private fun showLoadingButton() {
        valueAnimator.apply {
            setFloatValues(1f)
            duration = 3000
            addUpdateListener {
                downloadProgress = it.animatedValue as Float
                invalidate()
            }
            start()
        }
        text = resources.getString(R.string.button_loading)
        textWidth = textPaint.measureText(text)
        invalidate()
    }

    private fun hideLoadingButton() {
        valueAnimator.end()
        text = resources.getString(R.string.button_download)
        downloadProgress = 0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(beforeLoadBackgroundColor)
        canvas.drawRect(0f, 0f, widthSize * downloadProgress, heightSize.toFloat(), progressPaint)
        canvas.drawText(text, textPosition.x, textPosition.y, textPaint)
        canvas.drawArc(
            textPosition.x + textWidth / 2,
            (heightSize / 3).toFloat(),
            textPosition.x + textWidth / 2 + (heightSize / 3).toFloat(),
            (heightSize * 2 / 3).toFloat(),
            270f, 360 * downloadProgress, true, circlePaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)

        textPosition.x = (widthSize / 2).toFloat()
        textPosition.y = heightSize / 2 - (textPaint.descent() + textPaint.ascent()) / 2
    }

}