package com.scruge.scruge.dependencies

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

interface Codable

@JvmSuppressWildcards
fun Codable.toMap(): Map<String, Any> {

    val gson = GsonBuilder().registerTypeAdapter(object : TypeToken<Map<String, Any>>() {}.type,
                                                 MapDeserializerDoubleAsIntFix()).create()
    val json = gson.toJson(this)
    val map: Map<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
    Log.e("PARSE", json)
    Log.e("PARSE", map.toString())
    return map
}