package com.lind.vladimir.gallery

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView


 class SquareImageView : ImageView {

     constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)
     constructor(context: Context?): super(context)

    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = measuredWidth
        setMeasuredDimension(width, width)
    }


}