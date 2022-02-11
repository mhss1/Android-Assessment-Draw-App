package com.mhss.app.androidassessment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.*

const val DRAW_MODE_PEN = 0
const val DRAW_MODE_ARROW = 1
const val DRAW_MODE_REC = 2
const val DRAW_MODE_ELL = 3
const val DRAW_MODE_NON = -1

class CanvasView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    private lateinit var extraBitmap: Bitmap
    private lateinit var extraCanvas: Canvas

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.black, null)

    private val paint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 10f
    }

    private var path = Path()

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var startX = 0f
    private var startY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    private var drawMode = DRAW_MODE_PEN

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        when (drawMode) {
            DRAW_MODE_REC -> drawRect(canvas, startX, startY, currentX, currentY)
            DRAW_MODE_ELL -> drawEllipse(canvas, startX, startY, currentX, currentY)
            DRAW_MODE_ARROW -> drawArrow(canvas, startX, startY, currentX, currentY)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }

        invalidate()
        return true
    }

    private fun touchStart() {
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)

        currentX = motionTouchEventX
        currentY = motionTouchEventY
        startX = currentX
        startY = currentY
    }

    private fun touchMove() {
        if (drawMode == DRAW_MODE_PEN) {
            val dx = abs(motionTouchEventX - currentX)
            val dy = abs(motionTouchEventY - currentY)
            if (dx >= touchTolerance || dy >= touchTolerance) {
                path.quadTo(
                    currentX,
                    currentY,
                    (motionTouchEventX + currentX) / 2,
                    (motionTouchEventY + currentY) / 2
                )
                currentX = motionTouchEventX
                currentY = motionTouchEventY
                extraCanvas.drawPath(path, paint)
            }
        } else {
            currentX = motionTouchEventX
            currentY = motionTouchEventY
        }
    }

    private fun touchUp() {
        when (drawMode) {
            DRAW_MODE_ELL -> drawEllipse(extraCanvas, startX, startY, currentX, currentY)
            DRAW_MODE_REC -> drawRect(extraCanvas, startX, startY, currentX, currentY)
            DRAW_MODE_ARROW -> drawArrow(extraCanvas, startX, startY, currentX, currentY)
        }
        path.reset()
    }

    private fun drawEllipse(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        canvas.drawOval(x1, y1, x2, y2, paint)
    }

    private fun drawRect(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        canvas.drawRect(x1, y1, x2, y2, paint)
    }

    private fun drawArrow(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        val radius = 40f
        val angle = 45f

        val angleRad = (PI * angle / 180.0f).toFloat()
        val lineAngle = atan2(y2 - y1, x2 - x1)

        canvas.drawLine(x1, y1, x2, y2, paint)

        val path = Path()
        path.moveTo(x2, y2)
        path.lineTo(
            (x2 - radius * cos(lineAngle - angleRad)),
            (y2 - radius * sin(lineAngle - angleRad))
        )
        path.moveTo(x2, y2)
        path.lineTo(
            (x2 - radius * cos(lineAngle + angleRad)),
            (y2 - radius * sin(lineAngle + angleRad))
        )
        canvas.drawPath(path, paint)
    }

    fun setPaintColor(color: Int) {
        paint.color = ResourcesCompat.getColor(resources, color, null)
    }

    fun setDrawMode(mode: Int) {
        drawMode = mode
    }

}