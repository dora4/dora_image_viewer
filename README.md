# DoraImageViewer

描述：双指缩放控件

复杂度：★★★☆☆

分组：【Dora大控件组】

关系：暂无

技术要点：多点触控、事件分发、GestureDetector

### 照片

![avatar](https://github.com/dora4/dora_image_viewer/blob/main/art/dora_image_viewer.jpg)

### 动图

![avatar](https://github.com/dora4/dora_image_viewer/blob/main/art/dora_image_viewer.gif)

### 软件包

https://github.com/dora4/dora_image_viewer/blob/main/art/dora_image_viewer.apk

### 用法

```kotlin
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
```
