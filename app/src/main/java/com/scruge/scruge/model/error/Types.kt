package com.scruge.scruge.model.error

interface ScrugeError

data class ErrorMessage(val message:String):ScrugeError

enum class GeneralError:ScrugeError {

    unknown, implementationError;

    var code:Int = 0
}

enum class AuthError:ScrugeError {

    accountBlocked,

    accountExists,

    incorrectCredentials,

    invalidEmail,

    noToken, // token not found but required

    invalidToken, // invalid user token passed

    userNotFound, // user not found for this token

    incorrectEmailLength, // 5 to 254 symbols

    incorrectPasswordLength, // 5 to 50 symbols

    denied,

    emailNotConfirmed
}

enum class EOSError:ScrugeError {

    incorrectName,

    overdrawnBalance, // TO-DO: use this maybe?

    unknown,

    incorrectToken,

    abiError,

    actionError,

    notSupported,

    eosAccountExists,

    createAccountIpLimitReached,

    createAccountDailyLimitReached
}

enum class BackendError:ScrugeError {

    parsingError,

    resourceNotFound,

    invalidResourceId,

    notImplemented,

    unknown,

    emailSendError,

    paramsConflict,

    replyNotSupported
}

enum class NetworkingError:ScrugeError {

    connectionProblem,

    unknown
}

enum class WalletError:ScrugeError {

    noAccounts,

    noKey,

    incorrectPasscode,

    noSelectedAccount,

    selectedAccountMissing,

    unknown
}
