package com.scruge.scruge.services.api

import com.scruge.scruge.model.error.AuthError
import com.scruge.scruge.model.error.ScrugeError
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.model.*
import com.scruge.scruge.services.network.enqueue
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class Api {

    enum class Environment(val url: String) {

        test("http://testapi.scruge.world/"),

        prod("http://api.scruge.world/"),

        dev("http://176.213.156.167/")
    }

    // Initialization

    var instance = instance(Environment.test)

    fun instance(environment: Environment):BackendApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
                .baseUrl(environment.url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(BackendApi::class.java)
    }

    // WALLET

    fun createAccount(accountName:String, publicKey:String, completion: (Result<LoginResponse>) -> Unit) {

    }

    // AUTH

    fun login(email: String, password: String, completion: (LoginResponse?, ScrugeError?) -> Unit) {
        instance.login(AuthRequest(email, password)).enqueue(completion)
    }

    fun signUp(email: String, password: String, completion: (ResultResponse?, ScrugeError?) -> Unit) {
        instance.signUp(AuthRequest(email, password)).enqueue(completion)
    }

    fun checkEmail(email:String, completion: (ResultResponse?, ScrugeError?) -> Unit) {
        instance.checkEmail(EmailRequest(email)).enqueue(completion)
    }

    // PROFILE

    fun getUserId(completion: (UserIdResponse?, ScrugeError?) -> Unit) {
        val token = Service.tokenManager.getToken()
        if (token != null) {
            instance.getUserId(token).enqueue(completion)
        }
        else {
            completion(null, AuthError.noToken)
        }
    }
}
