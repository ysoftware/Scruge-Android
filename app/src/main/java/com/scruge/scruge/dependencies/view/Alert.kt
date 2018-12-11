package com.scruge.scruge.dependencies.view

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.scruge.scruge.model.error.ScrugeError

fun Fragment.alert(message:String, completion:(()->Unit)? = null) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    completion?.invoke()
}

fun Fragment.alert(error:Throwable) {

}

fun Fragment.alert(error:ScrugeError) {

}