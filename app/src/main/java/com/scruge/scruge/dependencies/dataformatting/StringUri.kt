package com.scruge.scruge.dependencies.dataformatting

import android.net.Uri

fun String.toUriOrNull(): Uri? {
    return try {
        Uri.parse(this)
    }
    catch (e:Exception) {
        return null
    }
}