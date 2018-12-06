package com.scruge.scruge.services.network

import android.util.Log
import com.google.gson.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.gson.internal.LinkedTreeMap
import com.scruge.scruge.model.error.*
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.model.ResultResponse
import java.lang.reflect.Type
import java.util.*

fun <T> callback(fn: (Throwable?, Response<T>?) -> Unit): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) = fn(null, response)
        override fun onFailure(call: Call<T>, t: Throwable) = fn(t, null)
    }
}

inline fun <reified T> Call<ResponseBody>.enqueue(crossinline completion: (Result<T>) -> Unit) {
    enqueue(callback { t:Throwable?, r:Response<ResponseBody>? ->
        r?.let {
            val gson = Gson()
            val body = it.body()?.string()

            if (body == null) {
                log("Error: status code: ${it.code()}")
                completion(Result.failure(BackendError.parsingError.wrap()))
                return@let
            }

            // if fails try parsing to ResultResponse
            val result = gson.fromJson(body, ResultResponse::class.java)

            if (result != null) {
                val error = ErrorHandler.error(result.result)
                if (error != null) {
                    if (error.isAuthenticationFailureError) {
                        Service.tokenManager.removeToken()
                    }
                    completion(Result.failure(error.wrap()))
                    return@let
                }
            }

            // try parsing response to T
            val obj:T = gson.fromJson(body, T::class.java)

            if (obj != null) {
                log(body)
                completion(Result.success(obj))
                return@let
            }

            log("Could not parse object")
            completion(Result.failure(BackendError.parsingError.wrap()))
        }
        t?.let {
            log(t.localizedMessage)
            completion(Result.failure(getNetworkError(t).wrap()))
        }
    })
}

fun log(message:String) {
    Log.e("backend", message)
}

fun handleResultError(t: Throwable):ScrugeError {
    return NetworkingError.unknown
}

fun getNetworkError(t: Throwable):ScrugeError {
    return NetworkingError.unknown
}
