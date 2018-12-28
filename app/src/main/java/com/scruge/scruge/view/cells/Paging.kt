package com.scruge.scruge.view.cells

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.view.setupForHorizontalLayout
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.viewmodel.faq.FaqAVM
import com.scruge.scruge.viewmodel.faq.FaqVM
import com.scruge.scruge.viewmodel.milestone.MilestoneAVM
import com.scruge.scruge.viewmodel.milestone.MilestoneVM
import kotlinx.android.synthetic.main.cell_paging.view.*

class PagingCell(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var faqVM:FaqAVM? = null
    var milestoneVM: MilestoneAVM? = null
    lateinit var currentMilestone: MilestoneVM
    private lateinit var faqTap:(FaqVM)->Unit
    private lateinit var milestneTap:(MilestoneVM)->Unit
    private var currentIndex = 0
    private var didScroll = false

    fun setup(vm: FaqAVM):PagingCell {
        itemView.paging_title.text = "Frequently answered questions"
        faqVM = vm
        itemView.paging_indicator.count = vm.numberOfItems
        setupTable()

        return this
    }

    fun setupTable() {
        itemView.paging_recycler_view.setupForHorizontalLayout()

        // fix for 'already set'
        if (itemView.paging_recycler_view.onFlingListener != null) { return }

        LinearSnapHelper().attachToRecyclerView(itemView.paging_recycler_view)
        itemView.paging_recycler_view.adapter = Adapter(this)

        itemView.paging_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val position = manager.findFirstVisibleItemPosition()

                itemView.paging_indicator.selection = position

                if (milestoneVM != null) {
                    itemView.paging_title.text = when (position) {
                        currentIndex -> "Current milestone"
                        currentIndex + 1 -> "Next milestone"
                        in 0 until currentIndex -> "Previous milestone"
                        else -> "Future milestone"
                    }
                }
            }
        })
    }

    fun setup(vm:MilestoneAVM, currentMilestone:MilestoneVM):PagingCell {
        itemView.paging_title.text = "Milestones"
        milestoneVM = vm
        this.currentMilestone = currentMilestone
        setupTable()
        itemView.paging_indicator.count = vm.numberOfItems

        // find current index
        for (i in 0 until vm.numberOfItems) {
            val s = vm.item(i)
            if (s.model?.id == currentMilestone.model?.id) {
                currentIndex = i
            }
        }

        // scroll and select indicator to current index
        if (vm.numberOfItems > currentIndex && !didScroll) {
            itemView.paging_recycler_view.scrollToPosition(currentIndex)
            itemView.paging_indicator.selection = currentIndex
        }

        return this
    }

    fun faqTap(tap: (FaqVM)->Unit): PagingCell {
        faqTap = tap
        return this
    }

    fun milestoneTap(tap: (MilestoneVM)->Unit): PagingCell {
        milestneTap = tap
        return this
    }

    class Adapter(private val pagingCell:PagingCell): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val i = LayoutInflater.from(parent.context)
            return if (pagingCell.faqVM != null) {
                FaqCell(i.inflate(R.layout.cell_faq, parent, false))
            }
            else {
                MilestoneCell(i.inflate(R.layout.cell_milestone, parent, false))
            }
        }

        override fun getItemCount(): Int {
            return pagingCell.faqVM?.numberOfItems ?: pagingCell.milestoneVM?.numberOfItems ?: 0
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as? FaqCell)?.let {
                val item = pagingCell.faqVM!!.item(position)
                it.setup(item)
                it.itemView.setOnClickListener {
                    pagingCell.faqTap.invoke(item)
                }
            } ?: (holder as? MilestoneCell)?.let {
                val item = pagingCell.milestoneVM!!.item(position)
                it.setup(item)
                it.itemView.setOnClickListener {
                    pagingCell.milestneTap.invoke(item)
                }
            }
        }
    }
}