package dora.widget

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager.widget.PagerAdapter

class MainActivity : AppCompatActivity() {

    val pages: IntArray = intArrayOf(R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_background)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val imageViewer = findViewById<DoraImageViewer>(R.id.imageViewer)
        val viewPager = findViewById<DoraViewPager>(R.id.viewPager)
        imageViewer.setTouchListener(object : DoraImageViewer.TouchListener {
            override fun onClick(v: View, e: MotionEvent) {
                Toast.makeText(this@MainActivity, "单击(${e.rawX},${e.rawY})", Toast.LENGTH_SHORT).show()
            }

            override fun onDoubleClick(v: View, e: MotionEvent) {
                Toast.makeText(this@MainActivity, "双击(${e.rawX},${e.rawY})", Toast.LENGTH_SHORT).show()
            }

            override fun onLongClick(v: View, e: MotionEvent) {
                Toast.makeText(this@MainActivity, "长按(${e.rawX},${e.rawY})", Toast.LENGTH_SHORT).show()
            }

        })
        viewPager.adapter = object : PagerAdapter() {

            override fun getCount(): Int {
                return pages.size
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = ImageView(this@MainActivity)
                imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
                imageView.setImageResource(pages[position])
                container.addView(imageView)
                return imageView
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                return view == `object`
            }
        }
    }
}