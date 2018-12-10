package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.scruge.scruge.R


class ProgressView(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_progress, this, true)
    }

    var value = 0.0; set(value) { field = value; updateLayout() }
    var total = 0.0; set(value) { field = value; updateLayout() }
    var firstGoal = 0.0; set(value) { field = value; updateLayout() }
    var prefix = "$"; set(value) { field = value; updateLayout() }
    var suffix = ""; set(value) { field = value; updateLayout() }

    fun updateLayout() {
        // todo
    }
}