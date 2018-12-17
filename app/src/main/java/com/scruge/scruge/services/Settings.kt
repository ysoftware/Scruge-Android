package com.scruge.scruge.services

import android.content.Context
import com.scruge.scruge.support.App


class Settings {

    enum class Setting {

        selectedAccount
    }

    val defaults = App.context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    inline fun <reified T> get(settins:Setting): T? {
        return when (T::class) {
            String::class -> defaults.getString(settins.name, null) as? T

            else -> null
        }
    }

    fun set(setting:Setting, value:Any) {
        if (value is String) {
            defaults.edit().putString(setting.name, value).apply()
        }
        else if (value is Int) {
            defaults.edit().putInt(setting.name, value).apply()
        }
        else if (value is Float) {
            defaults.edit().putFloat(setting.name, value).apply()
        }
        else if (value is Boolean) {
            defaults.edit().putBoolean(setting.name, value).apply()
        }
        else if (value is Long) {
            defaults.edit().putLong(setting.name, value).apply()
        }
    }

    fun set(setting:Setting, value:Set<String>) {
        defaults.edit().putStringSet(setting.name, value).apply()
    }

    fun remove(setting:Setting) {
        defaults.edit().remove(setting.name).apply()
    }
}