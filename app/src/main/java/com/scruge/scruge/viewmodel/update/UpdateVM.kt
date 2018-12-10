package com.scruge.scruge.viewmodel.update

import com.scruge.scruge.dependencies.dataformatting.datePresent
import com.scruge.scruge.model.entity.Update
import com.scruge.scruge.services.Service
import com.ysoftware.mvvm.single.ViewModel

class UpdateVM(model: Update?) : ViewModel<Update>(model) {

    val imageUrl get() = model?.imageUrl

    val date get() = model?.let { datePresent(it.timestamp, "d MMMM yyyy") } ?: ""

    val title get() = model?.title ?: ""

    val description get() = model?.description ?: ""

    // campaign info (for activity)

    val campaignId = model?.campaignInfo?.id ?: 0

    val campaignTitle = model?.campaignInfo?.title ?: ""

    val campaignImageUrl = model?.campaignInfo?.imageUrl

    // ACTIONS

    fun loadDescription(completion:(String)->Unit) {
        model?.let {
            Service.api.getUpdateContent(it) { result ->
                completion(result.getOrNull()?.content ?: "")
            }
        }
    }
}