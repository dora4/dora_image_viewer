package dora.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * fix for Issue #4 and
 * http://code.google.com/p/android/issues/detail?id=18990
 *
 *
 * ScaleGestureDetector seems to mess up the touch events, which means that
 * ViewGroups which make use of onInterceptTouchEvent throw a lot of
 * IllegalArgumentException: pointerIndex out of range.
 *
 *
 * There's not much I can do in my code for now, but we can mask the result by
 * just catching the problem and ignoring it.
 */
class DoraViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null)
    : ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return try {
            super.onInterceptTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            // 不理会
            false
        } catch (e: ArrayIndexOutOfBoundsException) {
            // 不理会
            false
        }
    }
}