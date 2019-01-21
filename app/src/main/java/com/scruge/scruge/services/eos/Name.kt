package com.scruge.scruge.services.eos

class EosName private constructor(private val string: String) {

    override fun toString(): String = string

    companion object {

        fun create(name: String): EosName = from(name)!!

        fun from(name: String): EosName? {
            val string = name.trim()
            return if (string.isValidEOSName()) EosName(string) else null
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other is EosName) { return other.string == string }
        return super.equals(other)
    }

    override fun hashCode(): Int = string.hashCode()
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
