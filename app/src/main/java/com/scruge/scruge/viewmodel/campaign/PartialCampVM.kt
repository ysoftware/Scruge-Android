package com.scruge.scruge.viewmodel.campaign

import com.scruge.scruge.dependencies.dataformatting.format
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.model.entity.PartialCampaign
import com.ysoftware.mvvm.single.ViewModel
import java.net.URL

class PartialCampaignVM(model: PartialCampaign?) : ViewModel<PartialCampaign>(model),
        PartialCampaignModelHolder<PartialCampaign>,
        PartialCampaignViewModel {

    var id = model?.id ?: 0

    override val description get() = model?.description ?: ""

    override val title get() = model?.title ?: ""

    override val imageUrl:URL? get() {
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
