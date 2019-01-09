package com.scruge.scruge.view.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.res.ResourcesCompat
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.dependencies.view.setHidden
import kotlinx.android.synthetic.main.view_progress.view.*

class ProgressView(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val greenLight = ResourcesCompat.getColor(resources, R.color.green_light, null)
    private val purpleLight = ResourcesCompat.getColor(resources, R.color.purple_light, null)
    private val gray = ResourcesCompat.getColor(resources, R.color.gray, null)
    private var needsLayout = true

    init {
        LayoutInflater.from(context).inflate(R.layout.view_progress, this, true)
        progress_bar_background.viewTreeObserver.addOnGlobalLayoutListener {
            if (needsLayout) updateLayout()
        }
    }

    var value = 0.0; set(value) { field = value; setNeedsLayout() }
    var total = 0.0; set(value) { field = value; setNeedsLayout() }
    var firstGoal = 0.0; set(value) { field = value; setNeedsLayout() }
    var prefix = "$"; set(value) { field = value; setNeedsLayout() }
    var suffix = ""; set(value) { field = value; setNeedsLayout() }
    var mode = Mode.funding; set(value) { field = value; setNeedsLayout() }

    // only for mode 'progress'
    var tintColor = purpleLight; set(value) { field = value; setNeedsLayout() }
    var backColor = gray; set(value) { field = value; setNeedsLayout() }
    var showLabels = false; set(value) { field = value; setNeedsLayout() }

    private val reachedGoal get() = value >= firstGoal

    private fun setNeedsLayout() { needsLayout = true }

    private fun updateLayout() {
        needsLayout = false

        val progress = Math.max(0.01, Math.min(1.0, value / firstGoal))
        val showLeftLabel = progress > 0.5
        progress_current.setHidden(!showLeftLabel)
        progress_current_right.setHidden(showLeftLabel)

        progress_current.text = "$prefix${value.formatDecimal(" ")}$suffix"
        progress_current_right.text = "$prefix${value.formatDecimal(" ")}$suffix"
        progress_indicator.setHidden(reachedGoal)

        // bar width
        val width = progress_bar_background.width
        progress_bar_view.layoutParams.width = (width.toDouble() * progress).toInt()

        if (mode == Mode.funding) {
            progress_total.visibility = View.VISIBLE
            progress_total.text = "$prefix${total.formatDecimal(" ")}$suffix"

            if (reachedGoal) {
                progress_total.setTextColor(greenLight)
                progress_bar_view.setBackgroundColor(greenLight)
            }
            else {
                progress_total.setTextColor(gray)
                progress_bar_view.setBackgroundColor(purpleLight)
            }
        }
        else {
            progress_total.visibility = View.GONE
            progress_indicator.setBackgroundColor(backColor)
            progress_bar_view.setBackgroundColor(tintColor)

            if (!showLabels) {
                progress_current.visibility = View.GONE
                progress_current_right.visibility = View.GONE
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateLayout()
        setNeedsLayout()
    }

    enum class Mode { funding, progress }
}