package com.scruge.scruge.services.wallet.storage

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.EosPublicKey
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import com.scruge.scruge.support.App
import org.jetbrains.anko.runOnUiThread
import java.io.File

class KeyStore {

    private val SharedKey = "RawPublicKey"
    private val keychain = App.context.getSharedPreferences("KEY_STORE", Context.MODE_PRIVATE)

    private fun file(key: String): File {
        val cw = ContextWrapper(App.context)
        val directory = cw.getDir("media", Context.MODE_PRIVATE)
        return File(directory, key)
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

    fun storeKey(key: EosPrivateKey, passcode: String, completion:(Boolean)->Unit) {
        val rawPublicKey = key.publicKey.toString()
        val f = file(rawPublicKey)

        if (f.exists()) { f.delete() }

        try {
            ECSymmetric().encrypt(key.toString(), passcode, object : ECResultListener {

                override fun onFailure(message: String, e: Exception) {
                    App.context.runOnUiThread {
                        Log.e("CRYPTO", message)
                        e.printStackTrace()
                        completion(false)
                    }
                }

                override fun <T> onSuccess(result: T) {
                    App.context.runOnUiThread {
                        (result as? String)?.let {
                            f.writeText(it)
                            keychain.edit().putString(SharedKey, rawPublicKey).apply()
                            completion(true)
                        } ?: completion(false)
                    }
                }
            })
        }
        catch (ex:Exception) {
            ex.printStackTrace()
            completion(false)
        }
    }

    fun retrieveKey(passcode: String, completion: (EosPrivateKey?)->Unit) {
        val rawPublicKey = getAccount()?.rawPublicKey ?: return completion(null)
        val f = file(rawPublicKey)

        if (!f.exists()) { completion(null) }
        val text = f.readText()

        try {
            ECSymmetric().decrypt(text, passcode, object : ECResultListener {

                override fun onFailure(message: String, e: Exception) {
                    App.context.runOnUiThread {
                        Log.e("CRYPTO", message)
                        e.printStackTrace()
                        completion(null)
                    }
                }

                override fun <T> onSuccess(result: T) {
                    App.context.runOnUiThread {
                        (result as? String)?.let {
                            try {
                                return@runOnUiThread completion(EosPrivateKey(it))
                            }
                            catch (ex: Exception) {
                            }
                        }
                        completion(null)
                    }
                }
            })
        }
        catch (ex: Exception) {
            ex.printStackTrace()
            completion(null)
        }
    }

    fun deleteAccount() {
        try {
            val rawPublicKey = getAccount()?.publicKey?.toString() ?: return
            keychain.edit().remove(SharedKey).apply()
            file(rawPublicKey).delete()
        }
        catch (ex:java.lang.Exception) {}
    }
}