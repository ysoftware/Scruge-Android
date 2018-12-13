package com.scruge.scruge.services.wallet.storage

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey

class LocalAccount(string: String? = null) {

    var keyPair: KeyPair? = null

    val rawPublicKey get() = keyPair?.publicKey?.toString()

    init {
        keyPair = KeyPair(string?.let { EosPrivateKey(it) } ?: EosPrivateKey())
    }

    companion object {

        fun fromBytes(bytes: ByteArray): LocalAccount? {
            val account = LocalAccount()
            val pk = EosPrivateKey(bytes)
            account.keyPair = KeyPair(pk)
            return account
        }
    }

    override fun toString(): String {
        return keyPair?.let { "Account with public Key: ${it.publicKey}}" } ?: "Empty account"
    }
}