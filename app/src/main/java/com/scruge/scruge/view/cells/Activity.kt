package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.update.UpdateVM
import kotlinx.android.synthetic.main.cell_activity.view.*


class VotingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

}

class ActivityViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private lateinit var vm:UpdateVM

    fun setup(vm: UpdateVM):ActivityViewHolder {
        this.vm = vm

        itemView.cell_activity_date.text = vm.date
        itemView.cell_activity_title.text = vm.title
        itemView.cell_activity_text.text = vm.description
        // todo image?

        itemView.cell_activity_campaign.text = "${vm.campaignTitle}  posted an update"

        return this
    }

    fun campaignTap(tap:(UpdateVM)->Unit):ActivityViewHolder {
        itemView.cell_activity_campaign.setOnClickListener { tap(vm) }
        return this
    }

    fun activityTap(tap:(UpdateVM)->Unit):ActivityViewHolder {
        itemView.cell_activity_view.setOnClickListener { tap(vm) }
        return this
    }
}