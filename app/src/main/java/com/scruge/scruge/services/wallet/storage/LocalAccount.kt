package com.scruge.scruge.services.wallet.storage

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.scruge.scruge.services.Service

/// Representing a secure eos keypair.
class LocalAccount(val publicKey:EosPublicKey) {

    val rawPublicKey get() = publicKey.toString()

    override fun toString(): String {
        return "Account with public Key: $rawPublicKey}"
    }

    fun retrievePrivateKey(passcode:String, completion:(EosPrivateKey?)->Unit) {
        Service.wallet.storage.retrieveKey(passcode, completion)
    }
}