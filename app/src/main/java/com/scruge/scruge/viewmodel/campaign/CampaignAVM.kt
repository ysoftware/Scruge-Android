package com.scruge.scruge.viewmodel.campaign

import com.scruge.scruge.model.entity.PartialCampaign
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.array.ArrayViewModel

class CampaignAVM : ArrayViewModel<PartialCampaign, PartialCampaignVM, CampaignQuery>() {

    init {
        query = CampaignQuery()
    }

    override fun fetchData(query: CampaignQuery?, block: (Result<List<PartialCampaign>>) -> Unit) {
        Service.api.getCampaignList(query) { result ->
            block(result.map { it.campaigns })
        }
    }
}