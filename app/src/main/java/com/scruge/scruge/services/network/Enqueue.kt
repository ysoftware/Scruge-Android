package com.scruge.scruge.services.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> callback(fn: (Throwable?, Response<T>?) -> Unit): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) = fn(null, response)
        override fun onFailure(call: Call<T>, t: Throwable) = fn(t, null)
    }
}

fun <T> Call<T>.enqueue(func: (T?, Throwable?) -> Unit) {
    enqueue(callback({ t, r ->
                          r?.let { func(r.body(), null) }
                          t?.let { func(null, t) }
                      }))
}