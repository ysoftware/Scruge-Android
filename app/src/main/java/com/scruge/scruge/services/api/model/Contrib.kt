package com.scruge.scruge.services.api.model

import com.scruge.scruge.model.entity.Contribution
import com.scruge.scruge.model.entity.VoteInfo
import com.scruge.scruge.model.entity.VoteResult
import com.scruge.scruge.model.entity.Voting

// request

data class ContributionNotificationRequest(val amount:Double, val campaignId:Int, val transactionId:String)

data class VoteNotificationRequest(val value:Boolean, val campaignId:Int, val transactionId:String)

// response

data class ContributionHistoryResponse(val contributions:List<Contribution>)

data class VotesResultResponse(val votings:List<VoteResult>)

data class VoteInfoResponse(val voting:VoteInfo)

data class ActiveVotesResponse(val votings:List<Voting>)