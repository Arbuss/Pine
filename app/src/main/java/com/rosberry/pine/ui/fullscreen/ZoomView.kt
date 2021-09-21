package com.rosberry.pine.ui.fullscreen

import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_POINTER_DOWN
import android.view.MotionEvent.ACTION_UP
import com.rosberry.pine.R

class ZoomView(context: Context, attrs: AttributeSet?, defStyle: Int) :
        androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyle) {

    companion object {

        private const val DEFAULT_MAX_ZOOM_IN = 3.0f
        private const val DEFAULT_MAX_ZOOM_OUT = 1.0f

        private const val MIN_ZOOM = 0.5f
        private const val MAX_ZOOM = 10f
    }

    private val pos = PointF(0f, 0f)
    private val dist = PointF(0f, 0f)
    private var scale = 1f
    private var moveLock = false

    var maxZoomIn = DEFAULT_MAX_ZOOM_IN
        set(value) {
            if (value < MIN_ZOOM)
                throw AssertionError("value should be more $MIN_ZOOM")
            field = value
        }

    var maxZoomOut = DEFAULT_MAX_ZOOM_OUT
        set(value) {
            if (value > MAX_ZOOM)
                throw AssertionError("value should be less $MAX_ZOOM")
            field = value
        }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        attrs?.let {
            setAttributes(context, attrs)
        }
    }

    constructor(context: Context) : this(context, null)

    private fun setAttributes(context: Context, attrs: AttributeSet) {
        val attrsArray = context.obtainStyledAttributes(attrs, R.styleable.ZoomView)
        maxZoomIn = attrsArray.getFloat(R.styleable.ZoomView_max_zoom_in, DEFAULT_MAX_ZOOM_IN)
        maxZoomOut = attrsArray.getFloat(R.styleable.ZoomView_max_zoom_out, DEFAULT_MAX_ZOOM_OUT)
        attrsArray.recycle()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (scaleType != ScaleType.MATRIX) {
            scaleType = ScaleType.MATRIX
        }

        if (event?.pointerCount == 1) {
            return onePointerCountHandler(event)
        }

        if (event?.pointerCount == 2) {
            twoPointerCountHandler(event)
        }
        return true
    }

    private fun onePointerCountHandler(event: MotionEvent): Boolean {
        when (event.action) {
            ACTION_UP -> {
                pos.set(event.x, event.y)
                moveLock = false
            }

            ACTION_DOWN -> {
                pos.set(event.x, event.y)
            }

            ACTION_MOVE -> {
                if (moveLock || scale == 1f)
                    return super.performClick()
                val posX = event.x - pos.x
                val posY = event.y - pos.y
                pos.set(event.x, event.y)
                val matrix = Matrix(imageMatrix)
                matrix.postTranslate(posX, posY)
                applyMatrix(matrix)
            }
        }
        return true
    }

    private fun twoPointerCountHandler(event: MotionEvent) {
        when (event.actionMasked) {
            ACTION_POINTER_DOWN -> {
                dist.set(event.getX(0) - event.getX(1),
                        event.getY(0) - event.getY(1))
                moveLock = true
            }

            ACTION_MOVE -> {
                val current = PointF(event.getX(0) - event.getX(1),
                        event.getY(0) - event.getY(1))
                scale = current.length() / dist.length()
                val matrix = Matrix(imageMatrix)
                matrix.postScale(scale, scale, width / 2f, height / 2f)
                dist.set(current.x, current.y)
                applyMatrix(matrix)
            }
        }
    }

    private fun applyMatrix(matrix: Matrix) {
        val values = FloatArray(9)
        matrix.getValues(values)

        val scale = (values[Matrix.MSCALE_X] + values[Matrix.MSCALE_Y]) / 2f
        this.scale = scale

        val width = (drawable?.intrinsicWidth ?: 1) * scale
        val height = (drawable?.intrinsicHeight ?: 1) * scale

        val leftBorder = values[Matrix.MTRANS_X]
        val rightBorder = -values[Matrix.MTRANS_X] - width + getWidth()
        val bottomBorder = values[Matrix.MTRANS_Y]
        val topBorder = -values[Matrix.MTRANS_Y] - height + getHeight()

        if (width > getWidth()) {
            if (rightBorder > 0)
                matrix.postTranslate(rightBorder, 0f)
            else if (leftBorder > 0)
                matrix.postTranslate(-leftBorder, 0f)
        } else if ((leftBorder < 0) xor (rightBorder < 0)) {
            if (rightBorder < 0)
                matrix.postTranslate(rightBorder, 0f)
            else
                matrix.postTranslate(-leftBorder, 0f)
        }

        if (height > getHeight()) {
            if (bottomBorder > 0)
                matrix.postTranslate(0f, -bottomBorder)
            else if (topBorder > 0)
                matrix.postTranslate(0f, topBorder)
        } else if ((topBorder < 0) xor (bottomBorder < 0)) {
            if (bottomBorder < 0)
                matrix.postTranslate(0f, -bottomBorder)
            else
                matrix.postTranslate(0f, topBorder)
        }

        if (scale > maxZoomIn) {
            val undoScale = maxZoomIn / scale
            matrix.postScale(undoScale, undoScale, getWidth() / 2f, getHeight() / 2f)
        } else if (scale < maxZoomOut) {
            val undoScale = maxZoomOut / scale
            matrix.postScale(undoScale, undoScale, getWidth() / 2f, getHeight() / 2f)
        }
        imageMatrix = matrix
    }
}