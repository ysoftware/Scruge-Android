package com.scruge.scruge.viewmodel.campaign

import com.scruge.scruge.dependencies.format
import com.scruge.scruge.dependencies.formatRounding
import com.scruge.scruge.model.entity.Campaign
import com.scruge.scruge.services.Service
import com.scruge.scruge.viewmodel.ViewState
import com.ysoftware.mvvm.single.ViewModel
import java.net.URL

class CampaignVM(model: Campaign?) : ViewModel<Campaign>(model), PartialCampaignViewModel,
        PartialCampaignModelHolder<Campaign> {

    enum class Status {

        funding, milestone, activeVote, waiting, closed
    }

    private var id:Int?

    var isSubscribed:Boolean? = null
        private set(value) {
            field = value
            notifyUpdated()
        }

    var canVote:Boolean? = null
        private set(value) {
            field = value
            notifyUpdated()
        }

    var state: ViewState = ViewState.loading
        private set(value) {
            field = value
            notifyUpdated()
        }

    // SETUP

    init {
        id = model?.id
    }

    constructor(id:Int) : this(null) {
        this.id = id
        load()
    }

    private fun resetViewModels() {
//        Service.api.getCampaign
    }

    // ACTIONS

    fun load() {
        state = ViewState.loading
        reloadData()
    }

    fun reloadData() {
        // todo
    }



    // partial

    override val description get() = model?.description ?: ""

    override val title get() = model?.title ?: ""

    override val imageUrl: URL? get() {
        val m = model
        if (m?.imageUrl != null) { return URL(m.imageUrl) }
        return null
    }

    override val progress:Double get() {
        val m = model
        if (m != null) { return m.economics.raised / m.economics.softCap }
        return 0.0
    }

    override val progressString
        get() = "${(progress * 100).formatRounding()}% raised"

    override val raisedString: String get() {
        val m = model
        if (m != null) {
            val raised = m.economics.raised.format()
            val total = m.economics.softCap.format()
            return "$raised raised of $total"
        }
        return ""
    }

    override val daysLeft:String get()  {
        val m = model
        if (m != null) {
            val end = m.endTimestamp
            return "$end"
        }
        else {
            return ""
        }
    }
}