package com.scruge.scruge.dependencies.view

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.scruge.scruge.model.error.ScrugeError

fun Fragment.alert(message:String, completion:(()->Unit)? = null) {
    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    completion?.invoke()
}

fun Fragment.alert(error:Throwable) {
    alert("todo: throwable")
}

fun Fragment.alert(error:ScrugeError) {
    alert("todo: scruge error")
}

fun ask(question:String, completion: (Boolean) -> Unit) {
    completion(true) // todo
}