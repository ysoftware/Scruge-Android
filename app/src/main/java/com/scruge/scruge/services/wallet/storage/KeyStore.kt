package com.scruge.scruge.services.wallet.storage

import android.content.Context
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Entity
import com.scruge.scruge.support.App
import java.io.File
import android.content.ContextWrapper
import com.facebook.crypto.keychain.KeyChain
import com.facebook.crypto.keygen.PasswordBasedKeyDerivation

class KeyStore {

    private val entity = Entity.create("Keystore")
    private val fileName = "keystore.dat"

    private val file: File
        get() {
            val cw = ContextWrapper(App.context)
            val directory = cw.getDir("media", Context.MODE_PRIVATE)
            return File(directory, fileName)
        }

    private fun getCrypto(passcode: String): Crypto {
        /// FIXME: SECURITY CHECK REQUIRED
        val keychain = PasswordGeneratedKeyChain(CryptoConfig.KEY_256)
        keychain.setPassword(passcode)
        keychain.salt = byteArrayOf(55, 12, 24, 17, 1, 1, 1, 5, 1, 21, 54, 94, 12, 12, 12, 14)
        keychain.generate()
        return AndroidConceal.get().createDefaultCrypto(keychain)
    }

    // PUBLIC

    val hasAccount:Boolean get() {
        return file.exists()
    }

    fun storeAccount(account: LocalAccount, passcode: String): Boolean {
        val crypto = getCrypto(passcode)
        val bytes = account.keyPair?.privateKey?.bytes ?: return false
        if (!crypto.isAvailable) {
            return false
        }

        val encrypted = try {
            crypto.encrypt(bytes, entity)
        }
        catch (ex:Exception) {
            ex.printStackTrace()
            return false
        }

        file.writeBytes(encrypted)
        return true
    }

    fun retrieveBytes(passcode: String): LocalAccount? {
        val crypto = getCrypto(passcode)
        if (!crypto.isAvailable) {
            return null
        }

        val decrypted = try {
            crypto.decrypt(file.readBytes(), entity)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

        return LocalAccount.fromBytes(decrypted)
    }

    fun deleteAccount():Boolean {
        return file.delete()
    }
}