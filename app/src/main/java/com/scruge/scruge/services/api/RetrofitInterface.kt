package com.scruge.scruge.services.api

import com.scruge.scruge.services.api.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface BackendApi {

    // WALLET
    @POST("user/{token}/create_eos_account")
    fun createAccount(@Path("token") token:String,
                      @Body request: AccountRequest): Call<ResponseBody>

    // AUTH

    @POST("auth/login")
    fun login(@Body request: AuthRequest): Call<ResponseBody>

    @POST("auth/register")
    fun signUp(@Body request: AuthRequest): Call<ResponseBody>

    @POST("auth/check_email")
    fun checkEmail(@Body request: EmailRequest): Call<ResponseBody>

    // PROFILE

    @GET("user/{token}/id")
    fun getUserId(@Path("token") token:String): Call<ResponseBody>

    @POST("avatar/{token}")
    fun updateProfileImage(@Path("token") token:String): Call<ResponseBody>

    @PUT("profile/{token}")
    fun updateProfile(@Path("token") token:String,
                      @Body request: ProfileRequest): Call<ResponseBody>

    @GET("profile/{token}")
    fun getProfile(@Path("token") token:String): Call<ResponseBody>

    // CATEGORIES & TAGS


    // CAMPAIGNS

    @GET("campaign/{id}")
    fun getCampaign(@Path("id") id:Int): Call<ResponseBody>

    @GET("campaigns/{token}/backed")
    fun getBacked(@Path("token") token:String): Call<ResponseBody>

    @GET("campaigns/{token}/subscribed")
    fun getSubscribed(@Path("token") token:String): Call<ResponseBody>

    @GET("campaigns")
    fun getCampaignList(@QueryMap map: Map<String, Any>): Call<ResponseBody>

    // SUBSCRIPTIONS

    @HTTP(method = "GET", path = "user/{token}/is_subscribed", hasBody = true)
    fun getSubscriptionStatus(@Path("token") token:String,
                              @Body request: CampaignRequest): Call<ResponseBody>

    @POST("user/{token}/campaign_subscribe")
    fun subscribe(@Path("token") token:String,
                  @Body request: CampaignRequest): Call<ResponseBody>

    @POST("user/{token}/campaign_unsubscribe")
    fun unsubscribe(@Path("token") token:String,
                    @Body request: CampaignRequest): Call<ResponseBody>

    // UPDATES

    @GET("campaign/{campaignId}/updates")
    fun getUpdateList(@Path("campaignId") campaignId:Int): Call<ResponseBody>

    @GET("user/{token}/campaign_updates")
    fun getActivity(@Path("token") token:String): Call<ResponseBody>

    @GET("user/{token}/votes")
    fun getVoteNotifications(@Path("token") token:String): Call<ResponseBody>

    // HTML DESCRIPTION

    @GET("campaign/{campaignId}/update/{updateId}/description")
    fun getUpdateDescription(@Path("campaignId") campaignId:Int,
                             @Path("updateId") updateId:String): Call<ResponseBody>


    @GET("campaign/{campaignId}/content")
    fun getCampaignContent(@Path("campaignId") campaignId:Int): Call<ResponseBody>

    @GET("update/{updateId}/content")
    fun getUpdateContent(@Path("updateId") updateId:String): Call<ResponseBody>

    // MILESTONES

    @GET("campaign/{campaignId}/milestones")
    fun getMilestones(@Path("campaignId") campaignId:Int): Call<ResponseBody>

    // CONTRIBUTIONS

    @HTTP(method = "GET", path = "user/{token}/did_contribute", hasBody = true)
    fun getDidContribute(@Path("token") token:String,
                         @Body request:CampaignRequest): Call<ResponseBody>

    @HTTP(method = "GET", path = "user/token/did_vote", hasBody = true)
    fun getDidVote(@Path("token") token:String,
                   @Body request:CampaignRequest): Call<ResponseBody>

    @POST("user/{token}/vote")
    fun notifyVote(@Path("token") token:String,
                   @Body request: VoteNotificationRequest):Call<ResponseBody>

    @POST("user/{token}/contributions")
    fun notifyContribution(@Path("token") token:String,
                           @Body request: ContributionNotificationRequest):Call<ResponseBody>

    @GET("user/{token}/contributions")
    fun getContributionHistory(@Path("token") token:String):Call<ResponseBody>

    @GET("campaign/{campaignId}/vote_results")
    fun getVoteResult(@Path("campaignId") campaignId:Int):Call<ResponseBody>

    @GET("campaign/{campaignId}/votes")
    fun getVoteInfo(@Path("campaignId") campaignId:Int):Call<ResponseBody>

    // COMMENTS

    @POST("campaign/{campaignId}/comment")
    fun postCampaignComment(@Path("campaignId") campaignId:Int,
                            @Body request:CommentRequest):Call<ResponseBody>

    @POST("update/{updateId}/comment")
    fun postUpdateComment(@Path("updateId") updateId:String,
                          @Body request:CommentRequest):Call<ResponseBody>

    @HTTP(method = "GET", path = "campaign/{campaignId}/comment", hasBody = true)
    fun getCampaignComments(@Path("campaignId") campaignId:Int,
                            @Body request:CommentListRequest):Call<ResponseBody>

    @HTTP(method = "GET", path = "update/{updateId}/comment", hasBody = true)
    fun getUpdateComments(@Path("updateId") updateId:String,
                          @Body request:CommentListRequest):Call<ResponseBody>
}