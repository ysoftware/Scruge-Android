package com.scruge.scruge.dependencies.view

import android.content.res.Resources

data class Dimension constructor(var px:Int = 0) {

    companion object {

        fun dp(dp:Int):Dimension {
            return Dimension(Math.round(dp * Resources.getSystem().displayMetrics.density))
        }

        fun px(px:Int):Dimension {
            return Dimension(px)
        }
    }

    val dp:Int get() = Math.ceil((px / Resources.getSystem().displayMetrics.density).toDouble()).toInt()
}