package com.scruge.scruge.dependencies.dataformatting

import java.text.DecimalFormat
import java.util.*

/// Rounds to set decimal places, trimming trailing zeros. Separator is used to for fraction value.
fun Double.formatRounding(decimalPlaces:Int = 1,
                          min:Int = 0,
                          separator:String = "."):String {
    val format = "%.${decimalPlaces}f"
    var string = String.format(Locale.ENGLISH, format, this).replace(",", ".").replace(".", separator)

    for (i in min until decimalPlaces) {
        string = string.removeSuffix("0")
    }
    return string.trim().removeSuffix(".")
}

/// Rounds number to integer and uses separator grouping numbers by three.
fun Double.formatDecimal(separator:String = ""):String {
    val systemSeparator = " " // warning: this is not a whitespace character
    val format = "%.4f"
    val temp = String.format(Locale.ENGLISH, format, this).replace(",", ".")
    val formatter = DecimalFormat("###,###,###,###,###")
    val tempValue = temp.toDouble()
    var string = formatter.format(tempValue)

    string += "." + temp.split(".")[1]
    for (i in 0 until 4) {
        string = string.removeSuffix("0")
    }
    return string.trim().removeSuffix(".").replace(systemSeparator, separator)
}

fun Int.formatDecimal(separator: String = ""):String = toDouble().formatDecimal(separator)