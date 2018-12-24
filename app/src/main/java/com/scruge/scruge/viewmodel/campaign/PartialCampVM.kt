package com.scruge.scruge.viewmodel.campaign

import android.net.Uri
import com.scruge.scruge.dependencies.dataformatting.dateToRelative
import com.scruge.scruge.model.entity.PartialCampaign
import com.ysoftware.mvvm.single.ViewModel
import kotlin.math.roundToInt

class PartialCampaignVM(model: PartialCampaign?) : ViewModel<PartialCampaign>(model),
        PartialCampaignModelHolder<PartialCampaign>,
        PartialCampaignViewModel {

    var id = model?.id ?: 0

    override val description get() = model?.description ?: ""

    override val title get() = model?.title ?: ""

    override val imageUrl: Uri? get() {
        val m = model
        if (m?.imageUrl != null) { return try { Uri.parse(m.imageUrl) } catch (e:Exception) { null }}
        return null
    }

    override val raised get() = model?.economics?.raised?.roundToInt()?.toDouble() ?: 0.0

    override val hardCap get() = model?.economics?.hardCap ?: 0

    override val softCap get() = model?.economics?.softCap ?: 0

    override val daysLeft:String get() = model?.let { dateToRelative(it.endTimestamp, "ends", "ended") } ?: ""
}
