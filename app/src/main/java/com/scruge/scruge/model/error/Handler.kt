package com.scruge.scruge.model.error

import com.scruge.scruge.model.error.AuthError.*
import com.scruge.scruge.model.error.BackendError.*
import com.scruge.scruge.model.error.NetworkingError.*
import com.scruge.scruge.model.error.NetworkingError.unknown
import com.scruge.scruge.model.error.WalletError.*

class ErrorHandler {

    companion object {

        fun message(throwable: Throwable):String {
            return message(error(throwable))
        }

        fun message(error: ScrugeError?):String {
            (error as? AuthError)?.let {
                return when (it) {
                    incorrectEmailLength -> "Email should be longer than 5 and shorter than 254 symbols."
                    incorrectPasswordLength -> "Password should be longer than 5 and shorter than 50 symbols."
                    noToken, invalidToken, userNotFound -> "Authentication failed. Please try signing in again."
                    invalidEmail -> "Incorrectly formatted email"
                    accountBlocked -> "This account was blocked"
                    accountExists -> "User already exists"
                    incorrectCredentials -> "Incorrect credentials"
                }
            }
            (error as? NetworkingError)?.let {
                return when (it) {
                    connectionProblem -> "Unable to connect to the server"
                    unknown -> "Unknown error"
                }
            }
            (error as? BackendError)?.let {
                return when (it) {
                    notImplemented -> "Not implemented"
                    invalidResourceId -> "Malformed request"
                    resourceNotFound -> "Nothing was found for this request"
                    parsingError -> "Unexpected server response"
                    BackendError.unknown -> "Unexpected server error"
                }
            }
            (error as? WalletError)?.let {
                return when (it) {
                    incorrectPasscode -> "Incorrect passcode"
                    noAccounts -> "No accounts are associated with imported public key"
                    noKey -> "You have no keys in your wallet"
                    noSelectedAccount -> "You did not verify any EOS accounts"
                    selectedAccountMissing -> "Your verified account is not accessible with imported EOS key"
                    WalletError.unknown -> "Unexpected wallet error"
                }
            }
            (error as? EOSError)?.let {
                return when (it) {
                    EOSError.overdrawnBalance -> "Overdrawn balance"
                    EOSError.unknown -> "Unknown error"
                    EOSError.abiError -> "Incorrect transaction format"
                }
            }
            return "Unexpected error"
        }

        fun error(throwable:Throwable?):ScrugeError? {
            if (throwable == null) { return null }
            (throwable as? Error)?.let {
                return it.error
            }
            return unknown // todo
        }

        fun error(result: Int): ScrugeError? {
            if (result == 0) { return null }

            return when (result) {
                10 -> invalidToken
                11 -> invalidResourceId
                12 -> resourceNotFound
                13 -> userNotFound

                // auth
                101 -> incorrectEmailLength
                102 -> invalidEmail
                103 -> incorrectPasswordLength
                104 -> noToken
                105 -> accountExists
                106 -> incorrectCredentials
                107 -> accountBlocked

                // special
                999 -> notImplemented
                else -> unknown
            }
        }
    }
}

val ScrugeError.isAuthenticationFailureError:Boolean
    get() = when (this) {
        userNotFound, invalidToken, noToken -> true
        else -> false
    }