package com.scruge.scruge.view.cells

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.viewmodel.faq.FaqAVM
import com.scruge.scruge.viewmodel.faq.FaqVM
import com.scruge.scruge.viewmodel.milestone.MilestoneAVM
import com.scruge.scruge.viewmodel.milestone.MilestoneVM

class PagingCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun setup(vm: FaqAVM):PagingCell {

        return this
    }

    fun setup(vm:MilestoneAVM, currentMilestone:MilestoneVM):PagingCell {

        return this
    }

    fun tap(tap: (FaqVM)->Unit): PagingCell {

        return this
    }
}