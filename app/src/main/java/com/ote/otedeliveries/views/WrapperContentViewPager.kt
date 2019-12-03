package com.ote.otedeliveries.views

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager


class WrapContentViewPager : ViewPager {

    constructor(context: Context) : super(context) {
        initPageChangeListener()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initPageChangeListener()
    }

    private fun initPageChangeListener() {
        addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                requestLayout()
            }
        })
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val myHeightMeasureSpec: Int

        var height = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            val h = child.measuredHeight
            if (h > height) height = h
        }

        myHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, myHeightMeasureSpec)
    }

}