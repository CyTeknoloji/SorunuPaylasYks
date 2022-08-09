package com.caneryildirim.sorunupaylasyks.util


import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class DynamicImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val drawable = this.drawable

        if (drawable != null) {
            //Ceil not round - avoid thin vertical gaps along the left/right edges

            var ratio= drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth

            var width = View.MeasureSpec.getSize(widthMeasureSpec)
            if (width > drawable.intrinsicWidth.toFloat()*1.8){
                val widthFloat=drawable.intrinsicWidth.toFloat()*1.8
                width=widthFloat.toInt()
            }
            val height =
                Math.ceil((width * drawable.intrinsicHeight.toFloat() / drawable.intrinsicWidth).toDouble())
                    .toInt()

            this.setMeasuredDimension(width, height)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}