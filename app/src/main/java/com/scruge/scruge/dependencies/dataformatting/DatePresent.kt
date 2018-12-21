package com.scruge.scruge.dependencies.dataformatting

import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

fun datePresent(milliseconds:Long, format:String):String {
    return SimpleDateFormat(format, Locale.ENGLISH).format(Date(milliseconds))
}

fun dateToRelative(milliseconds:Long, future:String = "", past:String = ""):String {
    val string = if (Date().time > milliseconds) past else future
    val date = PrettyTime(Locale.ENGLISH).format(Date(milliseconds))
    return "$string $date".trim()
}