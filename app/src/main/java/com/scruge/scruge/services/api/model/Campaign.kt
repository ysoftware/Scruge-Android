package com.scruge.scruge.services.api.model

import com.scruge.scruge.dependencies.serialization.Codable
import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.entity.PartialCampaign
import com.scruge.scruge.viewmodel.campaign.CampaignQuery

// response

data class CampaignResponse(val campaign: Campaign)

data class CampaignListResponse(val campaigns:List<PartialCampaign>)

// request

data class CampaignRequest(val campaignId:Int):Codable

class CampaignListRequest(q: CampaignQuery?): Codable {

    val page:Int = q?.page ?: 0

    val query:String? = q?.query

//    val category:String? = q?.category?.model?.id

    val tags:List<String>? = q?.tags

    val type:String? = q?.type
}