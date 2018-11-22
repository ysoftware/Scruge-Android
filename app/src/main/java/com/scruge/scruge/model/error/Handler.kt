package com.scruge.scruge.model.error

class ErrorHandler {

    companion object {

        fun message(error: ScrugeError):String {
            return "todo" // todo
        }

        fun error(result:Int):ScrugeError? {
            return NetworkingError.unknown
        }
    }
}

val ScrugeError.isAuthenticationFailureError:Boolean get() {
    return when (this) {
        AuthError.userNotFound, AuthError.invalidToken, AuthError.noToken -> true
        else -> false
    }
}