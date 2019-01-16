package com.scruge.scruge.viewmodel.update

import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.entity.Update
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.array.SimpleArrayViewModel

class UpdateAVM(val source:Source): SimpleArrayViewModel<Update, UpdateVM>() {

    enum class Source {
        campaign;

        var campaignObject: Campaign? = null
    }

    constructor(updates:List<Update>, campaign:Campaign): this(Source.campaign) {
        source.campaignObject = campaign
        setData(updates.map { UpdateVM(it) })
    }

    override fun fetchData(block: (Result<Collection<UpdateVM>>) -> Unit) {
        when (source) {
            Source.campaign -> {
                Service.api.getUpdateList(source.campaignObject!!) { result ->
                    block(result.map { it.updates.map { UpdateVM(it) }})
                }
            }
        }
    }
}