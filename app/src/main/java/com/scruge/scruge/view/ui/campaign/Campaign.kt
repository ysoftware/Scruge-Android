package com.scruge.scruge.view.ui.campaign

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.scruge.scruge.R
import com.scruge.scruge.dependencies.navigation.NavigationFragment
import com.scruge.scruge.dependencies.view.setupForVerticalLayout
import com.scruge.scruge.view.cells.CampaignCell
import com.scruge.scruge.viewmodel.campaign.CampaignVM
import com.ysoftware.mvvm.single.ViewModel
import com.ysoftware.mvvm.single.ViewModelDelegate
import kotlinx.android.synthetic.main.fragment_campaign.*

class CampaignFragment: NavigationFragment(), ViewModelDelegate {

    enum class Block(val rawValue:Int) {
        info(0),  economies(1), update(2), comments(3),
        about(4), faq(5), milestone(6), documents(7)
    }

    // PROPERTIES

    lateinit var vm: CampaignVM
    var adapter:Adapter? = null

    // SETUP

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_featured, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupVM()
        setupTable()
    }

    private fun setupVM() {
        vm.delegate = this
        vm.load()
        adapter = Adapter(this)
    }

    private fun setupTable() {
        recycler_view.setupForVerticalLayout()
        recycler_view.adapter = adapter
    }

    fun shouldDisplay(block:Block):Boolean {
        return when (block) {
            Block.info, Block.comments, Block.about -> true
            Block.milestone -> vm.currentMilestoneVM != null
            Block.update -> vm.lastUpdateVM != null
            Block.economies -> vm.economiesVM != null
            Block.documents -> vm.documentsVM?.numberOfItems ?: 0 != 0
            Block.faq -> vm.faqVM?.numberOfItems ?: 0 != 0
        }
    }

    // DELEGATE

    override fun <M : Comparable<M>> didUpdateData(viewModel: ViewModel<M>) {

    }

    // ADAPTER

    class Adapter(private val fr:CampaignFragment): RecyclerView.Adapter<CampaignCell>() {

        override fun getItemViewType(position: Int): Int {
            return Block.values().filter { fr.shouldDisplay(it) }[position].rawValue
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignCell {
            val id = R.layout.cell_campaign_small
            return CampaignCell(LayoutInflater.from(parent.context).inflate(id, parent, false))
        }

        override fun getItemCount(): Int {
            return Block.values().filter { fr.shouldDisplay(it) }.size
        }

        override fun onBindViewHolder(holder: CampaignCell, position: Int) {

        }
    }

}