package com.scruge.scruge.services

import android.content.Context
import com.scruge.scruge.support.App

class TokenManager {

    private val AUTH_TOKEN = "auth_token"

    private val keychain = App.context.getSharedPreferences(AUTH_TOKEN, Context.MODE_PRIVATE)

    fun save(token:String) {
        keychain.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun removeToken() {
        keychain.edit().remove(AUTH_TOKEN).apply()
    }

    fun getToken():String? {
        return keychain.getString(AUTH_TOKEN, null)
    }

    val hasToken get() = getToken() != null
}