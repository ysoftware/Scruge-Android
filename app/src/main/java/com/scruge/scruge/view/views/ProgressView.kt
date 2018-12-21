package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.format
import com.scruge.scruge.dependencies.view.setHidden
import kotlinx.android.synthetic.main.view_progress.view.*
import kotlin.math.roundToInt

class ProgressView(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val greenLight = ResourcesCompat.getColor(resources, R.color.green_light, null)
    private val purpleLight = ResourcesCompat.getColor(resources, R.color.purple_light, null)
    private val gray = ResourcesCompat.getColor(resources, R.color.gray, null)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_progress, this, true)
    }

    var value = 0.0; set(value) { field = value; updateLayout() }
    var total = 0.0; set(value) { field = value; updateLayout() }
    var firstGoal = 0.0; set(value) { field = value; updateLayout() }
    var prefix = "$"; set(value) { field = value; updateLayout() }
    var suffix = ""; set(value) { field = value; updateLayout() }

    private val reachedGoal get() = value >= firstGoal

    fun updateLayout() {
        val progress = Math.max(0.01, Math.min(1.0, value / firstGoal))
        val showLeftLabel = progress > 0.5

        progress_total.text = "$prefix${total.format(" ")}$suffix"
        progress_current.text = "$prefix${value.format(" ")}$suffix"
        progress_current_right.text = "$prefix${value.format(" ")}$suffix"

        if (reachedGoal) {
            progress_total.setTextColor(greenLight)
            progress_bar_view.setBackgroundColor(greenLight)
        }
        else {
            progress_total.setTextColor(gray)
            progress_bar_view.setBackgroundColor(purpleLight)
        }

        progress_indicator.setHidden(reachedGoal)
        progress_current.setHidden(!showLeftLabel)
        progress_current_right.setHidden(showLeftLabel)

        // bar width
        progress_bar_background.viewTreeObserver.addOnGlobalLayoutListener {
            val width = progress_bar_background.width
            progress_bar_view.layoutParams.width = (width.toDouble() * progress).toInt()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateLayout()
    }
}