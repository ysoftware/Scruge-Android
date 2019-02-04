package com.scruge.scruge.model.error

import androidx.core.content.ContextCompat
import com.scruge.scruge.model.error.AuthError.*
import com.scruge.scruge.model.error.BackendError.*
import com.scruge.scruge.model.error.EOSError.*
import com.scruge.scruge.model.error.NetworkingError.*
import com.scruge.scruge.model.error.NetworkingError.unknown
import com.scruge.scruge.model.error.WalletError.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.support.App
import com.scruge.scruge.support.App.Companion.context
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
                val message = if (msg.isBlank()) message(EOSError.unknown) else msg
                val string = R.string.error_eos_transaction_failed.string(message)
                return ErrorMessage(string)
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
                    incorrectEmailLength -> R.string.error_auth_incorrectEmailLength.string()
                    incorrectPasswordLength -> R.string.error_auth_incorrectPasswordLength.string()
                    noToken, invalidToken, userNotFound -> R.string.error_auth_notAuthorized.string()
                    invalidEmail -> R.string.error_auth_invalidEmail.string()
                    accountBlocked -> R.string.error_auth_accountBlocked.string()
                    accountExists -> R.string.error_auth_accountExists.string()
                    incorrectCredentials -> R.string.error_auth_incorrectCredentials.string()
                    denied -> R.string.error_auth_denied.string()
                    emailNotConfirmed -> R.string.error_auth_email_not_confirmed.string()
                }
            }
            (error as? NetworkingError)?.let {
                return when (it) {
                    connectionProblem -> R.string.error_network_connectionProblem.string()
                    unknown -> R.string.error_network_unknown.string()
                }
            }
            (error as? BackendError)?.let {
                return when (it) {
                    notImplemented -> R.string.error_backend_notImplemented.string()
                    invalidResourceId -> R.string.error_backend_invalidResourceId.string()
                    resourceNotFound -> R.string.error_backend_resourceNotFound.string()
                    parsingError -> R.string.error_backend_parsingError.string()
                    BackendError.unknown -> R.string.error_backend_unknown.string()
                    emailSendError -> R.string.error_backend_emailSendError.string()
                    paramsConflict -> R.string.error_backend_paramsConflict.string()
                    replyNotSupported -> R.string.error_backend_replyNotSupported.string()
                }
            }
            (error as? WalletError)?.let {
                return when (it) {
                    incorrectPasscode -> R.string.error_wallet_incorrectPasscode.string()
                    noAccounts -> R.string.error_wallet_noAccounts.string()
                    noKey -> R.string.error_wallet_noKey.string()
                    noSelectedAccount -> R.string.error_wallet_noSelectedAccount.string()
                    selectedAccountMissing -> R.string.error_wallet_selectedAccountMissing.string()
                    WalletError.unknown -> R.string.error_wallet_unknown.string()
                }
            }
            (error as? EOSError)?.let {
                return when (it) {
                    overdrawnBalance -> R.string.error_eos_overdrawnBalance.string()
                    EOSError.unknown -> R.string.error_eos_unknown.string()
                    abiError -> R.string.error_eos_abiError.string()
                    incorrectName -> R.string.error_eos_incorrectName.string()
                    incorrectToken -> R.string.error_eos_incorrectToken.string()
                    actionError -> R.string.error_eos_actionError.string()
                    notSupported -> R.string.error_eos_notSupported.string()
                    eosAccountExists -> R.string.error_eos_eosAccountExists.string()
                    createAccountIpLimitReached -> R.string.error_eos_createAccountIpLimitReached.string()
                    createAccountDailyLimitReached -> R.string.error_eos_createAccountDailyLimitReached.string()
                }
            }
            (error as? GeneralError)?.let {
                if (it.code != 0) {
                    return  R.string.error_general_code.string(it.code.toString())
                }
            }
            (error as? ErrorMessage)?.let {
                return error.message
            }
            return R.string.error_general_unexpected.string()
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
                506 -> createAccountIpLimitReached
                507 -> createAccountDailyLimitReached

                599 -> notSupported

                // http
                400 -> paramsConflict
                404 -> resourceNotFound
                500 -> BackendError.unknown

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