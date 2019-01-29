package com.scruge.scruge.model.error

import com.scruge.scruge.model.error.AuthError.*
import com.scruge.scruge.model.error.BackendError.*
import com.scruge.scruge.model.error.EOSError.*
import com.scruge.scruge.model.error.NetworkingError.*
import com.scruge.scruge.model.error.NetworkingError.unknown
import com.scruge.scruge.model.error.WalletError.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.Exception


class ErrorHandler {

    companion object {

        @Suppress("UNCHECKED_CAST")
        fun parse(eosError:String?):ErrorMessage? {
            try {
                val map: Map<String, Any>
                        = Gson().fromJson(eosError, object : TypeToken<Map<String, Any>>() {}.type)
                val error = map["error"] as? Map<String, Any> ?: return null
                val details = error["details"] as? List<Map<String, Any>> ?: return null
                val msg = details.mapNotNull { it["message"] as? String }
                        .joinToString("")
                        .replace("assertion failure with message:", "")
                        .replace("pending console output:", "")
                        .trim()
                return ErrorMessage(msg)
            }
            catch (ex:Exception) { }
            return null
        }

        fun message(throwable: Throwable?):String {
            return message(error(throwable))
        }

        fun message(error: ScrugeError?):String {
            (error as? AuthError)?.let {
                return when (it) {
                    incorrectEmailLength -> "Email should be longer than 5 and shorter than 254 symbols."
                    incorrectPasswordLength -> "Password should be longer than 5 and shorter than 50 symbols."
                    noToken, invalidToken, userNotFound -> "This action requires authentication. Please, sign in with your Scruge account."
                    invalidEmail -> "Incorrectly formatted email"
                    accountBlocked -> "This account was blocked"
                    accountExists -> "User already exists"
                    incorrectCredentials -> "Incorrect credentials"
                    denied -> "Access is restricted"
                }
            }
            (error as? NetworkingError)?.let {
                return when (it) {
                    connectionProblem -> "Unable to connect to the server"
                    unknown -> "Unknown network error"
                }
            }
            (error as? BackendError)?.let {
                return when (it) {
                    notImplemented -> "Not implemented"
                    invalidResourceId -> "Malformed request"
                    resourceNotFound -> "Nothing was found for this request"
                    parsingError -> "Unexpected server response"
                    BackendError.unknown -> "Unexpected server error"
                    emailSendError -> "Unable to send email"
                    paramsConflict -> "Parameters conflict with each other"
                    replyNotSupported -> "Replying to this is not allowed"
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
                    overdrawnBalance -> "Overdrawn balance"
                    EOSError.unknown -> "Unknown error"
                    abiError -> "Incorrect transaction format"
                    incorrectName -> "Incorrect name: it can only contain letters, numbers from 1 to 5 and a dot"
                    incorrectToken -> "Incorrect token input"
                    actionError -> "Server was unable to complete blockchain transaction, please try again"
                    notSupported -> "EOS: Not supported"
                    eosAccountExists -> "EOS account with this name already exists"
                }
            }
            (error as? GeneralError)?.let {
                if (it.code != 0) {
                    return "Error ${it.code}"
                }
            }
            (error as? ErrorMessage)?.let {
                return error.message
            }
            return "Unexpected error"
        }

        fun error(throwable:Throwable?):ScrugeError? {
            if (throwable == null) { return null }
            (throwable as? Error)?.let {
                return it.error
            }
            throwable.message?.let {
                if (it.contains("resolve host")) {
                    return NetworkingError.connectionProblem
                }
            }
            return GeneralError.unknown
        }

        fun error(result: Int): ScrugeError? {
            return when (result) {
                0 -> null

                // common
                10 -> invalidToken
                11 -> invalidResourceId
                12 -> resourceNotFound
                13 -> userNotFound
                14 -> denied
                15 -> paramsConflict
                16 -> replyNotSupported

                // auth
                101 -> incorrectEmailLength
                102 -> invalidEmail
                103 -> incorrectPasswordLength
                104 -> noToken
                105 -> accountExists
                106 -> incorrectCredentials
                107 -> accountBlocked

                // eos
                501 -> incorrectName
                502 -> eosAccountExists
                505 -> actionError
                599 -> notSupported

                // http
                400 -> paramsConflict
                404 -> resourceNotFound

                // special
                999 -> notImplemented
                else -> {
                    val error = GeneralError.unknown
                    error.code = result
                    error
                }
            }
        }
    }
}

val ScrugeError.isAuthenticationFailureError:Boolean
    get() = when (this) {
        userNotFound, invalidToken, noToken -> true
        else -> false
    }