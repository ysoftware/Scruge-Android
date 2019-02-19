package com.scruge.scruge.view.ui.bounty

import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.array.SimpleArrayViewModel

class BountyAVM(var projectVM:ProjectVM): SimpleArrayViewModel<Bounty, BountyVM>() {

    override fun fetchData(block: (Result<Collection<BountyVM>>) -> Unit) {
        Service.api.getBounties(projectVM) { result ->
            block(result.map { it.bounties.map { BountyVM(it) } })
        }
    }
}