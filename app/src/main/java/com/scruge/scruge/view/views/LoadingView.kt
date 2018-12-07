package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.scruge.scruge.R
import kotlinx.android.synthetic.main.loading_view.view.*

class LoadingView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {

    init {
        LayoutInflater.from(context).inflate(R.layout.loading_view, this, true)
        showTryAgainButtonIfNeeded()
    }

    interface ErrorViewDelegate {

        fun didTryAgain()
    }

    var delegate: ErrorViewDelegate? = null
        set(value) {
            field = value
            showTryAgainButtonIfNeeded()
        }

    fun setButtonTitle(title: String) {
        loading_view_button.text = title
    }

    fun setMessage(message: String) {
        loading_view_text.text = message
    }

    private fun showTryAgainButtonIfNeeded() {
        loading_view_button.visibility = if (delegate == null) View.GONE else View.VISIBLE
    }
}