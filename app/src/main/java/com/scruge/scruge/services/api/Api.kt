package com.scruge.scruge.services.api

import android.graphics.Bitmap
import com.scruge.scruge.services.api.model.*
import com.scruge.scruge.services.network.enqueue
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class Api {

    enum class Environment(val url: String) {

        test("http://testapi.scruge.world/"),

        prod("http://api.scruge.world/"),

        dev("http://176.213.156.167/");
    }

    // Initialization

    var instance = getInstance(Environment.prod)

    fun getInstance(environment: Environment):BackendApi {
        return Retrofit.Builder()
                .baseUrl(Environment.prod.url)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(BackendApi::class.java)
    }

    // AUTH

    fun login(email: String, password: String, completion: (LoginResponse?, Throwable?) -> Unit) {
        instance.login(AuthRequest(email, password)).enqueue(completion)
    }

    fun signUp(email: String, password: String, completion: (ResultResponse?, Throwable?) -> Unit) {
        instance.signUp(AuthRequest(email, password)).enqueue(completion)
    }

    fun checkEmail(email:String, completion: (ResultResponse?, Throwable?) -> Unit) {
        instance.checkEmail(EmailRequest(email)).enqueue(completion)
    }

    // PROFILE

    fun getUserId(completion: (UserIdResponse?, Throwable?) -> Unit) {
        val token = ""
        instance.getUserId(token).enqueue(completion)
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

    // PROFILE

    @GET("user/{token}/id")
    fun getUserId(@Path("token") token:String): Call<UserIdResponse>

    @POST("avatar/{token}")
    fun updateProfileImage(@Path("token") token:String): Call<ResultResponse>

    @PUT("profile/{token}")
    fun updateProfile(@Path("token") token:String, @Body request: ProfileRequest): Call<ResultResponse>

    @GET("profile/{token}")
    fun getProfile(@Path("token") token:String): Call<ProfileResponse>


}