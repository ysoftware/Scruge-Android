package com.scruge.scruge.dependencies.view

import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Picasso
import java.lang.Exception

fun ImageView.setImage(url: Uri?, hideOnFail:Boolean = true) {
    Picasso.Builder(context)
            .listener { _, _, ex ->
                if (ex != null && hideOnFail) {
                    setImageDrawable(null)
                    setHidden(false)
                }
                else if (ex == null) {
                    setHidden(false)
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
    catch (ex:Exception) {
        if (hideOnFail) {
            setImageDrawable(null)
            setHidden(true)
        }
    }
}