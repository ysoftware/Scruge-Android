package com.scruge.scruge.dependencies.view

import android.view.View

fun View.setHidden(value:Boolean) {
    visibility = if (value) View.GONE else View.VISIBLE
}

val View.isHidden:Boolean get() = visibility == View.GONE

fun View.toggleHidden():Boolean {
    setHidden(visibility == View.VISIBLE)
    return isHidden
}