package com.scruge.scruge.dependencies.view

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.alert(message:String, completion:(()->Unit)? = null) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    completion?.invoke()
}