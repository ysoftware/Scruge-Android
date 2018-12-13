package com.scruge.scruge.dependencies.dataformatting

import java.lang.Math.min

fun Double.formatRounding(decimalPlaces:Int = 1,
                          min:Int = 0,
                          separator:String = ""):String {
    // todo
    val digits = min(decimalPlaces, min)
    val format = "%.${digits}f"
    return String.format(format, this)
}

fun Double.format():String {
    // todo
    return "$this"
}