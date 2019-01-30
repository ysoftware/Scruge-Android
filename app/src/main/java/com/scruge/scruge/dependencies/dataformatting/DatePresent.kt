package com.scruge.scruge.dependencies.dataformatting

import android.content.res.Resources
import android.os.Build
import androidx.core.os.ConfigurationCompat
import com.scruge.scruge.support.App
import org.ocpsoft.prettytime.PrettyTime
import java.text.SimpleDateFormat
import java.util.*

fun datePresent(milliseconds:Long, format:String):String {
    return SimpleDateFormat(format, Locale.ENGLISH).format(Date(milliseconds))
}

// todo plurals
fun dateToRelative(milliseconds:Long, future:String = "", past:String = ""):String {
    val current = ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
    val string = if (Date().time > milliseconds) past else future
    val date = PrettyTime(current).format(Date(milliseconds))
    return "$string $date".trim()
}