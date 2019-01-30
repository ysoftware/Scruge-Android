package com.scruge.scruge.dependencies.view

import com.scruge.scruge.support.App

fun Int.string():String = App.context.getString(this)

fun Int.string(vararg args:Any):String = App.context.getString(this, args)