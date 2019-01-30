package com.scruge.scruge.dependencies.view

import com.scruge.scruge.support.App

fun Int.string():String = App.context.getString(this)

fun Int.string(vararg args:String):String = App.context.getString(this, args)

fun Int.plural(quantity:Int):String
        = App.context.resources.getQuantityString(this, quantity)

fun Int.plural(quantity:Int, vararg args:String):String
        = App.context.resources.getQuantityString(this, quantity, args)