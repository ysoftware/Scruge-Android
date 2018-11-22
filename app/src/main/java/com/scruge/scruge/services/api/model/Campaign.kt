package com.scruge.scruge.services.api.model

import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.entity.PartialCampaign

// response

data class CampaignResponse(val result: Int, val campaign: Campaign?)

data class CampaignListResponse(val result:Int, val campaigns:List<PartialCampaign>)

// request

data class CampaignRequest(val campaignId:String)

data class CampaignListRequest(q: CampaignQuery?) {

    val page:Int = q?.page ?: 0

    val query:String? = q?.query

    val category:String? = q?.category?.model?.id

    val tags:List<String>? = q?.tags

    val type:String? = q?.type
}