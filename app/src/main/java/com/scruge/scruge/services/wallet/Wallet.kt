package com.scruge.scruge.services.wallet

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.scruge.scruge.services.wallet.storage.KeyStore
import com.scruge.scruge.services.wallet.storage.LocalAccount

class Wallet {

    private val storage = KeyStore()

    val hasAccount:Boolean get() {
        return storage.hasAccount
    }

    fun deleteWallet() {
        storage.deleteAccount()
    }

    fun createKey(passcode: String, completion: (LocalAccount?) -> Unit) {
        val account = LocalAccount()
        storage.storeAccount(account, passcode)
        completion(account)
    }

    fun importKey(privateKey: String, passcode: String, completion: (LocalAccount?) -> Unit) {
        val account = LocalAccount(privateKey)
        storage.storeAccount(account, passcode)
        completion(account)
    }

    fun getKey(passcode: String, completion: (LocalAccount?) -> Unit) {
        completion(storage.retrieveBytes(passcode))
    }
}