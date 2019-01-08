package com.scruge.scruge.dependencies.view

import android.view.View
import kotlinx.android.synthetic.main.fragment_wallet.*

fun View.setHidden(value:Boolean) {
    visibility = if (value) View.GONE else View.VISIBLE
}

fun View.toggleHidden() {
    setHidden(visibility == View.VISIBLE)
}