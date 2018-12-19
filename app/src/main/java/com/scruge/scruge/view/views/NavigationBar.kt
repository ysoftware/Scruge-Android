package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.scruge.scruge.R
import kotlinx.android.synthetic.main.navigation_bar.view.*

class NavigationBar(context: Context, attrs: AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr) {

    interface Delegate {
        fun navigationBarDidClickBackButton(instance:NavigationBar)
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        LayoutInflater.from(context).inflate(R.layout.view_button, this, true)
    }

    var delegate:Delegate? = null
        set(value) {
            field = value

        }

    var title = "Scruge"
        set(value) {
            field = value
            navigation_bar_title.text = value
        }

    var isBackButtonHidden = true
        set(value) {
            field = value
            navigation_bar_back_button.visibility = if (value) View.GONE else View.VISIBLE
        }


}