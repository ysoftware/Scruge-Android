package com.scruge.scruge.dependencies.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.method.PasswordTransformationMethod
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.scruge.scruge.R
import com.scruge.scruge.model.error.ErrorHandler
import com.scruge.scruge.model.error.ScrugeError

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
    alert(ErrorHandler.message(error))
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
            .setPositiveButton(R.string.label_yes, dialogClickListener)
            .setNegativeButton(R.string.label_no, dialogClickListener)
            .show()
}

// input

fun Fragment.askForInput(title:String = "",
                         question:String,
                         placeholder:String = "",
                         isSecure:Boolean = false,
                         actionTitle:String = R.string.label_ok.string(),
                         initial:String = "",
                         completion: (String?) -> Unit) {
    activity?.askForInput(title, question, placeholder, isSecure, actionTitle, initial, completion)
}

fun Activity.askForInput(title:String = "",
                         question:String,
                         placeholder:String = "",
                         isSecure:Boolean = false,
                         actionTitle:String = R.string.label_ok.string(),
                         initial:String = "",
                         completion: (String?) -> Unit) {

    MaterialDialog(this).show {

        val inputType = if (isSecure) EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
            else EditorInfo.TYPE_TEXT_VARIATION_NORMAL

        title(text = title)
        message(text = question)

        input(hint = placeholder,
              inputType = inputType,
              prefill = initial) { _, text ->
            completion(text.toString())
        }
        negativeButton()
        positiveButton(text = actionTitle)

        if (isSecure) {
            getInputField().transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }
}