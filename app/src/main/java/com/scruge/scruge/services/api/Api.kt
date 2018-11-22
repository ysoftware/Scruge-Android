package com.scruge.scruge.services.api

import com.scruge.scruge.services.api.model.AuthRequest
import com.scruge.scruge.services.api.model.EmailRequest
import com.scruge.scruge.services.api.model.LoginResponse
import com.scruge.scruge.services.api.model.ResultResponse
import com.scruge.scruge.services.network.enqueue
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class Api {

    enum class Environment(url: String) {

        test("http://testapi.scruge.world/"),

        prod("http://api.scruge.world/"),

        dev("http://176.213.156.167/")
    }

    // Initialization

    var instance = getInstance(Environment.prod)

    fun getInstance(environment: Environment):BackendApi {
        return Retrofit.Builder()
                .baseUrl("http://testapi.scruge.world/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BackendApi::class.java)
    }

    fun login(email: String, password: String, completion: (LoginResponse?, Throwable?) -> Unit) {
        instance.login(AuthRequest(email, password)).enqueue(completion)
    }

    fun signUp(email: String, password: String, completion: (ResultResponse?, Throwable?) -> Unit) {
        instance.signUp(AuthRequest(email, password)).enqueue(completion)
    }

    fun checkEmail(email:String, completion: (ResultResponse?, Throwable?) -> Unit) {
        instance.checkEmail(EmailRequest(email)).enqueue(completion)
    }
}

interface BackendApi {

    // AUTH

    @POST("auth/login")
    fun login(@Body request: AuthRequest): Call<LoginResponse>

    @POST("auth/register")
    fun signUp(@Body request: AuthRequest): Call<ResultResponse>

    @POST("auth/check_email")
    fun checkEmail(@Body request: EmailRequest): Call<ResultResponse>


}