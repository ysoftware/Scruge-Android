package com.scruge.scruge.services.wallet

import com.memtrip.eos.core.crypto.EosPrivateKey
import com.scruge.scruge.services.wallet.storage.KeyStore
import com.scruge.scruge.services.wallet.storage.LocalAccount

class Wallet {

    val storage = KeyStore()

    fun getAccount():LocalAccount? {
        return storage.getAccount()
    }

    val hasAccount:Boolean get() {
        return storage.hasKey
    }

    fun deleteWallet() {
        storage.deleteAccount()
    }

//    fun createKey(passcode: String, completion: (LocalAccount?) -> Unit) {
//        val privateKey = EosPrivateKey()
//        storage.storeKey(privateKey, passcode) {
//            completion(if (it) LocalAccount(privateKey.publicKey) else null)
//        }
//    }

    fun importKey(rawPrivateKey: String, passcode: String, completion: (LocalAccount?) -> Unit) {
        val privateKey = EosPrivateKey(rawPrivateKey)
        storage.storeKey(privateKey, passcode) {
            completion(if (it) LocalAccount(privateKey.publicKey) else null)
        }
    }
}