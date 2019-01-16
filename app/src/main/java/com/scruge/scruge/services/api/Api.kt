package com.scruge.scruge.services.api

import android.net.Uri
import android.util.Log
import com.memtrip.eos.core.crypto.EosPublicKey
import com.scruge.scruge.dependencies.serialization.toMap
import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.entity.Comment
import com.scruge.scruge.model.entity.Update
import com.scruge.scruge.model.error.AuthError
import com.scruge.scruge.model.error.wrap
import com.scruge.scruge.services.Service
import com.scruge.scruge.services.api.model.*
import com.scruge.scruge.services.eos.EosName
import com.scruge.scruge.services.eos.Token
import com.scruge.scruge.viewmodel.campaign.CampaignQuery
import com.scruge.scruge.viewmodel.comment.CommentQuery
import com.scruge.scruge.viewmodel.comment.CommentSource
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.net.URI

class Api {

    var isLoggingEnabled = true
    var logLimit = 300

    enum class Environment(val url: String) {

        test("http://testapi.scruge.world/"),

        prod("https://api.scruge.world/"),

        dev("http://176.213.156.167/")
    }

    // Initialization

    val serviceUrl get() = environment.url
    var environment = Environment.test; set(value) { field = value; service = createService(value) }
    private var service = createService(Environment.test)

    private fun createService(environment: Environment): BackendApi {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder().baseUrl(environment.url).addConverterFactory(GsonConverterFactory.create())
                .client(client).build().create(BackendApi::class.java)
    }

    // WALLET

