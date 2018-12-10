package com.scruge.scruge.model.error

class ErrorHandler {

    companion object {

        fun message(throwable: Throwable):String {
            return message(NetworkingError.unknown) // todo
        }

        fun message(error: ScrugeError):String {
            return "todo" // todo
        }

        fun error(throwable:Throwable):ScrugeError? {
            return NetworkingError.unknown // todo
        }

        fun error(result:Int):ScrugeError? {
            return NetworkingError.unknown // todo
        }
    }
}

val ScrugeError.isAuthenticationFailureError:Boolean get() {
    return when (this) {
        AuthError.userNotFound, AuthError.invalidToken, AuthError.noToken -> true
        else -> false
    }
}