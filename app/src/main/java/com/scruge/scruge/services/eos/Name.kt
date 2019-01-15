package com.scruge.scruge.services.eos

class EosName private constructor(private val string: String) {

    override fun toString(): String = string

    companion object {

        fun from(name:String):EosName? = if (name.isValidEOSName()) EosName(name) else null
    }
}

fun String.toEosName():EosName? = EosName.from(this)

fun String.isValidEOSName():Boolean {
    if (length > 12 || length == 0 || this == ".") {
        return false
    }

    val charPool = ".12345abcdefghijklmnopqrstuvwxyz".toCharArray()
    for (s in toCharArray()) {
        if (!charPool.contains(s)) {
            return false
        }
    }

    return true
}
