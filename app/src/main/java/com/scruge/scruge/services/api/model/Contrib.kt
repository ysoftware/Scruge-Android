package com.scruge.scruge.services.api.model

import com.scruge.scruge.model.entity.Contribution

// request

data class ContributionNotificationRequest(val amount:Double, val campaignId:Int, val transactionId:String)

data class VoteNotificationRequest(val value:Boolean, val campaignId:Int, val transactionId:String)

// response

data class ContributionHistoryResponse(val contributions:List<Contribution>)