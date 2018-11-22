package com.scruge.scruge.model.error

interface ScrugeError

enum class AuthError:ScrugeError {

    accountBlocked,

    accountExists,

    incorrectCredentials,

    invalidEmail,

    noToken, // token not found but required

    invalidToken, // invalid user token passed

    userNotFound, // user not found for this token

    incorrectEmailLength, // 5 to 254 symbols

    incorrectPasswordLength // 5 to 50 symbols
}

enum class EOSError:ScrugeError {

    overdrawnBalance, // TO-DO: use this maybe?

    unknown,

    abiError
}

enum class BackendError:ScrugeError {

    parsingError,

    resourceNotFound,

    invalidResourceId,

    notImplemented
}

enum class NetworkingError:ScrugeError {

    connectionProblem,

    unknown
}

enum class WalletError:ScrugeError {

    noAccounts,

    noKey,

    incorrectPasscode
}
