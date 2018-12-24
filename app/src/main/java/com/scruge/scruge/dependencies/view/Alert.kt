package com.scruge.scruge.dependencies.view

import android.app.AlertDialog
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.scruge.scruge.model.error.ScrugeError
import android.content.DialogInterface

fun Fragment.alert(message:String, completion:(()->Unit)? = null) {
    activity?.runOnUiThread {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        completion?.invoke()
    }
}

fun Fragment.alert(error:Throwable) {
    alert(error.localizedMessage)
}

fun Fragment.alert(error:ScrugeError) {
    alert(error.toString())
}

fun Fragment.ask(title:String = "Attention", question:String, completion: (Boolean) -> Unit) {
    val context = activity ?: return
    val dialogClickListener = DialogInterface.OnClickListener { _, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> completion(true)
            DialogInterface.BUTTON_NEGATIVE -> completion(false)
        }
    }

    AlertDialog.Builder(context)
            .setMessage(question)
            .setTitle(title)
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
}