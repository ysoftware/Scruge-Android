package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import kotlinx.android.synthetic.main.cell_milestone.view.*

class MilestoneCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: MilestoneVM): MilestoneCell {
        itemView.milestone_title.text = "${vm.date}\n${vm.fundsRelease}"
        itemView.milestone_description.text = vm.description
        return this
    }
}