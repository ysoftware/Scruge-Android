package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.model.entity.Member
import com.scruge.scruge.model.entity.Social
import com.scruge.scruge.viewmodel.campaign.CampaignVM

class AboutCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: CampaignVM):AboutCell {

        return this
    }

    fun memberTap(tap: (Member) -> Unit):AboutCell {

        return this
    }

    fun socialTap(tap: (Social) -> Unit):AboutCell {

        return this
    }
}