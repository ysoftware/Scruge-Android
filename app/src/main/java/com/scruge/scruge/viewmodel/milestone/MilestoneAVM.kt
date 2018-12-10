package com.scruge.scruge.viewmodel.milestone

import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.model.entity.Milestone
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.array.SimpleArrayViewModel

class MilestoneAVM(val campaign: Campaign?) : SimpleArrayViewModel<Milestone, MilestoneVM>() {

    constructor(milestones: List<Milestone>) : this(null) {
        setData(milestones.map { MilestoneVM(it) })
    }

    override fun fetchData(block: (Result<Collection<MilestoneVM>>) -> Unit) {
        campaign?.let {
            Service.api.getMilestones(it) { result ->
                block(result.map { it.milestones.map { MilestoneVM(it) } })
            }
        }
    }
}