package com.dev.baqari.myapplication.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.dev.baqari.myapplication.R


class WeekView : View, GestureDetector.OnGestureListener {
    private var mWeekItemHeight = 0f
    private var mDisplayWidth = 0
    private var mDisplayHeight = 0
    private var mScrollY = 10.toDp()
    private val mWeekItemPaint: Paint by lazy {
        Paint()
    }
    private val mNewEventPaint: Paint by lazy {
        Paint()
    }
    private val mWeekTimeTextPaint: Paint by lazy {
        Paint()
    }
    private val mNewEventTextPaint: Paint by lazy {
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
    private var mRadius = 10f
    private var mNewEventText = ""

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.WeekView, 0, 0)

        mWeekItemPaint.color = a.getColor(R.styleable.WeekView_weekItemColor, Color.parseColor("#e4e4e4"))
        mWeekTimeTextPaint.color = a.getColor(R.styleable.WeekView_weekItemTimeColor, Color.BLACK)
        mNewEventPaint.color = a.getColor(R.styleable.WeekView_newEventColor, Color.RED)
        mNewEventTextPaint.color = a.getColor(R.styleable.WeekView_newEventTextColor, Color.WHITE)

        val weekItemTimeTextSize = a.getDimension(R.styleable.WeekView_weekItemTimeTextSize, 14f)
        mWeekTimeTextPaint.textSize = weekItemTimeTextSize.toSp()

        val newEventTextSize = a.getDimension(R.styleable.WeekView_newEventTextSize, 14f)
        mNewEventTextPaint.textSize = newEventTextSize.toSp()

        val weekItemHeight = a.getDimension(R.styleable.WeekView_weekitemHeight, 70f)
        mWeekItemHeight = weekItemHeight.toDp()

        val radius = a.getDimension(R.styleable.WeekView_weekItemRoundRadius, 10f)
        mRadius = radius

        val newEventText = a.getString(R.styleable.WeekView_newEventText)
        if (newEventText != null)
            mNewEventText = newEventText
        else
            mNewEventText = resources.getString(R.string.new_event)

        a.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mLocationMap.clear()
        (0..23).forEachIndexed { it, i ->
            val top = ((it * mWeekItemHeight) + mScrollY + 5)
            var bottom = (((it * mWeekItemHeight) + mWeekItemHeight + mScrollY))
            val right = (mDisplayWidth - Padding.RIGHT.value.toDp())
            val left = Padding.LEFT.value.toDp()
            if (i == 0 && top > 15) {
                mScrollY = 15f
                val rect = Rect(left.toInt(), (((it * mWeekItemHeight) + 20f).toInt()), right.toInt(), ((((it * mWeekItemHeight) + mWeekItemHeight + 15f).toInt())))
                canvas?.drawPath(rect.toPath(mRadius), mWeekItemPaint)
                canvas?.drawText(it.toHour(), (left / 5), ((it * mWeekItemHeight) + 25.toDp()), mWeekTimeTextPaint)
                onTouchEvent(null)
            } else if (i == 23 && bottom < mDisplayHeight) {
                mBottomOverlayed = false
                bottom = (mDisplayHeight - 100).toDp()
                val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                canvas?.drawPath(rect.toPath(mRadius), mWeekItemPaint)
                canvas?.drawText(it.toHour(), (left / 5), top + 15.toDp(), mWeekTimeTextPaint)
                onTouchEvent(null)
            } else {
                val rect = Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                canvas?.drawPath(rect.toPath(mRadius), mWeekItemPaint)
                canvas?.drawText(it.toHour(), (left / 5), top + 15.toDp(), mWeekTimeTextPaint)
            }

            mLocationMap.add(top)
        }
        if (mOnLongPressed) {
            val rect = Rect(Padding.LEFT.value.toDp().toInt(),
                    (mTouchItem.top).toInt(),
                    ((mDisplayWidth - Padding.RIGHT.value.toDp()).toInt()),
                    (mTouchItem.bottom - 5).toInt())
            canvas?.drawPath(rect.toPath(mRadius), mNewEventPaint)
            canvas?.drawText(mNewEventText, left + 70.toDp(), mTouchItem.top + 30, mNewEventTextPaint)
            mOnLongPressed = false
        }
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

    fun Int.toDp(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics)
    }

    fun Float.toDp(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, resources.displayMetrics)
    }

    fun Float.toSp(): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, resources.displayMetrics)
    }

}