    fun createAccount(accountName: EosName, publicKey: EosPublicKey, completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.createAccount(it, AccountRequest(accountName.toString(), publicKey.toString())).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getDefaultTokens(completion: (Result<List<Token>>)->Unit) {
        val list = listOf("diatokencore DIA",       // arbitrary list of "top" tokens
                          "eosblackteam BLACK",
                          "taketooktook TOOK",
                          "publytoken11 PUB",
                          "everipediaiq IQ",
                          "betdicetoken DICE",
                          "zkstokensr4u ZKS",
                          "hirevibeshvt HVT",
                          "goldioioioio FGIO",
                          "ethsidechain EETH")
        completion(Result.success(list.map { Token(it) }))
    }

    // AUTH

    fun resetPassword(email:String, completion: (Result<ResultResponse>) -> Unit) {
        service.resetPassword(LoginRequest(email)).enqueue(completion)
    }

    fun login(email: String, password: String, completion: (Result<LoginResponse>) -> Unit) {
        service.login(AuthRequest(email, password)).enqueue(completion)
    }

    fun signUp(email: String, password: String, completion: (Result<ResultResponse>) -> Unit) {
        service.signUp(AuthRequest(email, password)).enqueue(completion)
    }

    fun checkEmail(email: String, completion: (Result<ResultResponse>) -> Unit) {
        service.checkEmail(EmailRequest(email)).enqueue(completion)
    }

    // PROFILE

    fun getUserId(completion: (Result<UserIdResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getUserId(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun updateProfileImage(uri: Uri, completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val f = File(URI(uri.toString()))
            val reqFile = RequestBody.create(MediaType.parse("image/jpg"), f)
            val body = MultipartBody.Part.createFormData("image", "image.jpg", reqFile)
            service.updateProfileImage(body, it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun updateProfile(name: String, country: String, description: String,
                      completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.updateProfile(it, ProfileRequest(name, country, description)).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getProfile(completion: (Result<ProfileResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getProfile(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    // CATEGORIES & TAGS

    // CAMPAIGNS

    fun getCampaign(id: Int, completion: (Result<CampaignResponse>) -> Unit) {
        service.getCampaign(id).enqueue(completion)
    }

    fun getCampaignList(query: CampaignQuery?, completion: (Result<CampaignListResponse>) -> Unit) {
        if (query != null && query.requestType != CampaignQuery.RequestType.regular) {
            Service.tokenManager.getToken()?.let {
                when (query.requestType) {
                    CampaignQuery.RequestType.backed -> service.getBacked(it).enqueue(completion)
                    CampaignQuery.RequestType.subscribed -> service.getSubscribed(it).enqueue(completion)
                    else -> return@let
                }
            } ?: completion(Result.failure(AuthError.noToken.wrap()))
        }
        else {
            service.getCampaignList(CampaignListRequest(query).toMap()).enqueue(completion)
        }
    }

    // SUBSCRIPTIONS

    fun getSubscriptionStatus(campaign: Campaign, completion: (Result<BoolResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getSubscriptionStatus(it, CampaignRequest(campaign.id).toMap()).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun setSubscribing(subscribing: Boolean, campaign: Campaign,
                       completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val request = CampaignRequest(campaign.id)
            if (subscribing) service.subscribe(it, request).enqueue(completion)
            else service.unsubscribe(it, request).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    // UPDATES

    fun getUpdateList(campaign: Campaign, completion: (Result<UpdateListResponse>) -> Unit) {
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

    fun getUpdateDescription(update: Update, campaign: Campaign, completion: (Result<HTMLResponse>) -> Unit) {
        service.getUpdateDescription(campaign.id, update.id).enqueue(completion)
    }

    fun getCampaignContent(campaign: Campaign, completion: (Result<HTMLResponse>) -> Unit) {
        service.getCampaignContent(campaign.id).enqueue(completion)
    }

    fun getUpdateContent(update: Update, completion: (Result<HTMLResponse>) -> Unit) {
        service.getUpdateContent(update.id).enqueue(completion)
    }

    // MILESTONES

    fun getMilestones(campaign: Campaign, completion: (Result<MilestoneListResponse>) -> Unit) {
        service.getMilestones(campaign.id).enqueue(completion)
    }

    // CONTRIBUTIONS

    fun getDidContribute(campaignId: Int, completion: (Result<BoolResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getDidContribute(it, CampaignRequest(campaignId).toMap()).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getDidVote(campaignId: Int, completion: (Result<BoolResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getDidVote(it, CampaignRequest(campaignId).toMap()).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun notifyVote(campaignId: Int, value: Boolean, transactionId: String,
                   completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val request = VoteNotificationRequest(value, campaignId, transactionId)
            service.notifyVote(it, request).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun notifyContribution(campaignId: Int, amount: Double, transactionId: String,
                           completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val request = ContributionNotificationRequest(amount, campaignId, transactionId)
            service.notifyContribution(it, request).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getContributionHistory(completion: (Result<ContributionHistoryResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            service.getContributionHistory(it).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getVoteResult(campaignId: Int, completion: (Result<VotesResultResponse>) -> Unit) {
        service.getVoteResult(campaignId).enqueue(completion)
    }

    fun getVoteInfo(campaignId: Int, completion: (Result<VoteInfoResponse>) -> Unit) {
        service.getVoteInfo(campaignId).enqueue(completion)
    }

    // COMMENTS

    fun likeComment(comment: Comment, value: Boolean, completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val request = CommentLikeRequest(it, value)
            service.likeComment(comment.id, request).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun postComment(comment: String, source: CommentSource, completion: (Result<ResultResponse>) -> Unit) {
        Service.tokenManager.getToken()?.let {
            val request = CommentRequest(source, it, comment)
            service.postComment(request).enqueue(completion)
        } ?: completion(Result.failure(AuthError.noToken.wrap()))
    }

    fun getComments(query: CommentQuery, completion: (Result<CommentListResponse>) -> Unit) {
        val request = CommentListRequest(query, Service.tokenManager.getToken())
        service.getComments(request.toMap()).enqueue(completion)
    }

    // LOGGING

    fun log(message: String) {
        if (isLoggingEnabled) {
            Log.e("BACKEND API", message.take(logLimit))
        }
    }
}
