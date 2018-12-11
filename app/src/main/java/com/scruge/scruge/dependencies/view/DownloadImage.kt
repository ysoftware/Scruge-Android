package com.scruge.scruge.dependencies.view

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageView.setImage(url: Uri?, hideOnFail:Boolean = true) {
    Picasso.get().load(url).fit().centerCrop().into(this)
}

fun ImageView.setImage(url: String?, hideOnFail:Boolean = true) {
    Picasso.get().load(url).fit().centerCrop().into(this)
}