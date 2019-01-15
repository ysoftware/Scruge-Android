package com.scruge.scruge.dependencies.view

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.scruge.scruge.model.error.ScrugeError
import android.content.DialogInterface
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import com.scruge.scruge.model.error.ErrorHandler

// alert

fun Context.alert(message:String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.alert(message:String, completion:(()->Unit)? = null) {
    activity?.runOnUiThread {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        completion?.invoke()
    }
}

fun Fragment.alert(error:Throwable) {
    alert(error.localizedMessage ?: "Error with no description")
}

fun Fragment.alert(error:ScrugeError) {
    alert(ErrorHandler.message(error))
}

// ask

fun Fragment.ask(title:String = "", question:String, completion: (Boolean) -> Unit)
    = context?.ask(title, question, completion)

fun Context.ask(title:String = "", question:String, completion: (Boolean) -> Unit) {
    val dialogClickListener = DialogInterface.OnClickListener { _, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> completion(true)
            DialogInterface.BUTTON_NEGATIVE -> completion(false)
        }
    }

    AlertDialog.Builder(this)
            .setMessage(question)
            .setTitle(title)
            .setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener)
            .show()
}

// input

fun Fragment.askForInput(title:String = "",
                         question:String,
                         placeholder:String = "",
                         isSecure:Boolean = false,
                         actionTitle:String = "OK",
                         completion: (String?) -> Unit) = context?.askForInput(title,
                                                                               question,
                                                                               placeholder,
                                                                               isSecure,
                                                                               actionTitle,
                                                                               completion)

fun Context.askForInput(title:String = "",
                         question:String,
                         placeholder:String = "",
                         isSecure:Boolean = false,
                         actionTitle:String = "OK",
                         completion: (String?) -> Unit) {
    val editText = EditText(this)
    val layout = RelativeLayout(this)
    layout.addView(editText)
    val pad = Dimension.dp(15).px
    layout.setPadding(pad, 0, 0, 0)

    editText.inputType = if (isSecure) EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
        else EditorInfo.TYPE_TEXT_VARIATION_NORMAL
    editText.hint = placeholder

    val dialogClickListener = DialogInterface.OnClickListener { _, which ->
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> completion(editText.text.toString())
            DialogInterface.BUTTON_NEGATIVE -> completion(null)
        }
    }

    AlertDialog.Builder(this)
            .setMessage(question)
            .setTitle(title)
            .setPositiveButton(actionTitle, dialogClickListener)
            .setNegativeButton("Cancel", dialogClickListener)
            .setView(layout)
            .show()

    showKeyboard(editText)
}