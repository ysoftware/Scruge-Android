package com.scruge.scruge.services.wallet.storage

import android.content.Context
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.crypto.Crypto
import com.facebook.crypto.CryptoConfig
import com.facebook.crypto.Entity
import com.scruge.scruge.support.App
import java.io.File
import android.content.ContextWrapper
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import kotlin.random.Random

class KeyStore {

    private val SharedKey = "RawPublicKey"
    private val keychain = App.context.getSharedPreferences("KEY_STORE", Context.MODE_PRIVATE)
    private val entity = Entity.create("Keystore")

    private fun file(key: String): File {
        val cw = ContextWrapper(App.context)
        val directory = cw.getDir("media", Context.MODE_PRIVATE)
        return File(directory, key)
    }

    private fun getCrypto(passcode: String, salt:String): Crypto {
        /// FIXME: SECURITY CHECK REQUIRED
        val keychain = PasswordGeneratedKeyChain(CryptoConfig.KEY_256)
        keychain.setPassword(passcode)
        keychain.salt = salt.toByteArray()
        keychain.generate()
        return AndroidConceal.get().createDefaultCrypto(keychain)
    }

    // PUBLIC

    fun getAccount():LocalAccount? {
        val rawPublicKey = keychain.getString(SharedKey, null) ?: return null
        return LocalAccount(EosPublicKey(rawPublicKey))
    }

    val hasKey:Boolean get() {
        val rawPublicKey = getAccount()?.rawPublicKey ?: return false
        return file(rawPublicKey).exists() && keychain.contains(SharedKey)
    }

    fun storeKey(key: EosPrivateKey, passcode: String): Boolean {
        val rawPublicKey = key.publicKey.toString()
        val salt = randomString(16)

        val crypto = getCrypto(passcode, salt)
        if (!crypto.isAvailable) { return false }

        val encrypted = try {
            crypto.encrypt(key.bytes, entity)
        }
        catch (ex:Exception) {
            ex.printStackTrace()
            return false
        }

        file(rawPublicKey).writeBytes(encrypted)
        file("iv_$rawPublicKey").writeText(salt)
        keychain.edit().putString(SharedKey, rawPublicKey).apply()
        return true
    }

    fun retrieveKey(passcode: String): EosPrivateKey? {
        val rawPublicKey = getAccount()?.rawPublicKey ?: return null

        val salt = file("iv_$rawPublicKey").readText()
        if (salt.length != 16) { return null }

        val crypto = getCrypto(passcode, salt)
        if (!crypto.isAvailable) {
            return null
        }

        val decrypted = try {
            crypto.decrypt(file(rawPublicKey).readBytes(), entity)
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            return null
        }

        return EosPrivateKey(decrypted)
    }

    fun deleteAccount() {
        val rawPublicKey = getAccount()?.publicKey?.toString() ?: return
        keychain.edit().remove(SharedKey).apply()
        file("iv_$rawPublicKey").delete()
        file(rawPublicKey).delete()
    }

    private fun randomString(length:Int): String {
        val charPool = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
                .toCharArray().map { it.toString() }
        return (1..length).map { Random.nextInt(0, charPool.size) }
                .joinToString("", transform = charPool::get)

    }
}