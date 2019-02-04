package com.scruge.scruge.dependencies.serialization

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

interface Codable

@JvmSuppressWildcards
fun Codable.toMap(): Map<String, Any> {

    val gson = GsonBuilder().registerTypeAdapter(object : TypeToken<Map<String, Any>>() {}.type,
                                                 MapDeserializerDoubleAsIntFix()).create()
    val json = gson.toJson(this)
    val map: Map<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
    return map
}