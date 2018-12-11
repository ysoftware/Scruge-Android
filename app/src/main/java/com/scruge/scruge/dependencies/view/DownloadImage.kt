package com.scruge.scruge.dependencies.view

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.lang.Exception

fun ImageView.setImage(url: Uri?, hideOnFail:Boolean = true) {
    Picasso.Builder(context)
            .listener { _, _, _ ->
                if (hideOnFail) {
                    setImageDrawable(null)
                }
            }
            .build()
            .load(url)
            .fit()
            .centerCrop()
            .into(this)
}

fun ImageView.setImage(url: String?, hideOnFail:Boolean = true) {
    try {
        val uri = Uri.parse(url)
        setImage(uri, hideOnFail)
    }
    catch (ex:Exception) { }
}