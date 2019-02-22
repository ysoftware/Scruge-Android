package com.scruge.scruge.view.ui.bounty

import com.scruge.scruge.dependencies.dataformatting.formatDecimal
import com.scruge.scruge.model.entity.Document
import com.scruge.scruge.model.entity.Social
import com.scruge.scruge.viewmodel.campaign.makeYoutubeFrame
import com.ysoftware.mvvm.single.ViewModel

class ProjectVM(model: Project?) : ViewModel<Project>(model) {

    val providerName:String get() = model?.providerName ?: ""

    val name:String get() = model?.projectName ?: ""

    val imageUrl:String? get() = model?.imageUrl ?: ""

    val videoUrl:String? get() = model?.videoUrl ?: ""

    val description:String get() = model?.projectDescription ?: ""

    val social:List<Social> get() = model?.social ?: listOf()

    val documents:List<Document> get() = model?.documents ?: listOf()

    val tokenSupply:String get() = model?.economics?.tokenSupply?.formatDecimal(" ") ?: ""

    val inflation:String get() {
        val model = model?.economics ?: return ""
        val start = model.annualInflationPercent.start.formatDecimal()
        val end = model.annualInflationPercent.end.formatDecimal()
        return if (model.annualInflationPercent.start != model.annualInflationPercent.end)
            "$start% - $end%" else "$start%"
    }

    val tokenListingDate:String? get() = model?.economics?.listingDate

    val tokenExchange:String? get() = model?.economics?.exchange?.name

    val videoFrame:String? get() = videoUrl?.let { makeYoutubeFrame(it) }

    val tokenUrl:String? get() = model?.economics?.exchange?.url
}