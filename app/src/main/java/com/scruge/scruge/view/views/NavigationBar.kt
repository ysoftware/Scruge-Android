package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.scruge.scruge.R
import kotlinx.android.synthetic.main.view_navigation_bar.view.*

data class NavigationBarButton(val imageResource: Int, val tap: () -> Unit)

class NavigationBar(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        RelativeLayout(context, attrs, defStyleAttr) {

    interface Delegate {
        fun navigationBarDidClickBackButton(instance: NavigationBar)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_navigation_bar, this, true)
        navigation_bar_back_button.setOnClickListener { delegate?.navigationBarDidClickBackButton(this) }
    }

    var delegate: Delegate? = null

    var isBackButtonHidden = true
        set(value) {
            field = value
            navigation_bar_back_button.visibility = if (value) View.GONE else View.VISIBLE
        }

    fun setTitle(value: String): NavigationBar {
        navigation_bar_title.text = value
        return this
    }

    fun setupRightActionButton(button:NavigationBarButton?): NavigationBar {
        if (button != null) {
            navigation_bar_button.visibility = View.VISIBLE
            navigation_bar_button_image.setImageResource(button.imageResource)
            navigation_bar_button.setOnClickListener { button.tap() }
        }
        else {
            navigation_bar_button.visibility = View.GONE
        }
        return this
    }

    fun setupRightActionButton2(button:NavigationBarButton?): NavigationBar {
        if (button != null) {
            navigation_bar_button_2.visibility = View.VISIBLE
            navigation_bar_button_2_image.setImageResource(button.imageResource)
            navigation_bar_button_2.setOnClickListener { button.tap() }
        }
        else {
            navigation_bar_button_2.visibility = View.GONE
        }
        return this
    }
}