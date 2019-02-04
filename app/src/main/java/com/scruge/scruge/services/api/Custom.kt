package com.scruge.scruge.services.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.scruge.scruge.model.entity.*
import com.scruge.scruge.services.api.model.ActivityListResponse
import com.scruge.scruge.viewmodel.activity.ActivityType

class CustomParser {

    companion object {

        fun parseActivityListResponse(body: String): ActivityListResponse? {

            val gson = Gson()
            val array = mutableListOf<ActivityModel>()
            val json = JsonParser().parse(body).asJsonObject.get("activity").asJsonArray

            for (s in json) {
                val type = ActivityType.from(s.asJsonObject.get("type").asString)

                try {
                    val obj = gson.fromJson(s, when (type) {
                        ActivityType.update -> ActivityUpdate::class.java
                        ActivityType.reply -> ActivityReply::class.java
                        ActivityType.voting -> ActivityVoting::class.java
                        ActivityType.fundingInfo -> ActivityFunding::class.java
                        ActivityType.votingResults -> ActivityVotingResult::class.java
                    })
                    array.add(obj)
                }
                catch (ex: Exception) {
                    Log.e("Custom parsing",
                          "Could not parse ActivityListResponse element: ${ex.localizedMessage}")
                }
            }
            return ActivityListResponse(array)
        }
    }
}