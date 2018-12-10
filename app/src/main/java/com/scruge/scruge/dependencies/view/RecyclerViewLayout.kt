package com.scruge.scruge.dependencies.view

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.verticalLayout() {
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
}

fun RecyclerView.horizontalLayout() {
    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
}
