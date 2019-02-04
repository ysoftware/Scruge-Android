package com.scruge.scruge.services.api

import com.google.gson.Gson
import com.scruge.scruge.model.error.*
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.model.ActivityListResponse
import com.scruge.scruge.services.api.model.ResultResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> callback(fn: (Throwable?, Response<T>?) -> Unit): Callback<T> {
    return object : Callback<T> {
        override fun onResponse(call: Call<T>, response: retrofit2.Response<T>) = fn(null, response)
        override fun onFailure(call: Call<T>, t: Throwable) = fn(t, null)
    }
}

inline fun <reified T> Call<ResponseBody>.enqueue(crossinline completion: (Result<T>) -> Unit) {
    enqueue(callback { t: Throwable?, r: Response<ResponseBody>? ->
        r?.let {
            val gson = Gson()
            val body = it.body()?.string()

            if (r.code() != 200) {
                completion(Result.failure((ErrorHandler.error(r.code()) ?: BackendError.unknown).wrap()))
                return@let
            }

            if (body == null) {
                Service.api.log("Error: status code: ${it.code()}")
                completion(Result.failure(BackendError.parsingError.wrap()))
                return@let
            }

            // if fails try parsing to ResultResponse
            val result = gson.fromJson(body, ResultResponse::class.java)

            if (result != null && result.result != 0) {
                val error = ErrorHandler.error(result.result)
                if (error != null) {
                    if (error.isAuthenticationFailureError) {
                        Service.tokenManager.removeToken()
                    }
                    Service.api.log("Result: ${ErrorHandler.message(error)}")
                    completion(Result.failure(error.wrap()))
                    return@let
                }
            }

            // try parsing response to T
            val obj: T

            try {
                obj = if (T::class.java == ActivityListResponse::class.java) {
                    CustomParser.parseActivityListResponse(body) as T
                }
                else {
                    gson.fromJson(body, T::class.java)
                }
            }
            catch (exception: Exception) {
                completion(Result.failure(BackendError.parsingError.wrap()))
                return@let
            }

            if (obj != null) {
                Service.api.log(body)
                completion(Result.success(obj))
                return@let
            }

            Service.api.log("Could not parse object")
            completion(Result.failure(BackendError.parsingError.wrap()))
        }
        t?.let {
            Service.api.log(t.localizedMessage)
            completion(Result.failure((ErrorHandler.error(t) ?: NetworkingError.unknown).wrap()))
        }
    })
}
