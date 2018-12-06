package com.scruge.scruge.model.error

import kotlin.Throwable

class Error(val error: ScrugeError): Throwable()

fun ScrugeError.wrap():Error {
    return Error(this)
}