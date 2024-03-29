package com.scruge.scruge.dependencies.view

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.setupForVerticalLayout() {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
}

fun RecyclerView.setupForHorizontalLayout() {
    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
}

fun RecyclerView.setupForGridLayout(spanCount:Int) {
    layoutManager = GridLayoutManager(context, spanCount)
}