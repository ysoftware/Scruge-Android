package com.scruge.scruge.viewmodel.milestone

import com.scruge.scruge.R
import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.dependencies.dataformatting.formatRounding
import com.scruge.scruge.dependencies.view.string
import com.scruge.scruge.model.entity.Milestone
import com.ysoftware.mvvm.single.ViewModel

class MilestoneVM(model: Milestone?) : ViewModel<Milestone>(model) {

    val description get() = model?.description ?: ""

    val date get() = model?.let { datePresent(it.endTimestamp, "d MMMM yyyy") } ?: ""

    val fundsRelease:String get() {
        val value = (model?.fundsReleasePercent ?: 0.0).formatRounding()
        return R.string.label_milestone_funds_release.string(value)
    }
}