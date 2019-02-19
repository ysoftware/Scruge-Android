package com.scruge.scruge.services.api

import com.scruge.scruge.services.api.model.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

private const val versionString = "" // "v${Api.version}/"

@JvmSuppressWildcards
interface BackendApi {

    @GET(versionString + "/")
    fun getInfo(): Call<ResponseBody>

    // BOUNTY

    @GET(versionString + "projects")
    fun getProjects(): Call<ResponseBody>

    @GET(versionString + "bounties")
    fun getBounties(@QueryMap request: Map<String, Any>): Call<ResponseBody>

    @POST(versionString + "submissions")
    fun postSubmission(@Body request: SubmissionRequest): Call<ResponseBody>

    // WALLET

    @POST(versionString + "user/{token}/create_eos_account")
    fun createAccount(@Path("token") token:String,
                      @Body request: AccountRequest): Call<ResponseBody>

    // AUTH

    @POST(versionString + "auth/login")
    fun login(@Body request: AuthRequest): Call<ResponseBody>

    @POST(versionString + "auth/reset_password")
    fun resetPassword(@Body request: LoginRequest): Call<ResponseBody>

    @POST(versionString + "auth/register")
    fun signUp(@Body request: RegisterRequest): Call<ResponseBody>

    @POST(versionString + "auth/check_email")
    fun checkEmail(@Body request: EmailRequest): Call<ResponseBody>

    // PROFILE

    @GET(versionString + "user/{token}/id")
    fun getUserId(@Path("token") token:String): Call<ResponseBody>

    @Multipart @POST(versionString + "avatar/{token}")
    fun updateProfileImage(@Part image: MultipartBody.Part,
                           @Path("token") token:String): Call<ResponseBody>

    @PUT(versionString + "profile/{token}")
    fun updateProfile(@Path("token") token:String,
                      @Body request: ProfileRequest): Call<ResponseBody>

    @GET(versionString + "profile/{token}")
    fun getProfile(@Path("token") token:String): Call<ResponseBody>

    // CATEGORIES & TAGS


    // CAMPAIGNS

    @GET(versionString + "campaign/{id}")
    fun getCampaign(@Path("id") id:Int): Call<ResponseBody>

    @GET(versionString + "campaigns/{token}/backed")
    fun getBacked(@Path("token") token:String): Call<ResponseBody>

    @GET(versionString + "campaigns/{token}/subscribed")
    fun getSubscribed(@Path("token") token:String): Call<ResponseBody>

    @GET(versionString + "campaigns")
    fun getCampaignList(@QueryMap request: Map<String, Any>): Call<ResponseBody>

    // SUBSCRIPTIONS

    @GET(versionString + "user/{token}/is_subscribed")
    fun getSubscriptionStatus(@Path("token") token:String,
                              @QueryMap request: Map<String, Any>): Call<ResponseBody>

    @POST(versionString + "user/{token}/campaign_subscribe")
    fun subscribe(@Path("token") token:String,
                  @Body request: CampaignRequest): Call<ResponseBody>

    @POST(versionString + "user/{token}/campaign_unsubscribe")
    fun unsubscribe(@Path("token") token:String,
                    @Body request: CampaignRequest): Call<ResponseBody>

    // UPDATES

    @GET(versionString + "campaign/{campaignId}/updates")
    fun getUpdateList(@Path("campaignId") campaignId:Int): Call<ResponseBody>

    @GET(versionString + "user/{token}/activity")
    fun getActivity(@Path("token") token:String,
                    @QueryMap request: Map<String, Any>): Call<ResponseBody>

    @GET(versionString + "activity")
    fun getActivity(@QueryMap request: Map<String, Any>): Call<ResponseBody>

    @GET(versionString + "user/{token}/votes")
    fun getVoteNotifications(@Path("token") token:String): Call<ResponseBody>

    // HTML DESCRIPTION

    @GET(versionString + "campaign/{campaignId}/update/{updateId}/description")
    fun getUpdateDescription(@Path("campaignId") campaignId:Int,
                             @Path("updateId") updateId:String): Call<ResponseBody>


    @GET(versionString + "campaign/{campaignId}/content")
    fun getCampaignContent(@Path("campaignId") campaignId:Int): Call<ResponseBody>

    @GET(versionString + "update/{updateId}/content")
    fun getUpdateContent(@Path("updateId") updateId:String): Call<ResponseBody>

    // MILESTONES

    @GET(versionString + "campaign/{campaignId}/milestones")
    fun getMilestones(@Path("campaignId") campaignId:Int): Call<ResponseBody>

    // CONTRIBUTIONS

    @GET(versionString + "user/{token}/did_contribute")
    fun getDidContribute(@Path("token") token:String,
                         @QueryMap request:Map<String, Any>): Call<ResponseBody>

    @GET(versionString + "user/{token}/did_vote")
    fun getDidVote(@Path("token") token:String,
                   @QueryMap request:Map<String, Any>): Call<ResponseBody>

    @POST(versionString + "user/{token}/vote")
    fun notifyVote(@Path("token") token:String,
                   @Body request: VoteNotificationRequest):Call<ResponseBody>

    @POST(versionString + "user/{token}/contributions")
    fun notifyContribution(@Path("token") token:String,
                           @Body request: ContributionNotificationRequest):Call<ResponseBody>

    @GET(versionString + "user/{token}/contributions")
    fun getContributionHistory(@Path("token") token:String):Call<ResponseBody>

    @GET(versionString + "campaign/{campaignId}/vote_results")
    fun getVoteResult(@Path("campaignId") campaignId:Int):Call<ResponseBody>

    @GET(versionString + "campaign/{campaignId}/votes")
    fun getVoteInfo(@Path("campaignId") campaignId:Int):Call<ResponseBody>

    // COMMENTS

    @POST(versionString + "comment/{commentId}/like")
    fun likeComment(@Path("commentId") commentId:String,
                    @Body request:CommentLikeRequest):Call<ResponseBody>

    @POST(versionString + "comments")
    fun postComment(@Body request:CommentRequest):Call<ResponseBody>

    @GET(versionString + "comments")
    fun getComments(@QueryMap request:Map<String, Any>):Call<ResponseBody>
}