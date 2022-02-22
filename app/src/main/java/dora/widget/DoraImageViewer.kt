package dora.widget

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import java.lang.IllegalArgumentException
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * 图片双指缩放查看器。
 */
class DoraImageViewer @JvmOverloads constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    var touchMode: Int = TOUCH_MODE_NORMAL
    var downX: Float = 0f
    var downY: Float = 0f
    var pointerDownX: Float = 0f
    var pointerDownY: Float = 0f
    private var oldDist: Int = 0
    private var touchSlop: Int = 0
    private val gestureDetector: GestureDetector
    private lateinit var childView: View
    private var touchListener: TouchListener? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (childCount != 1) {
            throw IllegalArgumentException("DoraImageViewer can have only one child.")
        }
        childView = getChildAt(0)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when(ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                touchMode = TOUCH_MODE_NORMAL
                downX = ev.rawX
                downY = ev.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                // 垂直方向有变化，则拦截事件。不能全部拦截，否则viewPager没法得到事件
                if (abs(ev.rawY - downY) > touchSlop) {
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                downX = 0f
                downY = 0f
                pointerDownX = 0f
                pointerDownY = 0f
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                touchMode = TOUCH_MODE_POINTER
                pointerDownX = ev.getRawX(1)
                pointerDownY = ev.getRawY(1)
                oldDist = distance(downX, downY, pointerDownX, pointerDownY)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (ev.pointerCount < 2) {
                    touchMode == TOUCH_MODE_NORMAL
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                if (touchMode == TOUCH_MODE_NORMAL) {
                    if (abs(event.rawX - downX) > touchSlop || abs(event.rawY - downY) > touchSlop) {
                        onDrag(event.rawX - downX, event.rawY - downY)
                    }
                }
                if (touchMode == TOUCH_MODE_POINTER) {
                    if (event.pointerCount >= 2) {
                        pointerDownX = event.getRawX(1)
                        pointerDownY = event.getRawY(1)
                        zoom()
                    }
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                setBackgroundColor(Color.BLACK)
                // 缩小后反弹
                if (childView.scaleX < 1f) {
                    childView.scaleX = 1f
                }
                if (childView.scaleY < 1f) {
                    childView.scaleY = 1f
                }
                // 还原位置
                childView.translationX = 0f
                childView.translationY = 0f
            }
        }
        return true
    }

    private fun distance(x0: Float, y0: Float, x1: Float, y1: Float): Int {
        return sqrt((x1 - x0).toDouble().pow(2.0) + (y1 - y0).toDouble().pow(2.0)).toInt()
    }

    fun zoom() {
        if (oldDist == 0) {
            return
        }
        val newDist = distance(downX, downY, pointerDownX, pointerDownY)
        var scale = newDist * 1f / oldDist
        scale = scale.coerceAtLeast(SCALE_MIN).coerceAtMost(SCALE_MAX)
        childView.scaleX = scale
        childView.scaleY = scale
    }

    fun onDrag(dx: Float, dy: Float) {
        childView.translationX = dx
        childView.translationY = dy
        var scale = 1 - abs(dy) / ScreenUtils.getScreenHeight(context)
        val alpha = scale * 255
        //设置背景颜色
        setBackgroundColor(Color.argb(alpha.toInt(), 0, 0, 0))
    }

    companion object {
        private const val TOUCH_MODE_NORMAL = 0 // 单指操作
        private const val TOUCH_MODE_POINTER = 1 // 多指操作
        private const val SCALE_MIN = 0.25f
        private const val SCALE_MAX = 2f
    }

    override fun onDown(e: MotionEvent): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        touchListener?.onClick(this, e)
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        touchListener?.onLongClick(this, e)
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        touchListener?.onDoubleClick(this, e)
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    fun setTouchListener(listener: TouchListener) {
        this.touchListener = listener
    }

    interface TouchListener {
        fun onClick(v: View, e: MotionEvent)
        fun onDoubleClick(v: View, e: MotionEvent)
        fun onLongClick(v: View, e: MotionEvent)
    }

    init {
        setBackgroundColor(Color.BLACK)
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        gestureDetector = GestureDetector(context, this)
        gestureDetector.setOnDoubleTapListener(this)
    }
}