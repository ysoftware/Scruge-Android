package com.scruge.scruge.services.api

import android.graphics.Bitmap
import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.error.AuthError
import com.scruge.scruge.model.error.ScrugeError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.model.*
import com.scruge.scruge.services.network.enqueue
import com.scruge.scruge.viewmodel.campaign.CampaignQuery
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

    private var service = service(Environment.test)

    fun service(environment: Environment):BackendApi {
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

    fun createAccount(accountName:String,
                      publicKey:String,
                      completion: (Result<LoginResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.createAccount(it, AccountRequest(accountName, publicKey)).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    // AUTH

    fun login(email: String, password: String, completion: (Result<LoginResponse>) -> Unit) {
        service.login(AuthRequest(email, password)).enqueue(completion)
    }

    fun signUp(email: String, password: String, completion: (Result<ResultResponse>) -> Unit) {
        service.signUp(AuthRequest(email, password)).enqueue(completion)
    }

    fun checkEmail(email:String, completion: (Result<ResultResponse>) -> Unit) {
        service.checkEmail(EmailRequest(email)).enqueue(completion)
    }

    // PROFILE

    fun getUserId(completion: (Result<UserIdResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getUserId(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun updateProfileImage(image: Bitmap, completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            // todo
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getProfile(completion: (Result<ProfileResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getProfile(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    // CATEGORIES & TAGS

    // CAMPAIGNS

    fun getCampaign(id:Int, completion:(Result<CampaignResponse>) -> Unit) {
        service.getCampaign(id).enqueue(completion)
    }

    fun getCampaignList(query:CampaignQuery?, completion: (Result<CampaignListResponse>) -> Unit) {
        if (query != null && query.requestType != CampaignQuery.RequestType.regular) {
            Service.tokenManager.getToken()?.let {
                when (query.requestType) {
                    CampaignQuery.RequestType.backed -> service.getBacked(it)
                    CampaignQuery.RequestType.subscribed -> service.getSubscribed(it)
                    else -> return@let
                }
            } ?: completion(Result.failure(AuthError.noToken.wrap()))
            return
        }
        service.getCampaignList(CampaignListRequest(query))
    }

    // SUBSCRIPTIONS

    fun getSubscriptionStatus(campaign: Campaign, completion: (Result<BoolResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getSubscriptionStatus(it, CampaignRequest(campaign.id)).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun setSubscribing(subscribing:Boolean,
                       campaign:Campaign,
                       completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val request = CampaignRequest(campaign.id)
            if (subscribing) service.subscribe(it, request) else service.unsubscribe(it, request)
                    .enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    // UPDATES

    fun getUpdateList(campaign:Campaign, completion: (Result<UpdateListResponse>) -> Unit) {
        service.getUpdateList(campaign.id).enqueue(completion)
    }

    fun getActivity(completion: (Result<ActivityListResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getActivity(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getVoteNotifications(completion: (Result<ActiveVotesResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getVoteNotifications(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    // HTML DESCRIPTION


}
