package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import com.scruge.scruge.R
import kotlinx.android.synthetic.main.view_button.view.*

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
        button_view_button.setOnClickListener { this.click?.let { it(this) }}
    }

    var color:Color = Color.purple
        set(value) {
            field = value
            button_background.setBackgroundResource(value.id)
        }

    var title:String = "invest"
        set(value) {
            field = value
            button_view_button.text = title
        }
}