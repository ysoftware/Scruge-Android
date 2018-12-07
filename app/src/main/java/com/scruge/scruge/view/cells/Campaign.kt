package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.campaign.PartialCampaignVM
import kotlinx.android.synthetic.main.cell_campaign_small.view.*

class CampaignCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: PartialCampaignVM) {
        itemView.campaign_title.text = vm.title
        itemView.campaign_date.text = vm.daysLeft
        itemView.campaign_description.text = vm.description
    }
}