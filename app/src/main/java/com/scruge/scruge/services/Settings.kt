package com.scruge.scruge.services

import android.content.Context
import com.scruge.scruge.support.App
import java.io.File


class Settings {

    enum class Setting {

        selectedAccount, userTokens, nodeUrl
    }

    val defaults = App.context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    inline fun <reified T> get(settins:Setting): T? {
        return when (T::class) {
            String::class -> defaults.getString(settins.name, null) as? T

            else -> null
        }
    }

    fun set(setting:Setting, value:Any) {
        when (value) {
            is String -> defaults.edit().putString(setting.name, value).apply()
            is Int -> defaults.edit().putInt(setting.name, value).apply()
            is Float -> defaults.edit().putFloat(setting.name, value).apply()
            is Boolean -> defaults.edit().putBoolean(setting.name, value).apply()
            is Long -> defaults.edit().putLong(setting.name, value).apply()
        }
    }

    fun set(setting:Setting, value:Set<String>) {
        defaults.edit().putStringSet(setting.name, value).apply()
    }

    fun remove(setting:Setting) {
        defaults.edit().remove(setting.name).apply()
    }

    // special eos account creation check

    private val url = "${App.context.filesDir}/e"

    fun setDidCreateEosAccount() = File(url).writeBytes(ByteArray(1))

    val didCreateEosAccount:Boolean get() = File(url).readBytes().contentEquals(ByteArray(1))
}