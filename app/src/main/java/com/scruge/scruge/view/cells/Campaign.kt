package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.scruge.scruge.viewmodel.campaign.PartialCampaignVM
import com.scruge.scruge.viewmodel.campaign.PartialCampaignViewModel
import kotlinx.android.synthetic.main.cell_campaign_info.view.*
import kotlinx.android.synthetic.main.cell_campaign_small.view.*

class CampaignSmallCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: PartialCampaignViewModel) {
        itemView.campaign_small_title.text = vm.title
        itemView.campaign_small_date.text = vm.daysLeft
        itemView.campaign_small_description.text = vm.description
    }
}

class CampaignInfoCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: CampaignVM) {
        itemView.campaign_image
    }
}
