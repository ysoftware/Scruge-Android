package com.scruge.scruge.dependencies.dataformatting

import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

fun datePresent(milliseconds:Long, format:String):String {
    val current = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    return SimpleDateFormat(format, current).format(Date(milliseconds))
}

fun datePresentRelative(milliseconds: Long):String {
    val current = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    return PrettyTime(current).format(Date(milliseconds))
}

fun dateToRelative(milliseconds:Long, future:String = "", past:String = ""):String {
    val current = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    val string = if (Date().time > milliseconds) past else future
    val date = PrettyTime(current).format(Date(milliseconds))
    return "$string $date".trim()
}