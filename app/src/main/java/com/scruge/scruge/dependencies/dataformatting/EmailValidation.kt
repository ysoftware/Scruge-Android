package com.scruge.scruge.dependencies.dataformatting

fun String.isValidEmail():Boolean {
    if (length !in 5 until 255) { return false } // for our app needs

    return true // todo implement
}