package com.dev.baqari.myapplication.view

import android.content.Context
import android.graphics.*
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.dev.baqari.myapplication.R


class WeekView(context: Context, attrs: AttributeSet?) : View(context, attrs), GestureDetector.OnGestureListener {
    private var mWeekItemHeight = 0
    private var mDisplayWidth = 0
    private var mDisplayHeight = 0
    private var mScrollY = 10.toDip()
    private val mWeekItemPaint: Paint by lazy {
        Paint()
    }
    private val mTouchItemPaint: Paint by lazy {
        Paint()
    }
    private val mWeekHourTextPaint: Paint by lazy {
        Paint()
    }
    private val mTouchTextPaint: Paint by lazy {
        Paint()
    }
    private val mGestureDecector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(this.context, this)
    }
    private var mBottomOverlayed = true
    private val mLocationMap = ArrayList<Float>()
    private val mTouchItem: TouchItem by lazy {
        TouchItem(0f, 0f)
    }
    private var mOnLongPressed = false

    init {
        mWeekItemPaint.color = Color.parseColor("#e4e4e4")
        mWeekHourTextPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
        mWeekHourTextPaint.color = Color.BLACK
        mTouchItemPaint.color = Color.RED
        mTouchTextPaint.color = Color.WHITE
        mTouchTextPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
        mWeekItemHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70f, resources.displayMetrics).toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mLocationMap.clear()
        (0..23).forEachIndexed { it, i ->
            val top = ((it * mWeekItemHeight) + mScrollY + 5)
            var bottom = (((it * mWeekItemHeight) + mWeekItemHeight + mScrollY))
            val right = (mDisplayWidth - Padding.RIGHT.value.toDip())
            val left = Padding.LEFT.value.toDip()
            if (i == 0 && top > 15) {
                mScrollY = 15f
                val rect = Rect(left.toInt(), (((it * mWeekItemHeight) + 20f).toInt()), right.toInt(), ((((it * mWeekItemHeight) + mWeekItemHeight + 15f).toInt())))
                canvas?.drawPath(rect.toPath(10f), mWeekItemPaint)
                canvas?.drawText(it.toHour(), (left / 5), ((it * mWeekItemHeight) + 40f), mWeekHourTextPaint)
                onTouchEvent(null)
            } else if (i == 23 && bottom < mDisplayHeight) {
                mBottomOverlayed = false
                bottom = (((it * mWeekItemHeight) + mWeekItemHeight - 20.toDip()))

                val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                canvas?.drawPath(rect.toPath(10f), mWeekItemPaint)
                canvas?.drawText(it.toHour(), (left / 5), top + 20f, mWeekHourTextPaint)
                onTouchEvent(null)
            } else {
                val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                canvas?.drawPath(rect.toPath(10f), mWeekItemPaint)
                canvas?.drawText(it.toHour(), (left / 5), top + 15f, mWeekHourTextPaint)
            }

            mLocationMap.add(top)
        }
        if (mOnLongPressed) {
            val rect = Rect(Padding.LEFT.value.toDip().toInt(),
                    (mTouchItem.top).toInt(),
                    ((mDisplayWidth - Padding.RIGHT.value.toDip()).toInt()),
                    (mTouchItem.bottom - 5).toInt())
            canvas?.drawPath(rect.toPath(10f), mTouchItemPaint)
            canvas?.drawText(resources.getString(R.string.new_event) + " +", left + 70.toDip(), mTouchItem.top + 30, mTouchTextPaint)
            mOnLongPressed = false
        }
    }

    fun Int.toHour(): String {
        return (if (this >= 10) this.toString() else "0" + this.toString()) + ":00"
    }

    fun Int.toDip(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mDisplayHeight = MeasureSpec.getSize(heightMeasureSpec)
        mDisplayWidth = MeasureSpec.getSize(widthMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        } else {
            mGestureDecector.onTouchEvent(event)
            return true
        }
    }

    override fun onShowPress(p0: MotionEvent?) {
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        return true
    }

    override fun onLongPress(event: MotionEvent?) {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mLocationMap.forEachIndexed { i, top ->
                    val rawY = event.rawY
                    val bottom = top + mWeekItemHeight

                    if (top < rawY && bottom > rawY) {
                        try {
                            mTouchItem.top = mLocationMap[i - 1]
                            mTouchItem.bottom = bottom - mWeekItemHeight
                            mOnLongPressed = true
                            invalidate()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        return false
    }

    override fun onScroll(event: MotionEvent?, event1: MotionEvent?, p2: Float, p3: Float): Boolean {
        if (!mBottomOverlayed) {
            mBottomOverlayed = p3 < 0
        }
        if (mBottomOverlayed) {
            mScrollY -= p3
            invalidate()
            return true
        } else {
            return false
        }
    }
}

fun Rect.toPath(radius: Float): Path {
    val path = Path()

    path.moveTo(this.left + radius / 2, this.top.toFloat())
    path.lineTo(this.right - radius / 2, this.top.toFloat())
    path.quadTo(this.right.toFloat(), this.top.toFloat(), this.right.toFloat(), this.top + radius / 2)
    path.lineTo(this.right.toFloat(), this.bottom - radius / 2)
    path.quadTo(this.right.toFloat(), this.bottom.toFloat(), this.right - radius / 2, this.bottom.toFloat())
    path.lineTo(this.left + radius / 2, this.bottom.toFloat())
    path.quadTo(this.left.toFloat(), this.bottom.toFloat(), this.left.toFloat(), this.bottom - radius / 2)
    path.lineTo(this.left.toFloat(), this.top + radius / 2)
    path.quadTo(this.left.toFloat(), this.top.toFloat(), this.left + radius / 2, this.top.toFloat())
    path.close()

    return path
}


