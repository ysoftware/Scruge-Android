package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.scruge.scruge.R
import kotlinx.android.synthetic.main.view_button.view.*
import org.jetbrains.anko.runOnUiThread
import java.util.*

class ButtonView(context:Context, attrs:AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    enum class Color(val id:Int) {
        purple(R.drawable.purple_button), green(R.drawable.green_button), gray(R.drawable.gray_button)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_button, this, true)
    }

    private var click:((ButtonView)->Unit)? = null

    fun click(click:((ButtonView)->Unit)) {
        this.click = click

        button_view_button.setOnClickListener {
            if (shouldDebounce && shouldDebounce()) { return@setOnClickListener }
            if (isBusy) { return@setOnClickListener }

            this.click?.let { it(this) }
        }
    }

    var shouldDebounce = true

    var isBusy = false
        set(value) {
            context.runOnUiThread {
                field = value
                button_view_progress.visibility = if (isBusy) View.VISIBLE else View.GONE
                button_view_button.alpha = if (isBusy) 0f else 1f
            }
        }

    var color:Color = Color.purple
        set(value) {
            context.runOnUiThread {
                field = value
                button_background.setBackgroundResource(value.id)
            }
        }

    var title:String = ""
        set(value) {
            context.runOnUiThread {
                field = value
                button_view_button.text = title
            }
        }

    // DEBOUNCE

    private var debounceTime = 650L
    private var lastNavigationTime:Long = 0

    private fun shouldDebounce():Boolean {
        val time = Date().time
        val shouldDebounce = time - lastNavigationTime < debounceTime
        if (!shouldDebounce) {
            lastNavigationTime = time
        }
        return shouldDebounce
    }
}