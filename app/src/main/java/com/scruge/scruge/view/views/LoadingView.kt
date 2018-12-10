package com.scruge.scruge.view.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.scruge.scruge.R
import com.scruge.scruge.model.ViewState
import kotlinx.android.synthetic.main.loading_view.view.*

class LoadingView(context:Context, attrs:AttributeSet?, defStyleAttr:Int):
        RelativeLayout(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

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

    var state: ViewState = ViewState.loading
        set(value) {
            field = value

            when (value) {
                ViewState.loading -> {
                    visibility = View.VISIBLE
                    loading_indicator.visibility = View.VISIBLE
                    error_view.visibility = View.GONE
                }
                ViewState.error -> {
                    visibility = View.VISIBLE
                    loading_indicator.visibility = View.GONE
                    error_view.visibility = View.VISIBLE
                    setMessage(field.errorMessage)
                }
                ViewState.ready -> {
                    visibility = View.GONE
                }
            }
        }
}